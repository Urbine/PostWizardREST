package net.ygbstudio.postdirector.rest;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

/**
 * RESTful web service for the PostDirector application. This class provides a welcome endpoint that
 * returns a simple HTML message for debugging purposes.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@Path("welcome")
@RolesAllowed(value = {"user", "caller"})
public class PostDirectorRestfulMain {

  @Context private UriInfo context;

  @GET
  @Produces("text/html")
  public String getHtml() {
    return "<html lang=\"en\"><body><h1>Welcome to PostDirector!</h1></body></html>";
  }
}
