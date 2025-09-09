package net.ygbstudio.postwizard.dao;

import static net.ygbstudio.postwizard.utils.Reflection.getTransformClassFields;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.lang.reflect.Field;
import java.util.Collection;
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

  @Transactional
  @Override
  public Collection<WPost> getAllPosts() {
    return em.createNamedQuery("WPosts.FindAll", WPost.class).getResultList();
  }

  @Override
  public Optional<WPost> getPostById(long postID) {
    return getAllPosts().stream().filter(p -> p.getID() == postID).findFirst();
  }

  @Override
  public boolean isValidPost(WPost postItem) {
    long elemCount = Stream.of(postItem).filter(Objects::nonNull).count();
    return elemCount == getTransformClassFields(WPost.class, Field::getName).count();
  }

  @Override
  public boolean postExists(long postID) {
    return getPostById(postID).isPresent();
  }

  @Transactional
  @Override
  public void updatePostEntry(WPost postItem, boolean autoCreate) {

    boolean postExists = postExists(postItem.getID());
    if (autoCreate && !postExists) {
      if (isValidPost(postItem)) {
        em.persist(postItem);
        return;
      }
    } else if (postExists) {
      WPost targetPost = getPostById(postItem.getID()).get();
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

      if (!em.contains(targetPost)) em.merge(targetPost);
    }
  }
}
