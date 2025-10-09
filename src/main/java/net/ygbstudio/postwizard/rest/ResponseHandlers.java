package net.ygbstudio.postwizard.rest;

import static net.ygbstudio.postwizard.utils.Logging.logStepOut;

import jakarta.ws.rs.core.Response;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.logging.Logger;
import net.ygbstudio.postwizard.dto.ErrorResponse;
import org.jspecify.annotations.NullMarked;

/**
 * Utility class for handling responses in the PostWizard application's controller layer. Provides
 * methods to handle exceptions and return responses with appropriate status codes and error
 * messages via suppliers.
 *
 * <p>The main purpose of this class is to simplify logging and controller logic and make it
 * consistent through the application.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
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
    String errorMessage = errorMsgSupplier.get();
    logStepOut(
        classLogger,
        errorMessage,
        e,
        e.getCause(),
        e.getMessage(),
        Arrays.toString(e.getStackTrace()));
    ErrorResponse serverError =
        new ErrorResponse(
            errorMessage,
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
    String errorMessage = errorMsgSupplier.get();
    Response.StatusType notFound = Response.Status.NOT_FOUND;
    logStepOut(classLogger, notFound, errorMessage);
    return Response.status(notFound)
        .entity(new ErrorResponse(errorMessage, "Please try again", notFound.getStatusCode()))
        .build();
  }

  /**
   * Helper method that handles bad request exceptions by logging the error and returning a response
   * with a 400 Bad Request status code.
   *
   * @param errorMsgSupplier the error message supplier
   * @param classLogger logger associated with the class where the caller is located
   * @return a response with a 400 Bad Request status code and an error message
   */
  public static Response handleBadRequest(Supplier<String> errorMsgSupplier, Logger classLogger) {
    String errorMessage = errorMsgSupplier.get();
    Response.StatusType badRequest = Response.Status.BAD_REQUEST;
    logStepOut(classLogger, badRequest, errorMessage);
    return Response.status(badRequest)
        .entity(
            new ErrorResponse(
                errorMessage,
                "Please review your request and try again",
                badRequest.getStatusCode()))
        .build();
  }

  /**
   * Helper method that handles server errors by logging the error and returning a response with a
   * 500 Internal Server Error status code. This method handles server errors that are not related
   * to exceptions. If you need to handle an exception in a try/catch block, use {@link
   * ResponseHandlers#handleException(Supplier, Logger, Exception)}
   *
   * @param errorMsgSupplier the error message supplier
   * @param classLogger logger associated with the class where the caller is located
   * @return a response with a 500 Internal Server Error status code and an error message
   */
  public static Response handleServerError(Supplier<String> errorMsgSupplier, Logger classLogger) {
    String errorMessage = errorMsgSupplier.get();
    Response.StatusType internalServerError = Response.Status.INTERNAL_SERVER_ERROR;
    logStepOut(classLogger, internalServerError, errorMessage);
    return Response.status(internalServerError)
        .entity(
            new ErrorResponse(errorMessage, "Try again later", internalServerError.getStatusCode()))
        .build();
  }

  /**
   * Helper method that handles conflict exceptions by logging the error and returning a response
   * with a 409 Conflict status code.
   *
   * @param errorMsgSupplier the error message supplier
   * @param classLogger logger associated with the class where the caller is located
   * @return a response with a 409 Conflict status code and an error message
   */
  public static Response handleConflict(Supplier<String> errorMsgSupplier, Logger classLogger) {
    String errorMessage = errorMsgSupplier.get();
    Response.StatusType conflict = Response.Status.CONFLICT;
    logStepOut(classLogger, conflict, errorMessage);
    return Response.status(conflict)
        .entity(new ErrorResponse(errorMessage, "Please try again", conflict.getStatusCode()))
        .build();
  }
}
