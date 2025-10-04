package net.ygbstudio.postwizard.mappers;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import net.ygbstudio.postwizard.dto.ErrorResponse;

/**
 * Exception mapper for handling IllegalArgumentException exceptions. This class converts
 * IllegalArgumentException exceptions into HTTP responses with a 400 Bad Request status code and a
 * comprehensive error message.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {
  @Override
  public Response toResponse(IllegalArgumentException exception) {
    Response.StatusType badRequest = Response.Status.BAD_REQUEST;
    ErrorResponse illegalArgument =
        new ErrorResponse(
            "Invalid request format",
            "Review your request or contact your PostWizard Admin for more information",
            badRequest.getStatusCode());
    return Response.status(badRequest).entity(illegalArgument).build();
  }
}
