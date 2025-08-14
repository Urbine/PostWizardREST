package net.ygbstudio.postdirector.dto;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Data Transfer Object (DTO) for granting a token in the PostDirector application. This class
 * encapsulates the details of the granted token, including its status, issuance date, and
 * expiration date.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
public class GrantToken {

  private final String message;
  private int status;
  private Date issuanceDate;
  private Date expirationDate;

  public GrantToken(int status, Date issuanceDate, Date expirationDate) {
    super();
    this.message = "Token granted";
    this.status = status;
    this.issuanceDate = issuanceDate;
    this.expirationDate = expirationDate;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
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
    return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE, true);
  }
}
