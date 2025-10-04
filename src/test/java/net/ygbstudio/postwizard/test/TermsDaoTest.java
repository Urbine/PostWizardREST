package net.ygbstudio.postwizard.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import jakarta.inject.Inject;
import net.ygbstudio.postwizard.dao.TermsManager;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class TermsDaoTest {
  public static final String TEST_WAR = "arquillian-pw-tests-terms-dao.war";

  @Deployment
  public static WebArchive createDeployment() {
    WebArchive archive =
        ShrinkWrap.create(WebArchive.class, TEST_WAR)
            .addPackages(true, "net.ygbstudio.postwizard")
            .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    return archive;
  }

  @Inject private TermsManager termsDAO;

  @Test
  public void testTermsDAO() {
    assertThat(termsDAO, notNullValue());
  }

  @Test
  public void testTermIdExists() {
    assertThat(termsDAO.termIdExists(1L).findFirst().isPresent(), is(true));
    assertThat(termsDAO.termIdExists(9999L).findFirst().isPresent(), is(false));
  }

  @Test
  public void testTermSlugExists() {
    assertThat(termsDAO.termSlugExists("uncategorized").findFirst().isPresent(), is(true));
    assertThat(termsDAO.termSlugExists("non-existent-term").findFirst().isPresent(), is(false));
  }

  @Test
  public void testTermNameExists() {
    assertThat(termsDAO.termNameExists("Uncategorized").findFirst().isPresent(), is(true));
    assertThat(termsDAO.termNameExists("DoesNotExist").findFirst().isPresent(), is(false));
  }
}
