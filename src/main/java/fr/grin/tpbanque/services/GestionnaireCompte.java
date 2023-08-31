package fr.grin.tpbanque.services;

import fr.grin.tpbanque.entities.CompteBancaire;
import jakarta.annotation.PostConstruct;
import java.util.List;
import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.ejb.EJBTransactionRolledbackException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gère les comptes bancaires.
 *
 * @author grin
 */
@DataSourceDefinition(
        className = "com.mysql.cj.jdbc.MysqlDataSource",
        name = "java:app/jdbc/banque",
        serverName = "localhost", // host.docker.internal" si Docker, localhost si pas dans container Docker
        portNumber = 3306,
        user = "titi", // nom et
        password = "titi", // mot de passe que vous avez donnés lors de la création de la base de données
        databaseName = "banque",
        properties = {
          // "zeroDateTimeBehavior=CONVERT_TO_NULL",
          "useSSL=false",
          "allowPublicKeyRetrieval=true"
        }
)
@ApplicationScoped
public class GestionnaireCompte {

  @Inject
  private Logger logger;

  @PersistenceContext
  private EntityManager em;

  @PostConstruct
  public void init() {
    logger.log(Level.INFO, "****Répertoire courant : {0}", System.getProperty("user.dir"));
  }

  @Transactional
  public void creerCompte(CompteBancaire compte) {
    em.persist(compte);
  }

  @Transactional
  public List<CompteBancaire> getAllComptes() {
    Query q = em.createQuery("select c from CompteBancaire c");
    List<CompteBancaire> l = q.getResultList();
    TypedQuery query
            = em.createNamedQuery("CompteBancaire.findAll", CompteBancaire.class);
    return query.getResultList();
  }

  public long nbComptes() {
    TypedQuery<Long> query
            = em.createQuery("select count(c) from CompteBancaire c", Long.class);
    return query.getSingleResult();
  }

  @Transactional
  public void transferer(CompteBancaire source, CompteBancaire destination,
          int montant) {
    try {
      source.retirer(montant);
      destination.deposer(montant);
      update(source);
      update(destination);
      logger.log(Level.INFO, "Transfert de {0} de {1} vers {2}",
              new Object[]{montant, source.getNom(), destination.getNom()});
    } catch (CompteException ex) {
      // Juste pour tester. A la place de ex il faudrait mettre l'EJBTransactionRolledbackException.
      logger.throwing("GestionnaireCompte", "tranferer", ex);
      // Relance cette exception pour provoquer un rollback de la transaction
      throw new EJBTransactionRolledbackException(ex.getLocalizedMessage(), ex);
    }
  }

  @Transactional
  public CompteBancaire update(CompteBancaire compteBancaire) {
    return em.merge(compteBancaire);
  }

  @Transactional
  public void supprimer(CompteBancaire compte) {
    em.remove(em.merge(compte));
  }

  public CompteBancaire findById(long id) {
    return em.find(CompteBancaire.class, id);
  }

  /**
   * Dépôt d'argent sur un compte bancaire.
   *
   * @param compteBancaire
   * @param montant
   */
  @Transactional
  public void deposer(CompteBancaire compteBancaire, int montant) {
    compteBancaire.deposer(montant);
    update(compteBancaire);
    logger.log(Level.INFO, "Dépôt de {0} sur compte de {1}",
            new Object[]{montant, compteBancaire.getNom()});
  }

  /**
   * Retrait d'argent sur un compte bancaire.
   *
   * @param compteBancaire
   * @param montant
   */
  @Transactional
  public void retirer(CompteBancaire compteBancaire, int montant) {
    try {
      compteBancaire.retirer(montant);
      update(compteBancaire);
    } catch (CompteException ex) {
      // Pour provoquer un rollback de la transaction en cours.
      throw new EJBTransactionRolledbackException(ex.getLocalizedMessage(), ex);
    }
  }
}
