package net.ygbstudio.postwizard.dao;

import net.ygbstudio.postwizard.entities.taxonomies.WPTermMeta;
import org.jspecify.annotations.NonNull;

/**
 * Data Access Object (DAO) interface for reading WordPress term metadata. This interface defines
 * methods to retrieve and manipulate term meta in a WordPress-like environment.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
public interface TermsMetaManager {

  /**
   * Adds a new term meta to the server database. In case the name or slug exists, that term will be
   * returned; otherwise, returns the added term.
   *
   * @param metaKey the meta key of the term meta to be added
   * @param metaValue the meta value of the term meta to be added
   * @return the added term meta
   */
  WPTermMeta addTermMeta(@NonNull String metaKey, @NonNull String metaValue);
}
