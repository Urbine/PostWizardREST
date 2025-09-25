package net.ygbstudio.postwizard.filters;

import static net.ygbstudio.postwizard.utils.Logging.loggingInit;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

@WebFilter("/*")
public class ProbeProtectionFilter implements Filter {
  private static final Logger probeProtectionFilterLog =
      Logger.getLogger(ProbeProtectionFilter.class.getName());

  @SuppressWarnings("unused")
  private static final FileHandler probeProtectionFilterLogFileHandler =
      loggingInit(probeProtectionFilterLog, Level.ALL, true);

  // patterns for probes / known bad filenames or extensions
  private static final Pattern PROBE_PATTERN =
      Pattern.compile(
          "\\.(?:php|phtml|php5|phpinfo|git|xml)$|\\bphpinfo\\b|\\.env$|wp-admin|wp-login\\.php|/vendor/|/composer\\.json$",
          Pattern.CASE_INSENSITIVE);

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;

    String path = request.getRequestURI();
    String qs = request.getQueryString();

    // quick fingerprint checks
    if (path != null && PROBE_PATTERN.matcher(path).find()) {
      probeProtectionFilterLog.log(
          Level.WARNING,
          "Probe blocked: path={0}, query={1}, ip={2}, ua={3}",
          new Object[] {path, qs, request.getRemoteAddr(), request.getHeader("User-Agent")});

      // respond with 404 and serve the generic static page
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.setHeader("X-Content-Type-Options", "nosniff");
      response.setHeader("X-Frame-Options", "DENY");
      response.setHeader("Referrer-Policy", "no-referrer");
      request.getRequestDispatcher("/404.jsp").forward(request, response);
      return;
    }

    try {
      chain.doFilter(req, res);
    } catch (Exception anyEx) {
      // Log full exception (server side)
      probeProtectionFilterLog.log(
          Level.SEVERE, "Unhandled exception for request " + path, new Object[] {anyEx});

      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      response.setHeader("X-Content-Type-Options", "nosniff");
      response.setHeader("X-Frame-Options", "DENY");
      response.setHeader("Referrer-Policy", "no-referrer");
      request.getRequestDispatcher("/error.jsp").forward(request, response);
    }
  }
}
