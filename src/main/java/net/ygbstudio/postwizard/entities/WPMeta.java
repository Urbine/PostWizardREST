package net.ygbstudio.postwizard.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import java.util.Objects;

/**
 * Represents metadata associated with a WordPress post. {@link WPMeta} entities are children of
 * {@link WPost} entities.
 *
 * <p>Post metadata fields, in the same way as {@link
 * net.ygbstudio.postwizard.entities.taxonomies.WPTermMeta} entities, are used by themes or plugins
 * to store additional information about posts that allow for customizing the behavior of posts and
 * extending their capabilities in ways that affect classification, look and feel, and other
 * features.
 *
 * <p>This entity maps to the {@code wp_postmeta} table and allows reading and updating key-value
 * metadata pairs for posts.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@Entity
@Table(name = "`wp_postmeta`")
@NamedQuery(name = "WPMeta.FindAll", query = "SELECT postMeta FROM WPMeta postMeta")
@NamedQuery(
    name = "WPMeta.FindAllPostIDs",
    query = "SELECT postMeta.wpPost.id FROM WPMeta postMeta")
@NamedQuery(
    name = "WPMeta.FindPostByID",
    query = "SELECT postMeta FROM WPMeta postMeta WHERE postMeta.wpPost.id = :postId")
@NamedQuery(
    name = "WPMeta.FindByMetaKey",
    query = "SELECT postMeta FROM WPMeta postMeta WHERE postMeta.metaFieldKey = :metaKey")
@NamedQuery(
    name = "WPMeta.FindKeyByPostID",
    query =
        "SELECT postMeta FROM WPMeta postMeta WHERE postMeta.metaFieldKey = :metaKey AND postMeta.wpPost.id = :postId")
@NamedQuery(
    name = "WPMeta.RandomPostByMetaKey",
    query =
        "SELECT postMeta FROM WPMeta postMeta WHERE postMeta.metaFieldKey = :metaKey ORDER BY FUNCTION('RAND')")
@NamedQuery(
    name = "WPMeta.FindMetaValueLike",
    query =
        "SELECT postMeta FROM WPMeta postMeta WHERE postMeta.metaFieldValue LIKE :metaValuePattern")
@NamedNativeQuery(
    name = "WPMeta.FindMetaValueLikeNative",
    query = "SELECT `meta_value` FROM `wp_postmeta` where `meta_key` = ? AND `meta_value` LIKE ?")
public class WPMeta {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "meta_id")
  private Long metaID;

  @ManyToOne
  @JoinColumn(name = "post_id")
  private WPost wpPost;

  @Column(name = "meta_key")
  private String metaFieldKey;

  @Column(name = "meta_value", columnDefinition = "LONGTEXT")
  private String metaFieldValue;

  public Long getMetaID() {
    return metaID;
  }

  public void setMetaID(Long metaID) {
    this.metaID = metaID;
  }

  public WPost getPost() {
    return wpPost;
  }

  public void setPost(WPost wpPost) {
    this.wpPost = wpPost;
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
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    WPMeta wpMeta = (WPMeta) o;
    return Objects.equals(getMetaID(), wpMeta.getMetaID())
        && Objects.equals(wpPost, wpMeta.wpPost)
        && Objects.equals(getMetaFieldKey(), wpMeta.getMetaFieldKey())
        && Objects.equals(getMetaFieldValue(), wpMeta.getMetaFieldValue());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getMetaID(), wpPost, getMetaFieldKey(), getMetaFieldValue());
  }

  @Override
  public String toString() {
    return "WPMeta [metaID="
        + metaID
        + ", postItem="
        + wpPost
        + ", metaFieldKey="
        + metaFieldKey
        + ", metaFieldValue="
        + metaFieldValue
        + "]";
  }
}
