package net.ygbstudio.postwizard.service;

import static net.ygbstudio.postwizard.utils.Logging.loggingInit;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.ygbstudio.postwizard.dao.OptionManager;
import net.ygbstudio.postwizard.entities.WPOptions;
import net.ygbstudio.postwizard.models.InternalPath;
import net.ygbstudio.postwizard.models.SiteOption;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Service class for retrieving WordPress' configuration options.
 *
 * @see SiteOption
 * @author Yoham Gabriel @ YGB Studio
 */
@ApplicationScoped
public class EnvironmentService {

  private static final Logger environmentServiceLog =
      Logger.getLogger(EnvironmentService.class.getName());

  @SuppressWarnings("unused")
  @Nullable
  private static final FileHandler environmentServiceFileHandler =
      loggingInit(environmentServiceLog, Level.ALL, true);

  @Inject private OptionManager optionsDAO;

  @PostConstruct
  private void init() {
    environmentServiceLog.info(
        () -> "EnvironmentService initialized at UTC: " + Instant.now().atZone(ZoneOffset.UTC));
  }

  /**
   * Retrieves a specific WordPress option by name from the database.
   *
   * @param option The name of the option to retrieve.
   * @return An Optional containing the WordPress option if found, or {@code null} if not found.
   */
  @Transactional(value = TxType.REQUIRES_NEW)
  @Nullable
  public String getOption(@NonNull SiteOption option) {
    return optionsDAO
        .getOptionByName(option.toString())
        .map(WPOptions::getOptionValue)
        .orElse(null);
  }

  /**
   * Retrieves the uploads URL prefix without the year and month folders in the WordPress site.
   *
   * <p>Members of the {@link InternalPath} enum can be concatenated to form the path since their
   * class overrides the {@link Object#toString()} method in a programmer-friendly way.
   *
   * @see SiteOption
   * @return The uploads path if the site URL is found, or {@code null} if not found.
   */
  @Nullable
  public String getUploadsURLPrefix() {
    String siteUrl = getOption(SiteOption.SITE_URL);
    return Objects.nonNull(siteUrl)
        ? siteUrl + "/" + InternalPath.WP_CONTENT + "/" + InternalPath.UPLOADS
        : null;
  }
}
