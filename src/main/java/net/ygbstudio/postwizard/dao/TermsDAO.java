package net.ygbstudio.postwizard.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import net.ygbstudio.postwizard.entities.taxonomies.WPTermMeta;
import net.ygbstudio.postwizard.entities.taxonomies.WPTerms;
import org.jspecify.annotations.NonNull;

/**
 * Data Access Object (DAO) implementation for reading WordPress term data. This class provides
 * methods to retrieve and manipulate terms in a WordPress-like environment.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@ApplicationScoped
public class TermsDAO implements TermsManager {

  @PersistenceContext(unitName = "wpterms")
  private EntityManager em;

  @Transactional(value = TxType.REQUIRED)
  @Override
  public Stream<WPTerms> termIdExists(long termID) {
    return em.createNamedQuery("WPTerms.ExistsByID", WPTerms.class)
        .setParameter("termId", termID)
        .getResultStream();
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public Stream<WPTerms> termSlugExists(@NonNull String termSlug) {
    return em.createNamedQuery("WPTerms.ExistsBySlug", WPTerms.class)
        .setParameter("termSlug", termSlug)
        .getResultStream();
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public Stream<WPTerms> termNameExists(@NonNull String termName) {
    return em.createNamedQuery("WPTerms.ExistsByName", WPTerms.class)
        .setParameter("termName", termName)
        .getResultStream();
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public WPTerms addTerm(
      @NonNull String name,
      @NonNull String slug,
      @NonNull Set<WPTermMeta> termMetaSet,
      long termGroup) {
    if (name.isBlank() || slug.isBlank() || termGroup < 0) {
      throw new IllegalArgumentException("Invalid term data. Unable to add term.");
    }

    Optional<WPTerms> checkName = termNameExists(name).findFirst();
    Optional<WPTerms> checkSlug = termSlugExists(slug).findFirst();
    if (checkName.isPresent()) return checkName.get();
    if (checkSlug.isPresent()) return checkSlug.get();

    WPTerms newTerm = new WPTerms();
    newTerm.setName(name);
    newTerm.setSlug(slug);
    newTerm.setTermGroup(termGroup);

    termMetaSet.forEach(termMeta -> termMeta.setTermItem(newTerm));
    newTerm.setTermMeta(termMetaSet);
    em.persist(newTerm);

    return newTerm;
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public boolean deleteTerm(@NonNull WPTerms term) {
    Optional<WPTerms> checkTerm = termIdExists(term.getId()).findFirst();
    if (checkTerm.isPresent()) {
      em.remove(checkTerm.get());
      return true;
    } else {
      return false;
    }
  }
}
