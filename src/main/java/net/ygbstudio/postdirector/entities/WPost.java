package net.ygbstudio.postdirector.entities;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbPropertyOrder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import java.util.Objects;

/**
 * Represents a WordPress post entity.
 *
 * <p>This class maps to the `wp_posts` table and provides fields for various attributes of a post,
 * such as author, content, title, slug, status, and type.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@Entity
@Table(name = "`wp_posts`")
@NamedQuery(name = "WPosts.FindAll", query = "SELECT p from WPost P")
@JsonbPropertyOrder(
    value = {"ID", "postAuthor", "postContent", "postTitle", "postSlug", "postStatus", "postType"})
public class WPost {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private Long ID;

  @Column(name = "post_author")
  @JsonbProperty("post_author")
  private Long postAuthor;

  @Column(name = "post_content")
  @JsonbProperty("post_content")
  private String postContent;

  @Column(name = "post_title")
  @JsonbProperty("post_title")
  private String postTitle;

  @Column(name = "post_name")
  @JsonbProperty("post_name")
  private String postSlug;

  @Column(name = "post_status")
  @JsonbProperty("post_status")
  private String postStatus;

  @Column(name = "post_type")
  @JsonbProperty("post_type")
  private String postType;

  public WPost() {}

  public WPost(
      Long iD,
      Long postAuthor,
      String postContent,
      String postTitle,
      String postSlug,
      String postStatus,
      String postType) {
    super();
    ID = iD;
    this.postAuthor = postAuthor;
    this.postContent = postContent;
    this.postTitle = postTitle;
    this.postSlug = postSlug;
    this.postStatus = postStatus;
    this.postType = postType;
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

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof WPost)) return false;
    WPost anotherPost = (WPost) obj;
    return this.getID() == anotherPost.getID()
        && Objects.equals(this.getPostAuthor(), anotherPost.getPostAuthor())
        && Objects.equals(this.getPostContent(), anotherPost.getPostContent())
        && Objects.equals(this.getPostTitle(), anotherPost.getPostTitle())
        && Objects.equals(this.getPostSlug(), anotherPost.getPostSlug())
        && Objects.equals(this.getPostStatus(), anotherPost.getPostStatus())
        && Objects.equals(this.getPostType(), anotherPost.getPostType());
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        getID(),
        getPostAuthor(),
        getPostContent(),
        getPostTitle(),
        getPostSlug(),
        getPostStatus(),
        getPostType());
  }

  @Override
  public String toString() {
    return "WPost [ID="
        + ID
        + ", postAuthor="
        + postAuthor
        + ", postContent="
        + postContent
        + ", postTitle="
        + postTitle
        + ", postSlug="
        + postSlug
        + ", postStatus="
        + postStatus
        + ", postType="
        + postType
        + "]";
  }
}
