package net.ygbstudio.postwizard.dao;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import net.ygbstudio.postwizard.entities.WPMeta;
import net.ygbstudio.postwizard.entities.WPost;
import net.ygbstudio.postwizard.entities.taxonomies.WPTermRelationships;
import net.ygbstudio.postwizard.entities.taxonomies.WPTerms;
import org.jspecify.annotations.NonNull;

/**
 * Data Access Object (DAO) interface for reading WordPress posts. This interface defines methods to
 * retrieve and handle posts in a WordPress-like environment.
 *
 * @see PostDAO
 * @see WPost
 * @author Yoham Gabriel @ YGB Studio
 */
public interface PostManager {

  /**
   * NamedQuery for retrieving all posts.
   *
   * <p>{@code SELECT p FROM WPost p}
   *
   * @see WPost
   */
  String FIND_ALL = "WPost.FindAll";

  /**
   * NamedQuery for retrieving a post by its ID.
   *
   * <p>{@code SELECT p FROM WPost p WHERE p.id = :postId}
   *
   * @see WPost
   */
  String FIND_BY_ID = "WPost.FindById";

  /**
   * NamedQuery for retrieving all posts of a specific type.
   *
   * <p>{@code SELECT p FROM WPost p WHERE p.postType = :postType}
   *
   * @see WPost
   */
  String FIND_BY_TYPE = "WPost.FindByType";

  /**
   * NamedQuery for retrieving media posts by title.
   *
   * <p>{@code SELECT p FROM WPost p WHERE p.postType = 'attachment' AND p.postTitle = :title}
   *
   * @see WPost
   */
  String FIND_MEDIA_BY_TITLE = "WPost.FindMediaByTitle";

  /**
   * Retrieves all posts.
   *
   * @return a List of all WPost entries
   */
  List<WPost> getAllPosts();

  /**
   * Retrieves all posts of a specific type.
   *
   * @param postType the type of posts to retrieve
   * @return a List of WPost entries matching the specified type
   */
  List<WPost> getAllByType(@NonNull String postType);

  /**
   * Retrieves a specific post by its ID.
   *
   * @param postId the ID of the post to retrieve
   * @return an Optional containing the WPost entry if found, or empty if not found
   */
  Optional<WPost> getPostById(long postId);

  /**
   * Checks if a post exists by its ID.
   *
   * @param postId the ID of the post to check
   * @return true if the post exists, false otherwise
   */
  boolean postExists(long postId);

  /**
   * Validates a WPost item to ensure it has all the required fields.
   *
   * @param postItem the WPost item to validate
   * @return true if the post item is valid, false otherwise
   */
  boolean isValidPost(@NonNull WPost postItem);

  /**
   * Updates a post-entry with the provided WPost item. If the postID is less than or equal to 0, no
   * action is taken. If autoCreate is true and the post does not exist, it should create a new
   * post.
   *
   * @param postItem the WPost item to update
   */
  void updatePostEntry(@NonNull WPost postItem, boolean autoCreate);

  /**
   * Retrieves the term relationships for a specific post by its ID.
   *
   * @param postId the ID of the post to retrieve term relationships for
   * @return a Set of WPTermRelationships entries matching the specified post ID
   */
  Set<WPTermRelationships> getTermRelationshipsByPostID(@NonNull Long postId);

  /**
   * Retrieves the metadata for a specific post by its ID.
   *
   * @param postId the ID of the post to retrieve metadata for
   * @return a Set of WPMeta entries matching the specified post ID
   */
  Set<WPMeta> getPostMetaByPostID(@NonNull Long postId);

  /**
   * Retrieves the terms for a specific post by its ID.
   *
   * @param postId the ID of the post to retrieve terms for
   * @return a Set of WPTerms entries matching the specified post ID
   */
  Set<WPTerms> getPostTermsById(@NonNull Long postId);

  /**
   * Retrieves a media post by its slug.
   *
   * @param title the title of the media post to retrieve. Usually the slug of the post.
   * @return a Stream of WPost entries matching the specified slug
   */
  Stream<WPost> getMediaByTitle(String title);

  /**
   * Deletes a post-entry with the specified ID.
   *
   * @param postId the ID of the post to delete
   * @return true if the post was deleted, false otherwise
   */
  boolean deletePost(long postId);
}
