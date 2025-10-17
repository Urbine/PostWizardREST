package net.ygbstudio.postwizard.dao;

import java.util.Optional;
import java.util.stream.Stream;
import net.ygbstudio.postwizard.entities.WPOptions;

/**
 * Data Access Object (DAO) interface for reading WordPress options. This interface defines methods
 * to retrieve and handle WordPress options in a WordPress-like environment.
 *
 * @see OptionsDAO
 * @see WPOptions
 * @author Yoham Gabriel @ YGB Studio
 */
public interface OptionManager {

  /**
   * Named query for finding all WordPress options.
   *
   * <p>{@code SELECT o FROM WPOptions o}
   *
   * @see WPOptions
   */
  String FIND_ALL = "WPOptions.FindAll";

  /**
   * Named query for finding a specific WordPress option by name.
   *
   * <p>{@code SELECT o FROM WPOptions o WHERE o.optionName = :optionName}
   *
   * @see WPOptions
   */
  String FIND_BY_OPTION_NAME = "WPOptions.FindByOptionName";

  /**
   * Retrieves all WordPress options from the database.
   *
   * @return A Stream of all WordPress options.
   */
  Stream<WPOptions> getAllOptions();

  /**
   * Retrieves a specific WordPress option by name from the database.
   *
   * @param optionName The name of the option to retrieve.
   * @return An Optional containing the WordPress option if found, or an empty Optional if not
   *     found.
   */
  Optional<WPOptions> getOptionByName(String optionName);
}
