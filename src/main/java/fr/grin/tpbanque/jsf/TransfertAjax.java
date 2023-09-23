package fr.grin.tpbanque.jsf;

import fr.grin.tpbanque.service.GestionnaireCompte;
import fr.grin.tpbanque.entities.CompteBancaire;
import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Inject;

/**
 * Backing bean pour la page transfertAjax.xhtml.
 *
 * @author grin
 */
@Named(value = "transfertAjax")
@RequestScoped
public class TransfertAjax {

  @Inject
  private GestionnaireCompte gestionnaireCompte;

  private long idSource;
  private long idDestination;
  private int montant;
  private CompteBancaire compteSource;
  private CompteBancaire compteDestination;

  public long getIdSource() {
    return idSource;
  }

  public void setIdSource(long id) {
    this.idSource = id;
  }

  public long getIdDestination() {
    return idDestination;
  }

  public void setIdDestination(long id) {
    this.idDestination = id;
  }

  public int getMontant() {
    return montant;
  }

  public void setMontant(int montant) {
    this.montant = montant;
  }

  public CompteBancaire getCompteSource() {
    return compteSource;
  }

  public CompteBancaire getCompteDestination() {
    return compteDestination;
  }

  /**
   * Enregistre les données du formulaire pour faire le transfert entre les 2
   * comptes bancaires. Pas besoin de tester les id parce qu'ils ont déjà été
   * testé par les méthodes validateSurce et validateDestination dans le cycle
   * de vie JSF.
   *
   * @return
   */
  public String enregistrer() {
//    boolean erreur = false;
    if (compteSource.getSolde() < montant) {
      Util.messageErreur("Solde de " + compteSource.getNom()
              + " insuffisant", "Solde insuffisant", "form:montant");
      return null; // reste sur la même page
    }

    // Pas d'erreur de saisie (puisque le validateur a déjà été exécuté)
    gestionnaireCompte.transferer(compteSource, compteDestination, montant);
    Util.addFlashInfoMessage("Transfert de " + compteSource.getNom() + " vers "
            + compteDestination.getNom()
            + " pour un montant de " + montant + " correctement effectué");
    return "listeComptes?faces-redirect=true";
  }

  public void validateIdSource(FacesContext fc, UIComponent composant, Object valeur) {
    this.compteSource = gestionnaireCompte.findById((Long) valeur);
    if (compteSource == null) {
      throw new ValidatorException(
              new FacesMessage("l'id " + valeur + " ne correspond à aucun compte"));
    }
  }

  public void validateIdDestination(FacesContext fc, UIComponent composant, Object valeur) {
    this.compteDestination = gestionnaireCompte.findById((Long) valeur);
    if (this.compteDestination == null) {
      throw new ValidatorException(
              new FacesMessage("l'id " + valeur + " ne correspond à aucun compte"));
    }
  }
}
