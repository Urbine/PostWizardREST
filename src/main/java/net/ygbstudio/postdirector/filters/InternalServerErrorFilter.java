package net.ygbstudio.postdirector.filters;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import net.ygbstudio.postdirector.dto.ErrorResponse;

/**
 * Filter to handle internal server errors in the PostDirector application. This filter intercepts
 * responses with a 500 Internal Server Error status code and modifies the response entity to
 * include an error message.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
public class InternalServerErrorFilter implements ContainerResponseFilter {

  @Override
  public void filter(
      ContainerRequestContext requestContext, ContainerResponseContext responseContext)
      throws IOException {
    int internalServerErrorStatus = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
    ErrorResponse internalServerError =
        new ErrorResponse(
            "An error has occurred while fulfilling your request",
            "Internal Server Error",
            Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    if (responseContext.getStatus() == internalServerErrorStatus) {
      responseContext.setEntity(internalServerError);
      responseContext.getHeaders().add("Content-Type", "application/json");
    }
  }
}
