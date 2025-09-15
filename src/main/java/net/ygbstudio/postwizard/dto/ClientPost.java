package net.ygbstudio.postwizard.dto;

import jakarta.json.bind.annotation.JsonbNillable;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbPropertyOrder;
import jakarta.json.bind.annotation.JsonbVisibility;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Data Transfer Object (DTO) for client posts. This class represents a post in the Post Director
 * application, including its author, content, title, slug, status, and type.
 *
 * <p>The class fields are named according to the actual entries in the WordPress database, allowing
 * for easy validation when compared with the constants defined at {@link
 * net.ygbstudio.postwizard.models} while also avoiding the need to expose the database structure
 * directly to the client.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@JsonbPropertyOrder(
    value = {
      "ID",
      "postAuthor",
      "postTitle",
      "postName",
      "postStatus",
      "postContent",
      "postType",
      "guid",
      "postMimeType",
      "postParent",
      "postDate",
      "postDateGMT",
      "postModified",
      "postModifiedGMT"
    })
@JsonbVisibility(ClientDTOVisibilityStrategy.class)
public class ClientPost implements BatchDeliverable {

  @JsonbProperty("postID")
  private Long ID;

  @JsonbNillable
  @JsonbProperty("author")
  private Long postAuthor;

  @JsonbNillable
  @JsonbProperty("content")
  private String postContent;

  @JsonbNillable
  @JsonbProperty("title")
  private String postTitle;

  @JsonbNillable
  @JsonbProperty("slug")
  private String postName;

  @JsonbNillable
  @JsonbProperty("status")
  private String postStatus;

  @JsonbNillable
  @JsonbProperty("type")
  private String postType;

  @JsonbNillable
  @JsonbProperty("mimeType")
  private String postMimeType;

  @JsonbNillable
  @JsonbProperty("postParent")
  private Long postParent;

  @JsonbNillable
  @JsonbProperty("guid")
  private String guid;

  @JsonbNillable
  @JsonbProperty("createdAt")
  private LocalDateTime postDate;

  @JsonbNillable
  @JsonbProperty("createdAtGMT")
  private LocalDateTime postDateGMT;

  @JsonbNillable
  @JsonbProperty("modifiedAt")
  private LocalDateTime postModified;

  @JsonbNillable
  @JsonbProperty("modifiedAtGMT")
  private LocalDateTime postModifiedGMT;

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
   * <p>postDateGMT is set to the current date and time if postDate is provided, while post_modified
   * and post_modified_gmt are set to the current date and time by default in their respective
   * zones.
   *
   * @param iD The unique identifier of the post.
   * @param postAuthor The ID of the author of the post.
   * @param postContent The content of the post.
   * @param postTitle The title of the post.
   * @param postName The slug (URL-friendly name) of the post.
   * @param postStatus The status of the post (e.g., published, draft).
   * @param postType The type of the post (e.g., post, page).
   * @param postMimeType The MIME type of the post.
   * @param postParent The ID of the parent post, if applicable.
   * @param guid The globally unique identifier for the post.
   */
  public ClientPost(
      Long iD,
      Long postAuthor,
      String postContent,
      String postTitle,
      String postName,
      String postStatus,
      String postType,
      String postMimeType,
      Long postParent,
      String guid) {
    super();
    ID = iD;
    this.postAuthor = postAuthor;
    this.postContent = postContent;
    this.postTitle = postTitle;
    this.postName = postName;
    this.postStatus = postStatus;
    this.postType = postType;
    this.postMimeType = postMimeType;
    this.postParent = postParent;
    this.guid = guid;
    this.postDate = LocalDateTime.now();
    this.postDateGMT = ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime();
    this.postModified = LocalDateTime.now();
    this.postModifiedGMT = ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime();
  }

  public Long getID() {
    return ID;
  }

  public void setID(Long iD) {
    ID = iD;
  }

  public Long getAuthor() {
    return postAuthor;
  }

  public void setAuthor(Long post_author) {
    this.postAuthor = post_author;
  }

  public String getContent() {
    return postContent;
  }

  public void setContent(String post_content) {
    this.postContent = post_content;
  }

  public String getTitle() {
    return postTitle;
  }

  public void setTitle(String post_title) {
    this.postTitle = post_title;
  }

  public String getSlug() {
    return postName;
  }

  public void setSlug(String post_name) {
    this.postName = post_name;
  }

  public String getStatus() {
    return postStatus;
  }

  public void setStatus(String post_status) {
    this.postStatus = post_status;
  }

  public String getType() {
    return postType;
  }

  public void setType(String post_type) {
    this.postType = post_type;
  }

  public String getMimeType() {
    return postMimeType;
  }

  public void setMimeType(String post_mime_type) {
    this.postMimeType = post_mime_type;
  }

  public Long getParent() {
    return postParent;
  }

  public void setParent(Long post_parent) {
    this.postParent = post_parent;
  }

  public String getGuid() {
    return guid;
  }

  public void setGuid(String guid) {
    this.guid = guid;
  }

  public LocalDateTime getCreateDate() {
    return postDate;
  }

  public void setCreateDate(LocalDateTime post_date) {
    this.postDate = post_date;
  }

  public LocalDateTime getCreateDateGMT() {
    return postDateGMT;
  }

  public void setCreateDateGMT(LocalDateTime post_date_gmt) {
    this.postDateGMT = post_date_gmt;
  }

  public LocalDateTime getDateModified() {
    return postModified;
  }

  public void setDateModified(LocalDateTime post_modified) {
    this.postModified = post_modified;
  }

  public LocalDateTime getDateModifiedGMT() {
    return postModifiedGMT;
  }

  public void setDateModifiedGMT(LocalDateTime post_modified_gmt) {
    this.postModifiedGMT = post_modified_gmt;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE, true);
  }
}
