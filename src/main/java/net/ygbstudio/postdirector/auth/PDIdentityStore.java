package net.ygbstudio.postdirector.auth;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.ygbstudio.postdirector.exceptions.InvalidAuthAttempt;
import net.ygbstudio.postdirector.utils.Helpers;
import net.ygbstudio.postdirector.utils.Logging;

/**
 * IdentityStore implementation for validating user credentials against authentication context
 * properties. This class retrieves authentication properties from a resources file and validates
 * the provided username and password credentials.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@ApplicationScoped
public class PDIdentityStore implements IdentityStore {

  private static final Logger identityLogger = Logger.getLogger(PDIdentityStore.class.getName());

  @SuppressWarnings("unused")
  private static final FileHandler logFileHandler =
      Logging.LoggingInit(identityLogger, Level.FINE, true);

  @PostConstruct
  public void init() {
    identityLogger.fine("CDI -> IdentityStore Loaded");
  }

  /**
   * Retrieves authentication context properties from the resources file.
   *
   * @return a collection of key-value pairs representing the authentication context properties.
   * @throws FileNotFoundException if the properties file is not found.
   * @throws RuntimeException if an error occurs while reading the properties.
   */
  public Collection<Map.Entry<Object, Object>> getAuthContextProperties()
      throws FileNotFoundException, RuntimeException {

    return Helpers.getPropertiesFromResources("ApplicationProperties.properties").entrySet();
  }

  /**
   * Validates the provided username and password credentials against the authentication context
   * properties.
   *
   * @param userNamePassword the credentials to validate.
   * @return a CredentialValidationResult indicating the validation result.
   */
  @Override
  public CredentialValidationResult validate(Credential userNamePassword) {

    if (!(userNamePassword instanceof UsernamePasswordCredential)) {
      return CredentialValidationResult.NOT_VALIDATED_RESULT;
    }

    try {
      Collection<Map.Entry<Object, Object>> contextProperties = getAuthContextProperties();
      Optional<String> apiUser =
          contextProperties.stream()
              .filter(e -> e.getKey().equals("api.username"))
              .map(e -> e.getValue().toString())
              .findFirst();

      Optional<String> apiKey =
          contextProperties.stream()
              .filter(e -> e.getKey().equals("api.key"))
              .map(e -> e.getValue().toString())
              .findFirst();

      if (apiUser.isEmpty() || apiKey.isEmpty()) {
        throw new InvalidAuthAttempt(
            "The provided username and API Key are not valid. Try again later");
      }

      if (((UsernamePasswordCredential) userNamePassword).compareTo(apiUser.get(), apiKey.get())) {
        return new CredentialValidationResult(apiUser.get(), Set.of("user", "caller"));
      }

    } catch (Exception anyEx) {
      identityLogger.fine("Error validating credentials: " + anyEx.getMessage());
      return CredentialValidationResult.NOT_VALIDATED_RESULT;
    }
    identityLogger.fine("Returning Invalid Result");
    return CredentialValidationResult.INVALID_RESULT;
  }
}
