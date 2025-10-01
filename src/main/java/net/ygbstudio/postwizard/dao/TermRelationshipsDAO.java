package net.ygbstudio.postwizard.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
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
  public WPTermRelationships addTermRelationship(
      @NonNull WPost postItem, @NonNull WPTermTaxonomy taxonomy, int termOrder) {
    WPTermRelationships termRelationships = new WPTermRelationships();
    termRelationships.setTermTaxonomy(taxonomy);
    termRelationships.setPostObject(postItem);
    termRelationships.setTermOrder(termOrder);

    // Everytime a relationship is modified programmatically,
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

    Set<WPTermRelationships> termRelationshipsSet = postItem.getTermRelationships();
    termRelationshipsSet.stream()
        .filter(termRelationships -> termRelationships.getTermTaxonomy().equals(taxonomy))
        .findFirst()
        .ifPresentOrElse(
            termRelationships -> {
              if (termRelationshipsSet.remove(termRelationships)) {
                taxonomy.setCount(taxonomy.getCount() - 1);
                em.merge(taxonomy);
                WPTermRelationships termRelationship = em.merge(termRelationships);
                em.remove(termRelationship);
              }
            },
            () -> result.set(false));

    postItem.setTermRelationships(termRelationshipsSet);
    em.merge(postItem);

    return result.get();
  }
}
