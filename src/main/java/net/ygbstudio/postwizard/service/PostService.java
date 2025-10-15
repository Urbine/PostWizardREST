package net.ygbstudio.postwizard.service;

import static net.ygbstudio.postwizard.utils.Helpers.isInEnum;
import static net.ygbstudio.postwizard.utils.Logging.logStepIn;
import static net.ygbstudio.postwizard.utils.Logging.logStepOut;
import static net.ygbstudio.postwizard.utils.Logging.loggingInit;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.ygbstudio.postwizard.dao.PostManager;
import net.ygbstudio.postwizard.dto.ClientPost;
import net.ygbstudio.postwizard.entities.WPost;
import net.ygbstudio.postwizard.models.PostType;
import net.ygbstudio.postwizard.models.Taxonomy;
import net.ygbstudio.postwizard.rest.PostController;
import org.jspecify.annotations.Nullable;

/**
 * Service class for managing WordPress posts. This class provides methods to check post existence,
 * update post details, and retrieve post information in a structured format.
 *
 * <p>PostService also defines additional transactional boundaries for some methods to ensure data
 * consistency and integrity by isolating the transactional context of the database operations with
 * every method call.
 *
 * @see PostManager
 * @see PostController
 * @author Yoham Gabriel @ YGB Studio
 */
@ApplicationScoped
public class PostService {

  private static final Logger postServiceLog = Logger.getLogger(PostService.class.getName());

  @SuppressWarnings("unused")
  private static final FileHandler logFileHandler = loggingInit(postServiceLog, Level.ALL, true);

  /** Data Access Object (DAO) for reading and manipulating posts table. */
  @Inject private PostManager dbPostManager;

  /**
   * Checks whether a post exists in the server database.
   *
   * @param postID the ID of the post to check
   * @return true if the post exists, false otherwise
   */
  @Transactional(value = TxType.REQUIRES_NEW)
  public boolean postExists(long postID) {
    return dbPostManager.postExists(postID);
  }

  /**
   * Retrieves all posts from the database and converts them into ClientPost objects.
   *
   * @return a List of ClientPost objects representing all posts
   */
  @Transactional(value = TxType.REQUIRES_NEW)
  public List<ClientPost> getClientPostAll() {
    return dbPostManager.getAllPosts().stream()
        .filter(post -> Objects.nonNull(post.getId()))
        .map(post -> getClientPost(post.getId()))
        .toList();
  }

  @Transactional(value = TxType.REQUIRES_NEW)
  public List<ClientPost> getAllClientPostByType(PostType postType) {
    List<WPost> postByType =
        postType != PostType.ALL
            ? dbPostManager.getAllByType(postType.toString())
            : dbPostManager.getAllPosts();
    return postByType.stream()
        .filter(post -> Objects.nonNull(post.getId()))
        .map(post -> getClientPost(post.getId()))
        .toList();
  }

  /**
   * Retrieves the ID of a media post based on its slug (title).
   *
   * @param slug the slug (title) of the media post
   * @return the ID of the media post if found, otherwise returns 0
   */
  @Transactional(value = TxType.REQUIRES_NEW)
  public long getMediaPostIdBySlug(String slug) {
    return dbPostManager
        .getMediaByTitle(slug)
        .filter(wPost -> wPost.getId() != null)
        .map(WPost::getId)
        .orElse(0L);
  }

  /**
   * Validates if the provided post type is a valid PostType enumeration value.
   *
   * @param postType the post type to validate
   * @return true if the post type is valid, false otherwise
   */
  public boolean isValidPostType(@Nullable String postType) {
    return Objects.nonNull(postType)
        && isInEnum(PostType.class, String::valueOf, postType::equalsIgnoreCase);
  }

  /**
   * Validates if the provided taxonomy is a valid Taxonomy enumeration value.
   *
   * @param taxonomy the taxonomy to validate
   * @return true if the taxonomy is valid, false otherwise
   */
  public boolean isValidTaxonomy(@Nullable String taxonomy) {
    return Objects.nonNull(taxonomy)
        && isInEnum(Taxonomy.class, String::valueOf, taxonomy::equalsIgnoreCase);
  }

  /**
   * Updates the post details based on the provided ClientPost object. This method creates a new
   * WPost object, sets its properties from the ClientPost object, and updates the post entry in the
   * database.
   *
   * <p>Creation of new post entries in the database have been temporarily disabled to prevent
   * accidental data loss or malformed entries. The WordPress API should be used to create new
   * posts, so that relevant entries can be modified using this method.
   *
   * <p>This method runs within a new transaction context to ensure that the update operation is
   * isolated from other transactions.
   *
   * @param clientPost the ClientPost object containing post details to update
   */
  @Transactional(value = TxType.REQUIRES_NEW)
  public void clientPostUpdateStrategy(ClientPost clientPost) {
    logStepIn(postServiceLog, clientPost);

    WPost inMemoryPost = new WPost();

    inMemoryPost.setId(clientPost.getID());
    inMemoryPost.setPostAuthor(clientPost.getAuthor());
    inMemoryPost.setPostContent(clientPost.getContent());
    inMemoryPost.setPostTitle(clientPost.getTitle());
    inMemoryPost.setPostSlug(clientPost.getSlug());
    inMemoryPost.setPostStatus(clientPost.getStatus());
    inMemoryPost.setPostType(clientPost.getType());
    inMemoryPost.setGuid(clientPost.getGuid());
    inMemoryPost.setPostMimeType(clientPost.getMimeType());
    inMemoryPost.setPostParent(clientPost.getParent());
    inMemoryPost.setModifiedAtLocal(LocalDateTime.now());
    inMemoryPost.setModifiedAtGMT(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime());

    if (!isValidPostType(inMemoryPost.getPostType()))
      if (inMemoryPost.getId() != null && !dbPostManager.postExists(inMemoryPost.getId())) return;

      // postType is set to "null" in order that updatePostEntry() can use
      // the value of the existing post so as to not overwrite it with an invalid value.
      else inMemoryPost.setPostType(null);

    dbPostManager.updatePostEntry(inMemoryPost, false);
  }

  /**
   * Retrieves the post details for a given post ID and converts it into a ClientPost object. This
   * method fetches the post entries from the database and populates the ClientPost object with the
   * corresponding values based on the PostKeys enumeration.
   *
   * <p>This method is used to convert the raw post entries into a structured ClientPost object that
   * can be easily consumed by the client without exposing the underlying database structure. Also,
   * {@code getClientPost} runs within a new transaction context to ensure that the retrieval
   * operation is isolated from other transactions.
   *
   * @param postID the ID of the post for which details are requested
   * @return ClientPost object containing the post details
   */
  @Transactional(value = TxType.REQUIRES_NEW)
  public ClientPost getClientPost(long postID) {
    logStepIn(postServiceLog, postID);
    ClientPost convertedObj = new ClientPost();

    dbPostManager
        .getPostById(postID)
        .ifPresent(
            p -> {
              Long id = p.getId();
              if (id == null || id <= 0) return;
              convertedObj.setID(id);
              convertedObj.setAuthor(p.getPostAuthor());
              convertedObj.setContent(p.getPostContent());
              convertedObj.setTitle(p.getPostTitle());
              convertedObj.setSlug(p.getPostSlug());
              convertedObj.setStatus(p.getPostStatus());
              convertedObj.setType(p.getPostType());
              convertedObj.setParent(p.getPostParent());
              convertedObj.setGuid(p.getGuid());
              convertedObj.setMimeType(p.getPostMimeType());
              convertedObj.setCreateDate(p.getCreatedAtLocal());
              convertedObj.setCreateDateGMT(p.getCreatedAtGMT());
              convertedObj.setDateModified(p.getModifiedAtLocal());
              convertedObj.setDateModifiedGMT(p.getModifiedAtGMT());
            });

    logStepOut(postServiceLog, convertedObj);
    return convertedObj;
  }
}
