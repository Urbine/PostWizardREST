package net.ygbstudio.postdirector.dto;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class GrantToken {

	private int status;
	private String token;
	private Date issuanceDate;
	private Date expirationDate;

	public GrantToken(int status, String token, Date issuanceDate, Date expirationDate) {
		super();
		this.status = status;
		this.token = token;
		this.issuanceDate = issuanceDate;
		this.expirationDate = expirationDate;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getIssuanceDate() {
		return issuanceDate;
	}

	public void setIssuanceDate(Date issuanceDate) {
		this.issuanceDate = issuanceDate;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	@Override
	public String toString() {
		return ToStringBuilder
				.reflectionToString(this, ToStringStyle.JSON_STYLE, true);
	}

}
