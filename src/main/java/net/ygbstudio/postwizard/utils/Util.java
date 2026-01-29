/*
 * SPDX-License-Identifier: MPL-2.0
 * Copyright (c) Yoham Gabriel B.
 */

package net.ygbstudio.postwizard.utils;

/** Utility marker interface for reusable logic in the project. * */
public interface Util {

  static void unsupportedUtil() {
    throw new UnsupportedOperationException();
  }
}
