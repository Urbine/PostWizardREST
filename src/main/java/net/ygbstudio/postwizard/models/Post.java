package net.ygbstudio.postwizard.models;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbPropertyOrder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Represents a post in the Post Director application.
 *
 * <p>This class extends {@link PostMeta} and includes additional fields specific to a WordPress
 * post, such as author, content, title, slug, status, and type.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@JsonbPropertyOrder(
    value = {
      "ID",
      "postAuthor",
      "postContent",
      "postTitle",
      "postSlug",
      "postStatus",
      "postType",
      "videoProduction",
      "videoOrientation",
      "ethnicity",
      "hairColor",
      "videoHD",
      "videoURL",
      "hours",
      "minutes",
      "seconds",
      "vidDuration",
      "thumbURI",
      "embedCode"
    })
public class Post extends PostMeta {

  public Post(
      long iD,
      String videoProduction,
      String videoOrientation,
      String ethnicity,
      String hairColor,
      Boolean videoHD,
      String videoURL,
      int hours,
      int minutes,
      int seconds,
      String thumbURI,
      String embedCode,
      long postAuthor,
      String postContent,
      String postTitle,
      String postSlug,
      String postStatus,
      String postType) {
    super(
        iD,
        videoProduction,
        videoOrientation,
        ethnicity,
        hairColor,
        videoHD,
        videoURL,
        hours,
        minutes,
        seconds,
        thumbURI,
        embedCode);
    this.postAuthor = postAuthor;
    this.postContent = postContent;
    this.postTitle = postTitle;
    this.postSlug = postSlug;
    this.postStatus = postStatus;
    this.postType = postType;
  }

  @JsonbProperty("post_author")
  private long postAuthor;

  @JsonbProperty("post_content")
  private String postContent;

  @JsonbProperty("post_title")
  private String postTitle;

  @JsonbProperty("post_name")
  private String postSlug;

  @JsonbProperty("post_status")
  private String postStatus;

  @JsonbProperty("post_type")
  private String postType;

  public long getPostAuthor() {
    return postAuthor;
  }

  public void setPostAuthor(long postAuthor) {
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
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE, true);
  }
}
