package net.ygbstudio.postdirector.auth;

// Java Imports
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.enterprise.credential.Credential;
//Jakarta Imports
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;
import net.ygbstudio.postdirector.exceptions.InvalidAuthAttempt;
// Local Imports
import net.ygbstudio.postdirector.utils.Helpers;

@ApplicationScoped
public class PDIdentityStore implements IdentityStore {

	private Logger identityLogger = Logger.getLogger("identityLogger");

	@PostConstruct
	public void init() {
		identityLogger.warning("CDI -> IdentityStore Loaded");
	}

	public Collection<Map.Entry<Object, Object>> getAuthContextProperties()
			throws FileNotFoundException, RuntimeException {

		return Helpers.getPropertiesFromResources("ApplicationProperties.properties")
				.entrySet();
	}
	
	@Override
	public CredentialValidationResult validate(Credential userNamePassword) {
		
		if (!(userNamePassword instanceof UsernamePasswordCredential)) {
	        return CredentialValidationResult.NOT_VALIDATED_RESULT;
	    }
		
		try {
			Collection<Map.Entry<Object, Object>> contextProperties = getAuthContextProperties();
			identityLogger.info("Loaded properties: " + contextProperties);
			Optional<String> apiUser = contextProperties.stream()
					.filter(e -> e.getKey().equals("api.username"))
					.map(e -> e.getValue()
							.toString())
					.findFirst();

			Optional<String> apiKey = contextProperties.stream()
					.filter(e -> e.getKey().equals("api.key"))
					.map(e -> e.getValue()
							.toString())
					.findFirst();

			if (apiUser.isEmpty() || apiKey.isEmpty()) {
				throw new InvalidAuthAttempt("The provided username and API Key are not valid. Try again later");
			}

			identityLogger.warning("API Credentials are not empty.");
			identityLogger.warning("Passed in credentials: " +  ((UsernamePasswordCredential) userNamePassword).compareTo(apiUser.get(), apiKey.get()));
			if (((UsernamePasswordCredential) userNamePassword).compareTo(apiUser.get(), apiKey.get())) {
				return new CredentialValidationResult(apiUser.get(), Set.of("user", "caller"));
			}

		} catch (Exception anyEx) {
			identityLogger.warning("Error validating credentials: " + anyEx.getMessage());
			return CredentialValidationResult.NOT_VALIDATED_RESULT;
		}
		identityLogger.warning("Returning Invalid Result");
		return CredentialValidationResult.INVALID_RESULT;
	}

}
