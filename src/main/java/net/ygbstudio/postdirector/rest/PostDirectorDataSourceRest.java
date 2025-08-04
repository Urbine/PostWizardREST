package net.ygbstudio.postdirector.rest;

// Java imports
import java.util.Objects;

// Jakarta imports
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import net.ygbstudio.postdirector.dao.PostMetaReaderDAO;
import net.ygbstudio.postdirector.dto.ClientPostMeta;
import net.ygbstudio.postdirector.dto.ServerResponse;
import net.ygbstudio.postdirector.dto.ErrorResponse;
import net.ygbstudio.postdirector.enums.PostMetaKeys;
import net.ygbstudio.postdirector.exceptions.InvalidIdentifier;
import net.ygbstudio.postdirector.utils.Helpers;

/**
 * RESTful web service for managing post metadata in the PostDirector application.
 * This class provides endpoints to retrieve and update post metadata.
 * 
 * @author Yoham Gabriel @ YGB Studio
 */

@RequestScoped
@Path("posts")
public class PostDirectorDataSourceRest {

	/**
	 * Context for the RESTful web service, providing access to URI information.
	 * This is used to construct URIs and provide context for the service.
	 */
	@Context
	private UriInfo context;

	/**
	 * Data Access Object (DAO) for reading and manipulating post metadata.
	 * This DAO is injected to interact with the underlying data source.
	 */
	@Inject
	private PostMetaReaderDAO dbDao;

	/**
	 * Endpoint to retrieve post metadata by post ID.
	 * This method returns a JSON representation of the post metadata
	 * associated with the specified post ID.
	 * 
	 * @param postId | the ID of the post for which metadata is requested
	 * @return JSON representation of the post metadata
	 */
	@GET
	@Path("{postID: [0-9]+}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getPost(@PathParam("postID") Long postId) {
		JsonbConfig jsonConfig = new JsonbConfig()
				.withFormatting(true);

		Jsonb jsonBuilder = JsonbBuilder.create(jsonConfig);
		return jsonBuilder.toJson(dbDao.getEntriesByPostID(postId));
	}

	/**
	 * Endpoint to update post metadata based on the provided ClientPostMeta object.
	 * This method validates the post ID and updates the metadata in the database.
	 * 
	 * @param postMetaFields | the ClientPostMeta object containing post metadata to update
	 * @return Response indicating the result of the update operation
	 */
	@POST
	@Path("update")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updatePost(ClientPostMeta postMetaFields) {
		long postID = postMetaFields.getID();
	
		if (Objects.nonNull(postMetaFields) && Objects.nonNull(postID)) {
			clientPostUpdateStrategy(postMetaFields);
		} else {
			throw new InvalidIdentifier("Post ID field is invalid or it was not provided.");
		}
		
		if (!dbDao.postExists(postID)) {
			return Response.status(Response.Status.NOT_FOUND)
					.entity(new ErrorResponse("Post ID not found", 
							"Please provide a valid post ID" , 
							Response.Status.NOT_FOUND.getStatusCode())).build();
		} else {
			return Response.ok(new ServerResponse("Post ID: " 
					+ postID
					+ " updated with the fields provided.", 
					Response.Status.OK.getStatusCode()), 
					MediaType.APPLICATION_JSON).build();
		}
		
	}
	
	/**
	 * Updates the post metadata based on the provided ClientPostMeta object.
	 * This method iterates through the properties of the ClientPostMeta object
	 * and updates the corresponding metadata in the database.
	 * 
	 * @param clientPost | the ClientPostMeta object containing post metadata to update
	 */
	private void clientPostUpdateStrategy(ClientPostMeta clientPost){
		long postId = clientPost.getID();
		Helpers.getJsonBPropertyValues(clientPost.getClass())
		.stream()
		.forEach(p -> {
			switch (PostMetaKeys.fromValue(p).get()) {
				case POSTID:
					break;
				case HOURS:
					dbDao.updatePostMetaAuto(postId, PostMetaKeys.HOURS.toString(), Long.toString(clientPost.getHours()));
					break;
				case MINUTES:
					dbDao.updatePostMetaAuto(postId, PostMetaKeys.MINUTES.toString(), Long.toString(clientPost.getMinutes()));
					break;
				case SECONDS:
					dbDao.updatePostMetaAuto(postId, PostMetaKeys.SECONDS.toString(), Long.toString(clientPost.getSeconds()));
					break;
				case EMBED:
					dbDao.updatePostMetaAuto(postId, PostMetaKeys.EMBED.toString(), clientPost.getEmbedCode());
					break;
				case PRODUCTION:
					dbDao.updatePostMetaAuto(postId, PostMetaKeys.PRODUCTION.toString(), clientPost.getVideoProduction());
					break;
				case ORIENTATION:
					dbDao.updatePostMetaAuto(postId, PostMetaKeys.ORIENTATION.toString(), clientPost.getVideoOrientation());
					break;
				case ETHNICITY:
					dbDao.updatePostMetaAuto(postId, PostMetaKeys.ETHNICITY.toString(), clientPost.getEthnicity());
					break;
				case HAIRCOLOR:
					dbDao.updatePostMetaAuto(postId, PostMetaKeys.HAIRCOLOR.toString(), clientPost.getHairColor());
					break;
				case HDVIDEO:
					dbDao.updatePostMetaAuto(postId, PostMetaKeys.HDVIDEO.toString(), clientPost.getVideoHD() ? "on" : "off");
					break;
				case THUMBNAIL:
					dbDao.updatePostMetaAuto(postId, PostMetaKeys.THUMBNAIL.toString(), clientPost.getThumbURI());
					break;
				case VIDEOURL:
					dbDao.updatePostMetaAuto(postId, PostMetaKeys.VIDEOURL.toString(), clientPost.getVideoURL());
					break;
				default:
					throw new InvalidIdentifier("Unexpected value: " + p);
			}
		});
	}
	

}