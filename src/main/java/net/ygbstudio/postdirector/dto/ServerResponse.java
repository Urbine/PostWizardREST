package net.ygbstudio.postdirector.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * ServerResponse is a DTO class that represents a response from the server.
 * It contains fields for a message, HTTP status code, and a timestamp
 * indicating when the response was created.
 * 
 * This class is used to standardize responses across the API,
 * making it easier for clients to handle responses consistently.
 * 
 * @author Yoham Gabriel B @ YGB Studio
 *
 */
public class ServerResponse {
	private String message;
	private int status;
	private String timestamp;

	public ServerResponse(String message, int status) {
		super();
		this.message = message;
		this.status = status;
		this.timestamp = java.time.LocalDateTime.now().toString();
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
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE, true);
	}

}
