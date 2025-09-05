package net.ygbstudio.postwizard.rest;

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
import java.util.Collection;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.ygbstudio.postwizard.dao.PostMetaReaderDAO;
import net.ygbstudio.postwizard.dao.PostReaderDAO;
import net.ygbstudio.postwizard.dto.ClientPost;
import net.ygbstudio.postwizard.dto.ClientPostMeta;
import net.ygbstudio.postwizard.dto.ErrorResponse;
import net.ygbstudio.postwizard.dto.ServerResponse;
import net.ygbstudio.postwizard.entities.WPost;
import net.ygbstudio.postwizard.enums.PostMetaKeys;
import net.ygbstudio.postwizard.exceptions.InvalidIdentifier;
import net.ygbstudio.postwizard.utils.Logging;
import net.ygbstudio.postwizard.utils.Reflection;
import org.apache.commons.lang3.StringUtils;

/**
 * RESTful web service for managing post metadata in the PostWizard application. This class provides
 * endpoints to retrieve and update post metadata.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@RequestScoped
@Path("posts")
public class PostWizardDataSourceRest {

  private static final Logger dataSourceRestLog =
      Logger.getLogger(PostWizardDataSourceRest.class.getName());

  @SuppressWarnings("unused")
  private static final FileHandler logFileHandler =
      Logging.loggingInit(dataSourceRestLog, Level.ALL, true);

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

    Logging.logStepIn(dataSourceRestLog, postId);

    if (postId <= 0) {
      Logging.logStepOut(dataSourceRestLog, postId);
      dataSourceRestLog.fine("Invalid post ID: Response.Status.BAD_REQUEST");
      return Response.status(Response.Status.BAD_REQUEST)
          .entity(
              new ErrorResponse(
                  "Invalid Post ID",
                  "Please provide a valid post ID greater than 0",
                  Response.Status.BAD_REQUEST.getStatusCode()))
          .build();

    } else if (!dbPostMetaDao.postExists(postId)) {
      Logging.logStepOut(dataSourceRestLog, postId);
      dataSourceRestLog.fine("Post ID not found: Response.Status.NOT_FOUND");
      return Response.status(Response.Status.NOT_FOUND)
          .entity(
              new ErrorResponse(
                  "Post ID not found",
                  "Please provide a valid post ID",
                  Response.Status.NOT_FOUND.getStatusCode()))
          .build();
    }
    ClientPostMeta postMetaResult = getClientPostMeta(postId);
    Logging.logStepOut(dataSourceRestLog, postMetaResult);
    dataSourceRestLog.fine("Post metadata retrieved successfully: Response.Status.OK");
    return Response.ok(postMetaResult, MediaType.APPLICATION_JSON).build();
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
    Logging.logStepIn(dataSourceRestLog, postID);

    if (postID <= 0) {
      Logging.logStepOut(dataSourceRestLog, postID);
      dataSourceRestLog.fine("Invalid Post ID: Response.Status.BAD_REQUEST");
      return Response.status(Response.Status.BAD_REQUEST)
          .entity(
              new ErrorResponse(
                  "Invalid Post ID",
                  "Please provide a valid post ID greater than 0",
                  Response.Status.BAD_REQUEST.getStatusCode()))
          .build();
    } else if (!dbPostDao.postExists(postID)) {
      Logging.logStepOut(dataSourceRestLog, postID);
      dataSourceRestLog.fine("Post ID not found: Response.Status.NOT_FOUND");
      return Response.status(Response.Status.NOT_FOUND)
          .entity(
              new ErrorResponse(
                  "Post ID not found",
                  "Please provide a valid post ID",
                  Response.Status.NOT_FOUND.getStatusCode()))
          .build();
    }
    ClientPost postResult = getClientPost(postID);
    Logging.logStepOut(dataSourceRestLog, postResult);
    return Response.ok(postResult, MediaType.APPLICATION_JSON).build();
  }

  /**
   * Endpoint to update a post based on the provided ClientPost object. This method validates the
   * post ID and updates the post entry in the database. New posts cannot be created using this
   * method; it is intended for updating existing posts only.
   *
   * <p>Creation of new post entries in the database must be done through the WordPress API, so that
   * relevant entries can be modified using this method.
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
    Logging.logStepIn(dataSourceRestLog, postId);
    if (postId <= 0) {
      Logging.logStepOut(dataSourceRestLog, postId);
      dataSourceRestLog.fine("Invalid Post ID: Response.Status.BAD_REQUEST");
      return Response.status(Response.Status.BAD_REQUEST)
          .entity(
              new ErrorResponse(
                  "Invalid Post ID", "Bad Request", Response.Status.BAD_REQUEST.getStatusCode()))
          .build();

    } else {
      if (!dbPostDao.postExists(postId)) {
        Logging.logStepOut(dataSourceRestLog, postId);
        dataSourceRestLog.fine("Post ID not found: Response.Status.NOT_FOUND");
        return Response.status(Response.Status.NOT_FOUND)
            .entity(
                new ErrorResponse(
                    "The Post ID provided was not found.",
                    "Unable to find post: " + postId,
                    Response.Status.NOT_FOUND.getStatusCode()))
            .build();
      } else {
        clientPost.setPostID(postId);
        clientPostUpdateStrategy(clientPost);
        Logging.logStepOut(dataSourceRestLog, postId);
        dataSourceRestLog.fine("Post ID updated successfully: Response.Status.OK");
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

    Logging.logStepIn(dataSourceRestLog, postID, postMetaFields);
    boolean isPostMetaPost = dbPostMetaDao.postExists(postID);
    boolean isWPost = dbPostDao.postExists(postID);

    if (postID >= 0) {
      if (!isPostMetaPost && !isWPost) {
        Logging.logStepOut(dataSourceRestLog, postID, postMetaFields);
        dataSourceRestLog.fine("The post ID is neither a valid post nor is it linked to metadata.");
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
        Logging.logStepOut(dataSourceRestLog, postID, postMetaFields);
        dataSourceRestLog.fine("Post ID updated successfully: Response.Status.OK");
        return Response.ok(
                new ServerResponse(
                    "Post ID: " + postID + " modified with the fields provided.",
                    Response.Status.OK.getStatusCode()),
                MediaType.APPLICATION_JSON)
            .build();
      }

    } else {

      Logging.logStepOut(dataSourceRestLog, postID, postMetaFields);
      dataSourceRestLog.fine("Invalid Post ID or metadata: Response.Status.BAD_REQUEST");
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
   * Endpoint to update multiple post metadata entries in a single batch operation. This method
   * accepts a collection of ClientPostMeta objects, each representing the metadata for a specific
   * post. The method iterates through the collection and updates the metadata for each post
   * accordingly. This batch update helps to reduce the number of individual requests needed to
   * update multiple posts.
   *
   * @param postMetaColl | a collection of ClientPostMeta objects containing post metadata to update
   * @return Response indicating the result of the batch update operation
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed(value = {"user"})
  @Path("meta/batch")
  public Response postMetaBatchUpdate(Collection<ClientPostMeta> postMetaColl) {
    Response.Status malformedRequest = Response.Status.BAD_REQUEST;
    if (postMetaColl.isEmpty()) {
      Logging.logStepOut(dataSourceRestLog, postMetaColl);
      dataSourceRestLog.fine("No items to process: Response.Status.BAD_REQUEST");
      return Response.status(malformedRequest)
          .entity(
              new ErrorResponse(
                  "Unable to process items",
                  "No items to process",
                  malformedRequest.getStatusCode()))
          .build();
    }

    try {
      postMetaColl.forEach(item -> clientPostMetaUpdateStrategy(item, true));
    } catch (Exception anyEx) {
      Response.Status serverError = Response.Status.INTERNAL_SERVER_ERROR;
      Logging.logStepOut(dataSourceRestLog, postMetaColl);
      dataSourceRestLog.fine(
          "Update finished/interrupted with errors: Response.Status.INTERNAL_SERVER_ERROR");
      return Response.status(serverError)
          .entity(
              new ErrorResponse(
                  "Update finished/interrupted with errors",
                  "Try again later",
                  serverError.getStatusCode()))
          .build();
    }

    Logging.logStepOut(dataSourceRestLog, postMetaColl);
    dataSourceRestLog.fine("Batch job executed successfully: Response.Status.OK");
    return Response.ok()
        .entity(
            new ServerResponse(
                "Batch job executed successfully", Response.Status.OK.getStatusCode()))
        .build();
  }

  /**
   * Updates the post metadata based on the provided ClientPostMeta object. This method iterates
   * through the properties of the ClientPostMeta object and updates the corresponding metadata in
   * the database. Unlike posts, metadata fields can be created if the schema provided by the client
   * is correct and constitutes a relevant key in the WordPress site.
   *
   * @param clientPost | the ClientPostMeta object containing post metadata to update
   * @param autoCreate | boolean indicating whether to create metadata if it does not exist
   */
  private void clientPostMetaUpdateStrategy(ClientPostMeta clientPost, boolean autoCreate) {
    Logging.logStepIn(dataSourceRestLog, clientPost);

    long postId = clientPost.getID();
    if (postId <= 0) return;

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
                      StringUtils.capitalize(clientPost.getEthnicity()),
                      autoCreate);
                  break;
                case HAIRCOLOR:
                  dbPostMetaDao.updatePostMetaAuto(
                      postId,
                      PostMetaKeys.HAIRCOLOR.toString(),
                      StringUtils.capitalize(clientPost.getHairColor()),
                      autoCreate);
                  break;
                case HDVIDEO:
                  if (clientPost.getVideoHD() != null)
                    dbPostMetaDao.updatePostMetaAuto(
                        postId,
                        PostMetaKeys.HDVIDEO.toString(),
                        Boolean.TRUE.equals(clientPost.getVideoHD()) ? "on" : "off",
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
   * <p>Creation of new post entries in the database have been temporarily disabled to prevent
   * accidental data loss or malformed entries. The WordPress API should be used to create new
   * posts, so that relevant entries can be modified using this method.
   *
   * @param clientPost | the ClientPost object containing post details to update
   */
  private void clientPostUpdateStrategy(ClientPost clientPost) {
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

    dbPostDao.updatePostEntry(inMemoryPost, false);
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
                  convertedObj.setVideoHD(p.getMetaFieldValue().equals("on"));
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

    return convertedObj;
  }
}
