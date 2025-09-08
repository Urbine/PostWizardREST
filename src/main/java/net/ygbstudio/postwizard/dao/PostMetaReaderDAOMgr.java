package net.ygbstudio.postwizard.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import net.ygbstudio.postwizard.entities.WPMeta;

/**
 * Data Access Object (DAO) implementation for reading WordPress post metadata. This class provides
 * methods to retrieve and manipulate post metadata in a WordPress-like environment.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@ApplicationScoped
public class PostMetaReaderDAOMgr implements PostMetaReaderDAO {

  @PersistenceContext(unitName = "wpmeta")
  private EntityManager em;

  @Transactional
  @Override
  public Collection<WPMeta> getAll() {
    return em.createNamedQuery("WPMeta.FindAll", WPMeta.class).getResultList();
  }

  @Override
  public Collection<WPMeta> getEntriesByPostID(long id) {
    return getAll().parallelStream().filter(elem -> elem.getPostID() == id).toList();
  }

  @Override
  public Collection<WPMeta> getEntriesByMetaKey(String key) {
    return getAll().parallelStream().filter(elem -> elem.getMetaFieldKey().equals(key)).toList();
  }

  @Override
  public Collection<WPMeta> getMetaValueMatches(String pattern) {
    Predicate<String> metaValFind = Pattern.compile(pattern).asPredicate();

    return getAll().parallelStream()
        .filter(elem -> metaValFind.test(elem.getMetaFieldValue()))
        .toList();
  }

  @Transactional
  @Override
  public WPMeta getElemByMetaId(long id) {
    return em.find(WPMeta.class, id);
  }

  @Transactional
  @Override
  public Optional<WPMeta> updatePostMetaValue(long postID, String metaKey, String newValue) {
    return getEntriesByPostID(postID).parallelStream()
        .filter(p -> p.getMetaFieldKey().equals(metaKey))
        .findFirst()
        .map(
            existing -> {
              existing.setMetaFieldValue(newValue);
              return existing;
            });
  }

  @Transactional
  @Override
  public void updatePostMetaAuto(long postID, String metaKey, String newValue, boolean autoCreate) {
    if (postID <= 0 || (metaKey == null && newValue == null)) {
      return;
    }

    if (autoCreate && (!postExists(postID) || !metaKeyExists(postID, metaKey))) {
      WPMeta newMetaPair = new WPMeta();
      newMetaPair.setPostID(postID);
      newMetaPair.setMetaFieldKey(metaKey);
      newMetaPair.setMetaFieldValue(newValue);
      persistNewPostMeta(newMetaPair);
    } else {
      getEntriesByPostID(postID).parallelStream()
          .filter(p -> p.getMetaFieldKey().equals(metaKey))
          .findFirst()
          .ifPresent(
              p -> {
                WPMeta existing = em.find(WPMeta.class, p.getMetaID());
                existing.setMetaFieldValue(newValue);
              });
    }
  }

  @Override
  public boolean postExists(long postID) {
    return getAll().parallelStream().anyMatch(elem -> elem.getPostID() == postID);
  }

  @Transactional
  @Override
  public void persistNewPostMeta(WPMeta metaPair) {
    if (metaPair.getPostID() == null
        || metaPair.getMetaFieldKey() == null
        || metaPair.getMetaFieldValue() == null) {
      return;
    }
    em.persist(metaPair);
  }

  @Override
  public boolean metaKeyExists(long postID, String metaKey) {
    return getEntriesByMetaKey(metaKey).stream()
        .filter(elem -> elem.getPostID() == postID)
        .anyMatch(elem -> elem.getMetaFieldKey().equals(metaKey));
  }
}
