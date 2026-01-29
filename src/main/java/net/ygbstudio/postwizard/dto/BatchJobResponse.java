/*
 * SPDX-License-Identifier: MPL-2.0
 * Copyright (c) Yoham Gabriel B.
 */

package net.ygbstudio.postwizard.dto;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbPropertyOrder;
import java.time.Instant;
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
@JsonbPropertyOrder({"message", "status", "postIds", "totalProcessed", "timestamp"})
public class BatchJobResponse {

  @JsonbProperty("message")
  private String message;

  @JsonbProperty("status")
  private int status;

  @JsonbProperty("timestamp")
  private Instant timestamp;

  @JsonbProperty("processed")
  private List<Long> postIds;

  @JsonbProperty("totalProcessed")
  private long totalProcessed;

  /**
   * Constructor for BatchJobResponse.
   *
   * @param message A message describing the result of the batch job.
   * @param status The HTTP status code representing the outcome of the batch job.
   * @param postIds A list of post IDs that were processed in the batch job.
   */
  public BatchJobResponse(String message, int status, List<Long> postIds) {
    super();
    this.message = message;
    this.status = status;
    this.timestamp = Instant.now();
    this.postIds = List.copyOf(postIds);
    this.totalProcessed = postIds.size();
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

  public List<Long> getPostIds() {
    return List.copyOf(postIds);
  }

  public void setPostIds(List<Long> postIds) {
    this.postIds = List.copyOf(postIds);
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
