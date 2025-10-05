package net.ygbstudio.postwizard.test;

import static net.ygbstudio.postwizard.utils.Helpers.getPropertiesFromResources;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;

import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import java.io.File;
import java.net.URL;
import java.util.Base64;
import java.util.Optional;
import java.util.Properties;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jspecify.annotations.NonNull;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class PostDataControllerTests {

  public static final String TEST_WAR = "arquillian-pw-tests-posts-rest.war";
  public static final String API_VERSION = "v1";

  private static final Client client = ClientBuilder.newClient();

  @ArquillianResource private URL baseURL;

  @Deployment(testable = false)
  public static WebArchive createDeployment() {
    File[] authLibs =
        Maven.resolver()
            .loadPomFromFile("pom.xml")
            .resolve(
                "io.jsonwebtoken:jjwt-api:0.12.6",
                "io.jsonwebtoken:jjwt-impl:0.12.6",
                "io.jsonwebtoken:jjwt-jackson:0.12.6")
            .withTransitivity()
            .asFile();

    WebArchive archive =
        ShrinkWrap.create(WebArchive.class, TEST_WAR)
            .addPackages(true, "net.ygbstudio.postwizard")
            .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
            .addAsResource("ApplicationProperties.properties")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsLibraries(authLibs);

    return archive;
  }

  @NonNull
  private String getBearerToken() {
    Optional<Properties> loginProps =
        getPropertiesFromResources("ApplicationProperties.properties");
    assertThat(loginProps.isPresent(), is(true));
    String login = baseURL + API_VERSION + "/auth/login";
    String loginStr =
        loginProps.get().getProperty("api.username")
            + ":"
            + loginProps.get().getProperty("api.key");
    String authHeader = "Basic " + Base64.getEncoder().encodeToString(loginStr.getBytes());
    WebTarget target = client.target(login);
    Response response = target.request().header("Authorization", authHeader).get();
    JsonObject responseJson = response.readEntity(JsonObject.class);
    String token = responseJson.getString("access_token");
    assertThat(token, notNullValue());
    assertThat(token, not(emptyString()));
    return "Bearer " + token.trim();
  }

  private void testEndpointsAuth(String bearerToken, String requestURL) {
    WebTarget target = client.target(requestURL);
    Response requestResponse = target.request().get();
    assertEquals("This request should be unauthorized!", 401, requestResponse.getStatus());

    WebTarget targetAuth = client.target(requestURL);
    Response requestResponseAuth = targetAuth.request().header("Authorization", bearerToken).get();
    assertEquals("This request should be authorized!", 200, requestResponseAuth.getStatus());
    JsonObject responseJson = requestResponseAuth.readEntity(JsonObject.class);
    assertThat(
        "This request should return a JSON object!", responseJson, instanceOf(JsonObject.class));
  }

  private void testBadRequest(String bearerToken, String invalidRequestURL) {
    WebTarget testTarget = client.target(invalidRequestURL);
    Response requestResponseInvalidType =
        testTarget.request().header("Authorization", bearerToken).get();
    assertEquals("This request should be bad!", 400, requestResponseInvalidType.getStatus());
  }

  private void testNotFound(String bearerToken, String notFoundRequestURL) {
    WebTarget testTarget = client.target(notFoundRequestURL);
    Response requestResponseNotFound =
        testTarget.request().header("Authorization", bearerToken).get();
    assertEquals("This request should be Not Found!", 404, requestResponseNotFound.getStatus());
  }

  @Test
  @RunAsClient
  public void testGetPostMeta() {
    String requestURL = baseURL + API_VERSION + "/posts/meta/1";
    String authBearer = getBearerToken();
    testEndpointsAuth(authBearer, requestURL);

    String requestURLInvalidType = baseURL + API_VERSION + "/posts/meta/0";
    testBadRequest(authBearer, requestURLInvalidType);

    String requestURLNotFound = baseURL + API_VERSION + "/posts/meta/99999";
    testNotFound(authBearer, requestURLNotFound);
  }

  @Test
  @RunAsClient
  public void testGetPostMetaDump() {
    String requestURL = baseURL + API_VERSION + "/posts/meta/dump";
    String authBearer = getBearerToken();
    testEndpointsAuth(authBearer, requestURL);
  }

  @Test
  @RunAsClient
  public void getPostDump() {
    String requestURL = baseURL + API_VERSION + "/posts/dump";
    String authBearer = getBearerToken();
    testEndpointsAuth(authBearer, requestURL);
  }

  @Test
  @RunAsClient
  public void testGetPostById() {
    String requestURL = baseURL + API_VERSION + "/posts/1";
    String authBearer = getBearerToken();
    testEndpointsAuth(authBearer, requestURL);

    String requestURLInvalidType = baseURL + API_VERSION + "/posts/0";
    testBadRequest(authBearer, requestURLInvalidType);

    String requestURLNotFound = baseURL + API_VERSION + "/posts/99999";
    testNotFound(authBearer, requestURLNotFound);
  }

  @Test
  @RunAsClient
  public void testGetPostDumpByType() {
    String requestURL = baseURL + API_VERSION + "/posts/dump?type=post";
    String authBearer = getBearerToken();
    testEndpointsAuth(authBearer, requestURL);

    String requestURLInvalidType = baseURL + API_VERSION + "/posts/dump?type=notAValidType";
    testBadRequest(authBearer, requestURLInvalidType);
  }
}
