package net.ygbstudio.postwizard.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import net.ygbstudio.postwizard.entities.taxonomies.WPTermMeta;
import net.ygbstudio.postwizard.entities.taxonomies.WPTerms;
import org.jspecify.annotations.NonNull;

/**
 * Data Access Object (DAO) implementation of interface {@link TermsMetaManager} for reading
 * WordPress term metadata. This class implements methods to retrieve and handle term meta in a
 * WordPress-like environment.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@ApplicationScoped
public class TermsMetaDAO implements TermsMetaManager {

  @PersistenceContext(unitName = "wptermmeta")
  private EntityManager em;

  @Transactional(value = TxType.REQUIRED)
  @Override
  public WPTermMeta addTermMeta(
      @NonNull String metaKey, @NonNull String metaValue, @NonNull WPTerms termItem) {
    WPTermMeta newTermMeta = new WPTermMeta();
    newTermMeta.setMetaKey(metaKey);
    newTermMeta.setMetaValue(metaValue);
    newTermMeta.setTermItem(termItem);
    em.persist(newTermMeta);
    return newTermMeta;
  }
}
