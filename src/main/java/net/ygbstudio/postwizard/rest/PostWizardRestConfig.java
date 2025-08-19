package net.ygbstudio.postwizard.rest;

import jakarta.annotation.security.DeclareRoles;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Configuration class for the postwizard RESTful web service. This class sets the base URI path for
 * the REST API to "v1".
 *
 * @author Yoham Gabriel @ YGB Studio
 * @version 1.0
 */
@ApplicationScoped
@DeclareRoles({"user", "caller"})
@ApplicationPath("v1")
public class PostWizardRestConfig extends Application {}
