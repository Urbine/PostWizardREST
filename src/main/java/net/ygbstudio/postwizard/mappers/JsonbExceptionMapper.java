/*
 * SPDX-License-Identifier: MPL-2.0
 * Copyright (c) Yoham Gabriel B.
 */

package net.ygbstudio.postwizard.mappers;

import jakarta.json.bind.JsonbException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import net.ygbstudio.postwizard.dto.ErrorResponse;

/**
 * Exception mapper for handling JsonbException exceptions. This class converts JsonbException
 * exceptions into HTTP responses with a 400 Bad Request status code and an error message.
 *
 * @author Yoham Gabriel B @ YGB Studio
 */
@Provider
public class JsonbExceptionMapper implements ExceptionMapper<JsonbException> {
  @Override
  public Response toResponse(JsonbException exception) {
    Response.StatusType badRequest = Response.Status.BAD_REQUEST;
    ErrorResponse badClientSchema =
        new ErrorResponse(
            "Unable to parse your request schema.",
            "Review your request and try again",
            badRequest.getStatusCode());
    return Response.status(badRequest).entity(badClientSchema).build();
  }
}
