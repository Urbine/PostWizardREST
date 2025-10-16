package net.ygbstudio.postwizard.service;

import static net.ygbstudio.postwizard.utils.Helpers.enumFromValue;
import static net.ygbstudio.postwizard.utils.Helpers.specialCharCleanJoin;
import static net.ygbstudio.postwizard.utils.Logging.loggingInit;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.ygbstudio.postwizard.dao.PostManager;
import net.ygbstudio.postwizard.dao.TaxonomyManager;
import net.ygbstudio.postwizard.dao.TermRelationshipsManager;
import net.ygbstudio.postwizard.dao.TermsManager;
import net.ygbstudio.postwizard.dao.TermsMetaManager;
import net.ygbstudio.postwizard.dto.ClientTaxonomy;
import net.ygbstudio.postwizard.dto.ClientTerm;
import net.ygbstudio.postwizard.entities.WPost;
import net.ygbstudio.postwizard.entities.taxonomies.WPTermRelationships;
import net.ygbstudio.postwizard.entities.taxonomies.WPTermTaxonomy;
import net.ygbstudio.postwizard.entities.taxonomies.WPTerms;
import net.ygbstudio.postwizard.models.Taxonomy;
import net.ygbstudio.postwizard.models.TermMeta;
import net.ygbstudio.postwizard.rest.PostController;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Service class for managing taxonomies in the PostWizard application. This class provides methods
 * to validate, update, and retrieve taxonomies based on the ClientTaxonomy DTO and various
 * enumerations representing valid taxonomy values.
 *
 * <p>The service interacts with the TaxonomyManager to perform database operations related to
 * taxonomies. TaxonomyService also defines additional transactional boundaries for some methods to
 * ensure data consistency and integrity by isolating the transactional context of the database
 * operations with every method call.
 *
 * @see TaxonomyManager
 * @see TermsManager
 * @see TermsMetaManager
 * @see TermRelationshipsManager
 * @see PostManager
 * @see PostController
 * @author Yoham Gabriel @ YGB Studio
 */
@ApplicationScoped
public class TaxonomyService {
  private static final Logger taxonomyLogger = Logger.getLogger(TaxonomyService.class.getName());

  @Nullable
  private static final FileHandler taxonomyLogFileHandler =
      loggingInit(taxonomyLogger, Level.ALL, true);

  @Inject private TaxonomyManager taxonomyDAO;

  @Inject private TermsManager termsDAO;

  @Inject private TermsMetaManager termsMetaDAO;

  @Inject private TermRelationshipsManager termRelationshipsDAO;

  @Inject private PostManager postsDAO;

  /**
   * Converts a JPA-managed entity {@code WPTerms} to a client-side DTO {@code ClientTerm}.
   *
   * @param term the JPA-managed entity {@code WPTerms} to convert
   * @return the client-side DTO {@code ClientTerm} if the conversion is successful, {@code
   *     Optional.empty()} otherwise
   */
  @Transactional(TxType.REQUIRES_NEW)
  public Optional<ClientTerm> convertToClientTerm(@NonNull Optional<WPTerms> term) {
    if (term.isPresent()) {
      ClientTerm clientTerm = new ClientTerm();
      clientTerm.setTermId(term.get().getId());
      clientTerm.setName(term.get().getName());
      clientTerm.setSlug(term.get().getSlug());
      clientTerm.setTermGroup(term.get().getTermGroup());
      if (term.get().getTaxonomy() != null) {
        clientTerm.setTaxonomy(new ClientTaxonomy());
        clientTerm
            .getTaxonomy()
            .setTermTaxonomyId(
                Objects.requireNonNullElse(term.get().getTaxonomy().getTermTaxonomyId(), 0L));
        clientTerm.getTaxonomy().setTaxonomyName(term.get().getTaxonomy().getTaxonomy());
        clientTerm.getTaxonomy().setDescription(term.get().getTaxonomy().getDescription());
        clientTerm
            .getTaxonomy()
            .setCount(
                termRelationshipsDAO.countTaxonomyRelationships(
                    clientTerm.getTaxonomy().getTermTaxonomyId()));
        clientTerm.getTaxonomy().setParent(term.get().getTaxonomy().getParent());
      }
      return Optional.of(clientTerm);
    } else return Optional.empty();
  }

  /**
   * Converts a JPA-managed entity {@code WPTermTaxonomy} to a client-side DTO {@code
   * ClientTaxonomy}.
   *
   * @param termTaxonomy the JPA-managed entity {@code WPTermTaxonomy} to convert
   * @return the client-side DTO {@code ClientTaxonomy} if the conversion is successful, {@code
   *     Optional.empty()} otherwise
   */
  public Optional<ClientTaxonomy> convertToClientTaxonomy(
      @NonNull Optional<WPTermTaxonomy> termTaxonomy) {
    if (termTaxonomy.isPresent()) {
      WPTermTaxonomy wpTermTaxonomy = termTaxonomy.get();
      ClientTaxonomy clientTaxonomy = new ClientTaxonomy();
      clientTaxonomy.setTermTaxonomyId(wpTermTaxonomy.getTermTaxonomyId());
      clientTaxonomy.setTaxonomyName(wpTermTaxonomy.getTaxonomy());
      clientTaxonomy.setDescription(wpTermTaxonomy.getDescription());
      clientTaxonomy.setCount(wpTermTaxonomy.getCount());
      clientTaxonomy.setParent(wpTermTaxonomy.getParent());
      return Optional.of(clientTaxonomy);
    } else return Optional.empty();
  }

  /**
   * Creates a new {@link WPTerms} object with the provided name and slug.
   *
   * @param name the name of the term to create
   * @param slug the slug of the term to create
   * @return the new term
   */
  public WPTerms termFactory(String name, String slug) {
    WPTerms newTerm = new WPTerms();
    newTerm.setName(name);
    newTerm.setSlug(slug);
    newTerm.setTermGroup(0);
    return newTerm;
  }

  /**
   * Retrieves the first occurrence of a term by its name and converts it into a client-side DTO
   * {@code ClientTerm}.
   *
   * <p>If you need to search a term name and match it with the taxonomy name provided by the
   * client, use the {@link #eitherTermNameOrSlugExists(ClientTerm, boolean)} method.
   *
   * @param termName the name of the term to retrieve
   * @return the client-side DTO {@code ClientTerm} if the term exists, {@code Optional.empty()}
   *     otherwise
   */
  @Transactional(TxType.REQUIRES_NEW)
  public Optional<ClientTerm> termExists(@NonNull String termName) {
    Optional<WPTerms> isATerm = termsDAO.termNameExists(termName).findFirst();
    return convertToClientTerm(isATerm);
  }

  /**
   * Retrieves the first occurrence of a term by its slug and converts it into a client-side DTO
   * {@code ClientTerm}.
   *
   * <p>If you need to search a term slug and match it with the taxonomy name provided by the
   * client, use the {@link #eitherTermNameOrSlugExists(ClientTerm, boolean)} method.
   *
   * @param termSlug the slug of the term to retrieve
   * @return the client-side DTO {@code ClientTerm} if the term exists, {@code Optional.empty()}
   *     otherwise
   */
  @Transactional(TxType.REQUIRES_NEW)
  public Optional<ClientTerm> termSlugExists(@NonNull String termSlug) {
    Optional<WPTerms> isATermSlug = termsDAO.termSlugExists(termSlug).findFirst();
    return convertToClientTerm(isATermSlug);
  }

  /**
   * Retrieves a term by its name or slug and converts it into a client-side DTO {@code ClientTerm}.
   * If the term exists, it returns the term; otherwise, it returns an empty Optional.
   *
   * <p>This method will return the first term that matches the term name or slug that has the same
   * taxonomy name provided by the client, if any. If no taxonomy name is provided, it will return
   * the first match.
   *
   * <p><strong>WordPress supports duplicate name and slug for terms as long as the taxonomies are
   * different.
   *
   * @param clientTerm the client-side schema DTO term to retrieve
   * @param enforceTaxonomyMatch if true, the presence of a taxonomy name provided by the client
   *     will be enforced.
   * @return the client-side DTO {@code ClientTerm} if the term exists, {@code Optional.empty()}
   *     otherwise
   */
  @Transactional(TxType.REQUIRES_NEW)
  public Optional<WPTerms> eitherTermNameOrSlugExists(
      @NonNull ClientTerm clientTerm, boolean enforceTaxonomyMatch) {
    if (clientTerm.getTaxonomy().getTaxonomyName() == null && enforceTaxonomyMatch)
      return Optional.empty();

    Predicate<? super WPTerms> termMatchPredicate =
        term ->
            Objects.isNull(clientTerm.getTaxonomy().getTaxonomyName())
                || term.getTaxonomy()
                    .getTaxonomy()
                    .equals(clientTerm.getTaxonomy().getTaxonomyName());

    return termsDAO
        .termNameExists(clientTerm.getName())
        .filter(termMatchPredicate)
        .findAny()
        .or(
            () ->
                termsDAO.termSlugExists(clientTerm.getSlug()).filter(termMatchPredicate).findAny());
  }

  /**
   * Retrieves a term by its taxonomy ID and converts it into a client-side DTO {@code ClientTerm}.
   *
   * @param taxonomyId the ID of the taxonomy to retrieve
   * @return the client-side DTO {@code ClientTerm} if the term exists, {@code Optional.empty()}
   *     otherwise
   */
  @Transactional(TxType.REQUIRES_NEW)
  public Optional<ClientTerm> getClientTermByTaxonomyId(@NonNull Long taxonomyId) {
    Optional<WPTermTaxonomy> termTaxonomy = taxonomyDAO.termTaxonomyIdExists(taxonomyId).findAny();
    return termTaxonomy.isPresent()
        ? convertToClientTerm(Optional.of(termTaxonomy.get().getTerm()))
        : Optional.empty();
  }

  /**
   * Adds a new post tag to the database and returns the client-side DTO {@code ClientTaxonomy}.
   *
   * @param newTagName the name of the new tag to add
   * @param newTagSlug the slug of the new tag to add
   * @return the client-side DTO {@code ClientTaxonomy} if the tag is added successfully, {@code
   *     Optional.empty()} otherwise
   */
  @Transactional(TxType.REQUIRES_NEW)
  public Optional<ClientTaxonomy> addPostTag(
      @NonNull String newTagName, @NonNull String newTagSlug) {
    // Tags do not have metadata; therefore, an empty set is passed to the DAO here.
    WPTerms newTerm = termFactory(newTagName, newTagSlug);
    WPTermTaxonomy newTaxonomy =
        taxonomyDAO.addTermTaxonomy(newTerm, Taxonomy.TAG.toString(), "", 0, 0);
    return convertToClientTaxonomy(Optional.of(newTaxonomy));
  }

  /**
   * Adds a new model to the database and returns the client-side DTO {@code ClientTaxonomy}.
   *
   * @param newModelName the name of the new model to add
   * @param newModelSlug the slug of the new model to add
   * @return the client-side DTO {@code ClientTaxonomy} if the model is added successfully, {@code
   *     Optional.empty()} otherwise
   */
  @Transactional(TxType.REQUIRES_NEW)
  public Optional<ClientTaxonomy> addModel(
      @NonNull String newModelName, @NonNull String newModelSlug) {

    WPTerms newModelTerm = termFactory(newModelName, newModelSlug);

    WPTermTaxonomy newModelTaxonomy =
        taxonomyDAO.addTermTaxonomy(newModelTerm, Taxonomy.MODELS.toString(), "", 0, 0);

    termsMetaDAO.addTermMeta(TermMeta.MODEL_IMG_ID.toString(), "", newModelTaxonomy.getTerm());
    termsMetaDAO.addTermMeta(
        TermMeta.ACTORS_VIEW_COUNT.toString(), "0", newModelTaxonomy.getTerm());

    return convertToClientTaxonomy(Optional.of(newModelTaxonomy));
  }

  /**
   * Adds a new category to the database and returns the client-side DTO {@code ClientTaxonomy}.
   *
   * @param newCategoryName the name of the new category to add
   * @param newCategorySlug the slug of the new category to add
   * @param newCategoryDescription the description of the new category to add
   * @return the client-side DTO {@code ClientTaxonomy} if the category is added successfully,
   *     {@code Optional.empty()} otherwise.
   */
  @Transactional(TxType.REQUIRES_NEW)
  public Optional<ClientTaxonomy> addCategory(
      @NonNull String newCategoryName,
      @NonNull String newCategorySlug,
      @NonNull String newCategoryDescription) {

    WPTerms newCategoryTerm = termFactory(newCategoryName, newCategorySlug);

    WPTermTaxonomy newCategoryTaxonomy =
        taxonomyDAO.addTermTaxonomy(
            newCategoryTerm, Taxonomy.CATEGORY.toString(), newCategoryDescription, 0, 0);

    termsMetaDAO.addTermMeta(
        TermMeta.CATEGORY_IMAGE_ID.toString(), "", newCategoryTaxonomy.getTerm());
    termsMetaDAO.addTermMeta(
        TermMeta.CATEGORY_PREMIUM_ID.toString(), "", newCategoryTaxonomy.getTerm());
    termsMetaDAO.addTermMeta(
        TermMeta.CATEGORY_RECOMMEND_ID.toString(), "", newCategoryTaxonomy.getTerm());

    return convertToClientTaxonomy(Optional.of(newCategoryTaxonomy));
  }

  /**
   * Adds a new term to the database and returns the client-side DTO {@code ClientTaxonomy}.
   *
   * @param providedTerm the client-side schema DTO term to add
   * @param cleanTerm whether to clean the term name and slug of special characters
   * @return the client-side DTO {@code ClientTaxonomy} if the term is added successfully, {@code
   *     Optional.empty()} otherwise
   */
  public Optional<ClientTaxonomy> addTerm(@NonNull ClientTerm providedTerm, boolean cleanTerm) {
    providedTerm.setName(
        cleanTerm && Objects.nonNull(providedTerm.getName()) && !providedTerm.getName().isEmpty()
            ? specialCharCleanJoin(providedTerm.getName(), " ")
            : providedTerm.getName());

    if (providedTerm.getName() == null || providedTerm.getName().isEmpty()) return Optional.empty();

    Optional<WPTerms> eitherTermNameOrSlugExists = eitherTermNameOrSlugExists(providedTerm, true);
    String idealSlug = specialCharCleanJoin(providedTerm.getName().toLowerCase(), "-");

    String taxonomyName = providedTerm.getTaxonomy().getTaxonomyName();
    String providedSlug = providedTerm.getSlug();

    if (eitherTermNameOrSlugExists.isEmpty()) {
      if (providedSlug == null || !providedSlug.equals(idealSlug)) providedSlug = idealSlug;
      return switch (enumFromValue(Taxonomy.class, taxonomyName, true).orElse(Taxonomy.OTHERS)) {
        case TAG -> addPostTag(providedTerm.getName(), providedSlug);
        case MODELS -> addModel(StringUtils.capitalize(providedTerm.getName()), providedSlug);
        case CATEGORY ->
            addCategory(
                providedTerm.getName(),
                providedSlug,
                Objects.requireNonNullElse(providedTerm.getTaxonomy().getDescription(), ""));
        default -> Optional.empty();
      };
    }
    return convertToClientTaxonomy(Optional.of(eitherTermNameOrSlugExists.get().getTaxonomy()));
  }

  /**
   * Adds a missing relationship between a post and a term. The Data Access Layer has its own checks
   * to prevent duplicate relationships or integrity exceptions.
   *
   * @param sessionPost the post to add the relationship to
   * @param wpTerm the term to add the relationship to
   * @return the client-side DTO {@code ClientTaxonomy} if the relationship is added successfully,
   *     {@code Optional.empty()} otherwise
   */
  @Transactional(TxType.REQUIRES_NEW)
  public Optional<ClientTaxonomy> addMissingRelationship(
      @NonNull WPost sessionPost, @NonNull WPTerms wpTerm) {
    WPTermRelationships presumablyMissingTermRel =
        termRelationshipsDAO.addTermRelationship(sessionPost, wpTerm.getTaxonomy(), 0);
    return convertToClientTaxonomy(Optional.of(presumablyMissingTermRel.getTermTaxonomy()));
  }

  /**
   * Identifies if a term or slug exists in the database and is not already associated with the
   * post. If so, creates the missing relationship between the post and the term, if any.
   *
   * @param providedTerm the client-side schema DTO term to identify existing relationships for
   * @param sessionPost the post to identify existing relationships for
   * @return the client-side schema DTO term taxonomy if the relationship is identified
   *     successfully, {@code Optional.empty()} otherwise
   */
  @Transactional(TxType.REQUIRES_NEW)
  public Optional<ClientTaxonomy> identifyExistingTermRelationships(
      @NonNull ClientTerm providedTerm, WPost sessionPost) {

    String providedTermName = Objects.requireNonNullElse(providedTerm.getName(), "");
    String providedTermSlug = Objects.requireNonNullElse(providedTerm.getSlug(), "");

    Optional<WPTerms> wpTermByName = termsDAO.termNameExists(providedTermName).findFirst();
    Optional<WPTerms> wpTermBySlug = termsDAO.termSlugExists(providedTermSlug).findFirst();
    Optional<WPTerms> wpTermByNameAndSlug =
        termsDAO.termNameAndSlugExists(providedTermName, providedTermSlug).findFirst();

    if (wpTermByName.isPresent()) {
      return addMissingRelationship(sessionPost, wpTermByName.get());
    } else if (wpTermBySlug.isPresent()) {
      return addMissingRelationship(sessionPost, wpTermBySlug.get());
    } else if (wpTermByNameAndSlug.isPresent()) {
      return addMissingRelationship(sessionPost, wpTermByNameAndSlug.get());
    }
    return Optional.empty();
  }

  /**
   * Adds a new term relationship between a post and a term taxonomy. This is a helper method for
   * the {@link TaxonomyService#createTermRelationship} strategy.
   *
   * @param newTermTaxonomy the client-side schema DTO term taxonomy to add the relationship for
   * @param sessionPost the post to add the relationship for
   * @return the client-side schema DTO term taxonomy if the relationship is added successfully,
   *     {@code Optional.empty()} otherwise
   */
  @Transactional(TxType.REQUIRES_NEW)
  private Optional<ClientTaxonomy> addNewTermRelationship(
      @NonNull Optional<ClientTaxonomy> newTermTaxonomy, @NonNull Optional<WPost> sessionPost) {
    if (newTermTaxonomy.isPresent()) {
      Optional<WPTermTaxonomy> entityTaxonomy =
          taxonomyDAO.termTaxonomyIdExists(newTermTaxonomy.get().getTermTaxonomyId()).findFirst();
      if (sessionPost.isPresent() && entityTaxonomy.isPresent()) {
        WPTermRelationships newRel =
            termRelationshipsDAO.addTermRelationship(sessionPost.get(), entityTaxonomy.get(), 0);
        return convertToClientTaxonomy(Optional.of(newRel.getTermTaxonomy()));
      }
    }
    return Optional.empty();
  }

  /**
   * Creates a term relationship between a post and a term after checking if the terms already
   * exists and if a new relationship can be created with the information provided.
   *
   * @param providedTerm the client-side schema DTO term to create a relationship for
   * @param postId the ID of the post to create a relationship for
   * @return the client-side schema DTO term taxonomy if the relationship is created successfully,
   *     {@code Optional.empty()} otherwise
   */
  @Transactional(TxType.REQUIRES_NEW)
  public Optional<ClientTaxonomy> createTermRelationship(
      @NonNull ClientTerm providedTerm, long postId) {
    Optional<WPost> sessionPost = postsDAO.getPostById(postId);
    if (sessionPost.isEmpty()) return Optional.empty();

    Optional<ClientTaxonomy> existingRelationship =
        identifyExistingTermRelationships(providedTerm, sessionPost.get());

    if (existingRelationship.isEmpty()) {
      Optional<ClientTaxonomy> newClientTaxonomy = addTerm(providedTerm, true);
      if (newClientTaxonomy.isPresent())
        return addNewTermRelationship(newClientTaxonomy, sessionPost);
    }
    return existingRelationship;
  }

  /**
   * Removes a term relationship between a post and a term taxonomy.
   *
   * @param postId the ID of the post to remove the relationship from
   * @param clientTerm the client-side schema DTO term to remove the relationship from
   * @return true if the term relationship was removed, false otherwise
   */
  @Transactional(TxType.REQUIRES_NEW)
  public boolean removeTermRelationship(long postId, @NonNull ClientTerm clientTerm) {
    Optional<WPost> post = postsDAO.getPostById(postId);
    if (post.isEmpty()) return false;

    String clientTermName = Objects.requireNonNullElse(clientTerm.getName(), "");
    String clientTermSlug = Objects.requireNonNullElse(clientTerm.getSlug(), "");

    Optional<WPTerms> finalTerm;

    Optional<WPTerms> existingTermNameAndSlug =
        termsDAO.termNameAndSlugExists(clientTermName, clientTermSlug).findFirst();

    if (existingTermNameAndSlug.isEmpty()) {
      finalTerm = eitherTermNameOrSlugExists(clientTerm, false);
    } else finalTerm = existingTermNameAndSlug;

    return finalTerm
        .filter(
            terms -> termRelationshipsDAO.deleteTermRelationship(post.get(), terms.getTaxonomy()))
        .isPresent();
  }

  /**
   * Removes a term taxonomy from the database.
   *
   * @param clientTerm the client-side schema DTO term to remove the taxonomy from
   * @return the client-side schema DTO term if the taxonomy was removed, {@code Optional.empty()}
   *     otherwise
   */
  @Transactional(TxType.REQUIRES_NEW)
  public Optional<ClientTerm> removeTermTaxonomy(ClientTerm clientTerm) {
    WPTerms targetTerm;

    Optional<WPTerms> termByNameAndSlug =
        termsDAO.termNameAndSlugExists(clientTerm.getName(), clientTerm.getSlug()).findFirst();
    if (termByNameAndSlug.isEmpty()) {
      Optional<WPTerms> termByNameOrSlug =
          termsDAO
              .termNameExists(clientTerm.getName())
              .findFirst()
              .or(() -> termsDAO.termSlugExists(clientTerm.getSlug()).findFirst());
      if (termByNameOrSlug.isEmpty()) return Optional.empty();
      targetTerm = termByNameOrSlug.get();
    } else targetTerm = termByNameAndSlug.get();

    Optional<ClientTerm> optionalClientTerm = convertToClientTerm(Optional.of(targetTerm));
    boolean areTermRelationshipsRemoved =
        termRelationshipsDAO.cleanTaxonomyRelationships(
            targetTerm.getTaxonomy().getTermTaxonomyId());

    if (areTermRelationshipsRemoved) {
      boolean taxonomyRemoved = taxonomyDAO.removeTermTaxonomy(targetTerm.getTaxonomy());
      if (!taxonomyRemoved) return Optional.empty();
    }

    return optionalClientTerm;
  }
}
