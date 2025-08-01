package net.ygbstudio.postdirector.rest;

// Java imports
import java.util.List;

// Jakarta imports
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.core.MediaType;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

// Local imports
import net.ygbstudio.postdirector.dao.PostReaderDAO;

@RequestScoped
@Path("posts")
public class PostDirectorDataSourceRest {

	@Context
	private UriInfo context;

	@Inject
	private PostReaderDAO dbDao;

	@GET
	@Path("{postID: [0-9]*}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getPost(@PathParam("postID") Long postId) {
		JsonbConfig jsonConfig = new JsonbConfig()
				.withFormatting(true);

		Jsonb jsonBuilder = JsonbBuilder.create(jsonConfig);
		return jsonBuilder.toJson(dbDao.getEntriesByPostID(postId));
	}

	@POST
	@Path("update")
	@Consumes(MediaType.APPLICATION_JSON)
	public String updatePost(@PathParam("postObj") String postJSON) {
		return "";
	}

}
