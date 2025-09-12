package net.ygbstudio.postwizard.dto;

import jakarta.json.bind.annotation.JsonbNillable;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbPropertyOrder;
import jakarta.json.bind.annotation.JsonbVisibility;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
      "post_author",
      "post_title",
      "post_name",
      "post_status",
      "post_content",
      "post_type",
      "guid",
      "post_mime_type",
      "post_parent",
      "post_date",
      "post_date_gmt",
      "post_modified",
      "post_modified_gmt"
    })
@JsonbVisibility(ClientDTOVisibilityStrategy.class)
public class ClientPost implements BatchDeliverable {

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
  @JsonbProperty("postType")
  private String post_type;

  @JsonbNillable
  @JsonbProperty("mimeType")
  private String post_mime_type;

  @JsonbNillable
  @JsonbProperty("postParent")
  private Long post_parent;

  @JsonbNillable
  @JsonbProperty("guid")
  private String guid;

  @JsonbNillable
  @JsonbProperty("createdAt")
  private LocalDateTime post_date;

  @JsonbNillable
  @JsonbProperty("createdAtGMT")
  private LocalDateTime post_date_gmt;

  @JsonbNillable
  @JsonbProperty("modifiedAt")
  private LocalDateTime post_modified;

  @JsonbNillable
  @JsonbProperty("modifiedAtGMT")
  private LocalDateTime post_modified_gmt;

  public ClientPost() {
    super();
  }

  /**
   * Constructor for ClientPost.
   *
   * <p>*
   *
   * <p>Just like {@link ClientPostMeta}, the fields are expected to be optional and can be null.
   * postID can also be null when creating a new post, as it will be provided by the client in the
   * request path or in the corresponding payload.
   *
   * <p>post_date_gmt is set to the current date and time if post_date is provided, while
   * post_modified and post_modified_gmt are set to the current date and time by default in their
   * respective zones.
   *
   * @param iD The unique identifier of the post.
   * @param post_author The ID of the author of the post.
   * @param post_content The content of the post.
   * @param post_title The title of the post.
   * @param post_name The slug (URL-friendly name) of the post.
   * @param post_status The status of the post (e.g., published, draft).
   * @param post_type The type of the post (e.g., post, page).
   * @param post_mime_type The MIME type of the post.
   * @param post_parent The ID of the parent post, if applicable.
   * @param guid The globally unique identifier for the post.
   */
  public ClientPost(
      Long iD,
      Long post_author,
      String post_content,
      String post_title,
      String post_name,
      String post_status,
      String post_type,
      String post_mime_type,
      Long post_parent,
      String guid) {
    super();
    ID = iD;
    this.post_author = post_author;
    this.post_content = post_content;
    this.post_title = post_title;
    this.post_name = post_name;
    this.post_status = post_status;
    this.post_type = post_type;
    this.post_mime_type = post_mime_type;
    this.post_parent = post_parent;
    this.guid = guid;
    this.post_date = LocalDateTime.now();
    this.post_date_gmt = ZonedDateTime.now(ZoneId.of("Etc/GMT-0")).toLocalDateTime();
    this.post_modified = LocalDateTime.now();
    this.post_modified_gmt = ZonedDateTime.now(ZoneId.of("Etc/GMT-0")).toLocalDateTime();
  }

  public Long getID() {
    return ID;
  }

  public void setID(Long iD) {
    ID = iD;
  }

  public Long getAuthor() {
    return post_author;
  }

  public void setAuthor(Long post_author) {
    this.post_author = post_author;
  }

  public String getContent() {
    return post_content;
  }

  public void setContent(String post_content) {
    this.post_content = post_content;
  }

  public String getTitle() {
    return post_title;
  }

  public void setTitle(String post_title) {
    this.post_title = post_title;
  }

  public String getSlug() {
    return post_name;
  }

  public void setSlug(String post_name) {
    this.post_name = post_name;
  }

  public String getStatus() {
    return post_status;
  }

  public void setStatus(String post_status) {
    this.post_status = post_status;
  }

  public String getType() {
    return post_type;
  }

  public void setType(String post_type) {
    this.post_type = post_type;
  }

  public String getMimeType() {
    return post_mime_type;
  }

  public void setMimeType(String post_mime_type) {
    this.post_mime_type = post_mime_type;
  }

  public Long getParent() {
    return post_parent;
  }

  public void setParent(Long post_parent) {
    this.post_parent = post_parent;
  }

  public String getGuid() {
    return guid;
  }

  public void setGuid(String guid) {
    this.guid = guid;
  }

  public LocalDateTime getCreateDate() {
    return post_date;
  }

  public void setCreateDate(LocalDateTime post_date) {
    this.post_date = post_date;
  }

  public LocalDateTime getCreateDateGMT() {
    return post_date_gmt;
  }

  public void setCreateDateGMT(LocalDateTime post_date_gmt) {
    this.post_date_gmt = post_date_gmt;
  }

  public LocalDateTime getDateModified() {
    return post_modified;
  }

  public void setDateModified(LocalDateTime post_modified) {
    this.post_modified = post_modified;
  }

  public LocalDateTime getDateModifiedGMT() {
    return post_modified_gmt;
  }

  public void setDateModifiedGMT(LocalDateTime post_modified_gmt) {
    this.post_modified_gmt = post_modified_gmt;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE, true);
  }
}
