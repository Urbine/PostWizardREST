/*
 * SPDX-License-Identifier: MPL-2.0
 * Copyright (c) Yoham Gabriel B.
 */

package net.ygbstudio.postwizard.exceptions;

/**
 * Exception thrown when API credentials are unknown or invalid. This exception is used to indicate
 * that the provided API credentials do not match any known user or are not recognized by the
 * system.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
public class UnknownAPICredentials extends Exception {

  public UnknownAPICredentials() {
    super();
  }

  public UnknownAPICredentials(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public UnknownAPICredentials(String message, Throwable cause) {
    super(message, cause);
  }

  public UnknownAPICredentials(String message) {
    super(message);
  }

  public UnknownAPICredentials(Throwable cause) {
    super(cause);
  }
}
