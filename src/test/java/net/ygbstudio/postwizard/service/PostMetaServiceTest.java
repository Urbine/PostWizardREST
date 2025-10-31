package net.ygbstudio.postwizard.service;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.in;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import jakarta.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.ygbstudio.postwizard.dto.ClientPostMeta;
import net.ygbstudio.postwizard.entities.WPMeta;
import net.ygbstudio.postwizard.models.PostMetaKeys;
import net.ygbstudio.postwizard.models.ToggleField;
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

  public static final String TEST_WAR = "arquillian-pw-tests-pmeta-service.war";

  @Deployment
  public static WebArchive createDeployment() {
    WebArchive archive =
        ShrinkWrap.create(WebArchive.class, TEST_WAR)
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

  @Test
  public void testIsValidToggleField() {
    List<String> possibleToggleFields = List.of("on", "off", "notAToggleField", "anyString");
    assertThat(
        possibleToggleFields.stream().filter(postMetaService::isValidToggleField).count(), is(2L));
  }

  @Test
  public void testGetRandomPostIDs() {
    Set<Long> featuredPostIDs =
        postMetaService.filterMetaKeyEntriesBy(
            PostMetaKeys.FEATURED,
            p -> p.getMetaFieldValue().equals(ToggleField.ON.toString()),
            post -> post.getPost().getId());

    Predicate<? super WPMeta> excludePredicate =
        featuredPostIDs.isEmpty()
            ? post ->
                post.getMetaFieldKey().equals(PostMetaKeys.FEATURED.toString())
                    && post.getMetaFieldValue().equals(ToggleField.OFF.toString())
            : post -> !featuredPostIDs.contains(post.getPost());

    Set<Long> randomPostIDs =
        postMetaService
            .getRandomPostsByMetaKey(PostMetaKeys.FEATURED, 10, excludePredicate)
            .stream()
            .map(post -> post.getPost().getId())
            .collect(Collectors.toUnmodifiableSet());

    assertThat(randomPostIDs, not(empty()));
    assertThat(randomPostIDs.size(), is(10));
    randomPostIDs.forEach(postID -> assertThat(postID, not(in(featuredPostIDs))));
  }

  @Test
  public void testFeaturedVideoMethods() {
    if (!postMetaService
        .filterMetaKeyEntriesBy(
            PostMetaKeys.FEATURED,
            p -> p.getMetaFieldValue().equals(ToggleField.ON.toString()),
            post -> post.getPost().getId())
        .isEmpty()) {
      Set<Long> oldFeaturedIDs = postMetaService.disableFeaturedVideos();
      assertThat(oldFeaturedIDs, not(empty()));

      // Enable the videos again, so that the database is in a clean state for the next test.
      Set<Long> featuredVideos = postMetaService.featureVideos(oldFeaturedIDs);
      assertThat(featuredVideos, not(empty()));

      // Check that the number of featured videos is the same as the number of disabled featured
      // videos.
      assertThat(featuredVideos.size(), is(oldFeaturedIDs.size()));

      // randomise featured videos
      int numberOfPosts = 20;
      Set<Long> randomFeaturedVideos = postMetaService.randomiseFeaturedVideos(numberOfPosts);
      assertThat(randomFeaturedVideos, not(empty()));

      // Post IDs must be unique, at least different to the last randomisation batch.
      randomFeaturedVideos.forEach(postID -> assertThat(postID, not(in(oldFeaturedIDs))));

      // Check that the number of featured videos is the same as the number of
      // random featured videos I asked for.
      assertThat(randomFeaturedVideos.size(), is(numberOfPosts));

      // When we disable the featured flag, the method has to return the modified entries,
      // and it has to be the same to make sure the internals of the method are working as expected.
      Set<Long> randomOff =
          postMetaService.toggleFeaturedVideos(randomFeaturedVideos, ToggleField.OFF);
      assertThat(randomOff.size(), is(randomFeaturedVideos.size()));
      randomOff.forEach(postID -> assertThat(postID, is(in(randomFeaturedVideos))));

      // Enable the old videos again, so that the database remains as it was before.
      Set<Long> resetFeatured = postMetaService.featureVideos(oldFeaturedIDs);
      assertThat(resetFeatured.size(), is(oldFeaturedIDs.size()));
    }
  }
}
