/*
 * SPDX-License-Identifier: MPL-2.0
 * Copyright (c) Yoham Gabriel B.
 */

package net.ygbstudio.postwizard.dto;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbPropertyOrder;
import java.time.Instant;
import java.util.List;
import java.util.function.Supplier;

/**
 * ServerResult is a DTO class that represents a response from the server for operations that return
 * another client-side DTO as part of their response as long as they implement the {@link
 * ClientDeliverable} interface.
 *
 * <p>It contains fields for an action message, HTTP status code, a timestamp indicating when the
 * response was created, and a result object that is compatible with JsonB binding to preserve
 * property names for client-side communication.
 *
 * <p>This class is used to standardize responses for data modification operations across the API,
 * making it easier for clients to handle responses consistently with leaking internal member
 * conventions.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@JsonbPropertyOrder({"action", "timestamp", "result", "status"})
public class ServerResult {

  @JsonbProperty("action")
  private String action;

  @JsonbProperty("data")
  private List<ClientDeliverable> result;

  @JsonbProperty("status_code")
  private int status;

  @JsonbProperty("completed_at")
  private Instant timestamp;

  public ServerResult(Supplier<String> action, List<ClientDeliverable> result, int status) {
    this.action = action.get();
    this.result = result;
    this.status = status;
    this.timestamp = Instant.now();
  }

  public List<ClientDeliverable> getResult() {
    return result;
  }

  public void setResult(List<ClientDeliverable> result) {
    this.result = result;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
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
}
