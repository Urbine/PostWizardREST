package net.ygbstudio.postwizard.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import java.util.stream.Stream;
import net.ygbstudio.postwizard.entities.taxonomies.WPTerms;
import org.jspecify.annotations.NonNull;

/**
 * Data Access Object (DAO) implementation of interface {@link TermRelationshipsManager}for reading
 * WordPress term data. This class implements methods to retrieve and handle terms in a
 * WordPress-like environment.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@ApplicationScoped
public class TermsDAO implements TermsManager {

  @PersistenceContext(unitName = "wpterms")
  private EntityManager em;

  @Transactional(value = TxType.REQUIRED)
  @Override
  public Stream<WPTerms> termIdExists(long termID) {
    return em.createNamedQuery(TermsManager.EXISTS_BY_ID, WPTerms.class)
        .setParameter("termId", termID)
        .getResultStream();
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public Stream<WPTerms> termSlugExists(@NonNull String termSlug) {
    return em.createNamedQuery(TermsManager.EXISTS_BY_SLUG, WPTerms.class)
        .setParameter("termSlug", termSlug)
        .getResultStream();
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public Stream<WPTerms> termNameExists(@NonNull String termName) {
    return em.createNamedQuery(TermsManager.EXISTS_BY_NAME, WPTerms.class)
        .setParameter("termName", termName)
        .getResultStream();
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public Stream<WPTerms> termNameAndSlugExists(@NonNull String termName, @NonNull String termSlug) {
    return em.createNamedQuery(TermsManager.EXISTS_BY_NAME_AND_SLUG, WPTerms.class)
        .setParameter("termName", termName)
        .setParameter("termSlug", termSlug)
        .getResultStream();
  }
}
