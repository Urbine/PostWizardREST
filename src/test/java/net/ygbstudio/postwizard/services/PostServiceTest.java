package net.ygbstudio.postwizard.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import jakarta.inject.Inject;
import java.util.Collection;
import java.util.List;
import net.ygbstudio.postwizard.dto.ClientPost;
import net.ygbstudio.postwizard.models.PostType;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class PostServiceTest {

  public static final String TEST_WAR = "arquillian-pw-tests-post-service.war";

  @Deployment
  public static WebArchive createDeployment() {
    WebArchive archive =
        ShrinkWrap.create(WebArchive.class, TEST_WAR)
            .addPackages(true, "net.ygbstudio.postwizard")
            .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    return archive;
  }

  @Inject private PostService postService;

  @Test
  public void testService() {
    Assert.assertNotNull(postService);
  }

  @Test
  public void testGetAllPosts() {
    Collection<ClientPost> posts = postService.getClientPostAll();
    assertThat(posts, not(empty()));
  }

  @Test
  public void testGetAllPostsByType() {
    Collection<ClientPost> posts = postService.getAllClientPostByType(PostType.POST);
    assertThat(posts, not(empty()));

    Collection<ClientPost> attachments = postService.getAllClientPostByType(PostType.ATTACHMENT);
    assertThat(attachments, not(empty()));

    Collection<ClientPost> photos = postService.getAllClientPostByType(PostType.PHOTOS);
    assertThat(photos, not(empty()));

    Collection<ClientPost> allTypes = postService.getAllClientPostByType(PostType.PHOTOS);
    assertThat(allTypes, not(empty()));
  }

  @Test
  public void testGetPostByID() {
    boolean post = postService.postExists(1);
    assertThat(post, is(true));
  }

  @Test
  public void testPostExists() {
    boolean exists = postService.postExists(1);
    assertThat(exists, is(true));
  }

  @Test
  public void testIsValidPostType() {
    // Only 4 valid types in the following list
    List<String> options = List.of("post", "attachment", "photos", "all", "notAType", "AnyString");
    assertThat(options.stream().filter(postService::isValidPostType).count(), is(4L));
  }
}
