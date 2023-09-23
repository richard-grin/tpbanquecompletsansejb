package fr.grin.tpbanque.jsf;

import fr.grin.tpbanque.service.GestionnaireCompte;
import fr.grin.tpbanque.entities.CompteBancaire;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import jakarta.ejb.EJBException;
import jakarta.ejb.EJBTransactionRolledbackException;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Inject;
import jakarta.persistence.OptimisticLockException;
import java.util.logging.Logger;

/**
 * Backing bean pour la page mouvement.xhtml.
 *
 * @author grin
 */
@Named(value = "mouvement")
@ViewScoped
public class Mouvement implements Serializable {

  @Inject
  private GestionnaireCompte gestionnaireCompte;

  private Long id;
  private CompteBancaire compte;
  private String typeMouvement;
  private int montant;

  private static final Logger logger = Logger.getLogger("fr.grin.tpbanque.jsf.Mouvement");
  
  public int getMontant() {
    return montant;
  }

  public void setMontant(int montant) {
    this.montant = montant;
  }

  public String getTypeMouvement() {
    return typeMouvement;
  }

  public void setTypeMouvement(String typeMouvement) {
    this.typeMouvement = typeMouvement;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public CompteBancaire getCompte() {
    return compte;
  }

  public void loadCompte() {
    compte = gestionnaireCompte.findById(id);
  }

  // La méthode doit avoir cette signature.
  public void validateSolde(FacesContext fc, UIComponent composant, Object valeur) {
    UIInput composantTypeMouvement = (UIInput) composant.findComponent("typeMouvement");
    // Sans entrer dans les détails, il faut parfois utiliser
    // getSubmittedValue() à la place de getLocalValue.
    // typeMouvement n'a pas encore reçu de valeur tant que la validation n'est pas finie.
    String valeurTypeMouvement = (String) composantTypeMouvement.getLocalValue();
    if (valeurTypeMouvement.equals("retrait")) {
      int retrait = (int) valeur;
      if (compte.getSolde() < retrait) {
        FacesMessage message
                = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Le retrait doit être inférieur au solde du compte",
                        "Le retrait doit être inférieur au solde du compte");
        logger.warning(message.getDetail() + "++++++ répertoire exécution : " + System.getProperty("user.dir"));
        throw new ValidatorException(message);
      }
    }
  }

  public String enregistrerMouvement() {
    try {
      if (typeMouvement.equals("ajout")) {
        gestionnaireCompte.deposer(compte, montant);
      } else {
        try {
          gestionnaireCompte.retirer(compte, montant);
        } catch (EJBTransactionRolledbackException ex) {
          // Remarque : cette exception hérite de EJBException et c'est pour cela
          // Qu'on l'attrape directement, pas comme avec OptimisticLockException.
          // Cas d'un solde insuffisant dans compte
          Util.messageErreur(ex.getMessage(), ex.getMessage(), "form:montant");
          return null; // rester sur la même page
        }
      }
      // Le mouvement a bien été enregistré
      Util.addFlashInfoMessage("Mouvement enregistré sur compte de " + compte.getNom());
      return "listeComptes?faces-redirect=true";
    } catch (EJBException ex) {
      // La stratégie optimiste a échoué ; conflit avec un autre utilisateur
      Throwable cause = ex.getCause();
      if (cause != null) {
        if (cause instanceof OptimisticLockException) {
          Util.messageErreur("Le compte de " + compte.getNom()
                  + " a été modifié ou supprimé par un autre utilisateur !");
        } else { // ou bien afficher le message de cause...
          Util.messageErreur(cause.getMessage());
        }
      } else { // Pas de cause ; afficher le message de ex.
        Util.messageErreur(ex.getMessage());
      }
      return null; // rester sur la même page
    }

  }

}
