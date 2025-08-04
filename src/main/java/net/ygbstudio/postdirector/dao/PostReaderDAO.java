package net.ygbstudio.postdirector.dao;

// Java imports
import java.util.Collection;
import java.util.Optional;

// Local import
import net.ygbstudio.postdirector.entities.WPost;

/**
 * Data Access Object (DAO) interface for reading WordPress posts.
 * This interface defines methods to retrieve and manipulate posts
 * in a WordPress-like environment.
 * 
 * @author Yoham Gabriel @ YGB Studio
 */
public interface PostReaderDAO {
	Collection<WPost> getAllPosts();
	Optional<WPost> getPostById(long postID);
	
	 
}
