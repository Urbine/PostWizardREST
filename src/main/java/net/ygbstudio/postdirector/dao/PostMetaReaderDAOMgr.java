package net.ygbstudio.postdirector.dao;

// Java imports
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import jakarta.ejb.Local;
// Jakarta imports
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
// Local imports
import net.ygbstudio.postdirector.entities.WPMeta;

/**
 * Data Access Object (DAO) implementation for reading WordPress post metadata.
 * This class provides methods to retrieve and manipulate post metadata
 * in a WordPress-like environment.
 * 
 * @author Yoham Gabriel @ YGB Studio
 */
@Stateless
@Local(PostMetaReaderDAO.class)
public class PostMetaReaderDAOMgr implements PostMetaReaderDAO {

	@PersistenceContext(unitName = "wpmeta")
	private EntityManager em;

	@Override
	public Collection<WPMeta> getAll() {
		List<WPMeta> postList = em.createQuery("SELECT post FROM WPMeta post", WPMeta.class)
				.getResultList();
		return postList;
	}

	@Override
	public Collection<WPMeta> getEntriesByPostID(long id) {
		return getAll()
				.stream()
				.filter(elem -> elem.getPostID() == id)
				.toList();
	}

	@Override
	public Collection<WPMeta> getEntriesByMetaKey(String key) {
		return getAll()
				.stream()
				.filter(elem -> elem.getMetaFieldKey().equals(key))
				.toList();
	}

	@Override
	public Collection<WPMeta> getMetaValueMatches(String pattern) {
		Predicate<String> metaValFind = Pattern.compile(pattern)
				.asPredicate();

		return getAll()
				.stream()
				.filter((elem) -> metaValFind.test(elem.getMetaFieldValue()))
				.toList();
	}

	@Override
	public WPMeta getElemByMetaId(long id) {
		return em.find(WPMeta.class, id);
	}

	@Override
	public Optional<WPMeta> updatePostMetaValue(long postID, String metaKey, String newValue) {
		return getEntriesByPostID(postID).stream()
				.filter(p -> p.getMetaFieldKey().equals(metaKey))
				.findFirst()
				.map(existing -> {
					existing.setMetaFieldValue(newValue);
					return existing;
				});
	}

	@Override
	public void updatePostMetaAuto(long postID, String metaKey, String newValue) {
		if (Objects.isNull(postID) || Objects.isNull(metaKey) || Objects.isNull(newValue)) {
			return;
		}

		getEntriesByPostID(postID).stream()
				.filter(p -> p.getMetaFieldKey().equals(metaKey))
				.findFirst().ifPresent(p -> {
					WPMeta existing = em.find(WPMeta.class, p.getMetaID());
					existing.setMetaFieldValue(newValue);
				});

	}

	@Override
	public Boolean postExists(long postID) {
		return getAll()
				.parallelStream()
				.anyMatch(elem -> elem.getPostID() == postID);
	}

}
