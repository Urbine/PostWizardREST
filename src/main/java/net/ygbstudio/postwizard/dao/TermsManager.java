package net.ygbstudio.postwizard.dao;

import java.util.Set;
import java.util.stream.Stream;
import net.ygbstudio.postwizard.entities.taxonomies.WPTermMeta;
import net.ygbstudio.postwizard.entities.taxonomies.WPTerms;
import org.jspecify.annotations.NonNull;

/**
 * Data Access Object (DAO) interface for reading WordPress term data. This interface defines
 * methods to retrieve and manipulate terms in a WordPress-like environment.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
public interface TermsManager {

  /**
   * Checks if a term exists in the server database.
   *
   * @param termID the ID of the term for which existence is checked
   * @return a stream of terms that match the given ID
   */
  Stream<WPTerms> termIdExists(long termID);

  /**
   * Checks if a term exists in the server database.
   *
   * @param termSlug the slug of the term for which existence is checked
   * @return a stream of terms that match the given slug
   */
  Stream<WPTerms> termSlugExists(@NonNull String termSlug);

  /**
   * Checks if a term exists in the server database.
   *
   * @param termName the name of the term for which existence is checked
   * @return a stream of terms that match the given name
   */
  Stream<WPTerms> termNameExists(@NonNull String termName);

  /**
   * Adds a new term to the server database. In case the name or slug exists, that term will be
   * returned; otherwise, returns the added term.
   *
   * @param name the name of the term to be added
   * @param slug the slug of the term to be added
   * @param termMetaSet the set of term meta to be added
   * @param termGroup the term group to be added
   * @return the added term
   */
  WPTerms addTerm(
      @NonNull String name,
      @NonNull String slug,
      @NonNull Set<WPTermMeta> termMetaSet,
      long termGroup);

  /**
   * Deletes a term object and associations from the server database.
   *
   * @param term the term to be deleted
   * @return true if the term was deleted, false otherwise
   */
  boolean deleteTerm(@NonNull WPTerms term);
}
