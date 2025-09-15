package net.ygbstudio.postwizard.dto;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbPropertyOrder;
import java.time.Instant;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Data Transfer Object (DTO) for granting a token in the postwizard application. This class
 * encapsulates the details of the granted token, including its status, issuance date, and
 * expiration date.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@JsonbPropertyOrder({"accessToken", "type", "expiration"})
public class GrantToken {

  @JsonbProperty("access_token")
  private String accessToken;

  @JsonbProperty("type")
  private String type;

  @JsonbProperty("expiration")
  private Instant expirationDate;

  /**
   * Constructor for GrantToken.
   *
   * @param accessToken The access token string.
   * @param type The type of the token (e.g., Bearer).
   * @param expirationDate The expiration date of the token.
   */
  public GrantToken(String accessToken, String type) {
    super();
    this.accessToken = accessToken;
    this.type = type;
    this.expirationDate = Instant.now();
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Instant getExpirationDate() {
    return Instant.from(expirationDate);
  }

  public void setExpirationDate(Instant expirationDate) {
    this.expirationDate = Instant.from(expirationDate);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE, true);
  }
}
