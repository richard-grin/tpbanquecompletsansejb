package fr.grin.tpbanque.services;

import fr.grin.tpbanque.entities.CompteBancaire;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import java.util.logging.Logger;

/**
 * Initialise une base de données vide.
 *
 * @author grin
 */
@ApplicationScoped
public class Init {

  private final static Logger logger = Logger.getLogger("fr.grin.tpbanque.ejb.Init");

  @Inject
  private GestionnaireCompte gestionnaireCompte;

  public void initComptes(@Observes @Initialized(ApplicationScoped.class) ServletContext context) {
    if (gestionnaireCompte.nbComptes() != 0) {
      logger.info("La base de données n'est pas vide");
      return;
    }
    logger.info("Aucun compte dans la base de données");
    gestionnaireCompte.creerCompte(new CompteBancaire("John Lennon", 150000));
    gestionnaireCompte.creerCompte(new CompteBancaire("Paul McCartney", 950000));
    gestionnaireCompte.creerCompte(new CompteBancaire("Ringo Starr", 20000));
    gestionnaireCompte.creerCompte(new CompteBancaire("Georges Harrisson", 100000));
  }
}
