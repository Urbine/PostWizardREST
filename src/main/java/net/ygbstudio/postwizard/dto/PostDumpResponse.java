package net.ygbstudio.postwizard.dto;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbPropertyOrder;
import java.time.Instant;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jspecify.annotations.NullMarked;

/**
 * PostDumpResponse is a DTO class that represents a response from the server for post dump
 * operations. It contains fields for a message, HTTP status code, a timestamp indicating when the
 * response was created, and a List of items that were processed in the post dump operation. The
 * items can be of any type that implements the ClientBatchDeliverable interface.
 *
 * <p>This class is used to standardize responses for post dump operations across the API, making it
 * easier for clients to handle responses consistently.
 *
 * @author Yoham Gabriel B @ YGB Studio
 */
@NullMarked
@JsonbPropertyOrder({"message", "status", "siteItems", "totalProcessed", "timestamp"})
public class PostDumpResponse {

  @JsonbProperty("message")
  private String message;

  @JsonbProperty("status")
  private int status;

  @JsonbProperty("timestamp")
  private Instant timestamp;

  @JsonbProperty("dump")
  private List<? extends ClientBatchDeliverable> siteItems;

  @JsonbProperty("totalProcessed")
  private long totalProcessed;

  /** No argument constructor for JSON deserialization in test cases. */
  public PostDumpResponse() {}

  /**
   * Constructor for PostDumpResponse.
   *
   * @param message A message describing the result of the post dump operation.
   * @param status The HTTP status code representing the outcome of the post dump operation.
   * @param siteItems A List of items that were processed in the post dump operation.
   */
  public PostDumpResponse(
      String message, int status, List<? extends ClientBatchDeliverable> siteItems) {
    super();
    this.message = message;
    this.status = status;
    this.timestamp = Instant.now();
    this.siteItems = List.copyOf(siteItems);
    this.totalProcessed = siteItems.size();
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

  public Instant getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Instant timestamp) {
    this.timestamp = timestamp;
  }

  public List<? extends ClientBatchDeliverable> getSiteItems() {
    return List.copyOf(siteItems);
  }

  public void setSiteItems(List<? extends ClientBatchDeliverable> siteItems) {
    this.siteItems = List.copyOf(siteItems);
  }

  public long getTotalProcessed() {
    return totalProcessed;
  }

  public void setTotalProcessed(long totalProcessed) {
    this.totalProcessed = totalProcessed;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE, true);
  }
}
