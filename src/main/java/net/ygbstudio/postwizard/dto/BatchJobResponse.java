package net.ygbstudio.postwizard.dto;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbPropertyOrder;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * BatchJobResponse is a DTO class that represents a response from the server for batch job
 * operations. It contains fields for a message, HTTP status code, a timestamp indicating when the
 * response was created, and a list of post IDs that were processed in the batch job.
 *
 * <p>This class is used to standardize responses for batch operations across the API, making it
 * easier for clients to handle responses consistently.
 *
 * @author Yoham Gabriel B @ YGB Studio
 */
@JsonbPropertyOrder({"message", "status", "postIds", "timestamp"})
public class BatchJobResponse {

  @JsonbProperty("message")
  private String message;

  @JsonbProperty("status")
  private int status;

  @JsonbProperty("timestamp")
  private String timestamp;

  @JsonbProperty("processed")
  private List<Long> postIds;

  public BatchJobResponse(String message, int status, List<Long> postIds) {
    super();
    this.message = message;
    this.status = status;
    this.timestamp = LocalDateTime.now().toString();
    this.postIds = postIds;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public List<Long> getPostIds() {
    return postIds;
  }

  public void setPostIds(List<Long> postIds) {
    this.postIds = postIds;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE, true);
  }
}
