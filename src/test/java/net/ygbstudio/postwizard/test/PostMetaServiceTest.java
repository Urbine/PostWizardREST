package net.ygbstudio.postwizard.test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import jakarta.inject.Inject;
import java.util.Collection;
import java.util.List;
import net.ygbstudio.postwizard.dto.ClientPostMeta;
import net.ygbstudio.postwizard.service.PostMetaService;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class PostMetaServiceTest {

  public static final String TESTWAR = "arquillian-pw-tests.war";

  @Deployment(testable = true)
  public static WebArchive createDeployment() {
    WebArchive archive =
        ShrinkWrap.create(WebArchive.class, TESTWAR)
            .addPackages(true, "net.ygbstudio.postwizard")
            .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

    return archive;
  }

  @Inject private PostMetaService postMetaService;

  @Test
  public void testService() {
    Assert.assertNotNull(postMetaService);
  }

  @Test
  public void testGetAllPosts() {
    Collection<ClientPostMeta> posts = postMetaService.getClientPostMetaAll();
    assertThat(posts, not(empty()));
  }

  @Test
  public void testPostsMetaByID() {
    ClientPostMeta posts = postMetaService.getClientPostMeta(1);
    assertThat(posts, notNullValue());
  }

  @Test
  public void testPostExists() {
    boolean hasMetaFields = postMetaService.hasMetaFields(2);
    assertThat(hasMetaFields, is(true));
  }

  @Test
  public void testIsValidEthnicity() {
    List<String> possibleEthnicities =
        List.of(
            "Asian",
            "Latino",
            "Mixed",
            "Ebony",
            "White",
            "Indian",
            "Middle Eastern",
            "fromSomewhere",
            "fromNowhere");
    assertThat(
        possibleEthnicities.stream().filter(postMetaService::isValidEthnicity).count(), is(7L));
  }

  @Test
  public void testIsValidHairColor() {
    List<String> possibleHairColors =
        List.of("Blonde", "Brown", "Black", "Red", "Other", "PureRed", "Purple", "Crazy");
    assertThat(
        possibleHairColors.stream().filter(postMetaService::isValidHairColor).count(), is(5L));
  }

  @Test
  public void testIsValidOrientation() {
    List<String> possibleOrientations = List.of("Straight", "Trans", "None", "Binary");
    assertThat(
        possibleOrientations.stream().filter(postMetaService::isValidOrientation).count(), is(2L));
  }

  @Test
  public void testIsValidProduction() {
    List<String> possibleProduction = List.of("Professional", "Homemade", "Garage");
    assertThat(
        possibleProduction.stream().filter(postMetaService::isValidProduction).count(), is(2L));
  }
}
