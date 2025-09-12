package net.ygbstudio.postwizard.dto;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbPropertyOrder;
import java.time.Instant;
import java.util.Collection;
import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * PostDumpResponse is a DTO class that represents a response from the server for post dump
 * operations. It contains fields for a message, HTTP status code, a timestamp indicating when the
 * response was created, and a collection of items that were processed in the post dump operation.
 * The items can be of any type that implements the BatchDeliverable interface.
 *
 * <p>This class is used to standardize responses for post dump operations across the API, making it
 * easier for clients to handle responses consistently.
 *
 * @author Yoham Gabriel B @ YGB Studio
 */
@JsonbPropertyOrder({"message", "status", "siteItems", "totalProcessed", "timestamp"})
public class PostDumpResponse {

  @JsonbProperty("message")
  private String message;

  @JsonbProperty("status")
  private int status;

  @JsonbProperty("timestamp")
  private Instant timestamp;

  @JsonbProperty("dump")
  private Collection<? extends BatchDeliverable> siteItems;

  @JsonbProperty("totalProcessed")
  private long totalProcessed;

  /**
   * Constructor for PostDumpResponse.
   *
   * @param message A message describing the result of the post dump operation.
   * @param status The HTTP status code representing the outcome of the post dump operation.
   * @param siteItems A collection of items that were processed in the post dump operation.
   */
  public PostDumpResponse(
      String message, int status, Collection<? extends BatchDeliverable> siteItems) {
    super();
    this.message = message;
    this.status = status;
    this.timestamp = Instant.now();
    this.siteItems = siteItems;
    this.totalProcessed = Objects.nonNull(siteItems) ? siteItems.size() : 0;
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

  public Collection<? extends BatchDeliverable> getSiteItems() {
    return siteItems;
  }

  public void setSiteItems(Collection<? extends BatchDeliverable> siteItems) {
    this.siteItems = siteItems;
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
