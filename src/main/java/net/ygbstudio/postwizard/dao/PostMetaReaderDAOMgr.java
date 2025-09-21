package net.ygbstudio.postwizard.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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

  @Transactional(value = TxType.REQUIRED)
  @Override
  public List<WPMeta> getAll() {
    return em.createNamedQuery("WPMeta.FindAll", WPMeta.class).getResultList();
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public List<WPMeta> getEntriesByPostID(long id) {
    return em.createNamedQuery("WPMeta.FindPostByID", WPMeta.class)
        .setParameter("postID", id)
        .getResultList();
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public List<Long> getPostIDs() {
    return em.createNamedQuery("WPMeta.FindAllPostIDs", Long.class)
        .getResultStream()
        .distinct()
        .toList();
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public Stream<WPMeta> getEntriesByMetaKey(String key) {
    return em.createNamedQuery("WPMeta.FindByMetaKey", WPMeta.class)
        .setParameter("metaKey", key)
        .getResultStream();
  }

  @Transactional(value = TxType.REQUIRED)
  public Optional<WPMeta> findMetaKeyByPostID(String metaKey, long postID) {
    return em.createNamedQuery("WPMeta.FindKeyByPostID", WPMeta.class)
        .setParameter("metaKey", metaKey)
        .setParameter("postID", postID)
        .getResultStream()
        .findFirst();
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public List<WPMeta> getMetaValueMatches(String pattern) {
    Predicate<String> metaValFind = Pattern.compile(pattern).asPredicate();

    return em.createNamedQuery("WPMeta.FindAll", WPMeta.class)
        .getResultStream()
        .filter(elem -> metaValFind.test(elem.getMetaFieldValue()))
        .toList();
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public WPMeta getElemByMetaId(long id) {
    return em.find(WPMeta.class, id);
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public Optional<WPMeta> updatePostMetaValue(long postID, String metaKey, String newValue) {
    return em.createNamedQuery("WPMeta.FindPostByID", WPMeta.class)
        .setParameter("postID", postID)
        .getResultStream()
        .filter(p -> p.getMetaFieldKey().equals(metaKey))
        .findFirst()
        .map(
            existing -> {
              existing.setMetaFieldValue(newValue);
              return existing;
            });
  }

  @Transactional(value = TxType.REQUIRED)
  public Set<WPMeta> getRandomPostsByMetaKey(
      String metaKey, long limitBy, Predicate<? super WPMeta> filterPredicate) {
    return em.createNamedQuery("WPMeta.RandomPostByMetaKey", WPMeta.class)
        .setParameter("metaKey", metaKey)
        .getResultStream()
        .filter(filterPredicate)
        .limit(limitBy)
        .collect(Collectors.toUnmodifiableSet());
  }

  @Transactional(value = TxType.REQUIRED)
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
      getEntriesByPostID(postID).stream()
          .filter(p -> p.getMetaFieldKey().equals(metaKey))
          .findFirst()
          .ifPresent(
              p -> {
                WPMeta existing = em.find(WPMeta.class, p.getMetaID());
                existing.setMetaFieldValue(newValue);
              });
    }
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public boolean postExists(long postID) {
    return em.createNamedQuery("WPMeta.FindPostByID", WPMeta.class)
        .setParameter("postID", postID)
        .getResultStream()
        .findFirst()
        .isPresent();
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public void persistNewPostMeta(WPMeta metaPair) {
    if (metaPair.getPostID() == null
        || metaPair.getMetaFieldKey() == null
        || metaPair.getMetaFieldValue() == null) {
      return;
    }
    em.persist(metaPair);
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public boolean metaKeyExists(long postID, String metaKey) {
    return em.createNamedQuery("WPMeta.FindByMetaKey", WPMeta.class)
        .setParameter("metaKey", metaKey)
        .getResultStream()
        .findFirst()
        .isPresent();
  }
}
