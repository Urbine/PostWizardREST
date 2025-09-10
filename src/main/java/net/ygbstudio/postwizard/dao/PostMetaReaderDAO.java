package net.ygbstudio.postwizard.dao;

import java.util.Collection;
import java.util.Optional;
import net.ygbstudio.postwizard.entities.WPMeta;

/**
 * Data Access Object (DAO) interface for reading WordPress post metadata. This interface defines
 * methods to retrieve and manipulate post metadata in a WordPress-like environment.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
public interface PostMetaReaderDAO {

  /**
   * Retrieves all post metadata entries.
   *
   * @return a collection of all WPMeta entries
   */
  Collection<WPMeta> getAll();

  /**
   * Retrieves all post metadata entries associated with a specific post ID.
   *
   * @param id the ID of the post for which metadata is requested
   * @return a collection of WPMeta entries associated with the specified post ID
   */
  Collection<WPMeta> getEntriesByPostID(long id);

  /**
   * Retrieves all unique post IDs that have associated metadata entries.
   *
   * @return a collection of Long values representing unique post IDs
   */
  Collection<Long> getPostIDs();

  /**
   * Retrieves all post metadata entries that match a specific meta key.
   *
   * @param key the meta key to filter the metadata entries
   * @return a collection of WPMeta entries that match the specified meta key
   */
  Collection<WPMeta> getEntriesByMetaKey(String key);

  /**
   * Retrieves all post metadata entries that match a specific pattern in their value.
   *
   * @param pattern the regex String pattern to match against the metadata values
   * @return a collection of WPMeta entries whose values match the specified pattern
   */
  Collection<WPMeta> getMetaValueMatches(String pattern);

  /**
   * Retrieves a specific post metadata entry by its ID.
   *
   * @param id the ID of the metadata entry to retrieve
   * @return an Optional containing the WPMeta entry if found, or empty if not found
   */
  WPMeta getElemByMetaId(long id);

  /**
   * Retrieves a specific post metadata entry by its post ID and meta key.
   *
   * @param postID the ID of the post
   * @param metaKey the meta key to filter the metadata entry
   * @return an Optional containing the WPMeta entry if found, or empty if not found
   */
  Optional<WPMeta> updatePostMetaValue(long postID, String metaKey, String value);

  /**
   * Updates or creates a post metadata entry with the specified post ID, meta key, and value.
   *
   * @param postID the ID of the post
   * @param metaKey the meta key to update or create
   * @param value the new value for the metadata entry
   */
  void updatePostMetaAuto(long postID, String metaKey, String value, boolean autoCreate);

  /**
   * Inserts a new post metadata entry into the database. This method is used to add new metadata
   * for a post that does not already exist.
   *
   * @param meta the WPMeta object containing the metadata to insert
   */
  void persistNewPostMeta(WPMeta meta);

  /**
   * Checks if a post with the specified ID exists in the database.
   *
   * @param postID the ID of the post to check
   * @return true if the post exists, false otherwise
   */
  boolean postExists(long postID);

  /**
   * Checks if a specific meta key exists for a given post ID.
   *
   * @param postID the ID of the post to check
   * @param metaKey the meta key to check for existence
   * @return true if the meta key exists for the specified post ID, false otherwise
   */
  boolean metaKeyExists(long postID, String metaKey);
}
