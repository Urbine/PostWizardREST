package net.ygbstudio.postwizard.models;

/**
 * Enum representing the keys used in WordPress posts. This enum provides a way to manage and
 * retrieve post keys in a type-safe manner.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
public enum PostKeys {
  ID("ID"),
  AUTHOR("post_author"),
  CONTENT("post_content"),
  TITLE("post_title"),
  SLUG("post_name"),
  STATUS("post_status"),
  TYPE("post_type"),
  OTHERS("otherKeys");

  private final String value;

  PostKeys(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return this.value;
  }
}
