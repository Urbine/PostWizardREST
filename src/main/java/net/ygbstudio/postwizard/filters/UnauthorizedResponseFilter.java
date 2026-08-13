/*
 * SPDX-License-Identifier: MPL-2.0
 * Copyright (c) Yoham Gabriel B.
 */

package net.ygbstudio.postwizard.filters;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import net.ygbstudio.postwizard.dto.ErrorResponse;

/**
 * Filter to handle unauthorized responses in the postwizard application. This filter intercepts
 * responses with a 401 Unauthorized status code and modifies the response entity to include an
 * error message.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@Provider
public class UnauthorizedResponseFilter implements ContainerResponseFilter {

  /**
   * Filters the response to modify unauthorized responses.
   *
   * @param reqContext The request context.
   * @param resContext The response context.
   * @throws IOException If an I/O error occurs.
   */
  @Override
  public void filter(ContainerRequestContext reqContext, ContainerResponseContext resContext) {
    int unauthorizedStatusCode = Response.Status.UNAUTHORIZED.getStatusCode();

    ErrorResponse authException =
        new ErrorResponse("Authentication failed", "Invalid Credentials", unauthorizedStatusCode);

    if (resContext.getStatus() == unauthorizedStatusCode) {
      resContext.setEntity(authException);
      resContext.getHeaders().add("Content-Type", "application/json");
    }
  }
}
