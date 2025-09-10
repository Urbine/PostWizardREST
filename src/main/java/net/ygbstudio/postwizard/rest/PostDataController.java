package net.ygbstudio.postwizard.rest;

import static net.ygbstudio.postwizard.utils.Logging.logControllerPath;
import static net.ygbstudio.postwizard.utils.Logging.logStepIn;
import static net.ygbstudio.postwizard.utils.Logging.logStepOut;
import static net.ygbstudio.postwizard.utils.Logging.loggingInit;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.Collection;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.ygbstudio.postwizard.dto.BatchJobResponse;
import net.ygbstudio.postwizard.dto.ClientPost;
import net.ygbstudio.postwizard.dto.ClientPostMeta;
import net.ygbstudio.postwizard.dto.ErrorResponse;
import net.ygbstudio.postwizard.dto.ServerResponse;
import net.ygbstudio.postwizard.service.PostMetaService;
import net.ygbstudio.postwizard.service.PostService;

/**
 * RESTful web service for managing post metadata in the PostWizard application. This class provides
 * endpoints to retrieve and update post metadata.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@RequestScoped
@Path("posts")
public class PostDataController {

  private static final Logger postDataControllerLog =
      Logger.getLogger(PostDataController.class.getName());

  @SuppressWarnings("unused")
  private static final FileHandler logFileHandler =
      loggingInit(postDataControllerLog, Level.ALL, true);

  @Context private UriInfo context;
  @Context private HttpServletRequest request;

  @Inject private PostMetaService postMetaService;

  @Inject private PostService postService;

  /**
   * Endpoint to retrieve post metadata by post ID. This method returns a JSON representation of the
   * post metadata associated with the specified post ID.
   *
   * @param postId the ID of the post for which metadata is requested
   * @return JSON representation of the post metadata
   */
  @GET
  @Path("meta/{postID: [0-9]+}")
  @RolesAllowed(value = {"user"})
  @Produces(MediaType.APPLICATION_JSON)
  public Response getPostMeta(@PathParam("postID") long postId) {

    logStepIn(postDataControllerLog, postId);
    logControllerPath(postDataControllerLog, context, request);

    if (postId <= 0) {
      logStepOut(postDataControllerLog, postId);
      postDataControllerLog.fine("Invalid post ID: Response.Status.BAD_REQUEST");
      return Response.status(Response.Status.BAD_REQUEST)
          .entity(
              new ErrorResponse(
                  "Invalid Post ID",
                  "Please provide a valid post ID greater than 0",
                  Response.Status.BAD_REQUEST.getStatusCode()))
          .build();

    } else if (!postMetaService.hasMetaFields(postId)) {
      logStepOut(postDataControllerLog, postId);
      postDataControllerLog.fine("Post ID not found: Response.Status.NOT_FOUND");
      return Response.status(Response.Status.NOT_FOUND)
          .entity(
              new ErrorResponse(
                  "Post ID not found",
                  "Please provide a valid post ID",
                  Response.Status.NOT_FOUND.getStatusCode()))
          .build();
    }
    ClientPostMeta postMetaResult = postMetaService.getClientPostMeta(postId);
    logStepOut(postDataControllerLog, postMetaResult);
    postDataControllerLog.fine("Post metadata retrieved successfully: Response.Status.OK");
    return Response.ok(postMetaResult, MediaType.APPLICATION_JSON_TYPE).build();
  }

  /**
   * Endpoint to retrieve all post metadata entries. This method returns a JSON representation of
   * all post metadata in the system.
   *
   * @return JSON representation of all post metadata
   */
  @GET
  @Path("meta/dump")
  @RolesAllowed(value = {"user"})
  @Produces(MediaType.APPLICATION_JSON)
  public Response getPostMetaDump() {
    logStepIn(postDataControllerLog, context.getPath());
    try {
      Collection<ClientPostMeta> allMeta = postMetaService.getClientPostMetaAll();
      logStepOut(postDataControllerLog, allMeta);
      return Response.ok(allMeta, MediaType.APPLICATION_JSON_TYPE).build();
    } catch (Exception anyEx) {
      postDataControllerLog.log(Level.SEVERE, "Exception caught: ", anyEx);
      logStepOut(postDataControllerLog, anyEx);
      Response.StatusType internalError = Response.Status.INTERNAL_SERVER_ERROR;
      return Response.status(internalError)
          .entity(
              new ErrorResponse(
                  "Error while processing this batch request",
                  "Try again later",
                  internalError.getStatusCode()))
          .build();
    }
  }

  /**
   * Endpoint to retrieve a post by its ID. This method returns a JSON representation of the post
   * associated with the specified post ID.
   *
   * @param postID the ID of the post to retrieve
   * @return JSON representation of the post
   */
  @GET
  @Path("{postID: [0-9]+}")
  @RolesAllowed(value = {"user"})
  @Produces(MediaType.APPLICATION_JSON)
  public Response getPostById(@PathParam("postID") long postID) {
    logStepIn(postDataControllerLog, postID);
    logControllerPath(postDataControllerLog, context, request);

    if (postID <= 0) {
      logStepOut(postDataControllerLog, postID);
      postDataControllerLog.fine("Invalid Post ID: Response.Status.BAD_REQUEST");
      return Response.status(Response.Status.BAD_REQUEST)
          .entity(
              new ErrorResponse(
                  "Invalid Post ID",
                  "Please provide a valid post ID greater than 0",
                  Response.Status.BAD_REQUEST.getStatusCode()))
          .build();
    } else if (!postService.postExists(postID)) {
      logStepOut(postDataControllerLog, postID);
      postDataControllerLog.fine("Post ID not found: Response.Status.NOT_FOUND");
      return Response.status(Response.Status.NOT_FOUND)
          .entity(
              new ErrorResponse(
                  "Post ID not found",
                  "Please provide a valid post ID",
                  Response.Status.NOT_FOUND.getStatusCode()))
          .build();
    }
    ClientPost postResult = postService.getClientPost(postID);
    logStepOut(postDataControllerLog, postResult);
    return Response.ok(postResult, MediaType.APPLICATION_JSON_TYPE).build();
  }

  /**
   * Endpoint to update a post based on the provided ClientPost object. This method validates the
   * post ID and updates the post entry in the database. New posts cannot be created using this
   * method; it is intended for updating existing posts only.
   *
   * <p>Creation of new post entries in the database must be done through the WordPress API, so that
   * relevant entries can be modified using this method.
   *
   * @param postId the ID of the post to update
   * @param clientPost the ClientPost object containing post details to update
   * @return Response indicating the result of the update operation
   */
  @POST
  @Path("{PostID: [0-9]+}")
  @RolesAllowed(value = {"user"})
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response updatePostWP(@PathParam("PostID") long postId, ClientPost clientPost) {
    logStepIn(postDataControllerLog, postId);
    logControllerPath(postDataControllerLog, context, request);

    if (postId <= 0) {
      logStepOut(postDataControllerLog, postId);
      postDataControllerLog.fine("Invalid Post ID: Response.Status.BAD_REQUEST");
      return Response.status(Response.Status.BAD_REQUEST)
          .entity(
              new ErrorResponse(
                  "Invalid Post ID", "Bad Request", Response.Status.BAD_REQUEST.getStatusCode()))
          .build();

    } else {
      if (!postService.postExists(postId)) {
        logStepOut(postDataControllerLog, postId);
        postDataControllerLog.fine("Post ID not found: Response.Status.NOT_FOUND");
        return Response.status(Response.Status.NOT_FOUND)
            .entity(
                new ErrorResponse(
                    "The Post ID provided was not found.",
                    "Unable to find post: " + postId,
                    Response.Status.NOT_FOUND.getStatusCode()))
            .build();
      } else {
        clientPost.setPostID(postId);
        postService.clientPostUpdateStrategy(clientPost);
        logStepOut(postDataControllerLog, postId);
        postDataControllerLog.fine("Post ID updated successfully: Response.Status.OK");
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
   * @param postID the ID of the post for which metadata is to be updated
   * @param postMetaFields the ClientPostMeta object containing post metadata to update
   * @return Response indicating the result of the update operation
   */
  @POST
  @Path("meta/{postID: [0-9]+}")
  @RolesAllowed(value = {"user"})
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response updatePostMeta(@PathParam("postID") long postID, ClientPostMeta postMetaFields) {

    logStepIn(postDataControllerLog, postID, postMetaFields);
    logControllerPath(postDataControllerLog, context, request);

    boolean isPostMetaPost = postMetaService.hasMetaFields(postID);
    boolean isWPost = postService.postExists(postID);

    if (postID >= 0) {
      if (!isPostMetaPost && !isWPost) {
        logStepOut(postDataControllerLog, postID, postMetaFields);
        postDataControllerLog.fine(
            "The post ID is neither a valid post nor is it linked to metadata.");
        return Response.status(Response.Status.NOT_FOUND)
            .entity(
                new ErrorResponse(
                    "Post ID not found",
                    "Please provide a valid post ID that exists in the database",
                    Response.Status.NOT_FOUND.getStatusCode()))
            .build();

      } else {
        postMetaFields.setID(postID);
        postMetaService.clientPostMetaUpdateStrategy(postMetaFields, true);
        logStepOut(postDataControllerLog, postID, postMetaFields);
        postDataControllerLog.fine("Post ID updated successfully: Response.Status.OK");
        return Response.ok(
                new ServerResponse(
                    "Post ID: " + postID + " modified with the fields provided.",
                    Response.Status.OK.getStatusCode()),
                MediaType.APPLICATION_JSON_TYPE)
            .build();
      }

    } else {

      logStepOut(postDataControllerLog, postID, postMetaFields);
      postDataControllerLog.fine("Invalid Post ID or metadata: Response.Status.BAD_REQUEST");
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
   * @param postMetaColl a collection of ClientPostMeta objects containing post metadata to update
   * @return Response indicating the result of the batch update operation
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed(value = {"user"})
  @Path("meta/batch")
  public Response postMetaBatchUpdate(Collection<ClientPostMeta> postMetaColl) {
    logStepIn(postDataControllerLog, postMetaColl);
    logControllerPath(postDataControllerLog, context, request);

    Response.Status malformedRequest = Response.Status.BAD_REQUEST;
    if (postMetaColl.isEmpty()) {
      logStepOut(postDataControllerLog, postMetaColl);
      postDataControllerLog.fine("No items to process: Response.Status.BAD_REQUEST");
      return Response.status(malformedRequest)
          .entity(
              new ErrorResponse(
                  "Unable to process items",
                  "No items to process",
                  malformedRequest.getStatusCode()))
          .build();
    }

    try {
      postMetaColl.parallelStream()
          .forEach(item -> postMetaService.clientPostMetaUpdateStrategy(item, true));
    } catch (Exception anyEx) {
      Response.Status serverError = Response.Status.INTERNAL_SERVER_ERROR;
      logStepOut(postDataControllerLog, postMetaColl);
      postDataControllerLog.fine(
          "Update finished/interrupted with errors: Response.Status.INTERNAL_SERVER_ERROR");
      return Response.status(serverError)
          .entity(
              new ErrorResponse(
                  "Update finished/interrupted with errors",
                  "Try again later",
                  serverError.getStatusCode()))
          .build();
    }

    List<Long> postsModified =
        postMetaColl.stream().filter(p -> p.getID() > 0).map(ClientPostMeta::getID).toList();

    logStepOut(postDataControllerLog, postMetaColl, postsModified);
    postDataControllerLog.fine("Batch job executed successfully: Response.Status.OK");
    return Response.ok(
            new BatchJobResponse(
                "Batch job executed successfully",
                Response.Status.OK.getStatusCode(),
                postsModified),
            MediaType.APPLICATION_JSON_TYPE)
        .build();
  }
}
