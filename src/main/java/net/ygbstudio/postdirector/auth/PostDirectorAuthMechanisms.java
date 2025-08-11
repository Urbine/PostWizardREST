package net.ygbstudio.postdirector.auth;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
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
import jakarta.ws.rs.core.HttpHeaders;
import net.ygbstudio.postdirector.utils.Helpers;

@ApplicationScoped
public class PostDirectorAuthMechanisms implements HttpAuthenticationMechanism {

	@Inject
	private IdentityStoreHandler identityStoreHandler;

	@Override
	public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response,
			HttpMessageContext context) throws AuthenticationException {
		
		String currentURI = request.getRequestURI();
        String authHeaders = request.getHeader(HttpHeaders.AUTHORIZATION);

        Logger postDirectorAuthMechanisms = Logger.getLogger("validateRequest");
        postDirectorAuthMechanisms.warning(" ****Hit auth mechanism at" + currentURI + "****");
        
        if (currentURI.contains("/auth/login")) {
            if (authHeaders != null && authHeaders.startsWith("Basic ")) {
                return handleBasicAuth(authHeaders, context);
            }
           
            return context.doNothing();
        }

        if (authHeaders != null && authHeaders.startsWith("Bearer ")) {
            return handleJwtAuth(authHeaders, context);
        }

        return context.responseUnauthorized();
	}
	
	private AuthenticationStatus handleBasicAuth(String authHeaders, HttpMessageContext context) {
		
		Logger postDirectorAuthMechanisms = Logger.getLogger("handleBasicAuth");
        postDirectorAuthMechanisms.warning(" ****Hit Basic Auth mechanism at method"  + "****");
		
		String base64EncryptedCreds = authHeaders.substring("Basic ".length());
        String base64UnencryptCreds = new String(Base64.getDecoder().decode(base64EncryptedCreds),
                StandardCharsets.UTF_8);
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
        return context.responseUnauthorized();
    }

    private AuthenticationStatus handleJwtAuth(String authHeaders, HttpMessageContext context) {
        String jwt = authHeaders.substring("Bearer ".length());
        String jwtKey = Helpers.getPropertiesFromResources("ApplicationProperties.properties")
        		.getProperty("api.jwtKey");

        try {
            Claims jwtClaims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(jwtKey.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();

            String user = jwtClaims.getSubject();
            Set<String> roles = Optional.ofNullable((List<?>) jwtClaims.get("roles"))
                    .orElse(List.of())
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.toUnmodifiableSet());

            return context.notifyContainerAboutLogin(user, roles);

        } catch (JwtException e) {
            return context.responseUnauthorized();
        }
    }

}
