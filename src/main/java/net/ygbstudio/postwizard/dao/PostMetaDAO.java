package net.ygbstudio.postwizard.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.ygbstudio.postwizard.entities.WPMeta;
import net.ygbstudio.postwizard.entities.WPost;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Data Access Object (DAO) implementation of interface {@link PostMetaManager} for reading
 * WordPress post metadata. This class implements methods to retrieve and handle post metadata in a
 * WordPress-like environment.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@ApplicationScoped
public class PostMetaDAO implements PostMetaManager {

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
        .setParameter("postId", id)
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
  public Stream<WPMeta> getEntriesByMetaKey(@NonNull String key) {
    return em.createNamedQuery("WPMeta.FindByMetaKey", WPMeta.class)
        .setParameter("metaKey", key)
        .getResultStream();
  }

  @Transactional(value = TxType.REQUIRED)
  public Optional<WPMeta> findMetaKeyByPostID(@NonNull String metaKey, long postId) {
    return em.createNamedQuery("WPMeta.FindKeyByPostID", WPMeta.class)
        .setParameter("metaKey", metaKey)
        .setParameter("postId", postId)
        .getResultStream()
        .findFirst();
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public List<WPMeta> getMetaValueMatches(@NonNull String pattern) {
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
  public Optional<WPMeta> updatePostMetaValue(
      long postId, @NonNull String metaKey, @NonNull String newValue) {
    return em.createNamedQuery("WPMeta.FindPostByID", WPMeta.class)
        .setParameter("postId", postId)
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
      @NonNull String metaKey, long limitBy, @NonNull Predicate<? super WPMeta> filterPredicate) {
    return em.createNamedQuery("WPMeta.RandomPostByMetaKey", WPMeta.class)
        .setParameter("metaKey", metaKey)
        .getResultStream()
        .filter(filterPredicate)
        .limit(limitBy)
        .collect(Collectors.toUnmodifiableSet());
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public void updatePostMetaAuto(
      long postId, @NonNull String metaKey, @Nullable String newValue, boolean autoCreate) {
    if (postId <= 0 || !postExists(postId)) {
      return;
    }

    findMetaKeyByPostID(metaKey, postId)
        .ifPresentOrElse(
            p -> {
              WPMeta existing = em.find(WPMeta.class, p.getMetaID());
              existing.setMetaFieldValue(Objects.requireNonNullElse(newValue, ""));
            },
            () -> {
              if (autoCreate) {
                WPost wpPost = em.find(WPost.class, postId);
                WPMeta newMetaPair = new WPMeta();
                newMetaPair.setPost(wpPost);
                newMetaPair.setMetaFieldKey(metaKey);
                newMetaPair.setMetaFieldValue(Objects.requireNonNullElse(newValue, ""));
                persistNewPostMeta(newMetaPair);
              }
            });
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public boolean postExists(long postId) {
    return em.createNamedQuery("WPMeta.FindPostByID", WPMeta.class)
        .setParameter("postId", postId)
        .getResultStream()
        .findFirst()
        .isPresent();
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public void persistNewPostMeta(@NonNull WPMeta metaPair) {
    if (metaPair.getPost() == null
        || metaPair.getMetaFieldKey() == null
        || metaPair.getMetaFieldValue() == null) {
      return;
    }
    em.persist(metaPair);
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public boolean metaKeyExists(long postId, @NonNull String metaKey) {
    return em.createNamedQuery("WPMeta.FindByMetaKey", WPMeta.class)
        .setParameter("metaKey", metaKey)
        .getResultStream()
        .findFirst()
        .isPresent();
  }
}
