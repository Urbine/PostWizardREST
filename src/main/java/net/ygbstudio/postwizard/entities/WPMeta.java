package net.ygbstudio.postwizard.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

/**
 * Represents metadata associated with a WordPress post.
 *
 * <p>This entity maps to the {@code wp_postmeta} table and allows reading and updating key-value
 * metadata pairs for posts.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@Entity
@Table(name = "`wp_postmeta`")
@NamedQuery(name = "WPMeta.FindAll", query = "SELECT post FROM WPMeta post")
@NamedQuery(name = "WPMeta.FindAllPostIDs", query = "SELECT post.postID FROM WPMeta post")
@NamedQuery(
    name = "WPMeta.FindPostByID",
    query = "SELECT post FROM WPMeta post WHERE post.postID = :postID")
@NamedQuery(
    name = "WPMeta.FindByMetaKey",
    query = "SELECT post FROM WPMeta post WHERE post.metaFieldKey = :metaKey")
public class WPMeta {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "meta_id")
  private Long metaID;

  /** The post ID this metadata is associated with. Not the primary key in the database. */
  @Column(name = "post_id", nullable = false)
  private Long postID;

  @Column(name = "meta_key")
  private String metaFieldKey;

  @Column(name = "meta_value", columnDefinition = "LONGTEXT")
  private String metaFieldValue;

  public WPMeta() {}

  /**
   * Constructs a WPMeta instance with the specified parameters.
   *
   * @param metaID the unique identifier for the metadata entry
   * @param postID the ID of the post this metadata is associated with
   * @param metaFieldKey the key/name of the metadata field
   * @param metaFieldValue the value associated with the metadata field
   */
  public WPMeta(Long metaID, Long postID, String metaFieldKey, String metaFieldValue) {
    super();
    this.metaID = metaID;
    this.postID = postID;
    this.metaFieldKey = metaFieldKey;
    this.metaFieldValue = metaFieldValue;
  }

  public Long getMetaID() {
    return metaID;
  }

  public void setMetaID(Long metaID) {
    this.metaID = metaID;
  }

  public Long getPostID() {
    return postID;
  }

  public void setPostID(Long postID) {
    this.postID = postID;
  }

  public String getMetaFieldKey() {
    return metaFieldKey;
  }

  public void setMetaFieldKey(String metaFieldKey) {
    this.metaFieldKey = metaFieldKey;
  }

  public String getMetaFieldValue() {
    return metaFieldValue;
  }

  public void setMetaFieldValue(String metaFieldValue) {
    this.metaFieldValue = metaFieldValue;
  }

  @Override
  public String toString() {
    return "WPMeta [metaID="
        + metaID
        + ", postID="
        + postID
        + ", metaFieldKey="
        + metaFieldKey
        + ", metaFieldValue="
        + metaFieldValue
        + "]";
  }
}
