package net.ygbstudio.postwizard.mappers;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import net.ygbstudio.postwizard.dto.ErrorResponse;
import net.ygbstudio.postwizard.exceptions.UnknownAPICredentials;

/**
 * Exception mapper for handling UnknownAPICredentials exceptions. This class converts
 * UnknownAPICredentials exceptions into HTTP responses with a 401 Unauthorized status code and an
 * error message.
 *
 * @author Yoham Gabriel B @ YGB Studio
 */
@Provider
public class UnknownAPICredentialsMapper implements ExceptionMapper<UnknownAPICredentials> {

  /**
   * Converts an UnknownAPICredentials exception into a Response object.
   *
   * @param ex the UnknownAPICredentials exception to be mapped
   * @return a Response object with a 401 Unauthorized status and an error message
   */
  @Override
  public Response toResponse(UnknownAPICredentials ex) {

    ErrorResponse authError =
        new ErrorResponse(
            "No API Credentials were found or this request is unauthorized",
            ex.getMessage(),
            Response.Status.UNAUTHORIZED.getStatusCode());

    return Response.status(Response.Status.UNAUTHORIZED)
        .entity(authError)
        .type(MediaType.APPLICATION_JSON)
        .build();
  }
}
