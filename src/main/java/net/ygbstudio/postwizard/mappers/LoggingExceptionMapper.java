package net.ygbstudio.postwizard.mappers;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import net.ygbstudio.postwizard.dto.ErrorResponse;
import net.ygbstudio.postwizard.exceptions.LoggingException;

/**
 * Exception mapper for handling LoggingException in the postwizard application. This class converts
 * LoggingException into a standardized HTTP response.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@Provider
public class LoggingExceptionMapper implements ExceptionMapper<LoggingException> {

  /**
   * Converts a LoggingException into a Response object.
   *
   * @param logEx the LoggingException to be mapped
   * @return a Response object with a 500 Internal Server Error status and an error message
   */
  @Override
  public Response toResponse(LoggingException logEx) {
    ErrorResponse errorObj =
        new ErrorResponse(
            "An internal server error occurred. Please try again later.",
            logEx.getMessage(),
            Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());

    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorObj).build();
  }
}
