package net.ygbstudio.postwizard.dao;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.ygbstudio.postwizard.entities.WPMeta;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Data Access Object (DAO) interface for reading WordPress post metadata. This interface defines
 * methods to retrieve and handle post metadata in a WordPress-like environment.
 *
 * @see PostMetaDAO
 * @see WPMeta
 * @author Yoham Gabriel @ YGB Studio
 */
public interface PostMetaManager {

  /**
   * Retrieves all post metadata entries.
   *
   * @return a List of all WPMeta entries
   */
  List<WPMeta> getAll();

  /**
   * Retrieves all post metadata entries associated with a specific post ID.
   *
   * @param id the ID of the post for which metadata is requested
   * @return a List of WPMeta entries associated with the specified post ID
   */
  List<WPMeta> getEntriesByPostID(long id);

  /**
   * Retrieves all unique post IDs that have associated metadata entries.
   *
   * @return a List of Long values representing unique post IDs
   */
  List<Long> getPostIDs();

  /**
   * Retrieves all post metadata entries that match a specific meta key.
   *
   * @param key the meta key to filter the metadata entries
   * @return a Stream of WPMeta entries that match the specified meta key
   */
  Stream<WPMeta> getEntriesByMetaKey(@NonNull String key);

  /**
   * Retrieves a WPMeta object with the specified metakey and postID in the database.
   *
   * @param metaKey a meta key in the database
   * @param postID a post ID in the database
   * @return an Optional containing the WPMeta entry if found, or empty if not found
   */
  Optional<WPMeta> findMetaKeyByPostID(@NonNull String metaKey, long postID);

  /**
   * Retrieves all post metadata entries that match a specific meta value.
   *
   * @param metaValuePattern the meta value to filter the metadata entries (e.g. "%.jpg%")
   * @param metaKey the meta key to filter the metadata entries
   * @param useNative whether to use a native query or not
   * @return the meta value that matches the specified meta value pattern
   */
  String findMetaValueLike(
      @NonNull String metaValuePattern, @Nullable String metaKey, boolean useNative);

  /**
   * Retrieves all post metadata entries that match a specific pattern in their value.
   *
   * @param pattern the regex String pattern to match against the metadata values
   * @return a List of WPMeta entries whose values match the specified pattern
   */
  List<WPMeta> getMetaValueMatches(@NonNull String pattern);

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
  Optional<WPMeta> updatePostMetaValue(long postID, @NonNull String metaKey, @NonNull String value);

  /**
   * Updates or creates a post metadata entry with the specified post ID, meta key, and value.
   *
   * @param postID the ID of the post
   * @param metaKey the meta key to update or create
   * @param value the new value for the metadata entry, if a {@code null} value is provided, the
   *     entry will be left empty in the database, not {@code null}
   */
  void updatePostMetaAuto(
      long postID, @NonNull String metaKey, @Nullable String value, boolean autoCreate);

  /**
   * Inserts a new post metadata entry into the database. This method is used to add new metadata
   * for a post that does not already exist.
   *
   * @param meta the WPMeta object containing the metadata to insert
   */
  void persistNewPostMeta(@NonNull WPMeta meta);

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
  boolean metaKeyExists(long postID, @NonNull String metaKey);

  /**
   * Get random post IDs from the database with a native query. This method is used to obtain a
   * random selection of post IDs that match a specific meta key and limit the number of results
   * based on the limitBy parameter. The filterPredicate parameter is used to filter any posts that
   * match the predicate and that is useful, for example, to exclude posts that are already
   * featured.
   *
   * @param metaKey the meta key to filter the metadata entries by
   * @param limitBy the limit of posts to retrieve
   * @param filterPredicate the predicate to apply to each WPMeta object
   * @return a Set of WPMeta objects that match the meta key and filter predicate
   */
  Set<WPMeta> getRandomPostsByMetaKey(
      @NonNull String metaKey, long limitBy, @NonNull Predicate<? super WPMeta> filterPredicate);
}
