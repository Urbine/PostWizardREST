package net.ygbstudio.postwizard.rest;

import static net.ygbstudio.postwizard.utils.Logging.logStepOut;

import jakarta.ws.rs.core.Response;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.logging.Logger;
import net.ygbstudio.postwizard.dto.ErrorResponse;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ResponseHandlers {
  private ResponseHandlers() {
    throw new UnsupportedOperationException("Utility class -> Instantiation not allowed.");
  }

  /**
   * Helper method that handles exceptions by logging the error and returning a response with a 500
   * Internal Server Error status code.
   *
   * @param classLogger logger associated with the class where the caller is located
   * @param e the exception to handle
   * @return a response with a 500 Internal Server Error status code and an error message
   */
  public static Response handleException(Logger classLogger, Exception e) {
    logStepOut(classLogger, e, e.getCause(), e.getMessage(), Arrays.toString(e.getStackTrace()));
    ErrorResponse serverError =
        new ErrorResponse(
            "An error has occurred while processing this request",
            "Please review your request and try again",
            Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(serverError).build();
  }

  /**
   * Helper method that handles exceptions by logging the error and returning a response with a 500
   * Internal Server Error status code.
   *
   * @param errorMsgSupplier the error message supplier
   * @param classLogger logger associated with the class where the caller is located
   * @param e the exception to handle
   * @return a response with a 500 Internal Server Error status code and an error message
   */
  public static Response handleException(
      Supplier<String> errorMsgSupplier, Logger classLogger, Exception e) {
    logStepOut(classLogger, e, e.getCause(), e.getMessage(), Arrays.toString(e.getStackTrace()));
    ErrorResponse serverError =
        new ErrorResponse(
            errorMsgSupplier.get(),
            "Please review your request and try again",
            Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(serverError).build();
  }

  /**
   * Helper method that handles not found exceptions by logging the error and returning a response
   * with a 404 Not Found status code.
   *
   * @param classLogger logger associated with the class where the caller is located
   * @return a response with a 404 Not Found status code and an error message
   */
  public static Response handleNotFound(Logger classLogger) {
    Response.StatusType notFound = Response.Status.NOT_FOUND;
    logStepOut(classLogger, notFound);
    return Response.status(notFound)
        .entity(
            new ErrorResponse(
                "No item found nor processed in this request",
                "Please try again",
                notFound.getStatusCode()))
        .build();
  }

  /**
   * Helper method that handles not found exceptions by logging the error and returning a response
   * with a 404 Not Found status code.
   *
   * @param errorMsgSupplier the error message supplier
   * @param classLogger logger associated with the class where the caller is located
   * @return a response with a 404 Not Found status code and an error message
   */
  public static Response handleNotFound(Supplier<String> errorMsgSupplier, Logger classLogger) {
    Response.StatusType notFound = Response.Status.NOT_FOUND;
    logStepOut(classLogger, notFound);
    return Response.status(notFound)
        .entity(
            new ErrorResponse(errorMsgSupplier.get(), "Please try again", notFound.getStatusCode()))
        .build();
  }
}
