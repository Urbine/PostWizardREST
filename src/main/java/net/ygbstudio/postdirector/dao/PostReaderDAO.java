package net.ygbstudio.postdirector.dao;

// Java Imports
import java.util.Collection;
import java.util.Optional;

// Local imports
import net.ygbstudio.postdirector.entities.WPMeta;

public interface PostReaderDAO {
	Collection<WPMeta> getAll();

	Collection<WPMeta> getEntriesByPostID(long id);

	Collection<WPMeta> getEntriesByMetaKey(String key);

	Collection<WPMeta> getMetaValueMatches(String pattern);

	WPMeta getElemByMetaId(long id);

	Optional<WPMeta> updateMetaValue(long postID, String metaKey, String newValue);
}
