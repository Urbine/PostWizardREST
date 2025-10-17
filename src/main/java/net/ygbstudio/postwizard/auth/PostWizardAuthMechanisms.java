package net.ygbstudio.postwizard.auth;

import static net.ygbstudio.postwizard.utils.Debugging.getCallingMethod;
import static net.ygbstudio.postwizard.utils.Logging.logStepIn;
import static net.ygbstudio.postwizard.utils.Logging.logStepOut;
import static net.ygbstudio.postwizard.utils.Logging.loggingInit;
import static net.ygbstudio.postwizard.utils.Security.generateHS256Key;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
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
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Custom authentication mechanism for the PostWizard application. This class handles both Basic and
 * JWT authentication methods.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@ApplicationScoped
public class PostWizardAuthMechanisms implements HttpAuthenticationMechanism {

  private static final Logger authMechanismsLogging =
      Logger.getLogger(PostWizardAuthMechanisms.class.getName());

  @SuppressWarnings("unused")
  @Nullable
  private static final FileHandler logFileHandler =
      loggingInit(authMechanismsLogging, Level.ALL, true);

  @Nullable public static final SecretKey secretKey = initialiseSecretKey();

  @Inject private IdentityStoreHandler identityStoreHandler;

  @Context private HttpServletRequest request;

  @PostConstruct
  public void init() {
    authMechanismsLogging.fine("CDI -> postwizardAuthMechanisms Loaded");
    if (logFileHandler == null) {
      authMechanismsLogging.setLevel(Level.ALL);
      authMechanismsLogging.severe(
          "Failed to initialize log file handler -> Make sure the server user has write permissions to the log directory");

    } else if (secretKey == null) {
      authMechanismsLogging.setLevel(Level.ALL);
      authMechanismsLogging.severe(
          "Failed to initialize secret key -> Please provide a valid Secret key in the environment variable JWT_KEY and restart the application");
    }
  }

  @Override
  public AuthenticationStatus validateRequest(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull HttpMessageContext context) {
    String userIP = request.getRemoteAddr();
    String currentURI = request.getRequestURI();
    String authHeaders = request.getHeader(HttpHeaders.AUTHORIZATION);

    logStepIn(
        authMechanismsLogging,
        "**** Hit auth mechanism at method with IP " + userIP + " ****",
        currentURI,
        authHeaders,
        context);

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
        () -> "No valid authentication headers found in request: " + currentURI);
    return context.responseUnauthorized();
  }

  /**
   * Handles Basic Authentication by validating the provided credentials.
   *
   * @param authHeaders The Authorization header containing the Basic credentials.
   * @param context The HttpMessageContext for the current request.
   * @return The AuthenticationStatus indicating the result of the authentication.
   */
  private AuthenticationStatus handleBasicAuth(
      @NonNull String authHeaders, @NonNull HttpMessageContext context) {

    logStepIn(
        authMechanismsLogging,
        "**** Hit Basic Auth mechanism at method "
            + Arrays.toString(getCallingMethod(false))
            + "****",
        authHeaders,
        context);

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
        () -> "Invalid Basic Authentication credentials provided: " + authValues[0]);
    return context.responseUnauthorized();
  }

  /**
   * Handles JWT Authentication by validating the provided JWT token. This method extracts the JWT
   * from the 'Authorization' header, parses it, and retrieves the user and roles from the claims.
   *
   * @param authHeaders authorization header containing the JWT token.
   * @param context HttpMessageContext for the current request.
   * @return The AuthenticationStatus indicating the result of the authentication.
   */
  private AuthenticationStatus handleJwtAuth(String authHeaders, HttpMessageContext context) {

    logStepIn(
        authMechanismsLogging,
        "**** Hit JWT Auth mechanism at method "
            + Arrays.toString(getCallingMethod(false))
            + "****",
        authHeaders,
        context);

    String jwt = authHeaders.substring("Bearer ".length());

    try {
      Claims jwtClaims =
          Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(jwt).getPayload();

      String user = jwtClaims.getSubject();
      Set<String> roles =
          Optional.ofNullable((List<?>) jwtClaims.get("roles")).orElse(List.of()).stream()
              .map(Object::toString)
              .collect(Collectors.toUnmodifiableSet());

      return context.notifyContainerAboutLogin(user, roles);

    } catch (JwtException jwtEx) {
      logStepOut(
          authMechanismsLogging,
          "handleJwtAuth",
          jwtEx.getMessage(),
          jwtEx.getCause(),
          jwtEx.getStackTrace());
      return context.responseUnauthorized();
    }
  }

  /**
   * Initialise SecretKey from environment variable or generate a new one if not set.
   *
   * @return SecretKey (HMAC SHA-256) for JWT signing.
   */
  @Nullable
  private static SecretKey initialiseSecretKey() {
    String secretKey = System.getenv("JWT_KEY");
    if (secretKey != null) {
      return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    } else {
      try {
        return generateHS256Key();
      } catch (NoSuchAlgorithmException e) {
        authMechanismsLogging.log(Level.SEVERE, "Error generating secret key: ", e);
        return null;
      }
    }
  }
}
