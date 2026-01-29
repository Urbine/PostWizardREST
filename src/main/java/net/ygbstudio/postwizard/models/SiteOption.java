/*
 * SPDX-License-Identifier: MPL-2.0
 * Copyright (c) Yoham Gabriel B.
 */

package net.ygbstudio.postwizard.models;

/**
 * Represents WordPress options stored in the database.
 *
 * <p>Site options enum class for service-layer validation of site configuration in a WordPress
 * installation.
 *
 * <p>This model is not exhaustive and more options may be added in the future.
 *
 * @see net.ygbstudio.postwizard.entities.WPOptions
 * @author Yoham Gabriel @ YGB Studio
 */
public enum SiteOption {
  SITE_URL("siteurl"),
  HOME_URL("home"),
  BLOG_NAME("blogname"),
  BLOG_DESCRIPTION("blogdescription"),
  ADMIN_EMAIL("admin_email"),
  TIMEZONE_STRING("timezone_str");

  private final String value;

  SiteOption(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }
}
