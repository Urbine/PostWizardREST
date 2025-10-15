package net.ygbstudio.postwizard.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import java.util.Optional;
import java.util.stream.Stream;
import net.ygbstudio.postwizard.entities.WPOptions;

/**
 * Data Access Object (DAO) implementation of interface {@link OptionManager} for reading WordPress
 * options.
 *
 * <p>Provides methods to access and manage WordPress configuration options, enabling dynamic
 * customization of application behavior and algorithms.
 *
 * @see OptionManager
 * @see WPOptions
 * @author Yoham Gabriel @ YGB Studio
 */
@ApplicationScoped
public class OptionsDAO implements OptionManager {

  @PersistenceContext(unitName = "wpoptions")
  private EntityManager em;

  @Transactional(TxType.REQUIRED)
  @Override
  public Stream<WPOptions> getAllOptions() {
    return em.createNamedQuery("WPOptions.FindAll", WPOptions.class).getResultStream();
  }

  @Transactional(TxType.REQUIRED)
  @Override
  public Optional<WPOptions> getOptionByName(String optionName) {
    return em.createNamedQuery("WPOptions.FindByOptionName", WPOptions.class)
        .setParameter("optionName", optionName)
        .getResultStream()
        .findFirst();
  }
}
