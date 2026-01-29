/*
 * SPDX-License-Identifier: MPL-2.0
 * Copyright (c) Yoham Gabriel B.
 */

package net.ygbstudio.postwizard.dao;

import java.util.stream.Stream;
import net.ygbstudio.postwizard.entities.taxonomies.WPTerms;
import org.jspecify.annotations.NonNull;

/**
 * Data Access Object (DAO) interface for reading WordPress term data. This interface defines
 * methods to retrieve and handle terms in a WordPress-like environment.
 *
 * @see TermsDAO
 * @see WPTerms
 * @author Yoham Gabriel @ YGB Studio
 */
public interface TermsManager {

  /**
   * Named query for finding all terms in the server database.
   *
   * <p>{@code SELECT t FROM WPTerms t}
   *
   * @see WPTerms
   */
  String FIND_ALL = "WPTerms.FindAll";

  /**
   * Named query for checking if a term exists in the server database by ID.
   *
   * <p>{@code SELECT t FROM WPTerms t WHERE t.term_id = :termId}
   *
   * @see WPTerms
   */
  String EXISTS_BY_ID = "WPTerms.ExistsById";

  /**
   * Named query for checking if a term exists in the server database by slug.
   *
   * <p>{@code SELECT t FROM WPTerms t WHERE t.slug = :termSlug}
   *
   * @see WPTerms
   */
  String EXISTS_BY_SLUG = "WPTerms.ExistsBySlug";

  /**
   * Named query for checking if a term exists in the server database by name.
   *
   * <p>{@code SELECT t FROM WPTerms t WHERE t.name = :termName}
   *
   * @see WPTerms
   */
  String EXISTS_BY_NAME = "WPTerms.ExistsByName";

  /**
   * Named query for checking if a term exists in the server database by name and slug.
   *
   * <p>{@code SELECT t FROM WPTerms t WHERE t.name = :termName AND t.slug = :termSlug}
   *
   * @see WPTerms
   */
  String EXISTS_BY_NAME_AND_SLUG = "WPTerms.ExistsByNameAndSlug";

  /**
   * Checks if a term exists in the server database.
   *
   * @param termID the ID of the term for which existence is checked
   * @return a stream of terms that match the given ID
   */
  Stream<WPTerms> termIdExists(long termID);

  /**
   * Checks if a term with a specific slug exists in the server database.
   *
   * @param termSlug the slug of the term for which existence is checked
   * @return a stream of terms that match the given slug
   */
  Stream<WPTerms> termSlugExists(@NonNull String termSlug);

  /**
   * Checks if a term with a specific name exists in the server database.
   *
   * @param termName the name of the term for which existence is checked
   * @return a stream of terms that match the given name
   */
  Stream<WPTerms> termNameExists(@NonNull String termName);

  /**
   * Checks if a term exists in the server database in which name and slug refer to the same entry.
   *
   * @param termName the name of the term for which existence is checked
   * @param termSlug the slug of the term for which existence is checked
   * @return a stream of terms that match the given name and slug
   */
  Stream<WPTerms> termNameAndSlugExists(@NonNull String termName, @NonNull String termSlug);
}
