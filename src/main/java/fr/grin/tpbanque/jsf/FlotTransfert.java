package fr.grin.tpbanque.jsf;

import fr.grin.tpbanque.services.GestionnaireCompte;
import fr.grin.tpbanque.entities.CompteBancaire;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.ejb.EJBTransactionRolledbackException;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.faces.flow.FlowScoped;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Inject;

/**
 * Backing bean pour le flow "flotTransfert". Ce flot permet de transférer de
 * l'argent entre 2 comptes.
 *
 * @author grin
 */
@Named(value = "flotTransfert")
@FlowScoped("flotTransfert")
public class FlotTransfert implements Serializable {

  @Inject
  private GestionnaireCompte gestionnaireCompte;

  private CompteBancaire compteSource;
  private CompteBancaire compteDestination;
  private Integer montant;

  private List<CompteBancaire> comptes;

  /**
   * Creates a new instance of FlotTransfert
   */
  public FlotTransfert() {
  }

  public CompteBancaire getCompteSource() {
    return compteSource;
  }

  public void setCompteSource(CompteBancaire compteSource) {
    this.compteSource = compteSource;
  }

  public CompteBancaire getCompteDestination() {
    return compteDestination;
  }

  public void setCompteDestination(CompteBancaire compteDestination) {
    this.compteDestination = compteDestination;
  }

  public Integer getMontant() {
    return montant;
  }

  public void setMontant(Integer montant) {
    this.montant = montant;
  }

  public List<CompteBancaire> getComptes() {
    if (comptes == null) {
      comptes = gestionnaireCompte.getAllComptes();
      // Seulement si on veut les comptes triés par défaut par id
      comptes.sort(Comparator.comparing(c -> c.getId()));
    }
    return comptes;
  }

  /**
   * Validation du montant du transfert. Il ne doit pas être supérieur au solde
   * du compte source. On n'attend pas la levée d'une CompteException.
   */
  public void validerMontant(FacesContext ctx, UIComponent composant,
          Object valeur) {
    int montant = (Integer) valeur;
    if (compteSource == null) {
      FacesMessage message
              = new FacesMessage("Indiquez d'abord le compte source !");
      throw new ValidatorException(message);
    }
    if (montant > compteSource.getSolde()) {
      FacesMessage message
              = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                      "Solde insuffisant",
                      "Le solde de " + compteSource.getNom()
                      + " est insuffisant"
              );
      throw new ValidatorException(message);
    }
  }

  /**
   * Enregistre le transfert dans la base de données.
   *
   * @return
   */
  public String enregistrer() {
    // Validation car il est possible d'arriver à la confirmation
    // sans avoir saisi les informations nécessaires au transfert.

    boolean erreur = false;
    if (compteSource == null) {
      Util.messageErreur("Vous devez indiquer la source du transfert");
      erreur = true;
    }
    if (compteDestination == null) {
      Util.messageErreur("Vous devez indiquer la destination du transfert");
      erreur = true;
    }

    if (montant == null) {
      Util.messageErreur(
              "Vous devez indiquer le montant du transfert");
      return null;
    }
    // montant != null dans la suite
    if (montant <= 0) {
      Util.messageErreur(
              "Le montant doit être un nombre positif");
      erreur = true;
    }
    // Vérifie que le montant du retrait ne dépasse pas le solde
    // du compte source.
    if (compteSource != null && montant > compteSource.getSolde()) {
      Util.messageErreur(
              "Le montant dépasse le solde du compte de " + compteSource.getNom());
      erreur = true;
    }

    if (erreur) {
      // Réaffiche la page
      return null;
    }
    // Enregistre le transfert dans la base de données
    try {
      gestionnaireCompte.transferer(compteSource, compteDestination, montant);
      // Message de succès
      Util.addFlashInfoMessage("Transfert de "
              + montant + " effectué du compte de " + compteSource.getNom()
              + " sur le compte de " + compteDestination.getNom());
      // La sortie du flot ne marche pas si on fait une redirection...
      return "sortieFlot";
    } catch (EJBTransactionRolledbackException ex) {
      // Transaction invalidée par l'EJB
      Util.messageErreur(
              ex.getMessage(),
              "Problème lors du transfert : " + ex.getMessage(),
              null);
      Logger.getLogger(FlotTransfert.class.getName()).log(Level.WARNING, null, ex);
      return null;
    }
  }

}
