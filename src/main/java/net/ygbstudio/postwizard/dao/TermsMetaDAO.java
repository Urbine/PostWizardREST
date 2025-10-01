package net.ygbstudio.postwizard.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import net.ygbstudio.postwizard.entities.taxonomies.WPTermMeta;
import org.jspecify.annotations.NonNull;

/**
 * Data Access Object (DAO) implementation for reading WordPress term metadata. This class provides
 * methods to retrieve and manipulate term meta in a WordPress-like environment.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@ApplicationScoped
public class TermsMetaDAO implements TermsMetaManager {

  @PersistenceContext(unitName = "wptermmeta")
  private EntityManager em;

  @Transactional(value = TxType.REQUIRED)
  @Override
  public WPTermMeta addTermMeta(@NonNull String metaKey, @NonNull String metaValue) {
    WPTermMeta newTermMeta = new WPTermMeta();
    newTermMeta.setMetaKey(metaKey);
    newTermMeta.setMetaValue(metaValue);
    em.persist(newTermMeta);
    return newTermMeta;
  }
}
