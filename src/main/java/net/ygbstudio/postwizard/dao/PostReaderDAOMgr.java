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
import java.util.stream.Stream;
import net.ygbstudio.postwizard.entities.WPost;

/**
 * Data Access Object (DAO) implementation for reading WordPress posts. This class provides methods
 * to retrieve and manipulate posts in a WordPress-like environment.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@ApplicationScoped
public class PostReaderDAOMgr implements PostReaderDAO {

  @PersistenceContext(unitName = "wpost")
  private EntityManager em;

  @Transactional(value = TxType.REQUIRED)
  @Override
  public List<WPost> getAllPosts() {
    return em.createNamedQuery("WPost.FindAll", WPost.class).getResultList();
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public List<WPost> getAllByType(String postType) {
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

  @Override
  public boolean isValidPost(WPost postItem) {
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
  public void updatePostEntry(WPost postItem, boolean autoCreate) {

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
}
