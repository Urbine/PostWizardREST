package net.ygbstudio.postwizard.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import net.ygbstudio.postwizard.entities.WPost;
import net.ygbstudio.postwizard.entities.taxonomies.WPTermRelationships;
import net.ygbstudio.postwizard.entities.taxonomies.WPTermTaxonomy;
import org.jspecify.annotations.NonNull;

/**
 * Data Access Object (DAO) implementation for reading WordPress term relationships. This class
 * provides methods to retrieve and manipulate term relationships in a WordPress-like environment.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@ApplicationScoped
public class TermRelationshipsDAO implements TermRelationshipsManager {

  @PersistenceContext(unitName = "wptermrelationships")
  private EntityManager em;

  @Transactional(value = TxType.REQUIRED)
  @Override
  public Stream<WPTermRelationships> findExistingRelationship(
      @NonNull WPost postItem, @NonNull WPTermTaxonomy taxonomy) {
    return em.createNamedQuery("WPTermRelationships.FindExisting", WPTermRelationships.class)
        .setParameter("objectID", postItem.getId())
        .setParameter("termTaxonomyID", taxonomy.getTermTaxonomyId())
        .getResultStream();
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public WPTermRelationships addTermRelationship(
      @NonNull WPost postItem, @NonNull WPTermTaxonomy taxonomy, int termOrder) {
    Optional<WPTermRelationships> existingRelationship =
        findExistingRelationship(postItem, taxonomy).findFirst();

    if (existingRelationship.isPresent()) return existingRelationship.get();

    WPTermRelationships termRelationships = new WPTermRelationships();
    termRelationships.setTermTaxonomy(taxonomy);
    termRelationships.setPostObject(postItem);
    termRelationships.setTermOrder(termOrder);

    // Everytime a relationship is created,
    // the database count of the taxonomy should be updated.
    taxonomy.setCount(taxonomy.getCount() + 1);

    em.merge(taxonomy);
    em.persist(termRelationships);

    return termRelationships;
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public boolean deleteTermRelationship(@NonNull WPost postItem, @NonNull WPTermTaxonomy taxonomy) {
    AtomicBoolean result = new AtomicBoolean(true);

    findExistingRelationship(postItem, taxonomy)
        .findFirst()
        .ifPresentOrElse(
            termRelationships -> {
              WPTermRelationships termRelationship = em.merge(termRelationships);
              termRelationship
                  .getTermTaxonomy()
                  .setCount(termRelationship.getTermTaxonomy().getCount() - 1);
              em.remove(termRelationship);
            },
            () -> result.set(false));

    return result.get();
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public boolean cleanTaxonomyRelationships(long termTaxonomyId) {
    return em.createNamedQuery(
                "WPTermRelationships.DeleteByTermTaxonomyID", WPTermRelationships.class)
            .setParameter("termTaxonomyID", termTaxonomyId)
            .executeUpdate()
        > 0;
  }
}
