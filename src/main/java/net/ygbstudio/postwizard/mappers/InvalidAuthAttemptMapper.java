package net.ygbstudio.postwizard.mappers;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import net.ygbstudio.postwizard.dto.ErrorResponse;
import net.ygbstudio.postwizard.exceptions.InvalidAuthAttempt;

/**
 * Exception mapper for handling InvalidAuthAttempt exceptions. This class converts
 * InvalidAuthAttempt exceptions into HTTP responses with a 401 Unauthorized status code and an
 * error message.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@Provider
public class InvalidAuthAttemptMapper implements ExceptionMapper<InvalidAuthAttempt> {

  /**
   * Converts an InvalidAuthAttempt exception into a Response object.
   *
   * @param ex the InvalidAuthAttempt exception to be mapped
   * @return a Response object with a 401 Unauthorized status and an error message
   */
  @Override
  public Response toResponse(InvalidAuthAttempt ex) {
    ErrorResponse invalidAuthAttempt =
        new ErrorResponse(
            "Auth Attempt unsuccesful. Please try again later.",
            ex.getMessage(),
            Response.Status.UNAUTHORIZED.getStatusCode());
    return Response.status(Response.Status.UNAUTHORIZED)
        .entity(invalidAuthAttempt)
        .type(MediaType.APPLICATION_JSON)
        .build();
  }
}
