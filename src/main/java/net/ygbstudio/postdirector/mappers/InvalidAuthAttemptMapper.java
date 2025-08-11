package net.ygbstudio.postdirector.mappers;

import jakarta.ws.rs.core.MediaType;
// Jakarta Imports
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

// Local Imports
import net.ygbstudio.postdirector.exceptions.InvalidAuthAttempt;
import net.ygbstudio.postdirector.dto.ErrorResponse;

/**
 * Exception mapper for handling InvalidAuthAttempt exceptions.
 * This class converts InvalidAuthAttempt exceptions into HTTP responses
 * with a 401 Unauthorized status code and an error message.
 * 
 * @author Yoham Gabriel @ YGB Studio
 */
@Provider
public class InvalidAuthAttemptMapper implements ExceptionMapper<InvalidAuthAttempt> {
	
	
	/**
	 * Converts an InvalidAuthAttempt exception into a Response object.
	 * 
	 * @param ex | the InvalidAuthAttempt exception to be mapped
	 * @return a | Response object with a 401 Unauthorized status and an error message
	 */
	@Override
	public Response toResponse(InvalidAuthAttempt ex) {
		ErrorResponse invalidAuthAttempt = new ErrorResponse("Auth Attempt unsuccesful. Please try again later.",
				ex.getMessage(), Response.Status.UNAUTHORIZED.getStatusCode());
		return Response.status(Response.Status.UNAUTHORIZED)
				.entity(invalidAuthAttempt)
				.type(MediaType.APPLICATION_JSON)
				.build();
	}

}
