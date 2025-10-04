package net.ygbstudio.postwizard.dto;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbPropertyOrder;
import java.time.Instant;
import java.util.List;

/**
 * ServerResult is a DTO class that represents a response from the server for operations that return
 * another client-side DTO as part of their response as long as they implement the {@link
 * EmbeddedResult} interface.
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
  private List<EmbeddedResult> result;

  @JsonbProperty("status_code")
  private int status;

  @JsonbProperty("completed_at")
  private Instant timestamp;

  public ServerResult(List<EmbeddedResult> result, String action, int status) {
    this.result = result;
    this.action = action;
    this.status = status;
    this.timestamp = Instant.now();
  }

  public List<EmbeddedResult> getResult() {
    return result;
  }

  public void setResult(List<EmbeddedResult> result) {
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
