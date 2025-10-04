package net.ygbstudio.postwizard.dao;

import java.util.stream.Stream;
import net.ygbstudio.postwizard.entities.taxonomies.WPTermTaxonomy;
import net.ygbstudio.postwizard.entities.taxonomies.WPTerms;
import org.jspecify.annotations.NonNull;

/**
 * Data Access Object (DAO) interface for reading WordPress taxonomy data. This interface defines
 * methods to retrieve and manipulate taxonomies in a WordPress-like environment.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
public interface TaxonomyManager {
  /**
   * Checks if a taxonomy exists in the server database.
   *
   * @param taxonomyID the ID of the taxonomy for which existence is checked
   * @return a stream of taxonomies that match the given ID
   */
  Stream<WPTermTaxonomy> termTaxonomyIdExists(long taxonomyID);

  /**
   * Checks if a specific term taxonomy exists in the server database.
   *
   * @param term the term for which existence is checked
   * @return a stream of taxonomies that match the given term
   */
  Stream<WPTermTaxonomy> taxonomyTermExists(@NonNull WPTerms term);

  /**
   * Checks if a taxonomy type exists in the server database.
   *
   * @param taxonomy the taxonomy for which existence is checked
   * @return a stream of taxonomies that match the given taxonomy
   */
  Stream<WPTermTaxonomy> taxonomyExists(@NonNull String taxonomy);

  /**
   * Adds a new taxonomy to the server database. In case a term exists for a taxonomy, that taxonomy
   * will be returned; otherwise, returns the added taxonomy.
   *
   * @param term the term to be added and linked to the taxonomy
   * @param taxonomy the taxonomy to be added
   * @param description the description of the taxonomy to be added
   * @param parent the parent of the taxonomy to be added
   * @param count the count of the taxonomy to be added
   * @return the added taxonomy
   */
  WPTermTaxonomy addTermTaxonomy(
      @NonNull WPTerms term,
      @NonNull String taxonomy,
      @NonNull String description,
      int parent,
      long count);

  /**
   * Removes a taxonomy from the server database.
   *
   * @param termTaxonomy the taxonomy to be removed
   * @return true if the taxonomy was removed successfully, false otherwise
   */
  boolean removeTermTaxonomy(@NonNull WPTermTaxonomy termTaxonomy);
}
