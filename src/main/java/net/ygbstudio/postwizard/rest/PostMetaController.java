package net.ygbstudio.postwizard.rest;

import static net.ygbstudio.postwizard.rest.ResponseHandlers.handleBadRequest;
import static net.ygbstudio.postwizard.rest.ResponseHandlers.handleException;
import static net.ygbstudio.postwizard.rest.ResponseHandlers.handleNotFound;
import static net.ygbstudio.postwizard.utils.Logging.logControllerPath;
import static net.ygbstudio.postwizard.utils.Logging.logStepIn;
import static net.ygbstudio.postwizard.utils.Logging.logStepOut;
import static net.ygbstudio.postwizard.utils.Logging.loggingInit;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.ygbstudio.postwizard.dto.BatchJobResponse;
import net.ygbstudio.postwizard.dto.ClientPost;
import net.ygbstudio.postwizard.dto.ClientPostMeta;
import net.ygbstudio.postwizard.dto.PostDumpResponse;
import net.ygbstudio.postwizard.dto.ServerResponse;
import net.ygbstudio.postwizard.models.PostMetaKeys;
import net.ygbstudio.postwizard.service.EnvironmentService;
import net.ygbstudio.postwizard.service.PostMetaService;
import net.ygbstudio.postwizard.service.PostService;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
@Path("posts/meta")
public class PostMetaController {
  public static final Logger postMetaControllerLog =
      Logger.getLogger(PostMetaController.class.getName());

  @Nullable
  public static final FileHandler postMetaControllerFileHandler =
      loggingInit(postMetaControllerLog, Level.ALL, true);

  @Context HttpServletRequest request;

  @Context UriInfo context;

  @Inject PostMetaService postMetaService;

  @Inject PostService postService;

  @Inject EnvironmentService environment;

  /**
   * Endpoint to retrieve post metadata by post ID. This method returns a JSON representation of the
   * post metadata associated with the specified post ID.
   *
   * @param postId the ID of the post for which metadata is requested
   * @return JSON representation of the post metadata
   */
  @GET
  @Path("{postId: [0-9]+}")
  @RolesAllowed(value = {"user"})
  @Produces(MediaType.APPLICATION_JSON)
  public Response getPostMeta(@PathParam("postId") long postId) {

    logStepIn(postMetaControllerLog, postId);
    logControllerPath(postMetaControllerLog, context, request);

    if (postId <= 0) {
      return handleBadRequest(
          () -> "Invalid Post ID " + postId + " Please provide a valid post ID greater than 0",
          postMetaControllerLog);

    } else if (!postMetaService.hasMetaFields(postId)) {
      return handleNotFound(() -> "Post ID " + postId + " not found", postMetaControllerLog);
    }
    ClientPostMeta postMetaResult = postMetaService.getClientPostMeta(postId);
    logStepOut(postMetaControllerLog, postMetaResult);
    postMetaControllerLog.fine(
        "Post metadata retrieved successfully: Response.Status.OK - Requested by "
            + request.getRemoteAddr());
    return Response.ok(postMetaResult, MediaType.APPLICATION_JSON_TYPE).build();
  }

  /**
   * Endpoint to retrieve all post metadata entries. This method returns a JSON representation of
   * all post metadata in the system.
   *
   * @return JSON representation of all post metadata
   */
  @GET
  @Path("dump")
  @RolesAllowed(value = {"user"})
  @Produces(MediaType.APPLICATION_JSON)
  public Response getPostMetaDump() {
    logStepIn(postMetaControllerLog, context.getPath());
    try {
      List<ClientPostMeta> allMeta = postMetaService.getClientPostMetaAll();
      postMetaControllerLog.info(
          () ->
              "Returned %d posts on %s - Requested by %s"
                  .formatted(allMeta.size(), LocalDateTime.now(), request.getRemoteAddr()));
      logStepOut(postMetaControllerLog, allMeta);
      return Response.ok(
              new PostDumpResponse(
                  "Dump request processed successfully",
                  Response.Status.OK.getStatusCode(),
                  allMeta),
              MediaType.APPLICATION_JSON_TYPE)
          .build();
    } catch (Exception anyEx) {
      return handleException(postMetaControllerLog, anyEx);
    }
  }

  /**
   * Endpoint to update post metadata based on the provided ClientPostMeta object. This method
   * validates the post ID and updates the metadata in the database.
   *
   * <p>This method has incorporates an {@code autoThumb} feature that automatically updates the
   * thumbnail based on any attachment posts named after the post's slug. This feature is disabled
   * by default. To enable it, set the {@code autoThumb} parameter to true in the request.
   *
   * @param postId the ID of the post for which metadata is to be updated
   * @param postMetaFields the ClientPostMeta object containing post metadata to update
   * @param autoThumb whether to automatically update the thumbnail based on related post media
   * @return Response indicating the result of the update operation
   */
  @POST
  @Path("{postId: [0-9]+}")
  @RolesAllowed(value = {"user"})
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response updatePostMeta(
      @PathParam("postId") long postId,
      @QueryParam("autothumb") boolean autoThumb,
      ClientPostMeta postMetaFields) {

    logStepIn(postMetaControllerLog, postId, postMetaFields);
    logControllerPath(postMetaControllerLog, context, request);

    boolean isPostMetaPost = postMetaService.hasMetaFields(postId);
    boolean isWPost = postService.postExists(postId);

    if (postId >= 0) {
      if (!isPostMetaPost && !isWPost) {
        return handleNotFound(() -> "Post ID " + postId + " not found", postMetaControllerLog);

      } else {
        if (autoThumb && isWPost) {
          ClientPost post = postService.getClientPost(postId);
          long mediaPostId = postService.getMediaPostIdBySlug(post.getSlug());
          if (mediaPostId > 0) {
            Optional<String> mediaFile =
                postMetaService.getMetaValueByPostID(mediaPostId, PostMetaKeys.WP_ATTACHED_FILE);
            if (mediaFile.isPresent()) {
              String siteUploadsPath = environment.getUploadsURLPrefix();
              String mediaFilePath =
                  Objects.nonNull(siteUploadsPath)
                      ? String.join("/", siteUploadsPath, mediaFile.get())
                      : "";
              if (!mediaFilePath.isEmpty()) postMetaFields.setThumbURI(mediaFilePath);
            }
          }
        }
        postMetaFields.setID(postId);
        postMetaService.clientPostMetaUpdateStrategy(postMetaFields, true);
        logStepOut(postMetaControllerLog, postId, postMetaFields);
        postMetaControllerLog.fine(
            "Post ID updated successfully: Response.Status.OK - Requested by "
                + request.getRemoteAddr());
        return Response.ok(
                new ServerResponse(
                    "Post ID: " + postId + " modified with the fields provided.",
                    Response.Status.OK.getStatusCode()),
                MediaType.APPLICATION_JSON_TYPE)
            .build();
      }

    } else {
      logStepOut(postMetaControllerLog, postId, postMetaFields);
      postMetaControllerLog.fine("Invalid Post ID or metadata: Response.Status.BAD_REQUEST");
      return handleBadRequest(
          () -> "Invalid Post ID " + postId + " Please provide a valid post ID greater than 0",
          postMetaControllerLog);
    }
  }

  /**
   * Endpoint to update multiple post metadata entries in a single batch operation. This method
   * accepts a List of ClientPostMeta objects, each representing the metadata for a specific post.
   * The method iterates through the List and updates the metadata for each post accordingly. This
   * batch update helps to reduce the number of individual requests needed to update multiple posts.
   *
   * @param postMetaColl a List of ClientPostMeta objects containing post metadata to update
   * @return Response indicating the result of the batch update operation
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed(value = {"user"})
  @Path("batch")
  public Response postMetaBatchUpdate(List<ClientPostMeta> postMetaColl) {
    logStepIn(postMetaControllerLog, postMetaColl);
    logControllerPath(postMetaControllerLog, context, request);

    if (postMetaColl.isEmpty()) {
      return handleBadRequest(
          () -> "Unable to process items - No items to process", postMetaControllerLog);
    }

    try {
      postMetaColl.forEach(item -> postMetaService.clientPostMetaUpdateStrategy(item, true));
    } catch (Exception anyEx) {
      return handleException(
          () -> "Update finished/interrupted with errors", postMetaControllerLog, anyEx);
    }

    List<Long> postsModified =
        postMetaColl.stream().map(ClientPostMeta::getID).filter(p -> p > 0).toList();

    logStepOut(postMetaControllerLog, postMetaColl, postsModified);
    postMetaControllerLog.fine(
        "Batch job executed successfully: Response.Status.OK - Requested by "
            + request.getRemoteAddr());
    return Response.ok(
            new BatchJobResponse(
                "Batch job executed successfully",
                Response.Status.OK.getStatusCode(),
                postsModified),
            MediaType.APPLICATION_JSON_TYPE)
        .build();
  }
}
