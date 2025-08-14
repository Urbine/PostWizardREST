package net.ygbstudio.postdirector.rest;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.lang.reflect.Field;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.ygbstudio.postdirector.dao.PostMetaReaderDAO;
import net.ygbstudio.postdirector.dao.PostReaderDAO;
import net.ygbstudio.postdirector.dto.ClientPost;
import net.ygbstudio.postdirector.dto.ClientPostMeta;
import net.ygbstudio.postdirector.dto.ErrorResponse;
import net.ygbstudio.postdirector.dto.ServerResponse;
import net.ygbstudio.postdirector.entities.WPost;
import net.ygbstudio.postdirector.enums.PostMetaKeys;
import net.ygbstudio.postdirector.exceptions.InvalidIdentifier;
import net.ygbstudio.postdirector.utils.Logging;
import net.ygbstudio.postdirector.utils.Reflection;

/**
 * RESTful web service for managing post metadata in the PostDirector application. This class
 * provides endpoints to retrieve and update post metadata.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@RequestScoped
@Path("posts")
public class PostDirectorDataSourceRest {

  private static final Logger dataSourceRestLog =
      Logger.getLogger(PostDirectorDataSourceRest.class.getName());

  @SuppressWarnings("unused")
  private static final FileHandler logFileHandler =
      Logging.LoggingInit(dataSourceRestLog, Level.ALL, true);

  @Context private UriInfo context;

  /** Data Access Object (DAO) for reading and manipulating the post metadata table. */
  @Inject private PostMetaReaderDAO dbPostMetaDao;

  /** Data Access Object (DAO) for reading and manipulating posts table. */
  @Inject private PostReaderDAO dbPostDao;

  /**
   * Endpoint to retrieve post metadata by post ID. This method returns a JSON representation of the
   * post metadata associated with the specified post ID.
   *
   * @param postId | the ID of the post for which metadata is requested
   * @return JSON representation of the post metadata
   */
  @GET
  @Path("meta/{postID: [0-9]+}")
  @RolesAllowed(value = {"user"})
  @Produces(MediaType.APPLICATION_JSON)
  public Response getPostMeta(@PathParam("postID") long postId) {

    dataSourceRestLog.entering(
        "Called getPostMeta at " + context.getPath(), "getPostMeta", new Object[] {postId});

    if (postId <= 0) {
      dataSourceRestLog.exiting("Post ID - Bad Request", "getPostMeta", new Object[] {postId});
      return Response.status(Response.Status.BAD_REQUEST)
          .entity(
              new ErrorResponse(
                  "Invalid Post ID",
                  "Please provide a valid post ID greater than 0",
                  Response.Status.BAD_REQUEST.getStatusCode()))
          .build();

    } else if (!dbPostMetaDao.postExists(postId)) {
      dataSourceRestLog.exiting("Post ID not found", "getPostMeta", new Object[] {postId});
      return Response.status(Response.Status.NOT_FOUND)
          .entity(
              new ErrorResponse(
                  "Post ID not found",
                  "Please provide a valid post ID",
                  Response.Status.NOT_FOUND.getStatusCode()))
          .build();
    }

    dataSourceRestLog.exiting("Post ID found", "getPostMeta", new Object[] {postId});
    return Response.ok(getClientPostMeta(postId), MediaType.APPLICATION_JSON).build();
  }

  /**
   * Endpoint to retrieve a post by its ID. This method returns a JSON representation of the post
   * associated with the specified post ID.
   *
   * @param postID | the ID of the post to retrieve
   * @return JSON representation of the post
   */
  @GET
  @Path("{postID: [0-9]+}")
  @RolesAllowed(value = {"user"})
  @Produces(MediaType.APPLICATION_JSON)
  public Response getPostById(@PathParam("postID") long postID) {
    dataSourceRestLog.entering(
        "Called getPostById at " + context.getPath(), "getPostById", new Object[] {postID});

    if (postID <= 0) {
      dataSourceRestLog.exiting("Post ID - Bad Request", "getPostById", new Object[] {postID});
      return Response.status(Response.Status.BAD_REQUEST)
          .entity(
              new ErrorResponse(
                  "Invalid Post ID",
                  "Please provide a valid post ID greater than 0",
                  Response.Status.BAD_REQUEST.getStatusCode()))
          .build();
    } else if (!dbPostDao.postExists(postID)) {
      dataSourceRestLog.exiting("Post ID not found", "getPostById", new Object[] {postID});
      return Response.status(Response.Status.NOT_FOUND)
          .entity(
              new ErrorResponse(
                  "Post ID not found",
                  "Please provide a valid post ID",
                  Response.Status.NOT_FOUND.getStatusCode()))
          .build();
    }

    dataSourceRestLog.exiting("Post ID found", "getPostById", new Object[] {postID});
    return Response.ok(getClientPost(postID), MediaType.APPLICATION_JSON).build();
  }

  /**
   * Endpoint to update a post based on the provided ClientPost object. This method validates the
   * post ID and updates the post entry in the database.
   *
   * @param postId | the ID of the post to update
   * @param clientPost | the ClientPost object containing post details to update
   * @return Response indicating the result of the update operation
   */
  @POST
  @Path("{PostID: [0-9]+}")
  @RolesAllowed(value = {"user"})
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response updatePostWP(@PathParam("PostID") long postId, ClientPost clientPost) {
    if (postId <= 0) {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity(
              new ErrorResponse(
                  "Invalid Post ID", "Bad Request", Response.Status.BAD_REQUEST.getStatusCode()))
          .build();

    } else {
      if (!dbPostDao.postExists(postId)) {
        return Response.status(Response.Status.NOT_FOUND)
            .entity(
                new ErrorResponse(
                    "The Post ID provided was not found.",
                    "Unable to find post: " + postId,
                    Response.Status.NOT_FOUND.getStatusCode()))
            .build();
      } else {
        clientPost.setPostID(postId);
        ClientPostUpdateStrategy(clientPost);
        return Response.ok(
                new ServerResponse(
                    "Post ID " + postId + " has been modified with the fields provided",
                    Response.Status.OK.getStatusCode()))
            .build();
      }
    }
  }

  /**
   * Endpoint to update post metadata based on the provided ClientPostMeta object. This method
   * validates the post ID and updates the metadata in the database.
   *
   * @param postID | the ID of the post for which metadata is to be updated
   * @param postMetaFields | the ClientPostMeta object containing post metadata to update
   * @return Response indicating the result of the update operation
   */
  @POST
  @Path("meta/{postID: [0-9]+}")
  @RolesAllowed(value = {"user"})
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response updatePostMeta(@PathParam("postID") long postID, ClientPostMeta postMetaFields) {
    dataSourceRestLog.entering(
        "Called updatePost at " + context.getPath(),
        "updatePost",
        new Object[] {postID, postMetaFields});

    boolean isPostMetaPost = dbPostMetaDao.postExists(postID);
    boolean isWPost = dbPostDao.postExists(postID);

    if (postMetaFields != null && postID > 0) {
      if (!isPostMetaPost && !isWPost) {
        dataSourceRestLog.exiting(
            "Post ID not found", "updatePostMeta", new Object[] {postID, postMetaFields});

        return Response.status(Response.Status.NOT_FOUND)
            .entity(
                new ErrorResponse(
                    "Post ID not found",
                    "Please provide a valid post ID that exists in the database",
                    Response.Status.NOT_FOUND.getStatusCode()))
            .build();

      } else {
        postMetaFields.setID(postID);
        clientPostMetaUpdateStrategy(postMetaFields, true);

        return Response.ok(
                new ServerResponse(
                    "Post ID: " + postID + " modified with the fields provided.",
                    Response.Status.OK.getStatusCode()),
                MediaType.APPLICATION_JSON)
            .build();
      }

    } else {
      dataSourceRestLog.exiting(
          "Tried to update metadata by providing either null post id param or content",
          "updatePostMeta",
          new Object[] {postID, postMetaFields});

      return Response.status(Response.Status.BAD_REQUEST)
          .entity(
              new ErrorResponse(
                  "Invalid Post ID or metadata",
                  "Please provide a valid post ID and metadata to update",
                  Response.Status.BAD_REQUEST.getStatusCode()))
          .build();
    }
  }

  /**
   * Updates the post metadata based on the provided ClientPostMeta object. This method iterates
   * through the properties of the ClientPostMeta object and updates the corresponding metadata in
   * the database.
   *
   * @param clientPost | the ClientPostMeta object containing post metadata to update
   * @param autoCreate | boolean indicating whether to create metadata if it does not exist
   */
  private void clientPostMetaUpdateStrategy(ClientPostMeta clientPost, boolean autoCreate) {
    dataSourceRestLog.entering(
        "Called clientPostUpdateStrategy at " + context.getPath(),
        "clientPostUpdateStrategy",
        new Object[] {clientPost});

    long postId = clientPost.getID();
    Reflection.getTransformClassFields(clientPost.getClass(), Field::getName)
        .forEach(
            p -> {
              switch (PostMetaKeys.fromValue(p).get()) {
                case ID:
                  break;
                case HOURS:
                  if (clientPost.getHours() != 0)
                    dbPostMetaDao.updatePostMetaAuto(
                        postId,
                        PostMetaKeys.HOURS.toString(),
                        Long.toString(clientPost.getHours()),
                        autoCreate);
                  break;
                case MINUTES:
                  if (clientPost.getMinutes() != 0)
                    dbPostMetaDao.updatePostMetaAuto(
                        postId,
                        PostMetaKeys.MINUTES.toString(),
                        Long.toString(clientPost.getMinutes()),
                        autoCreate);
                  break;
                case SECONDS:
                  if (clientPost.getSeconds() != 0)
                    dbPostMetaDao.updatePostMetaAuto(
                        postId,
                        PostMetaKeys.SECONDS.toString(),
                        Long.toString(clientPost.getSeconds()),
                        autoCreate);
                  break;
                case EMBED:
                  dbPostMetaDao.updatePostMetaAuto(
                      postId, PostMetaKeys.EMBED.toString(), clientPost.getEmbedCode(), autoCreate);
                  break;
                case PRODUCTION:
                  dbPostMetaDao.updatePostMetaAuto(
                      postId,
                      PostMetaKeys.PRODUCTION.toString(),
                      clientPost.getVideoProduction(),
                      autoCreate);
                  break;
                case ORIENTATION:
                  dbPostMetaDao.updatePostMetaAuto(
                      postId,
                      PostMetaKeys.ORIENTATION.toString(),
                      clientPost.getVideoOrientation(),
                      autoCreate);
                  break;
                case ETHNICITY:
                  dbPostMetaDao.updatePostMetaAuto(
                      postId,
                      PostMetaKeys.ETHNICITY.toString(),
                      clientPost.getEthnicity(),
                      autoCreate);
                  break;
                case HAIRCOLOR:
                  dbPostMetaDao.updatePostMetaAuto(
                      postId,
                      PostMetaKeys.HAIRCOLOR.toString(),
                      clientPost.getHairColor(),
                      autoCreate);
                  break;
                case HDVIDEO:
                  if (clientPost.getVideoHD() != null)
                    dbPostMetaDao.updatePostMetaAuto(
                        postId,
                        PostMetaKeys.HDVIDEO.toString(),
                        clientPost.getVideoHD() ? "on" : "off",
                        autoCreate);
                  break;
                case THUMBNAIL:
                  dbPostMetaDao.updatePostMetaAuto(
                      postId,
                      PostMetaKeys.THUMBNAIL.toString(),
                      clientPost.getThumbURI(),
                      autoCreate);
                  break;
                case VIDEOURL:
                  dbPostMetaDao.updatePostMetaAuto(
                      postId,
                      PostMetaKeys.VIDEOURL.toString(),
                      clientPost.getVideoURL(),
                      autoCreate);
                  break;
                case YOAST_FOCUSKW:
                  dbPostMetaDao.updatePostMetaAuto(
                      postId,
                      PostMetaKeys.YOAST_FOCUSKW.toString(),
                      clientPost.getYoastFocusKW(),
                      autoCreate);
                  break;
                case YOAST_METADESC:
                  dbPostMetaDao.updatePostMetaAuto(
                      postId,
                      PostMetaKeys.YOAST_METADESC.toString(),
                      clientPost.getYoastMetaDesc(),
                      autoCreate);
                  break;
                default:
                  dataSourceRestLog.throwing(
                      this.getClass().getName(),
                      "clientPostUpdateStrategy",
                      new InvalidIdentifier("Unexpected value: " + p));
                  throw new InvalidIdentifier("Unexpected value: " + p);
              }
            });
  }

  /**
   * Updates the post details based on the provided ClientPost object. This method creates a new
   * WPost object, sets its properties from the ClientPost object, and updates the post entry in the
   * database.
   *
   * @param clientPost | the ClientPost object containing post details to update
   */
  private void ClientPostUpdateStrategy(ClientPost clientPost) {
    dataSourceRestLog.entering(
        "Called ClientPostUpdateStrategy at " + context.getPath(),
        "ClientPostUpdateStrategy",
        new Object[] {clientPost});

    WPost inMemoryPost = new WPost();

    inMemoryPost.setID(clientPost.getPostID());
    inMemoryPost.setPostAuthor(clientPost.getPostAuthor());
    inMemoryPost.setPostContent(clientPost.getPostContent());
    inMemoryPost.setPostTitle(clientPost.getPostTitle());
    inMemoryPost.setPostSlug(clientPost.getPostSlug());
    inMemoryPost.setPostStatus(clientPost.getPostStatus());
    inMemoryPost.setPostType(clientPost.getPostType());

    dbPostDao.updatePostEntry(inMemoryPost, true);
  }

  /**
   * Retrieves the post metadata for a given post ID and converts it into a ClientPostMeta object.
   * This method fetches the metadata entries from the database and populates the ClientPostMeta
   * object with the corresponding values based on the PostMetaKeys enumeration.
   *
   * <p>This method is used to convert the raw metadata entries into a structured ClientPostMeta
   * object that can be easily consumed by the client without exposing the underlying database
   * structure.
   *
   * @param postID | the ID of the post for which metadata is requested
   * @return ClientPostMeta object containing the post metadata
   */
  private ClientPostMeta getClientPostMeta(long postID) {
    ClientPostMeta convertedObj = new ClientPostMeta();

    dbPostMetaDao
        .getEntriesByPostID(postID)
        .forEach(
            p -> {
              if (convertedObj.getID() <= 0) convertedObj.setID(p.getPostID());
              switch (PostMetaKeys.fromValue(p.getMetaFieldKey()).orElse(PostMetaKeys.OTHERS)) {
                case HOURS:
                  convertedObj.setHours(Integer.parseInt(p.getMetaFieldValue()));
                  break;
                case MINUTES:
                  convertedObj.setMinutes(Integer.parseInt(p.getMetaFieldValue()));
                  break;
                case SECONDS:
                  convertedObj.setSeconds(Integer.parseInt(p.getMetaFieldValue()));
                  break;
                case EMBED:
                  convertedObj.setEmbedCode(p.getMetaFieldValue());
                  break;
                case PRODUCTION:
                  convertedObj.setVideoProduction(p.getMetaFieldValue());
                  break;
                case ORIENTATION:
                  convertedObj.setVideoOrientation(p.getMetaFieldValue());
                  break;
                case ETHNICITY:
                  convertedObj.setEthnicity(p.getMetaFieldValue());
                  break;
                case HAIRCOLOR:
                  convertedObj.setHairColor(p.getMetaFieldValue());
                  break;
                case HDVIDEO:
                  convertedObj.setVideoHD(p.getMetaFieldValue().equals("on") ? true : false);
                  break;
                case THUMBNAIL:
                  convertedObj.setThumb(p.getMetaFieldValue());
                  break;
                case VIDEOURL:
                  convertedObj.setVideoURL(p.getMetaFieldValue());
                  break;
                case YOAST_FOCUSKW:
                  convertedObj.setYoastFocusKW(p.getMetaFieldValue());
                  break;
                case YOAST_METADESC:
                  convertedObj.setYoastMetaDesc(p.getMetaFieldValue());
                  break;
                default:
                  break;
              }
            });

    return convertedObj;
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
  private ClientPost getClientPost(long postID) {
    ClientPost convertedObj = new ClientPost();

    dbPostDao.getPostById(postID).stream()
        .forEach(
            p -> {
              convertedObj.setPostID(p.getID());
              convertedObj.setPostAuthor(p.getPostAuthor());
              convertedObj.setPostContent(p.getPostContent());
              convertedObj.setPostTitle(p.getPostTitle());
              convertedObj.setPostSlug(p.getPostSlug());
              convertedObj.setPostStatus(p.getPostStatus());
              convertedObj.setPostType(p.getPostType());
            });

    return convertedObj;
  }
}
