package net.ygbstudio.postwizard.service;

import static net.ygbstudio.postwizard.utils.Logging.logStepIn;
import static net.ygbstudio.postwizard.utils.Logging.logStepOut;
import static net.ygbstudio.postwizard.utils.Logging.loggingInit;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.ygbstudio.postwizard.dao.PostReaderDAO;
import net.ygbstudio.postwizard.dto.ClientPost;
import net.ygbstudio.postwizard.entities.WPost;

@ApplicationScoped
public class PostService {

  private static final Logger postServiceLog = Logger.getLogger(PostService.class.getName());

  @SuppressWarnings("unused")
  private static final FileHandler logFileHandler = loggingInit(postServiceLog, Level.ALL, true);

  /** Data Access Object (DAO) for reading and manipulating posts table. */
  @Inject private PostReaderDAO dbPostDao;

  /**
   * Checks whether a post exists in the server database.
   *
   * @param postID
   * @return
   */
  public boolean postExists(long postID) {
    return dbPostDao.postExists(postID);
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
   * @param clientPost | the ClientPost object containing post details to update
   */
  public void clientPostUpdateStrategy(ClientPost clientPost) {
    logStepIn(postServiceLog, clientPost);

    WPost inMemoryPost = new WPost();

    inMemoryPost.setID(clientPost.getPostID());
    inMemoryPost.setPostAuthor(clientPost.getPostAuthor());
    inMemoryPost.setPostContent(clientPost.getPostContent());
    inMemoryPost.setPostTitle(clientPost.getPostTitle());
    inMemoryPost.setPostSlug(clientPost.getPostSlug());
    inMemoryPost.setPostStatus(clientPost.getPostStatus());
    inMemoryPost.setPostType(clientPost.getPostType());

    dbPostDao.updatePostEntry(inMemoryPost, false);
  }

  /**
   * Retrieves the post details for a given post ID and converts it into a ClientPost object. This
   * method fetches the post entries from the database and populates the ClientPost object with the
   * corresponding values based on the PostKeys enumeration.
   *
   * <p>This method is used to convert the raw post entries into a structured ClientPost object that
   * can be easily consumed by the client without exposing the underlying database structure.
   *
   * @param postID | the ID of the post for which details are requested
   * @return ClientPost object containing the post details
   */
  public ClientPost getClientPost(long postID) {
    logStepIn(postServiceLog, postID);
    ClientPost convertedObj = new ClientPost();

    dbPostDao
        .getPostById(postID)
        .ifPresent(
            p -> {
              convertedObj.setPostID(p.getID());
              convertedObj.setPostAuthor(p.getPostAuthor());
              convertedObj.setPostContent(p.getPostContent());
              convertedObj.setPostTitle(p.getPostTitle());
              convertedObj.setPostSlug(p.getPostSlug());
              convertedObj.setPostStatus(p.getPostStatus());
              convertedObj.setPostType(p.getPostType());
            });

    logStepOut(postServiceLog, convertedObj);
    return convertedObj;
  }
}
