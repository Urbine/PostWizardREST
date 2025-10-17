package net.ygbstudio.postwizard.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import java.util.Optional;
import java.util.stream.Stream;
import net.ygbstudio.postwizard.entities.taxonomies.WPTermTaxonomy;
import net.ygbstudio.postwizard.entities.taxonomies.WPTerms;
import org.jspecify.annotations.NonNull;

/**
 * Data Access Object (DAO) implementation of interface {@link TaxonomyManager} for reading
 * WordPress taxonomy data. This class implements methods to retrieve and handle taxonomies in a
 * WordPress-like environment.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@ApplicationScoped
public class TaxonomyDAO implements TaxonomyManager {

  @PersistenceContext(unitName = "wptermtaxonomy")
  private EntityManager em;

  @Transactional(value = TxType.REQUIRED)
  @Override
  public Stream<WPTermTaxonomy> termTaxonomyIdExists(long taxonomyID) {
    return em.createNamedQuery(TaxonomyManager.EXISTS_BY_ID, WPTermTaxonomy.class)
        .setParameter("termTaxonomyId", taxonomyID)
        .getResultStream();
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public Stream<WPTermTaxonomy> taxonomyTermExists(@NonNull WPTerms term) {
    return em.createNamedQuery(TaxonomyManager.EXISTS_BY_TERM, WPTermTaxonomy.class)
        .setParameter("term", term)
        .getResultStream();
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public Stream<WPTermTaxonomy> taxonomyExists(@NonNull String taxonomy) {
    return em.createNamedQuery(TaxonomyManager.EXISTS_BY_TAXONOMY, WPTermTaxonomy.class)
        .setParameter("taxonomy", taxonomy)
        .getResultStream();
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public WPTermTaxonomy addTermTaxonomy(
      @NonNull WPTerms term,
      @NonNull String taxonomy,
      @NonNull String description,
      int parent,
      long count) {
    Optional<WPTermTaxonomy> checkTermTaxonomy = taxonomyTermExists(term).findFirst();
    if (checkTermTaxonomy.isPresent()) return checkTermTaxonomy.get();

    WPTermTaxonomy newTaxonomy = new WPTermTaxonomy();
    newTaxonomy.setTaxonomy(taxonomy);
    newTaxonomy.setDescription(description);
    newTaxonomy.setParent(parent);
    newTaxonomy.setCount(count);
    newTaxonomy.setTerm(term);
    em.persist(newTaxonomy);
    em.flush();

    return newTaxonomy;
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public boolean removeTermTaxonomy(@NonNull WPTermTaxonomy termTaxonomy) {
    Optional<WPTermTaxonomy> checkTermTaxonomy =
        termTaxonomyIdExists(termTaxonomy.getTermTaxonomyId()).findFirst();
    if (checkTermTaxonomy.isPresent()) {
      em.remove(checkTermTaxonomy.get());
      return true;
    } else {
      return false;
    }
  }
}
