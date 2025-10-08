package net.ygbstudio.postwizard.dao;

import java.util.stream.Stream;
import net.ygbstudio.postwizard.entities.WPost;
import net.ygbstudio.postwizard.entities.taxonomies.WPTermRelationships;
import net.ygbstudio.postwizard.entities.taxonomies.WPTermTaxonomy;
import org.jspecify.annotations.NonNull;

/**
 * Data Access Object (DAO) interface for reading WordPress term relationships. This interface
 * defines methods to retrieve and manipulate term relationships in a WordPress-like environment.
 *
 * @see TermRelationshipsDAO
 * @see WPTermRelationships
 * @see WPTermTaxonomy
 * @see WPost
 * @author Yoham Gabriel @ YGB Studio
 */
public interface TermRelationshipsManager {

  /**
   * Finds an existing relationship between a post and a term taxonomy.
   *
   * @param postItem post object
   * @param taxonomy term taxonomy object
   * @return stream of term relationship objects
   */
  Stream<WPTermRelationships> findExistingRelationship(
      @NonNull WPost postItem, @NonNull WPTermTaxonomy taxonomy);

  /**
   * Connects a post and a term taxonomy in a term relationship.
   *
   * @param postItem post object
   * @param taxonomy term taxonomy object
   * @param termOrder term order (usually 0, depending on your use case)
   * @return term relationship object
   */
  WPTermRelationships addTermRelationship(
      @NonNull WPost postItem, @NonNull WPTermTaxonomy taxonomy, int termOrder);

  /**
   * Deletes the relationship between a post and a term taxonomy.
   *
   * @param postItem post object
   * @param taxonomy term taxonomy object
   * @return true if the term relationship was deleted, false otherwise
   */
  boolean deleteTermRelationship(@NonNull WPost postItem, @NonNull WPTermTaxonomy taxonomy);

  /**
   * Cleans up the term relationships associated with a term taxonomy.
   *
   * @param termTaxonomyId term taxonomy ID
   * @return true if the term relationships were cleaned up, false otherwise
   */
  boolean cleanTaxonomyRelationships(long termTaxonomyId);

  /**
   * Counts the number of term relationships associated with a term taxonomy.
   *
   * @param termTaxonomyId term taxonomy ID
   * @return number of term relationships associated with the term taxonomy
   */
  long countTaxonomyRelationships(long termTaxonomyId);
}
