package net.ygbstudio.postdirector.rest;

// Jakarta imports
import jakarta.ws.rs.Path;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

@Path("welcome")
public class PostDirectorRestfulMain {

	@Context
	private UriInfo context;

	@GET
	@Produces("text/html")
	public String getHtml() {
		return "<html lang=\"en\"><body><h1>Welcome to PostDirector!</h1></body></html>";
	}

}
