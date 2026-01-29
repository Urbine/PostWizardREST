/*
 * SPDX-License-Identifier: MPL-2.0
 * Copyright (c) Yoham Gabriel B.
 */

package net.ygbstudio.postwizard.dto;

import java.time.Instant;
import java.util.StringJoiner;

/**
 * ErrorResponse is a DTO class that represents an error response from the REST API. It contains
 * fields for the error type, a message describing the error, the HTTP status code, and a timestamp
 * indicating when the error occurred.
 *
 * <p>This class is used to standardize error responses across the API, making it easier for clients
 * to handle errors consistently.
 *
 * @author Yoham Gabriel B @ YGB Studio
 */
public class ErrorResponse {
  private String error;
  private String message;
  private int status;
  private Instant timestamp;

  /** No argument constructor for JSON deserialization in test cases. */
  public ErrorResponse() {}

  /**
   * Constructor for ErrorResponse.
   *
   * @param error A brief description of the error type.
   * @param message A detailed message describing the error.
   * @param status The HTTP status code representing the error.
   */
  public ErrorResponse(String error, String message, int status) {
    super();
    this.error = error;
    this.message = message;
    this.status = status;
    this.timestamp = Instant.now();
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
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
    return new StringJoiner(", ", ErrorResponse.class.getSimpleName() + "{", "}")
        .add("error: \"" + error + "\"")
        .add("message:\"" + message + "\"")
        .add("status:" + status)
        .add("timestamp:" + timestamp)
        .toString();
  }
}
