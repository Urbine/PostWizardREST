/*
 * SPDX-License-Identifier: MPL-2.0
 * Copyright (c) Yoham Gabriel B.
 */

package net.ygbstudio.postwizard.mappers;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.NoSuchElementException;
import net.ygbstudio.postwizard.dto.ErrorResponse;

/**
 * Exception mapper for handling NoSuchElementException and converting it to a standardized error
 * response. As this ExceptionMapper assumes that the client does not know which element was
 * missing, or if there was a missing object at all, the exception returns a 500 status code.
 *
 * <p>This class implements the ExceptionMapper interface to catch NoSuchElementException instances
 * thrown during request processing. It generates a Response object with an appropriate HTTP status
 * code and a JSON body containing error details.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@Provider
public class NoSuchElementExceptionMapper implements ExceptionMapper<NoSuchElementException> {

  @Override
  public Response toResponse(NoSuchElementException exception) {
    Response.Status internalServerError = Response.Status.INTERNAL_SERVER_ERROR;
    ErrorResponse serverError =
        new ErrorResponse(
            "An internal server error has occurred",
            "Try again later",
            internalServerError.getStatusCode());
    return Response.status(internalServerError).entity(serverError).build();
  }
}
