package net.ygbstudio.postdirector.rest;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
// Local imports
import net.ygbstudio.postdirector.dto.GrantToken;

@RequestScoped
@Path("auth")
public class PostDirectorAuth {

	@Inject
	private SecurityContext securityContext;
	
	private static final byte[] SECRET_KEY = "change_this_to_a_secret_key_at_least_32_chars!".getBytes(StandardCharsets.UTF_8);
	
	@GET
	@Path("login")
	@RolesAllowed("user")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAppToken() {
		String username = securityContext.getCallerPrincipal().getName();
		SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY);
		Date issuanceDate = Date.from(Instant.now());
		Date expirationDate = Date.from(Instant.now().plus(1, ChronoUnit.HOURS));

		String jwt = Jwts.builder()
				.subject(username)
				.claim("roles", securityContext.isCallerInRole("user") ? List.of("user") : List.of("caller"))
				.issuedAt(issuanceDate)
				.expiration(expirationDate)
				.signWith(key, Jwts.SIG.HS256)
				.compact();
		
		return Response.ok(new GrantToken(Response.Status.OK.getStatusCode(), jwt, issuanceDate, expirationDate))
				.type(MediaType.APPLICATION_JSON)
				.build();
	}

}
