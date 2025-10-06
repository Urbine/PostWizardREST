package net.ygbstudio.postwizard.rest;

import static net.ygbstudio.postwizard.utils.Helpers.enumFromValue;
import static net.ygbstudio.postwizard.utils.Logging.logControllerPath;
import static net.ygbstudio.postwizard.utils.Logging.logStepIn;
import static net.ygbstudio.postwizard.utils.Logging.logStepOut;
import static net.ygbstudio.postwizard.utils.Logging.loggingInit;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
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
import java.util.Optional;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.ygbstudio.postwizard.dto.BatchJobResponse;
import net.ygbstudio.postwizard.dto.ClientPost;
import net.ygbstudio.postwizard.dto.ClientPostMeta;
import net.ygbstudio.postwizard.dto.ClientTaxonomy;
import net.ygbstudio.postwizard.dto.ClientTerm;
import net.ygbstudio.postwizard.dto.ErrorResponse;
import net.ygbstudio.postwizard.dto.PostDumpResponse;
import net.ygbstudio.postwizard.dto.ServerResponse;
import net.ygbstudio.postwizard.dto.ServerResult;
import net.ygbstudio.postwizard.models.PostType;
import net.ygbstudio.postwizard.service.PostMetaService;
import net.ygbstudio.postwizard.service.PostService;
import net.ygbstudio.postwizard.service.TaxonomyService;
import net.ygbstudio.postwizard.tasks.RandomiseFeaturedTask;

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

  @Inject private RandomiseFeaturedTask randomiseFeaturedTask;

  @Inject private TaxonomyService taxonomyService;

  /**
   * Endpoint to retrieve post metadata by post ID. This method returns a JSON representation of the
   * post metadata associated with the specified post ID.
   *
   * @param postId the ID of the post for which metadata is requested
   * @return JSON representation of the post metadata
   */
  @GET
  @Path("meta/{postId: [0-9]+}")
  @RolesAllowed(value = {"user"})
  @Produces(MediaType.APPLICATION_JSON)
  public Response getPostMeta(@PathParam("postId") long postId) {

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
    postDataControllerLog.fine(
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
  @Path("meta/dump")
  @RolesAllowed(value = {"user"})
  @Produces(MediaType.APPLICATION_JSON)
  public Response getPostMetaDump() {
    logStepIn(postDataControllerLog, context.getPath());
    try {
      List<ClientPostMeta> allMeta = postMetaService.getClientPostMetaAll();
      postDataControllerLog.info(
          () ->
              "Returned %d posts on %s - Requested by %s"
                  .formatted(allMeta.size(), LocalDateTime.now(), request.getRemoteAddr()));
      logStepOut(postDataControllerLog, allMeta);
      return Response.ok(
              new PostDumpResponse(
                  "Dump request processed successfully",
                  Response.Status.OK.getStatusCode(),
                  allMeta),
              MediaType.APPLICATION_JSON_TYPE)
          .build();
    } catch (Exception anyEx) {
      postDataControllerLog.log(Level.SEVERE, "Exception caught: ", anyEx);
      Response.StatusType internalError = Response.Status.INTERNAL_SERVER_ERROR;
      logStepOut(postDataControllerLog, anyEx, internalError);
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
   * Endpoint to retrieve all posts of a specific type. This method returns a JSON representation of
   * all posts matching the specified type.
   *
   * @param postType the type of posts to retrieve (e.g., "post", "attachment", "photos", or "all")
   * @return JSON representation of all posts of the specified type
   */
  @GET
  @Path("dump")
  @RolesAllowed(value = {"user"})
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
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
        Response.StatusType badRequest = Response.Status.BAD_REQUEST;
        logStepOut(postDataControllerLog, postType, badRequest);
        return Response.status(badRequest)
            .entity(
                new ErrorResponse(
                    "Invalid post type " + postType,
                    "Post types can be: (e.g., \"post\", \"attachment\", \"photos\", or \"all\")",
                    badRequest.getStatusCode()))
            .build();
      }
    } catch (Exception anyEx) {
      postDataControllerLog.log(Level.SEVERE, "Exception caught: ", anyEx);
      Response.StatusType internalServerError = Response.Status.INTERNAL_SERVER_ERROR;
      logStepOut(postDataControllerLog, anyEx, internalServerError);
      return Response.status(internalServerError)
          .entity(
              new ErrorResponse(
                  "Unable to obtain a batch dump of site posts",
                  "Try again later",
                  internalServerError.getStatusCode()))
          .build();
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
  @RolesAllowed(value = {"user"})
  @Produces(MediaType.APPLICATION_JSON)
  public Response getPostById(@PathParam("postId") long postId) {
    logStepIn(postDataControllerLog, postId);
    logControllerPath(postDataControllerLog, context, request);

    if (postId <= 0) {
      logStepOut(postDataControllerLog, postId);
      postDataControllerLog.fine("Invalid Post ID: Response.Status.BAD_REQUEST");
      return Response.status(Response.Status.BAD_REQUEST)
          .entity(
              new ErrorResponse(
                  "Invalid Post ID",
                  "Please provide a valid post ID greater than 0",
                  Response.Status.BAD_REQUEST.getStatusCode()))
          .build();
    } else if (!postService.postExists(postId)) {
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
    ClientPost postResult = postService.getClientPost(postId);
    logStepOut(postDataControllerLog, postResult);
    postDataControllerLog.fine(
        () ->
            "Post metadata retrieved successfully post id %d : Response.Status.OK - Requested by %s"
                .formatted(postId, request.getRemoteAddr()));
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
  @Path("{postId: [0-9]+}")
  @RolesAllowed(value = {"user"})
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response updatePostWP(@PathParam("postId") long postId, ClientPost clientPost) {
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
        clientPost.setID(postId);
        postService.clientPostUpdateStrategy(clientPost);
        logStepOut(postDataControllerLog, postId);
        postDataControllerLog.fine(
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
   * Endpoint to update post metadata based on the provided ClientPostMeta object. This method
   * validates the post ID and updates the metadata in the database.
   *
   * @param postId the ID of the post for which metadata is to be updated
   * @param postMetaFields the ClientPostMeta object containing post metadata to update
   * @return Response indicating the result of the update operation
   */
  @POST
  @Path("meta/{postId: [0-9]+}")
  @RolesAllowed(value = {"user"})
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response updatePostMeta(@PathParam("postId") long postId, ClientPostMeta postMetaFields) {

    logStepIn(postDataControllerLog, postId, postMetaFields);
    logControllerPath(postDataControllerLog, context, request);

    boolean isPostMetaPost = postMetaService.hasMetaFields(postId);
    boolean isWPost = postService.postExists(postId);

    if (postId >= 0) {
      if (!isPostMetaPost && !isWPost) {
        logStepOut(postDataControllerLog, postId, postMetaFields);
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
        postMetaFields.setID(postId);
        postMetaService.clientPostMetaUpdateStrategy(postMetaFields, true);
        logStepOut(postDataControllerLog, postId, postMetaFields);
        postDataControllerLog.fine(
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

      logStepOut(postDataControllerLog, postId, postMetaFields);
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
  @Path("meta/batch")
  public Response postMetaBatchUpdate(List<ClientPostMeta> postMetaColl) {
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
      postMetaColl.forEach(item -> postMetaService.clientPostMetaUpdateStrategy(item, true));
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
        postMetaColl.stream().map(ClientPostMeta::getID).filter(p -> p > 0).toList();

    logStepOut(postDataControllerLog, postMetaColl, postsModified);
    postDataControllerLog.fine(
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
  @RolesAllowed(value = {"user"})
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response postBatchUpdate(List<ClientPost> clientPosts) {
    logStepIn(postDataControllerLog, clientPosts);
    if (clientPosts.isEmpty()) {
      logStepOut(postDataControllerLog, "No items to process");
      Response.StatusType badRequest = Response.Status.BAD_REQUEST;
      return Response.status(badRequest)
          .entity(
              new ErrorResponse(
                  "Unable to complete batch job",
                  "No items to process",
                  badRequest.getStatusCode()))
          .build();
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
        Response.StatusType internalServerError = Response.Status.INTERNAL_SERVER_ERROR;
        return Response.status(internalServerError)
            .entity(
                new ErrorResponse(
                    "An error occurred while processing this batch",
                    "Try again later",
                    internalServerError.getStatusCode()))
            .build();
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
  @RolesAllowed(value = {"user"})
  @Produces(MediaType.APPLICATION_JSON)
  public Response randomiseFeaturedVideos(@QueryParam("limit") @DefaultValue("10") int limit) {
    if (limit < 0) {
      Response.Status badRequest = Response.Status.BAD_REQUEST;
      return Response.status(badRequest)
          .entity(
              new ErrorResponse(
                  "Limit has to be greater than 0",
                  "Malformed request",
                  badRequest.getStatusCode()))
          .build();
    } else {
      Response.Status internalServerError = Response.Status.INTERNAL_SERVER_ERROR;
      ErrorResponse randomisationError =
          new ErrorResponse(
              "There was an error in the randomisation process",
              "Try again",
              internalServerError.getStatusCode());
      try {
        Set<Long> newFeaturedVideos = randomiseFeaturedTask.randomiseFeaturedVideosBean(limit);
        if (newFeaturedVideos.isEmpty()) {
          return Response.status(internalServerError).entity(randomisationError).build();
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
        postDataControllerLog.log(Level.SEVERE, "Exception caught: ", anyEx);
        logStepOut(
            postDataControllerLog,
            anyEx,
            anyEx.getMessage(),
            anyEx.getCause(),
            "Status " + internalServerError.getStatusCode());
        return Response.status(internalServerError).entity(randomisationError).build();
      }
    }
  }

  /**
   * Endpoint to link a term to a post. This method accepts a {@code postId}, a {@code ClientTerm}
   * payload, and an optional {@code link} parameter. If the {@code link} parameter is true, the
   * method will attempt to link the term to the post provided as a path parameter.
   *
   * <p>If the {@code link} parameter is false, the method will search for the term provided in the
   * payload. If the term is found, it will be returned as a response. If the term name and slug are
   * located and linked to different term IDs, the method will return a response with a conflict
   * status {@code 409}.
   *
   * <p>Enabling {@code link} will resolve any conflicts automatically and create the term if it
   * does not exist or if it seems unique.
   *
   * @param postId the ID of the post to link the term to
   * @param link a boolean indicating whether to auto-link the term to the post
   * @param clientTerm the term to link to the post
   * @return a Response object containing the result of the link operation and the term ID
   */
  @POST
  @Path("taxonomies")
  @RolesAllowed(value = {"user"})
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response checkTermTaxonomy(
      @QueryParam("Id") long postId,
      @QueryParam("link") @DefaultValue("false") boolean link,
      @QueryParam("unlink") @DefaultValue("false") boolean unlink,
      ClientTerm clientTerm) {
    Response.StatusType internalServerError = Response.Status.INTERNAL_SERVER_ERROR;
    Response.StatusType conflict = Response.Status.CONFLICT;
    try {
      logStepIn(postDataControllerLog, postId, link, unlink, clientTerm);
      if (postId <= 0 && link) {
        Response.StatusType badRequest = Response.Status.BAD_REQUEST;
        ErrorResponse invalidpostId =
            new ErrorResponse(
                "Invalid Post ID or Payload",
                "Provide a valid details and try again",
                badRequest.getStatusCode());
        logStepOut(postDataControllerLog, invalidpostId, badRequest);
        return Response.status(badRequest).entity(invalidpostId).build();
      } else if (postId > 0 && link) {
        Optional<ClientTaxonomy> createdRelationship =
            taxonomyService.createTermRelationship(clientTerm, postId);
        if (createdRelationship.isPresent()) {
          Optional<ClientTerm> fromTermTaxonomyId =
              taxonomyService.getClientTermByTaxonomyId(
                  createdRelationship.get().getTermTaxonomyId());
          if (fromTermTaxonomyId.isPresent()) {
            logStepOut(postDataControllerLog, createdRelationship.get());
            return Response.ok(
                    new ServerResponse(
                        "Post ID: "
                            + postId
                            + " has been linked to taxonomy type: "
                            + createdRelationship.get().getTaxonomyName()
                            + " with ID: "
                            + createdRelationship.get().getTermTaxonomyId()
                            + " with name: "
                            + fromTermTaxonomyId.get().getName()
                            + " and slug: "
                            + fromTermTaxonomyId.get().getSlug(),
                        Response.Status.OK.getStatusCode()),
                    MediaType.APPLICATION_JSON_TYPE)
                .build();
          }
        } else {
          ErrorResponse noRelationshipCreated =
              new ErrorResponse(
                  "No relationship modified. The term/slug combination is likely not unique",
                  "Please try again with a different term/slug",
                  conflict.getStatusCode());
          logStepOut(postDataControllerLog, noRelationshipCreated, conflict);
          return Response.status(conflict).entity(noRelationshipCreated).build();
        }
      } else if (unlink) {
        boolean isRemoved = taxonomyService.removeTermRelationship(postId, clientTerm);
        if (isRemoved) {
          /*
           termExists should not throw NoSuchElementException since the term indeed exists,
           thus the request reached this far, else the exception is caught by this method and
           the response will be 500. This is rare, but possible.
          */
          ClientTerm termExists = taxonomyService.termExists(clientTerm.getName()).orElseThrow();
          logStepOut(postDataControllerLog, "Post relationship removed");
          return Response.ok(
                  new ServerResponse(
                      "Removed relationship for post "
                          + postId
                          + " and term: "
                          + termExists.getName()
                          + " with slug: "
                          + termExists.getSlug(),
                      Response.Status.OK.getStatusCode()),
                  MediaType.APPLICATION_JSON_TYPE)
              .build();
        }
      } else {
        Optional<ClientTerm> termExists = taxonomyService.termExists(clientTerm.getName());
        Optional<ClientTerm> termSlugExists = taxonomyService.termSlugExists(clientTerm.getSlug());
        if (termExists.isPresent() && termSlugExists.isPresent()) {
          if (termExists.get().equals(termSlugExists.get())) {
            return Response.ok(termExists.get(), MediaType.APPLICATION_JSON_TYPE).build();
          } else {
            ServerResponse differingTerms =
                new ServerResponse(
                    "Found terms distinct terms: "
                        + termExists.get().getName()
                        + " and "
                        + termExists.get().getSlug()
                        + " vs "
                        + termSlugExists.get().getName()
                        + " and "
                        + termSlugExists.get().getSlug(),
                    conflict.getStatusCode());
            logStepOut(postDataControllerLog, differingTerms, conflict);
            return Response.status(conflict).entity(differingTerms).build();
          }
        }
      }
    } catch (Exception e) {
      logStepOut(postDataControllerLog, e, e.getCause(), e.getMessage(), e.getStackTrace());
      ErrorResponse serverError =
          new ErrorResponse(
              "An error has occurred while processing this request",
              "Please review your request and try again",
              internalServerError.getStatusCode());
      return Response.status(internalServerError).entity(serverError).build();
    }
    logStepOut(postDataControllerLog, internalServerError);
    return Response.status(internalServerError)
        .entity(
            new ErrorResponse(
                "No term found nor processed in this request",
                "Please try again",
                internalServerError.getStatusCode()))
        .build();
  }

  @DELETE
  @Path("taxonomies/remove")
  @RolesAllowed(value = {"user"})
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response removeTermTaxonomy(ClientTerm clientTerm) {
    try {
      Optional<ClientTerm> removedTerm = taxonomyService.removeTermTaxonomy(clientTerm);
      if (removedTerm.isPresent()) {
        logStepOut(postDataControllerLog, removedTerm.get().toString());
        ServerResult removedTermResponse =
            new ServerResult(
                List.of(removedTerm.get()),
                "Term removed from the database successfully",
                Response.Status.OK.getStatusCode());
        return Response.ok(removedTermResponse, MediaType.APPLICATION_JSON_TYPE).build();
      } else {
        Response.StatusType notFound = Response.Status.NOT_FOUND;
        logStepOut(postDataControllerLog, clientTerm.toString(), notFound);
        return Response.status(notFound)
            .entity(
                new ErrorResponse("Term not found", "Please try again", notFound.getStatusCode()))
            .build();
      }
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity(
              new ErrorResponse(
                  "An error has occurred while processing this request",
                  "Please review your request and try again",
                  Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
          .build();
    }
  }
}
