package net.ygbstudio.postwizard.models;

/**
 * Enum class representing a series of taxonomies for content classification purposes. Some of these
 * taxonomies are not native to WordPress and are added by themes to allow for custom functionality.
 *
 * <p>PostWizard enforces the use of these taxonomies in a way that theme compatibility is preserved
 * throughout the application.
 *
 * @see net.ygbstudio.postwizard.service.TaxonomyService
 * @see TermMeta
 * @author Yoham Gabriel B @ YGB Studio
 */
public enum Taxonomy {
  CATEGORY("category"),
  TAG("post_tag"),
  FORMAT("post_format"),
  PHOTOS_TAG("photos_tag"),
  MODELS("pornstars"),
  OTHERS("others");

  private final String value;

  Taxonomy(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }
}
