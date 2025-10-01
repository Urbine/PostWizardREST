package net.ygbstudio.postwizard.models;

/**
 * Enum class for validating a series of term meta keys for custom taxonomies in the WordPress site.
 * Not all taxonomies have this meta terms, but they are still used in the application to allow for
 * theme-specific functionality that depends on data in the {@code wp_termmeta}.
 *
 * @see Taxonomy
 * @author Yoham Gabriel B @ YGB Studio
 */
public enum TermMeta {
  MODEL_IMG_ID("pornstars-image-id"),
  ACTORS_VIEW_COUNT("actors_views_count");

  private final String value;

  TermMeta(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }
}
