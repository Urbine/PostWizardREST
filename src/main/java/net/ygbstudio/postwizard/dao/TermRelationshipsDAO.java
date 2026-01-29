/*
 * SPDX-License-Identifier: MPL-2.0
 * Copyright (c) Yoham Gabriel B.
 */

package net.ygbstudio.postwizard.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import net.ygbstudio.postwizard.entities.WPost;
import net.ygbstudio.postwizard.entities.taxonomies.WPTermRelationships;
import net.ygbstudio.postwizard.entities.taxonomies.WPTermTaxonomy;
import org.jspecify.annotations.NonNull;

/**
 * Data Access Object (DAO) implementation of interface {@link TermRelationshipsManager} for reading
 * WordPress term relationships. This class implements methods to retrieve and handle term
 * relationships in a WordPress-like environment.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@ApplicationScoped
public class TermRelationshipsDAO implements TermRelationshipsManager {

  @PersistenceContext(unitName = "wptermrelationships")
  private EntityManager em;

  @Transactional(value = TxType.REQUIRED)
  @Override
  public Stream<WPTermRelationships> findExistingRelationship(
      @NonNull WPost postItem, @NonNull WPTermTaxonomy taxonomy) {
    return em.createNamedQuery(TermRelationshipsManager.FIND_EXISTING, WPTermRelationships.class)
        .setParameter("objectID", postItem.getId())
        .setParameter("termTaxonomyID", taxonomy.getTermTaxonomyId())
        .getResultStream();
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public WPTermRelationships addTermRelationship(
      @NonNull WPost postItem, @NonNull WPTermTaxonomy taxonomy, int termOrder) {
    Optional<WPTermRelationships> existingRelationship =
        findExistingRelationship(postItem, taxonomy).findFirst();

    if (existingRelationship.isPresent()) return existingRelationship.get();

    WPTermRelationships termRelationships = new WPTermRelationships();
    termRelationships.setTermTaxonomy(taxonomy);
    termRelationships.setPostObject(postItem);
    termRelationships.setTermOrder(termOrder);

    // Everytime a relationship is created,
    // the database count of the taxonomy should be updated.
    taxonomy.setCount(countTaxonomyRelationships(taxonomy.getTermTaxonomyId()) + 1);

    em.merge(taxonomy);
    em.persist(termRelationships);

    return termRelationships;
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public long countTaxonomyRelationships(long termTaxonomyId) {
    return em.createNamedQuery(TermRelationshipsManager.COUNT_BY_TERM_TAXONOMY_ID, Long.class)
        .setParameter("termTaxonomyID", termTaxonomyId)
        .getSingleResult();
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public boolean deleteTermRelationship(@NonNull WPost postItem, @NonNull WPTermTaxonomy taxonomy) {
    AtomicBoolean result = new AtomicBoolean(true);

    findExistingRelationship(postItem, taxonomy)
        .findFirst()
        .ifPresentOrElse(
            termRelationships -> {
              WPTermRelationships termRelationship = em.merge(termRelationships);
              WPTermTaxonomy termTaxonomy = em.merge(termRelationship.getTermTaxonomy());
              em.remove(termRelationship);
              termTaxonomy.setCount(countTaxonomyRelationships(termTaxonomy.getTermTaxonomyId()));
            },
            () -> result.set(false));

    return result.get();
  }

  @Transactional(value = TxType.REQUIRED)
  @Override
  public boolean cleanTaxonomyRelationships(long termTaxonomyId) {
    return em.createNamedQuery(
                TermRelationshipsManager.DELETE_BY_TERM_TAXONOMY_ID, WPTermRelationships.class)
            .setParameter("termTaxonomyID", termTaxonomyId)
            .executeUpdate()
        > 0;
  }
}
