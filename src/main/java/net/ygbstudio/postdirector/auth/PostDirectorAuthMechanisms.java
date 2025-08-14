package net.ygbstudio.postdirector.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.security.enterprise.credential.Password;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStoreHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import net.ygbstudio.postdirector.utils.Logging;

/**
 * Custom authentication mechanism for the PostDirector application. This class handles both Basic
 * and JWT authentication methods.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@ApplicationScoped
public class PostDirectorAuthMechanisms implements HttpAuthenticationMechanism {

  private static final Logger authMechanismsLogging =
      Logger.getLogger(PostDirectorAuthMechanisms.class.getName());

  @SuppressWarnings("unused")
  private static final FileHandler logFileHandler =
      Logging.LoggingInit(authMechanismsLogging, Level.ALL, true);

  @Inject private IdentityStoreHandler identityStoreHandler;

  @Context private HttpServletRequest request;

  @PostConstruct
  public void init() {
    authMechanismsLogging.fine("CDI -> PostDirectorAuthMechanisms Loaded");
  }

  @Override
  public AuthenticationStatus validateRequest(
      HttpServletRequest request, HttpServletResponse response, HttpMessageContext context)
      throws AuthenticationException {
    String userIP = request.getRemoteAddr();
    String currentURI = request.getRequestURI();
    String authHeaders = request.getHeader(HttpHeaders.AUTHORIZATION);

    authMechanismsLogging.entering(
        "**** Hit auth mechanism at method with IP " + userIP + " ****",
        currentURI,
        new Object[] {authHeaders, context});

    if (currentURI.contains("/auth/login")) {
      if (authHeaders != null && authHeaders.startsWith("Basic ")) {
        return handleBasicAuth(authHeaders, context);
      }

      return context.doNothing();
    }

    if (authHeaders != null && authHeaders.startsWith("Bearer ")) {
      return handleJwtAuth(authHeaders, context);
    }

    authMechanismsLogging.warning(
        "No valid authentication headers found in request: " + currentURI);
    return context.responseUnauthorized();
  }

  /**
   * Handles Basic Authentication by validating the provided credentials.
   *
   * @param authHeaders The Authorization header containing the Basic credentials.
   * @param context The HttpMessageContext for the current request.
   * @return The AuthenticationStatus indicating the result of the authentication.
   */
  private AuthenticationStatus handleBasicAuth(String authHeaders, HttpMessageContext context) {

    authMechanismsLogging.entering(
        "**** Hit Basic Auth mechanism at method ****", authHeaders, new Object[] {context});

    String base64EncryptedCreds = authHeaders.substring("Basic ".length());
    String base64UnencryptCreds =
        new String(Base64.getDecoder().decode(base64EncryptedCreds), StandardCharsets.UTF_8);
    String[] authValues = base64UnencryptCreds.split(":", 2);

    if (authValues.length != 2) {
      return context.responseUnauthorized();
    }

    UsernamePasswordCredential authCredential =
        new UsernamePasswordCredential(authValues[0], new Password(authValues[1]));

    CredentialValidationResult result = identityStoreHandler.validate(authCredential);

    if (result.getStatus() == CredentialValidationResult.Status.VALID) {
      return context.notifyContainerAboutLogin(result);
    }

    authMechanismsLogging.warning(
        "Invalid Basic Authentication credentials provided: " + authValues[0]);
    return context.responseUnauthorized();
  }

  /**
   * Handles JWT Authentication by validating the provided JWT token. This method extracts the JWT
   * from the 'Authorization' header, parses it, and retrieves the user and roles from the claims.
   *
   * @param authHeaders
   * @param context
   * @return The AuthenticationStatus indicating the result of the authentication.
   */
  private AuthenticationStatus handleJwtAuth(String authHeaders, HttpMessageContext context) {

    authMechanismsLogging.entering(
        " ****Hit JWT Auth mechanism at method" + "****", authHeaders, new Object[] {context});

    String jwt = authHeaders.substring("Bearer ".length());
    String jwtKey = System.getenv("JWT_KEY");

    try {
      Claims jwtClaims =
          Jwts.parser()
              .verifyWith(Keys.hmacShaKeyFor(jwtKey.getBytes(StandardCharsets.UTF_8)))
              .build()
              .parseSignedClaims(jwt)
              .getPayload();

      String user = jwtClaims.getSubject();
      Set<String> roles =
          Optional.ofNullable((List<?>) jwtClaims.get("roles")).orElse(List.of()).stream()
              .map(Object::toString)
              .collect(Collectors.toUnmodifiableSet());

      return context.notifyContainerAboutLogin(user, roles);

    } catch (JwtException jwtEx) {
      Throwable e = jwtEx;
      authMechanismsLogging.throwing(this.getClass().getName(), "handleJwtAuth", e);
      return context.responseUnauthorized();
    }
  }
}
