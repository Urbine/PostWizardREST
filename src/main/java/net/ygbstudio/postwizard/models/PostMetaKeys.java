package net.ygbstudio.postwizard.models;

/**
 * Enum representing the keys used in post metadata. This enum provides a way to manage and retrieve
 * post metadata keys in a type-safe manner.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
public enum PostMetaKeys {
  ID("post_id"),
  HOURS("hours"),
  MINUTES("minute"),
  SECONDS("second"),
  EMBED("embed"),
  PARTNER("partner"),
  ORIENTATION("video_orientation"),
  ETHNICITY("ethnicity"),
  HAIRCOLOR("hair_color"),
  HDVIDEO("hd_video"),
  THUMBNAIL("thumb"),
  PRODUCTION("production"),
  VIDEOURL("video_url"),
  DURATION("duration"),
  YOAST_FOCUSKW("_yoast_wpseo_focuskw"),
  YOAST_METADESC("_yoast_wpseo_metadesc"),
  OTHERS("otherKeys"),
  FEATURED("featured_video");

  private final String value;

  PostMetaKeys(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }
}
