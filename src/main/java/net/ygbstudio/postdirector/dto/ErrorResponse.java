package net.ygbstudio.postdirector.dto;

/**
 * ErrorResponse is a DTO class that represents an error response
 * from the REST API. It contains fields for the error type,
 * a message describing the error, the HTTP status code,
 * and a timestamp indicating when the error occurred.
 * 
 * This class is used to standardize error responses
 * across the API, making it easier for clients
 * to handle errors consistently.
 * 
 * @author Yoham Gabriel B @ YGB Studio
 *
 */
public class ErrorResponse {
	private String error;
	private String message;
	private int status;
	private String timestamp;

	public ErrorResponse(String error, String message, int status) {
		super();
		this.error = error;
		this.message = message;
		this.status = status;
		this.timestamp = java.time.LocalDateTime.now().toString();
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

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

}
