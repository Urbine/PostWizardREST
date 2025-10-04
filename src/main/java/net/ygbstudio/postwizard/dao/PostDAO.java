package net.ygbstudio.postwizard.dao;

import static net.ygbstudio.postwizard.utils.Reflection.getTransformClassFields;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.ygbstudio.postwizard.entities.WPMeta;
import net.ygbstudio.postwizard.entities.WPost;
import net.ygbstudio.postwizard.entities.taxonomies.WPTermRelationships;
import net.ygbstudio.postwizard.entities.taxonomies.WPTerms;
import org.jspecify.annotations.NonNull;

/**
 * Data Access Object (DAO) implementation for reading WordPress posts. This class provides methods
 * to retrieve and manipulate posts in a WordPress-like environment.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@ApplicationScoped
public class PostDAO implements PostManager {

  @PersistenceContext(unitName = "wpost")
  private EntityManager em;

  @Transactional(value = TxType.REQUIRED)
  @Override
  public List<WPost> getAllPosts() {
    return em.createNamedQuery("WPost.FindAll", WPost.class).getResultList();
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public List<WPost> getAllByType(@NonNull String postType) {
    return em.createNamedQuery("WPost.FindByType", WPost.class)
        .setParameter("postType", postType)
        .getResultList();
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public Optional<WPost> getPostById(long postID) {
    return em.createNamedQuery("WPost.FindByID", WPost.class)
        .setParameter("postID", postID)
        .getResultStream()
        .findFirst();
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public Set<WPTermRelationships> getTermRelationshipsByPostID(@NonNull Long postId) {
    return em.find(WPost.class, postId).getTermRelationships();
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public Set<WPTerms> getPostTermsById(@NonNull Long postId) {
    return em.find(WPost.class, postId).getTermRelationships().stream()
        .map(rel -> rel.getTermTaxonomy().getTerm())
        .collect(Collectors.toSet());
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public Set<WPMeta> getPostMetaByPostID(@NonNull Long postId) {
    return em.find(WPost.class, postId).getPostMetadataSet();
  }

  @Override
  public boolean isValidPost(@NonNull WPost postItem) {
    long elemCount = Stream.of(postItem).filter(Objects::nonNull).count();
    return elemCount == getTransformClassFields(WPost.class, Field::getName).count();
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public boolean postExists(long postID) {
    return getPostById(postID).isPresent();
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public void updatePostEntry(@NonNull WPost postItem, boolean autoCreate) {

    if (postItem.getId() == null) return;

    boolean postExists = postExists(postItem.getId());
    if (autoCreate && !postExists) {
      if (isValidPost(postItem)) {
        postItem.setCreatedAtLocal(LocalDateTime.now());
        postItem.setCreatedAtGMT(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime());
        em.persist(postItem);
      }
    } else if (postExists) {
      WPost targetPost = getPostById(postItem.getId()).orElseThrow();
      targetPost.setPostAuthor(
          Objects.requireNonNullElse(postItem.getPostAuthor(), targetPost.getPostAuthor()));
      targetPost.setPostContent(
          Objects.requireNonNullElse(postItem.getPostContent(), targetPost.getPostContent()));
      targetPost.setPostTitle(
          Objects.requireNonNullElse(postItem.getPostTitle(), targetPost.getPostTitle()));
      targetPost.setPostSlug(
          Objects.requireNonNullElse(postItem.getPostSlug(), targetPost.getPostSlug()));
      targetPost.setPostStatus(
          Objects.requireNonNullElse(postItem.getPostStatus(), targetPost.getPostStatus()));
      targetPost.setPostType(
          Objects.requireNonNullElse(postItem.getPostType(), targetPost.getPostType()));
      targetPost.setPostParent(
          Objects.requireNonNullElse(postItem.getPostParent(), targetPost.getPostParent()));
      targetPost.setGuid(Objects.requireNonNullElse(postItem.getGuid(), targetPost.getGuid()));
      targetPost.setPostMimeType(
          Objects.requireNonNullElse(postItem.getPostMimeType(), targetPost.getPostMimeType()));
      targetPost.setModifiedAtLocal(
          Objects.requireNonNullElse(
              postItem.getModifiedAtLocal(), targetPost.getModifiedAtLocal()));
      targetPost.setModifiedAtGMT(
          Objects.requireNonNullElse(postItem.getModifiedAtGMT(), targetPost.getModifiedAtGMT()));

      if (!em.contains(targetPost)) em.merge(targetPost);
    }
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public boolean deletePost(long postID) {
    boolean deleted = false;
    Optional<WPost> post = getPostById(postID);
    if (post.isPresent()) {
      em.remove(post.get());
      deleted = true;
    }
    return deleted;
  }
}
