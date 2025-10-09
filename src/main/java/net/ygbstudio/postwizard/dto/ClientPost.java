package net.ygbstudio.postwizard.dto;

import jakarta.json.bind.annotation.JsonbNillable;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbPropertyOrder;
import jakarta.json.bind.annotation.JsonbVisibility;
import java.time.LocalDateTime;
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
 * @see net.ygbstudio.postwizard.entities.WPost
 * @see net.ygbstudio.postwizard.models.PostType
 * @see net.ygbstudio.postwizard.models.PostKeys
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
public class ClientPost implements ClientBatchDeliverable {

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
