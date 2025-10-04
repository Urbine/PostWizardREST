package net.ygbstudio.postwizard.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import jakarta.inject.Inject;
import java.util.Optional;
import net.ygbstudio.postwizard.dao.TaxonomyManager;
import net.ygbstudio.postwizard.dao.TermsManager;
import net.ygbstudio.postwizard.entities.taxonomies.WPTerms;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class TaxonomyDaoTest {
  public static final String TEST_WAR = "arquillian-pw-tests-taxonomy-dao.war";

  @Deployment
  public static WebArchive createDeployment() {
    WebArchive archive =
        ShrinkWrap.create(WebArchive.class, TEST_WAR)
            .addPackages(true, "net.ygbstudio.postwizard")
            .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    return archive;
  }

  @Inject private TaxonomyManager taxonomyDAO;

  @Inject private TermsManager termsDAO;

  @Test
  public void testTaxonomyDAO() {
    assertThat(taxonomyDAO, notNullValue());
  }

  @Test
  public void testTermsDAO() {
    assertThat(termsDAO, notNullValue());
  }

  @Test
  public void testTaxonomyIdExists() {
    assertThat(taxonomyDAO.termTaxonomyIdExists(1L).findFirst().isPresent(), is(true));
    assertThat(taxonomyDAO.termTaxonomyIdExists(9999L).findFirst().isPresent(), is(false));
  }

  @Test
  public void testTaxonomyTermExists() {
    Optional<WPTerms> firstTerm = termsDAO.termIdExists(1L).findFirst();
    assertThat(firstTerm.isPresent(), is(true));
    assertThat(taxonomyDAO.taxonomyTermExists(firstTerm.get()).findFirst().isPresent(), is(true));
  }

  @Test
  public void testTaxonomyExists() {
    assertThat(taxonomyDAO.taxonomyExists("category").findFirst().isPresent(), is(true));
    assertThat(taxonomyDAO.taxonomyExists("NotATaxonomy").findFirst().isPresent(), is(false));
  }
}
