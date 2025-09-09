package net.ygbstudio.postwizard.dto;

import jakarta.json.bind.annotation.JsonbNillable;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbPropertyOrder;
import jakarta.json.bind.annotation.JsonbVisibility;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Data Transfer Object (DTO) for client posts. This class represents a post in the Post Director
 * application, including its author, content, title, slug, status, and type.
 *
 * <p>The class fields are named according to the actual entries in the WordPress database, allowing
 * for easy validation when compared with the constants defined at {@link
 * net.ygbstudio.postwizard.enums} while also avoiding the need to expose the database structure
 * directly to the client.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@JsonbPropertyOrder(
    value = {
      "ID",
      "post_author",
      "post_content",
      "post_title",
      "post_name",
      "post_status",
      "post_type"
    })
@JsonbVisibility(ClientDTOVisibilityStrategy.class)
public class ClientPost {

  @JsonbProperty("postID")
  private Long ID;

  @JsonbNillable
  @JsonbProperty("author")
  private Long post_author;

  @JsonbNillable
  @JsonbProperty("content")
  private String post_content;

  @JsonbNillable
  @JsonbProperty("title")
  private String post_title;

  @JsonbNillable
  @JsonbProperty("slug")
  private String post_name;

  @JsonbNillable
  @JsonbProperty("status")
  private String post_status;

  @JsonbNillable
  @JsonbProperty("type")
  private String post_type;

  public ClientPost() {
    super();
  }

  /**
   * Constructor for ClientPost.
   *
   * <p>Just like {@link ClientPostMeta}, the fields are expected to be optional and can be null.
   * postID can also be null when creating a new post, as it will be provided by the client in the
   * request path or in the corresponding payload.
   *
   * @param postID the ID of the post
   * @param postAuthor the author ID of the post
   * @param postContent the content of the post
   * @param postTitle the title of the post
   * @param postSlug the slug of the post
   * @param postStatus the status of the post (e.g., "publish", "draft")
   * @param postType the type of the post (e.g., "post", "page")
   */
  public ClientPost(
      Long postID,
      Long postAuthor,
      String postContent,
      String postTitle,
      String postSlug,
      String postStatus,
      String postType) {
    super();
    ID = postID;
    this.post_author = postAuthor;
    this.post_content = postContent;
    this.post_title = postTitle;
    this.post_name = postSlug;
    this.post_status = postStatus;
    this.post_type = postType;
  }

  public Long getPostID() {
    return ID;
  }

  public void setPostID(Long postID) {
    ID = postID;
  }

  public Long getPostAuthor() {
    return post_author;
  }

  public void setPostAuthor(Long postAuthor) {
    this.post_author = postAuthor;
  }

  public String getPostContent() {
    return post_content;
  }

  public void setPostContent(String postContent) {
    this.post_content = postContent;
  }

  public String getPostTitle() {
    return post_title;
  }

  public void setPostTitle(String postTitle) {
    this.post_title = postTitle;
  }

  public String getPostSlug() {
    return post_name;
  }

  public void setPostSlug(String postSlug) {
    this.post_name = postSlug;
  }

  public String getPostStatus() {
    return post_status;
  }

  public void setPostStatus(String postStatus) {
    this.post_status = postStatus;
  }

  public String getPostType() {
    return post_type;
  }

  public void setPostType(String postType) {
    this.post_type = postType;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE, true);
  }
}
