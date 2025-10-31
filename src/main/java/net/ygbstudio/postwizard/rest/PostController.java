package net.ygbstudio.postwizard.rest;

import static net.ygbstudio.postwizard.rest.ResponseHandlers.handleBadRequest;
import static net.ygbstudio.postwizard.rest.ResponseHandlers.handleException;
import static net.ygbstudio.postwizard.rest.ResponseHandlers.handleNotFound;
import static net.ygbstudio.postwizard.rest.ResponseHandlers.handleServerError;
import static net.ygbstudio.postwizard.utils.Helpers.enumFromValue;
import static net.ygbstudio.postwizard.utils.Logging.logControllerPath;
import static net.ygbstudio.postwizard.utils.Logging.logStepIn;
import static net.ygbstudio.postwizard.utils.Logging.logStepOut;
import static net.ygbstudio.postwizard.utils.Logging.loggingInit;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.ygbstudio.postwizard.dto.BatchJobResponse;
import net.ygbstudio.postwizard.dto.ClientPost;
import net.ygbstudio.postwizard.dto.PostDumpResponse;
import net.ygbstudio.postwizard.dto.ServerResponse;
import net.ygbstudio.postwizard.models.PostType;
import net.ygbstudio.postwizard.service.PostService;
import net.ygbstudio.postwizard.tasks.RandomiseFeaturedTask;
import org.jspecify.annotations.Nullable;

/**
 * RESTful web service for managing post metadata in the PostWizard application. This class provides
 * endpoints to retrieve and update post metadata.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@RolesAllowed(value = {"user"})
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("posts")
public class PostController {

  private static final Logger postDataControllerLog =
      Logger.getLogger(PostController.class.getName());

  @SuppressWarnings("unused")
  @Nullable
  private static final FileHandler logFileHandler =
      loggingInit(postDataControllerLog, Level.ALL, true);

  @Context private UriInfo context;
  @Context private HttpServletRequest request;

  @Inject private PostService postService;

  @Inject private RandomiseFeaturedTask randomiseFeaturedTask;

  /**
   * Endpoint to retrieve all posts of a specific type. This method returns a JSON representation of
   * all posts matching the specified type.
   *
   * @param postType the type of posts to retrieve (e.g., "post", "attachment", "photos", or "all")
   * @return JSON representation of all posts of the specified type
   */
  @GET
  @Path("dump")
  public Response getPostDump(@DefaultValue("post") @QueryParam("type") String postType) {
    logStepIn(postDataControllerLog, context.getPath());
    try {
      if (postService.isValidPostType(postType)) {
        /*
         Any NoSuchElementException is caught by the catch block
         or by its mapper to avoid information leaks.
         In either case the response will be 500.
        */
        List<ClientPost> allPosts =
            postService.getAllClientPostByType(
                enumFromValue(PostType.class, postType, true).orElseThrow());
        postDataControllerLog.info(
            () ->
                "Returned %d posts on %s - Requested by %s"
                    .formatted(allPosts.size(), LocalDateTime.now(), request.getRemoteAddr()));
        return Response.ok(
                new PostDumpResponse(
                    "Dump request processed successfully",
                    Response.Status.OK.getStatusCode(),
                    allPosts),
                MediaType.APPLICATION_JSON_TYPE)
            .build();
      } else {
        return handleBadRequest(
            () ->
                "Invalid post type "
                    + postType
                    + " Post types can be: (e.g., \"post\", \"attachment\", \"photos\", or \"all\")",
            postDataControllerLog);
      }
    } catch (Exception anyEx) {
      return handleException(
          () -> "Unable to obtain a batch dump of site posts", postDataControllerLog, anyEx);
    }
  }

  /**
   * Endpoint to retrieve a post by its ID. This method returns a JSON representation of the post
   * associated with the specified post ID.
   *
   * @param postId the ID of the post to retrieve
   * @return JSON representation of the post
   */
  @GET
  @Path("{postId: [0-9]+}")
  public Response getPostById(@PathParam("postId") long postId) {
    logStepIn(postDataControllerLog, postId);
    logControllerPath(postDataControllerLog, context, request);
    try {
      if (postId <= 0) {
        return handleBadRequest(
            () -> "Invalid Post ID " + postId + " Please provide a valid post ID greater than 0",
            postDataControllerLog);
      } else if (!postService.postExists(postId)) {
        return handleNotFound(
            () -> "Post ID not found" + " Please provide a valid post ID", postDataControllerLog);
      }
      ClientPost postResult = postService.getClientPost(postId);
      logStepOut(postDataControllerLog, postResult);
      postDataControllerLog.fine(
          () ->
              "Post metadata retrieved successfully post id %d : Response.Status.OK - Requested by %s"
                  .formatted(postId, request.getRemoteAddr()));
      return Response.ok(postResult, MediaType.APPLICATION_JSON_TYPE).build();
    } catch (Exception anyEx) {
      return handleException(
          () -> "Unable to retrieve post metadata", postDataControllerLog, anyEx);
    }
  }

  /**
   * Endpoint to update a post based on the provided ClientPost object. This method validates the
   * post ID and updates the post-entry in the database. New posts cannot be created using this
   * method; it is intended for updating existing posts only.
   *
   * <p>Creation of new post-entries in the database must be done through the WordPress API, so that
   * relevant entries can be modified using this method.
   *
   * @param postId the ID of the post to update
   * @param clientPost the ClientPost object containing post details to update
   * @return Response indicating the result of the update operation
   */
  @POST
  @Path("{postId: [0-9]+}")
  public Response updatePostWP(@PathParam("postId") long postId, ClientPost clientPost) {
    logStepIn(postDataControllerLog, postId);
    logControllerPath(postDataControllerLog, context, request);

    if (postId <= 0) {
      return handleBadRequest(
          () -> "Invalid Post ID " + postId + " Please provide a valid post ID greater than 0",
          postDataControllerLog);

    } else {
      if (!postService.postExists(postId)) {
        return handleNotFound(() -> "Post ID " + postId + " not found", postDataControllerLog);
      } else {
        clientPost.setID(postId);
        postService.clientPostUpdateStrategy(clientPost);
        logStepOut(postDataControllerLog, postId);
        postDataControllerLog.fine(
            () ->
                "Post ID updated successfully: Response.Status.OK - Requested by "
                    + request.getRemoteAddr());
        return Response.ok(
                new ServerResponse(
                    "Post ID " + postId + " has been modified with the fields provided",
                    Response.Status.OK.getStatusCode()))
            .build();
      }
    }
  }

  /**
   * Endpoint to update multiple posts in a single batch operation. This method accepts a List of
   * ClientPost objects, each representing a post to be updated. The method iterates through the
   * List and updates each post accordingly. This batch update helps to reduce the number of
   * individual requests needed to update multiple posts.
   *
   * @param clientPosts a List of ClientPost objects containing post details to update
   * @return Response indicating the result of the batch update operation
   */
  @POST
  @Path("batch")
  public Response postBatchUpdate(List<ClientPost> clientPosts) {
    logStepIn(postDataControllerLog, clientPosts);
    if (clientPosts.isEmpty()) {
      return handleBadRequest(
          () -> "Unable to complete batch job." + " No items to process", postDataControllerLog);
    } else {
      try {
        clientPosts.forEach(post -> postService.clientPostUpdateStrategy(post));
        return Response.ok(
                new BatchJobResponse(
                    "Post batch job executed successfully",
                    Response.Status.OK.getStatusCode(),
                    clientPosts.stream().map(ClientPost::getID).filter(Objects::nonNull).toList()),
                MediaType.APPLICATION_JSON_TYPE)
            .build();

      } catch (Exception anyEx) {
        return handleException(() -> "Unable to complete batch job", postDataControllerLog, anyEx);
      }
    }
  }

  /**
   * Endpoint to randomise featured videos in a single batch operation. This method accepts a limit
   * of posts to randomise. The limit is optional and defaults to 10 if not provided.
   *
   * @param limit the limit of posts to randomise
   * @return a Response object containing the result of the randomisation operation and the list of
   *     featured videos
   */
  @GET
  @Path("randomfeatured")
  public Response randomiseFeaturedVideos(@QueryParam("limit") @DefaultValue("10") int limit) {
    if (limit < 0) {
      return handleBadRequest(() -> "Limit has to be greater than 0", postDataControllerLog);
    } else {
      try {
        Set<Long> newFeaturedVideos = randomiseFeaturedTask.randomiseFeaturedVideosBean(limit);
        if (newFeaturedVideos.isEmpty()) {
          return handleServerError(
              () -> "Unable to complete randomisation job", postDataControllerLog);
        } else {
          return Response.ok(
                  new BatchJobResponse(
                      "Your post randomisation request has succeeded",
                      Response.Status.OK.getStatusCode(),
                      new ArrayList<>(newFeaturedVideos)),
                  MediaType.APPLICATION_JSON_TYPE)
              .build();
        }
      } catch (Exception anyEx) {
        return handleException(
            () -> "Unable to complete randomisation job", postDataControllerLog, anyEx);
      }
    }
  }
}
