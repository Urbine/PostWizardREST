package net.ygbstudio.postwizard.dao;

import java.util.Collection;
import java.util.Optional;
import net.ygbstudio.postwizard.entities.WPost;

/**
 * Data Access Object (DAO) interface for reading WordPress posts. This interface defines methods to
 * retrieve and manipulate posts in a WordPress-like environment.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
public interface PostReaderDAO {

  /**
   * Retrieves all posts.
   *
   * @return a collection of all WPost entries
   */
  Collection<WPost> getAllPosts();

  /**
   * Retrieves all posts of a specific type.
   *
   * @param postType the type of posts to retrieve
   * @return a collection of WPost entries matching the specified type
   */
  Collection<WPost> getAllByType(String postType);

  /**
   * Retrieves a specific post by its ID.
   *
   * @param postID the ID of the post to retrieve
   * @return an Optional containing the WPost entry if found, or empty if not found
   */
  Optional<WPost> getPostById(long postID);

  /**
   * Checks if a post exists by its ID.
   *
   * @param postID the ID of the post to check
   * @return true if the post exists, false otherwise
   */
  boolean postExists(long postID);

  /**
   * Validates a WPost item to ensure it has all the required fields.
   *
   * @param postItem the WPost item to validate
   * @return true if the post item is valid, false otherwise
   */
  boolean isValidPost(WPost postItem);

  /**
   * Updates a post entry with the provided WPost item. If the postID is less than or equal to 0, no
   * action is taken. If autoCreate is true and the post does not exist, it should create a new
   * post.
   *
   * @param postItem the WPost item to update
   */
  void updatePostEntry(WPost postItem, boolean autoCreate);
}
