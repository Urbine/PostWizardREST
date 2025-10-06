package net.ygbstudio.postwizard.models;

/**
 * Enum representing different types of WordPress posts.
 *
 * <p>This enum includes common post types such as standard posts, attachments, and photos. Each
 * enum constant is associated with its corresponding string representation used in the WordPress
 * database.
 *
 * @see net.ygbstudio.postwizard.service.PostService
 * @see <a
 *     href="https://developer.wordpress.org/reference/functions/get_post_type/">get_post_type()</a>
 * @author Yoham Gabriel @ YGB Studio
 */
public enum PostType {
  POST("post"),
  ATTACHMENT("attachment"),
  PHOTOS("photos"),
  ALL("all");

  private final String typeName;

  PostType(String typeName) {
    this.typeName = typeName;
  }

  @Override
  public String toString() {
    return typeName;
  }
}
