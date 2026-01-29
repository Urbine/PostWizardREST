/*
 * SPDX-License-Identifier: MPL-2.0
 * Copyright (c) Yoham Gabriel B.
 */

package net.ygbstudio.postwizard.auth;

import static net.ygbstudio.postwizard.utils.Helpers.getPropertiesFromResources;
import static net.ygbstudio.postwizard.utils.Logging.loggingInit;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.ygbstudio.postwizard.exceptions.InvalidAuthAttempt;
import org.jspecify.annotations.Nullable;

/**
 * IdentityStore implementation for validating user credentials against authentication context
 * properties. This class retrieves authentication properties from a resources file and validates
 * the provided username and password credentials.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@ApplicationScoped
public class PWIdentityStore implements IdentityStore {

  private static final Logger identityLogger = Logger.getLogger(PWIdentityStore.class.getName());

  @SuppressWarnings("unused")
  @Nullable
  private static final FileHandler logFileHandler = loggingInit(identityLogger, Level.FINE, true);

  @PostConstruct
  public void init() {
    identityLogger.fine("CDI -> IdentityStore Loaded");
    if (logFileHandler == null) {
      identityLogger.setLevel(Level.ALL);
      identityLogger.severe(
          "Failed to initialize log file handler -> Make sure the server user has write permissions to the log directory");
    }
  }

  /**
   * Helper method that retrieves authentication context properties from the resources file.
   *
   * @return a collection of key-value pairs representing the authentication context properties or
   *     an empty set if the properties file is not found.
   */
  private Collection<Map.Entry<Object, Object>> getAuthContextProperties() {
    Optional<Properties> optionalProperties =
        getPropertiesFromResources("ApplicationProperties.properties");
    return optionalProperties
        .<Collection<Map.Entry<Object, Object>>>map(Properties::entrySet)
        .orElse(Collections.emptySet());
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

      if (((UsernamePasswordCredential) userNamePassword).compareTo(apiUser.get(), apiKey.get()))
        return new CredentialValidationResult(apiUser.get(), Set.of("user", "caller"));

    } catch (Exception anyEx) {
      identityLogger.warning(
          "Error validating credentials from application properties: " + anyEx.getMessage());
      try {
        identityLogger.warning("Falling back to environment variables for authentication.");
        String apiUser = System.getenv("PW_API_USER");
        String apiKey = System.getenv("PW_API_KEY");

        if (((UsernamePasswordCredential) userNamePassword).compareTo(apiUser, apiKey))
          return new CredentialValidationResult(apiUser, Set.of("user", "caller"));
      } catch (Exception envEx) {
        identityLogger.fine("Error getting env variables: " + envEx.getMessage());
        identityLogger.fine("Error validating credentials: " + anyEx.getMessage());
        return CredentialValidationResult.NOT_VALIDATED_RESULT;
      }
    }
    identityLogger.fine("Returning Invalid Result");
    return CredentialValidationResult.INVALID_RESULT;
  }
}
