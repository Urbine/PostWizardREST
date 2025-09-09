package net.ygbstudio.postwizard.mappers;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import net.ygbstudio.postwizard.dto.ErrorResponse;
import net.ygbstudio.postwizard.exceptions.InvalidIdentifier;

/**
 * Exception mapper for handling InvalidIdentifier exceptions. This class converts InvalidIdentifier
 * exceptions into HTTP responses with a 404 Not Found status code and an error message.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@Provider
public class InvalidIdentifierMapper implements ExceptionMapper<InvalidIdentifier> {

  /**
   * Converts an InvalidIdentifier exception into a Response object.
   *
   * @param ex the InvalidIdentifier exception to be mapped
   * @return a Response object with a 404 Not Found status and an error message
   */
  @Override
  public Response toResponse(InvalidIdentifier ex) {
    ErrorResponse errorObj =
        new ErrorResponse(
            "Error - Resource Not Found",
            ex.getMessage(),
            Response.Status.NOT_FOUND.getStatusCode());

    return Response.status(Response.Status.NOT_FOUND)
        .entity(errorObj)
        .type(MediaType.APPLICATION_JSON)
        .build();
  }
}
