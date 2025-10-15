package net.ygbstudio.postwizard.rest;

import static net.ygbstudio.postwizard.rest.ResponseHandlers.handleBadRequest;
import static net.ygbstudio.postwizard.rest.ResponseHandlers.handleConflict;
import static net.ygbstudio.postwizard.rest.ResponseHandlers.handleException;
import static net.ygbstudio.postwizard.rest.ResponseHandlers.handleNotFound;
import static net.ygbstudio.postwizard.utils.Logging.logStepIn;
import static net.ygbstudio.postwizard.utils.Logging.logStepOut;
import static net.ygbstudio.postwizard.utils.Logging.loggingInit;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.ygbstudio.postwizard.dto.ClientDeliverable;
import net.ygbstudio.postwizard.dto.ClientTaxonomy;
import net.ygbstudio.postwizard.dto.ClientTerm;
import net.ygbstudio.postwizard.dto.ServerResult;
import net.ygbstudio.postwizard.entities.taxonomies.WPTerms;
import net.ygbstudio.postwizard.service.TaxonomyService;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
@Path("taxonomies")
public class TaxonomyController {

  public static final Logger taxonomyControllerLog =
      Logger.getLogger(TaxonomyController.class.getName());

  @Nullable
  public static final FileHandler taxonomyControllerFileHandler =
      loggingInit(taxonomyControllerLog, Level.ALL, true);

  @Inject private TaxonomyService taxonomyService;

  /**
   * Endpoint to link a term to a post. This method accepts a {@code postId}, a {@code ClientTerm}
   * payload, and an optional {@code link} parameter. If the {@code link} parameter is true, the
   * method will attempt to link the term to the post provided as a path parameter. The same is true
   * if no post id is provided, the method tries to locate an existing term and creates additional
   * ones if needed.
   *
   * <p>If aterm name and slug are located and linked to different term IDs, the method will return
   * a response with a conflict status {@code 409}.
   *
   * @param postId the ID of the post to link the term to
   * @param link a boolean indicating whether to auto-link the term to the post
   * @param clientTerm the term to link to the post
   * @return a Response object containing the result of the link operation and the term ID
   */
  @POST
  @Path("check")
  @RolesAllowed(value = {"user"})
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response checkTermTaxonomy(
      @QueryParam("id") long postId,
      @QueryParam("link") @DefaultValue("false") boolean link,
      @QueryParam("unlink") @DefaultValue("false") boolean unlink,
      ClientTerm clientTerm) {
    try {
      logStepIn(taxonomyControllerLog, postId, link, unlink, clientTerm);
      if (postId <= 0 && link) {
        return handleBadRequest(() -> "Invalid Post ID or Payload", taxonomyControllerLog);
      } else if (postId > 0 && link) {
        Optional<ClientTaxonomy> createdRelationship =
            taxonomyService.createTermRelationship(clientTerm, postId);
        if (createdRelationship.isPresent()) {
          Optional<ClientTerm> fromTermTaxonomyId =
              taxonomyService.getClientTermByTaxonomyId(
                  createdRelationship.get().getTermTaxonomyId());
          if (fromTermTaxonomyId.isPresent()) {
            Response.StatusType created = Response.Status.CREATED;
            logStepOut(taxonomyControllerLog, createdRelationship.get(), created.getStatusCode());
            return Response.status(created)
                .entity(
                    new ServerResult(
                        () -> "Created term relationship for" + postId,
                        List.of(createdRelationship.get(), fromTermTaxonomyId.get()),
                        created.getStatusCode()))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .build();
          }
        } else {
          return handleConflict(() -> "No relationship modified", taxonomyControllerLog);
        }
      } else if (postId > 0 && unlink) {
        boolean isRemoved = taxonomyService.removeTermRelationship(postId, clientTerm);
        if (isRemoved) {
          /*
           termExists should not throw NoSuchElementException since the term indeed exists,
           thus the request reached this far, else the exception is caught by this method and
           the response will be 500. This is rare, but possible.
          */
          ClientTerm termExists = taxonomyService.termExists(clientTerm.getName()).orElseThrow();
          logStepOut(taxonomyControllerLog, "Post relationship removed");
          return Response.ok(
                  new ServerResult(
                      () -> "Removed relationship for post " + postId,
                      List.of(termExists),
                      Response.Status.OK.getStatusCode()),
                  MediaType.APPLICATION_JSON_TYPE)
              .build();
        }
      }
    } catch (Exception e) {
      return handleException(taxonomyControllerLog, e);
    }
    return handleNotFound(taxonomyControllerLog);
  }

  @POST
  @Path("add")
  @RolesAllowed(value = {"user"})
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response addTaxonomyTerm(ClientTerm clientTerm) {
    try {
      if (clientTerm.getName() != null && !clientTerm.getName().isBlank()) {
        if (clientTerm.getTaxonomy() == null
            || clientTerm.getTaxonomy().getTaxonomyName() == null) {
          return handleBadRequest(
              () -> "A Taxonomy name is required for term creation", taxonomyControllerLog);
        } else {
          Optional<WPTerms> termNameAndSlugExists =
              taxonomyService.eitherTermNameOrSlugExists(clientTerm, true);
          if (termNameAndSlugExists.isPresent()) {
            Optional<ClientTerm> termExists =
                taxonomyService.convertToClientTerm(termNameAndSlugExists);
            return Response.ok(
                    new ServerResult(
                        () -> "Found match for term: " + clientTerm.getName(),
                        termExists
                            .<List<ClientDeliverable>>map(List::of)
                            .orElse(Collections.emptyList()),
                        Response.Status.OK.getStatusCode()),
                    MediaType.APPLICATION_JSON_TYPE)
                .build();
          } else {
            Optional<ClientTaxonomy> taxonomyTermCreate = taxonomyService.addTerm(clientTerm);
            if (taxonomyTermCreate.isPresent()) {
              Optional<WPTerms> newClientTerm =
                  taxonomyService.eitherTermNameOrSlugExists(clientTerm, true);
              if (newClientTerm.isPresent()) {
                Response.StatusType created = Response.Status.CREATED;
                logStepOut(
                    taxonomyControllerLog, newClientTerm.get().toString(), created.getStatusCode());
                return Response.status(created)
                    .entity(
                        new ServerResult(
                            () ->
                                "Term: "
                                    + clientTerm.getName()
                                    + " added to the database successfully",
                            List.of(
                                taxonomyService.convertToClientTerm(newClientTerm).orElseThrow()),
                            created.getStatusCode()))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .build();
              }
            }
          }
        }
      }
    } catch (Exception e) {
      return handleException(taxonomyControllerLog, e);
    }
    return handleNotFound(taxonomyControllerLog);
  }

  @DELETE
  @Path("remove")
  @RolesAllowed(value = {"user"})
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response removeTermTaxonomy(ClientTerm clientTerm) {
    try {
      Optional<ClientTerm> removedTerm = taxonomyService.removeTermTaxonomy(clientTerm);
      if (removedTerm.isPresent()) {
        logStepOut(taxonomyControllerLog, removedTerm.get().toString());
        ServerResult removedTermResponse =
            new ServerResult(
                () -> "Term removed from the database successfully",
                List.of(removedTerm.get()),
                Response.Status.OK.getStatusCode());
        return Response.ok(removedTermResponse, MediaType.APPLICATION_JSON_TYPE).build();
      } else {
        return handleNotFound(() -> "Term not found", taxonomyControllerLog);
      }
    } catch (Exception e) {
      return handleException(taxonomyControllerLog, e);
    }
  }
}
