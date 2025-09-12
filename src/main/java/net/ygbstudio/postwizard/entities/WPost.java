package net.ygbstudio.postwizard.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Represents a WordPress post entity.
 *
 * <p>This class maps to the `wp_posts` table and provides fields for various attributes of a post,
 * such as author, content, title, slug, status, and type.
 *
 * @see <a href="https://developer.wordpress.org/reference/classes/wp_post/">WP_Post</a>
 * @author Yoham Gabriel @ YGB Studio
 */
@Entity
@Table(name = "`wp_posts`")
@NamedQuery(name = "WPosts.FindAll", query = "SELECT p from WPost P")
@NamedQuery(name = "WPosts.FindByID", query = "SELECT p from WPost P where p.ID = :postID")
@NamedQuery(
    name = "WPosts.FindByType",
    query = "SELECT p from WPost P where p.postType = :postType")
public class WPost {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private Long ID;

  @Column(name = "post_author")
  private Long postAuthor;

  @Column(name = "post_date_gmt")
  private LocalDateTime createdAtGMT;

  @Column(name = "post_date")
  private LocalDateTime createdAtLocal;

  @Column(name = "post_modified_gmt")
  private LocalDateTime modifiedAtGMT;

  @Column(name = "post_modified")
  private LocalDateTime modifiedAtLocal;

  @Column(name = "post_content")
  private String postContent;

  @Column(name = "post_title")
  private String postTitle;

  @Column(name = "post_name")
  private String postSlug;

  @Column(name = "post_status")
  private String postStatus;

  @Column(name = "post_type")
  private String postType;

  @Column(name = "post_parent")
  private Long postParent;

  @Column(name = "guid")
  private String guid;

  @Column(name = "post_mime_type")
  private String postMimeType;

  public WPost() {}

  /**
   * Constructor for WPost. Fields are mapped to the corresponding columns in the `wp_posts` table.
   *
   * @param iD
   * @param postAuthor
   * @param createdAtGMT
   * @param createdAtLocal
   * @param modifiedAtGMT
   * @param modifiedAtLocal
   * @param postContent
   * @param postTitle
   * @param postSlug
   * @param postStatus
   * @param postType
   * @param postParent
   * @param guid
   * @param postMimeType
   */
  public WPost(
      Long iD,
      Long postAuthor,
      LocalDateTime createdAtGMT,
      LocalDateTime createdAtLocal,
      LocalDateTime modifiedAtGMT,
      LocalDateTime modifiedAtLocal,
      String postContent,
      String postTitle,
      String postSlug,
      String postStatus,
      String postType,
      Long postParent,
      String guid,
      String postMimeType) {
    super();
    ID = iD;
    this.postAuthor = postAuthor;
    this.createdAtGMT = createdAtGMT;
    this.createdAtLocal = createdAtLocal;
    this.modifiedAtGMT = modifiedAtGMT;
    this.modifiedAtLocal = modifiedAtLocal;
    this.postContent = postContent;
    this.postTitle = postTitle;
    this.postSlug = postSlug;
    this.postStatus = postStatus;
    this.postType = postType;
    this.postParent = postParent;
    this.guid = guid;
    this.postMimeType = postMimeType;
  }

  public Long getID() {
    return ID;
  }

  public void setID(Long iD) {
    ID = iD;
  }

  public Long getPostAuthor() {
    return postAuthor;
  }

  public void setPostAuthor(Long postAuthor) {
    this.postAuthor = postAuthor;
  }

  public LocalDateTime getCreatedAtGMT() {
    return createdAtGMT;
  }

  public void setCreatedAtGMT(LocalDateTime createdAtGMT) {
    this.createdAtGMT = createdAtGMT;
  }

  public LocalDateTime getCreatedAtLocal() {
    return createdAtLocal;
  }

  public void setCreatedAtLocal(LocalDateTime createdAtLocal) {
    this.createdAtLocal = createdAtLocal;
  }

  public LocalDateTime getModifiedAtGMT() {
    return modifiedAtGMT;
  }

  public void setModifiedAtGMT(LocalDateTime modifiedAtGMT) {
    this.modifiedAtGMT = modifiedAtGMT;
  }

  public LocalDateTime getModifiedAtLocal() {
    return modifiedAtLocal;
  }

  public void setModifiedAtLocal(LocalDateTime modifiedAtLocal) {
    this.modifiedAtLocal = modifiedAtLocal;
  }

  public String getPostContent() {
    return postContent;
  }

  public void setPostContent(String postContent) {
    this.postContent = postContent;
  }

  public String getPostTitle() {
    return postTitle;
  }

  public void setPostTitle(String postTitle) {
    this.postTitle = postTitle;
  }

  public String getPostSlug() {
    return postSlug;
  }

  public void setPostSlug(String postSlug) {
    this.postSlug = postSlug;
  }

  public String getPostStatus() {
    return postStatus;
  }

  public void setPostStatus(String postStatus) {
    this.postStatus = postStatus;
  }

  public String getPostType() {
    return postType;
  }

  public void setPostType(String postType) {
    this.postType = postType;
  }

  public Long getPostParent() {
    return postParent;
  }

  public void setPostParent(Long postParent) {
    this.postParent = postParent;
  }

  public String getGuid() {
    return guid;
  }

  public void setGuid(String guid) {
    this.guid = guid;
  }

  public String getPostMimeType() {
    return postMimeType;
  }

  public void setPostMimeType(String postMimeType) {
    this.postMimeType = postMimeType;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    WPost other = (WPost) obj;
    return Objects.equals(getID(), other.getID())
        && Objects.equals(getPostAuthor(), other.getPostAuthor())
        && Objects.equals(getPostContent(), other.getPostContent())
        && Objects.equals(getPostTitle(), other.getPostTitle())
        && Objects.equals(getPostSlug(), other.getPostSlug())
        && Objects.equals(getPostStatus(), other.getPostStatus())
        && Objects.equals(getPostType(), other.getPostType())
        && Objects.equals(getCreatedAtGMT(), other.getCreatedAtGMT())
        && Objects.equals(getCreatedAtLocal(), other.getCreatedAtLocal())
        && Objects.equals(getModifiedAtGMT(), other.getModifiedAtGMT())
        && Objects.equals(getModifiedAtLocal(), other.getModifiedAtLocal())
        && Objects.equals(getPostParent(), other.getPostParent())
        && Objects.equals(getGuid(), other.getGuid())
        && Objects.equals(getPostMimeType(), other.getPostMimeType());
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        ID,
        postAuthor,
        postContent,
        postTitle,
        postSlug,
        postStatus,
        postType,
        createdAtGMT,
        createdAtLocal,
        modifiedAtGMT,
        modifiedAtLocal,
        postParent,
        guid,
        postMimeType);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE, true);
  }
}
