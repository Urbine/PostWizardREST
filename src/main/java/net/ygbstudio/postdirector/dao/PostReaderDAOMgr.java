package net.ygbstudio.postdirector.dao;

import java.util.Collection;
import java.util.Optional;

import net.ygbstudio.postdirector.entities.WPost;

// Jakarta imports
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.EntityManager;
import jakarta.ejb.Stateless;
import jakarta.ejb.Local;


/**
 * Data Access Object (DAO) implementation for reading WordPress posts.
 * This class provides methods to retrieve and manipulate posts
 * in a WordPress-like environment.
 * 
 * @author Yoham Gabriel @ YGB Studio
 */
@Stateless
@Local(PostReaderDAO.class)
public class PostReaderDAOMgr implements PostReaderDAO{

	@PersistenceContext(unitName = "wpost" )
	private EntityManager em;
	
	@Override
	public Collection<WPost> getAllPosts() {
		return em.createQuery("SELECT p from WPost P", WPost.class)
				.getResultList();
	}
	
	@Override
	public Optional<WPost> getPostById(long postID) {
		return getAllPosts()
				.stream()
				.filter(p -> p.getID() == postID)
				.findFirst();
	}

	
	
}
