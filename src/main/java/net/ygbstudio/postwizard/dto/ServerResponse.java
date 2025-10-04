package net.ygbstudio.postwizard.dto;

import java.time.Instant;
import java.util.StringJoiner;

/**
 * ServerResponse is a DTO class that represents a response from the server. It contains fields for
 * a message, HTTP status code, and a timestamp indicating when the response was created.
 *
 * <p>This class is used to standardize responses across the API, making it easier for clients to
 * handle responses consistently.
 *
 * @author Yoham Gabriel B @ YGB Studio
 */
public class ServerResponse {
  private String message;
  private int status;
  private Instant timestamp;

  /** No argument constructor for JSON deserialization in test cases. */
  public ServerResponse() {}

  /**
   * Constructor for ServerResponse.
   *
   * @param message A message describing the result of the server operation.
   * @param status The HTTP status code representing the outcome of the server operation.
   */
  public ServerResponse(String message, int status) {
    super();
    this.message = message;
    this.status = status;
    this.timestamp = Instant.now();
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

  @Override
  public String toString() {
    return new StringJoiner(", ", ServerResponse.class.getSimpleName() + "[", "]")
        .add("message='" + message + "'")
        .add("status=" + status)
        .add("timestamp=" + timestamp)
        .toString();
  }
}
