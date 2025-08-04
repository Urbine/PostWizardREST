package net.ygbstudio.postdirector.dto;

// Jakarta imports
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbPropertyOrder;
import jakarta.json.bind.annotation.JsonbNillable;

// Third-party imports
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * Data Transfer Object (DTO) for client post metadata.
 * This class represents the metadata associated with a post in the
 * Post Director application, including video details and other attributes.
 * 
 * @author Yoham Gabriel @ YGB Studio
 */
@JsonbPropertyOrder(value = {
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
		"embedCode" })
public class ClientPostMeta {

	@JsonbNillable
	@JsonbProperty("post_id")
	private long ID;

	@JsonbNillable
	@JsonbProperty("production")
	private String videoProduction;

	@JsonbNillable
	@JsonbProperty("video_orientation")
	private String videoOrientation;

	@JsonbNillable
	@JsonbProperty("ethnicity")
	private String ethnicity;

	@JsonbNillable
	@JsonbProperty("hair_color")
	private String hairColor;

	@JsonbNillable
	@JsonbProperty("hd_video")
	private boolean videoHD;

	@JsonbNillable
	@JsonbProperty("video_url")
	private String videoURL;

	@JsonbNillable
	@JsonbProperty("hours")
	private int hours;

	@JsonbNillable
	@JsonbProperty("minute")
	private int minutes;

	@JsonbNillable
	@JsonbProperty("second")
	private int seconds;

	@JsonbNillable
	@JsonbProperty("thumb")
	private String thumbURI;

	@JsonbNillable
	@JsonbProperty("embed")
	private String embedCode;

	public ClientPostMeta() {
		super();
	}

	public ClientPostMeta(long iD,
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
		return ToStringBuilder
				.reflectionToString(this, ToStringStyle.JSON_STYLE, true);
	}

}
