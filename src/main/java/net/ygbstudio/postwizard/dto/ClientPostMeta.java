package net.ygbstudio.postwizard.dto;

import jakarta.json.bind.annotation.JsonbNillable;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbPropertyOrder;
import jakarta.json.bind.annotation.JsonbVisibility;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Data Transfer Object (DTO) for client post metadata. This class represents the metadata
 * associated with a post in the PostWizard application, including video details and other
 * attributes.
 *
 * <p>The class fields are named according to the actual entries in the WordPress database, allowing
 * for easy validation when compared with the constants defined at {@link
 * net.ygbstudio.postwizard.models} while also avoiding the need to expose the database structure
 * directly to the client.
 *
 * @see net.ygbstudio.postwizard.models.PostMeta
 * @see net.ygbstudio.postwizard.entities.WPMeta
 * @author Yoham Gabriel @ YGB Studio
 */
@JsonbPropertyOrder(
    value = {
      "post_id",
      "production",
      "video_orientation",
      "ethnicity",
      "hair_color",
      "hd_video",
      "video_url",
      "hours",
      "minutes",
      "seconds",
      "thumb",
      "embed",
      "_yoast_wpseo_focuskw",
      "_yoast_wpseo_metadesc"
    })
@JsonbVisibility(ClientDTOVisibilityStrategy.class)
public class ClientPostMeta implements ClientBatchDeliverable {

  @JsonbNillable
  @JsonbProperty("postID")
  private long post_id;

  @JsonbNillable
  @JsonbProperty("production")
  private String production;

  @JsonbNillable
  @JsonbProperty("orientation")
  private String video_orientation;

  @JsonbNillable
  @JsonbProperty("ethnicity")
  private String ethnicity;

  @JsonbNillable
  @JsonbProperty("hairColor")
  private String hair_color;

  @JsonbNillable
  @JsonbProperty("hd")
  private Boolean hd_video;

  @JsonbNillable
  @JsonbProperty("videoURL")
  private String video_url;

  @JsonbNillable
  @JsonbProperty("hours")
  private int hours;

  @JsonbNillable
  @JsonbProperty("minutes")
  private int minute;

  @JsonbNillable
  @JsonbProperty("seconds")
  private int second;

  @JsonbNillable
  @JsonbProperty("thumbURL")
  private String thumb;

  @JsonbNillable
  @JsonbProperty("embedCode")
  private String embed;

  @JsonbNillable
  @JsonbProperty("yoastFocusKw")
  private String _yoast_wpseo_focuskw;

  @JsonbNillable
  @JsonbProperty("yoastMetaDesc")
  private String _yoast_wpseo_metadesc;

  public ClientPostMeta() {
    super();
  }

  /**
   * Constructor for ClientPostMeta. When receiving data from the client, all fields are optional
   * and nullable, which means that the client will send only the fields that need to be updated.
   *
   * <p>** post_id will be provided in the path of the request or in batch operations **
   *
   * @param post_id the ID of the post
   * @param production the production means (e.g., "Professional", "Homemade")
   * @param video_orientation the orientation of the video (e.g., "straight", "trans")
   * @param ethnicity ethnicity of the performer (e.g., "White", "Asian")
   * @param hair_color the hair color of the performer (e.g., "Blonde", "Brown")
   * @param hd_video indicates if the video is in HD quality
   * @param video_url source URL of the video, if any
   * @param hours duration hours of the video
   * @param minute duration minutes of the video
   * @param second duration seconds of the video
   * @param thumb URL of the video's thumbnail image
   * @param embed embed code for the video, if any
   * @param _yoast_wpseo_focuskw Yoast SEO focus keyword for the post, if any
   * @param _yoast_wpseo_metadesc Yoast SEO meta description for the post, if any
   */
  public ClientPostMeta(
      long post_id,
      String production,
      String video_orientation,
      String ethnicity,
      String hair_color,
      Boolean hd_video,
      String video_url,
      int hours,
      int minute,
      int second,
      String thumb,
      String embed,
      String _yoast_wpseo_focuskw,
      String _yoast_wpseo_metadesc) {
    super();
    this.post_id = post_id;
    this.production = production;
    this.video_orientation = video_orientation;
    this.ethnicity = ethnicity;
    this.hair_color = hair_color;
    this.hd_video = hd_video;
    this.video_url = video_url;
    this.hours = hours;
    this.minute = minute;
    this.second = second;
    this.thumb = thumb;
    this.embed = embed;
    this._yoast_wpseo_focuskw = _yoast_wpseo_focuskw;
    this._yoast_wpseo_metadesc = _yoast_wpseo_metadesc;
  }

  public long getID() {
    return post_id;
  }

  public void setID(long post_id) {
    this.post_id = post_id;
  }

  public String getVideoProduction() {
    return production;
  }

  public void setVideoProduction(String production) {
    this.production = production;
  }

  public String getVideoOrientation() {
    return video_orientation;
  }

  public void setVideoOrientation(String video_orientation) {
    this.video_orientation = video_orientation;
  }

  public String getEthnicity() {
    return ethnicity;
  }

  public void setEthnicity(String ethnicity) {
    this.ethnicity = ethnicity;
  }

  public String getHairColor() {
    return hair_color;
  }

  public void setHairColor(String hair_color) {
    this.hair_color = hair_color;
  }

  public Boolean getVideoHD() {
    return hd_video;
  }

  public void setVideoHD(Boolean hd_video) {
    this.hd_video = hd_video;
  }

  public String getVideoURL() {
    return video_url;
  }

  public void setVideoURL(String video_url) {
    this.video_url = video_url;
  }

  public int getHours() {
    return hours;
  }

  public void setHours(int hours) {
    this.hours = hours;
  }

  public int getMinutes() {
    return minute;
  }

  public void setMinutes(int minute) {
    this.minute = minute;
  }

  public int getSeconds() {
    return second;
  }

  public void setSeconds(int second) {
    this.second = second;
  }

  public String getThumbURI() {
    return thumb;
  }

  public void setThumb(String thumb) {
    this.thumb = thumb;
  }

  public String getEmbedCode() {
    return embed;
  }

  public void setEmbedCode(String embed) {
    this.embed = embed;
  }

  public String getYoastFocusKW() {
    return _yoast_wpseo_focuskw;
  }

  public void setYoastFocusKW(String _yoast_wpseo_focuskw) {
    this._yoast_wpseo_focuskw = _yoast_wpseo_focuskw;
  }

  public String getYoastMetaDesc() {
    return _yoast_wpseo_metadesc;
  }

  public void setYoastMetaDesc(String _yoast_wpseo_metadesc) {
    this._yoast_wpseo_metadesc = _yoast_wpseo_metadesc;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE, true);
  }
}
