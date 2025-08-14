package net.ygbstudio.postdirector.models;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbPropertyOrder;
import java.time.Duration;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Represents metadata for a post in the Post Director application.
 *
 * <p>This class contains various attributes related to a video post, such as production details,
 * orientation, and other fields that model the object.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@JsonbPropertyOrder(
    value = {
      "ID",
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
public class PostMeta {

  @JsonbProperty("post_id")
  private long ID;

  @JsonbProperty("production")
  private String videoProduction;

  @JsonbProperty("orientation")
  private String videoOrientation;

  @JsonbProperty("ethnicity")
  private String ethnicity;

  @JsonbProperty("hair_color")
  private String hairColor;

  @JsonbProperty("hd_video")
  private Boolean videoHD;

  @JsonbProperty("video_url")
  private String videoURL;

  @JsonbProperty("hours")
  private int hours;

  @JsonbProperty("minutes")
  private int minutes;

  @JsonbProperty("seconds")
  private int seconds;

  @JsonbProperty("duration")
  private Duration vidDuration;

  @JsonbProperty("thumbnail_uri")
  private String thumbURI;

  @JsonbProperty("thumbnail_uri")
  private String embedCode;

  public PostMeta(
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
      String embedCode) {
    super();
    ID = iD;
    this.videoProduction = videoProduction;
    this.videoOrientation = videoOrientation;
    this.ethnicity = ethnicity;
    this.hairColor = hairColor;
    this.videoHD = videoHD;
    this.videoURL = videoURL;
    this.hours = hours;
    this.minutes = minutes;
    this.seconds = seconds;
    this.vidDuration = Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds);
    this.thumbURI = thumbURI;
    this.embedCode = embedCode;
  }

  public long getID() {
    return ID;
  }

  public void setID(long iD) {
    ID = iD;
  }

  public String getVideoProduction() {
    return videoProduction;
  }

  public void setVideoProduction(String videoProduction) {
    this.videoProduction = videoProduction;
  }

  public String getVideoOrientation() {
    return videoOrientation;
  }

  public void setVideoOrientation(String videoOrientation) {
    this.videoOrientation = videoOrientation;
  }

  public String getEthnicity() {
    return ethnicity;
  }

  public void setEthnicity(String ethnicity) {
    this.ethnicity = ethnicity;
  }

  public String getHairColor() {
    return hairColor;
  }

  public void setHairColor(String hairColor) {
    this.hairColor = hairColor;
  }

  public Boolean getVideoHD() {
    return videoHD;
  }

  public void setVideoHD(Boolean videoHD) {
    this.videoHD = videoHD;
  }

  public String getVideoURL() {
    return videoURL;
  }

  public void setVideoURL(String videoURL) {
    this.videoURL = videoURL;
  }

  public int getHours() {
    return hours;
  }

  public void setHours(int hours) {
    this.hours = hours;
  }

  public int getMinutes() {
    return minutes;
  }

  public void setMinutes(int minutes) {
    this.minutes = minutes;
  }

  public int getSeconds() {
    return seconds;
  }

  public void setSeconds(int seconds) {
    this.seconds = seconds;
  }

  public Duration getVidDuration() {
    return vidDuration;
  }

  public void setVidDuration(Duration vidDuration) {
    this.vidDuration = vidDuration;
  }

  public String getThumbURI() {
    return thumbURI;
  }

  public void setThumbURI(String thumbURI) {
    this.thumbURI = thumbURI;
  }

  public String getEmbedCode() {
    return embedCode;
  }

  public void setEmbedCode(String embedCode) {
    this.embedCode = embedCode;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE, true);
  }
}
