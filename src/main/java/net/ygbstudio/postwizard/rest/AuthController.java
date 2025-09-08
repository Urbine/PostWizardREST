package net.ygbstudio.postwizard.rest;

import static net.ygbstudio.postwizard.auth.PostWizardAuthMechanisms.secretKey;

import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.ygbstudio.postwizard.dto.ErrorResponse;
import net.ygbstudio.postwizard.dto.GrantToken;
import net.ygbstudio.postwizard.utils.Logging;

/**
 * RESTful web service for handling authentication in the postwizard application. This class
 * provides an endpoint to generate a JWT token for authenticated users.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@RequestScoped
@Path("auth")
public class AuthController {

  private static final Logger postwizardAuthEPoint =
      Logger.getLogger(AuthController.class.getName());

  @SuppressWarnings("unused")
  private static final FileHandler logFileHandler =
      Logging.loggingInit(postwizardAuthEPoint, Level.ALL, true);

  @Inject private SecurityContext securityContext;

  @Context private HttpServletRequest request;

  @PostConstruct
  public void init() {
    postwizardAuthEPoint.fine("CDI -> postwizardAuth endpoint loaded.");
  }

  /**
   * Endpoint to generate a JWT token for authenticated users. This endpoint is protected and
   * requires the user to be authenticated.
   *
   * @return A Response containing the JWT token and its metadata.
   */
  @GET
  @Path("login")
  @RolesAllowed("user")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAppToken() {
    String userIP = request.getRemoteAddr();
    postwizardAuthEPoint.entering("User with IP: " + userIP, "getAppToken()");

    String username = securityContext.getCallerPrincipal().getName();
    Date issuanceDate = Date.from(Instant.now());
    Date expirationDate = Date.from(Instant.now().plus(1, ChronoUnit.HOURS));

    try {
      String jwt =
          Jwts.builder()
              .subject(username)
              .claim(
                  "roles",
                  securityContext.isCallerInRole("user") ? List.of("user") : List.of("caller"))
              .issuedAt(issuanceDate)
              .expiration(expirationDate)
              .signWith(secretKey, Jwts.SIG.HS256)
              .compact();

      return Response.ok()
          .entity(new GrantToken(jwt, "bearer", expirationDate))
          .type(MediaType.APPLICATION_JSON)
          .build();

    } catch (Exception anyEx) {
      Response.StatusType internalServerError = Response.Status.INTERNAL_SERVER_ERROR;
      return Response.status(internalServerError)
          .entity(
              new ErrorResponse(
                  "Internal Server Error", "Try again later", internalServerError.getStatusCode()))
          .build();
    }
  }
}
