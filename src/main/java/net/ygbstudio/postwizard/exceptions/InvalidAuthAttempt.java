/*
 * SPDX-License-Identifier: MPL-2.0
 * Copyright (c) Yoham Gabriel B.
 */

package net.ygbstudio.postwizard.exceptions;

/**
 * Exception thrown when an invalid authentication attempt is made. This exception is used to
 * indicate that the provided credentials do not match any known user or are not recognized by the
 * system.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
public class InvalidAuthAttempt extends RuntimeException {

  public InvalidAuthAttempt() {
    super();
  }

  public InvalidAuthAttempt(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public InvalidAuthAttempt(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidAuthAttempt(String message) {
    super(message);
  }

  public InvalidAuthAttempt(Throwable cause) {
    super(cause);
  }
}
