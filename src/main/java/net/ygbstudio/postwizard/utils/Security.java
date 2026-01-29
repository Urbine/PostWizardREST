/*
 * SPDX-License-Identifier: MPL-2.0
 * Copyright (c) Yoham Gabriel B.
 */

package net.ygbstudio.postwizard.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.jspecify.annotations.NullMarked;

/**
 * Utility class for generating JWT signing keys. This class provides methods to generate random
 * secret keys for JWT signing using HMAC algorithms.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@NullMarked
public final class Security implements Util {

  private Security() {
    throw new AssertionError("Cannot instantiate utility class");
  }

  /**
   * Generates a random secret key for JWT signing.
   *
   * @param bits the bit strength of the key (must be a multiple of 8).
   * @param algorithm Algorithm to use for the key. Refer to {@link <a
   *     href="https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#KeyGenerator">StandardNames
   *     - KeyGenerator</a>} for available algorithm names supported by {@link SecretKeySpec}.
   * @return A SecretKey object for the specified algorithm.
   * @throws NoSuchAlgorithmException If the specified algorithm is not available. Exception is
   *     thrown by {@link SecureRandom#getInstanceStrong()} and passed to the caller for handling.
   */
  public static SecretKey generateKey(int bits, String algorithm) throws NoSuchAlgorithmException {
    if (bits % 8 != 0) throw new IllegalArgumentException("Bit strength must be multiple of 8.");

    int bytes = bits / 8;
    byte[] keyBytes = new byte[bytes];
    SecureRandom secRand = SecureRandom.getInstanceStrong();
    secRand.nextBytes(keyBytes);
    return new SecretKeySpec(keyBytes, algorithm);
  }

  /**
   * Generates a random secret key for HMAC SHA-256 signing.
   *
   * @return A SecretKey object for HMAC SHA-256.
   * @throws NoSuchAlgorithmException If the specified algorithm is not available. Exception is
   *     thrown by {@link SecureRandom#getInstanceStrong()} and passed to the caller for handling
   */
  public static SecretKey generateHS256Key() throws NoSuchAlgorithmException {
    return generateKey(256, "HmacSHA256");
  }

  /**
   * Generates a random secret key for HMAC SHA-256 signing and encodes it in Base64.
   *
   * @return A Base64 encoded string representation of the HMAC SHA-256 key.
   * @throws NoSuchAlgorithmException If the specified algorithm is not available. Exception is
   *     thrown by {@link SecureRandom#getInstanceStrong()} and passed to the caller for handling
   */
  public static String generateHS256KeyB64() throws NoSuchAlgorithmException {
    SecretKey keyHS256 = generateHS256Key();
    return Base64.getEncoder().encodeToString(keyHS256.getEncoded());
  }
}
