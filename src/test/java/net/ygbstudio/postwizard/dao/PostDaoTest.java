package net.ygbstudio.postwizard.dao;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotNull;

import jakarta.inject.Inject;
import java.util.Set;
import net.ygbstudio.postwizard.entities.WPMeta;
import net.ygbstudio.postwizard.entities.taxonomies.WPTermRelationships;
import net.ygbstudio.postwizard.entities.taxonomies.WPTerms;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class PostDaoTest {

  private static final String TEST_WAR = "arquillian-pw-tests-posts-dao.war";

  @Deployment
  public static WebArchive createDeployment() {
    WebArchive archive =
        ShrinkWrap.create(WebArchive.class, TEST_WAR)
            .addPackages(true, "net.ygbstudio.postwizard")
            .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    return archive;
  }

  @Inject private PostDAO postReaderDAO;

  @Test
  public void testDAO() {
    assertNotNull(postReaderDAO);
  }

  @Test
  public void testGetTermRelationshipsByPostID() {
    Set<WPTermRelationships> onePost = postReaderDAO.getTermRelationshipsByPostID(1L);
    assertThat(onePost, is(not(empty())));
    onePost.forEach(post -> assertThat(post, notNullValue()));
  }

  @Test
  public void testGetPostMetaByPostID() {
    Set<WPMeta> onePost = postReaderDAO.getPostMetaByPostID(1L);
    assertThat(onePost, is(not(empty())));
    onePost.forEach(post -> assertThat(post, notNullValue()));
  }

  @Test
  public void testGetPostTermsByID() {
    Set<WPTerms> onePost = postReaderDAO.getPostTermsById(1L);
    assertThat(onePost, is(not(empty())));
    onePost.forEach(post -> assertThat(post, notNullValue()));
  }
}
