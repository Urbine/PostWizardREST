package net.ygbstudio.postdirector.filters;

import java.io.IOException;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

// Local imports
import net.ygbstudio.postdirector.dto.ErrorResponse;

@Provider
public class UnauthorizedResponseFilter implements ContainerResponseFilter {

	@Override
	public void filter(ContainerRequestContext reqContext, ContainerResponseContext resContext) throws IOException {
		int unauthorizedStatusCode  = Response.Status.UNAUTHORIZED.getStatusCode();
		
		ErrorResponse authException = new ErrorResponse("Authentication failed",
				"Invalid Credentials", unauthorizedStatusCode);

		if (resContext.getStatus() == unauthorizedStatusCode)
			resContext.setEntity(authException.toString());
	}

}
