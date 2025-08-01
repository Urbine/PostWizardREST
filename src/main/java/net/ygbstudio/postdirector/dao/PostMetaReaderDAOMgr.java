package net.ygbstudio.postdirector.dao;

// Java imports
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

// Jakarta imports
import jakarta.ejb.Stateless;
import jakarta.ejb.Local;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

// Local imports
import net.ygbstudio.postdirector.entities.WPMeta;

@Stateless
@Local(PostReaderDAO.class)
public class PostMetaReaderDAOMgr implements PostReaderDAO {

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
	public Optional<WPMeta> updateMetaValue(long postID, String metaKey, String newValue) {
		List<WPMeta> lst = getEntriesByPostID(postID)
				.stream()
				.filter(p -> p.getMetaFieldValue().equals(newValue))
				.limit(1)
				.toList();

		if (Objects.nonNull(lst) && !lst.isEmpty()) {
			return Optional.empty();
		} else {
			WPMeta toModifyElem = lst.getFirst();
			toModifyElem.setMetaFieldValue(newValue);
			em.persist(toModifyElem);
			em.flush();
			return Optional.of(toModifyElem);
		}
	}

}
