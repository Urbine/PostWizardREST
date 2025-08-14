package net.ygbstudio.postdirector.auth;

import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Utility class for generating JWT signing keys. This class provides methods to generate random
 * secret keys for JWT signing using HMAC algorithms.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
public class JwtKeyGen {

  /**
   * Generates a random secret key for JWT signing.
   *
   * @param bits | Bit strength of the key (must be a multiple of 8).
   * @param algorithm | Algorithm to use for the key. Refer to {@link
   *     https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#KeyGenerator}
   *     for available algorithm names supported by {@link javax.crypto.spec.SecretKeySpec}.
   * @return
   */
  public static SecretKey generateKey(int bits, String algorithm) {
    if (bits % 8 != 0) throw new IllegalArgumentException("Bit strength must be multiple of 8.");

    int bytes = bits / 8;
    byte[] keyBytes = new byte[bytes];
    SecureRandom secRand = new SecureRandom();
    secRand.nextBytes(keyBytes);
    return new SecretKeySpec(keyBytes, algorithm);
  }

  /**
   * Generates a random secret key for HMAC SHA-256 signing.
   *
   * @return A SecretKey object for HMAC SHA-256.
   */
  public static SecretKey generateHS256Key() {
    return generateKey(256, "HmacSHA256");
  }

  /**
   * Generates a random secret key for HMAC SHA-256 signing and encodes it in Base64.
   *
   * @return A Base64 encoded string representation of the HMAC SHA-256 key.
   */
  public static String generateHS256KeyB64() {
    SecretKey keyHS256 = generateHS256Key();
    return Base64.getEncoder().encodeToString(keyHS256.getEncoded());
  }
}
