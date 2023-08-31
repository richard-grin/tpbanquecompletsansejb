package fr.grin.tpbanque.jsf;

import fr.grin.tpbanque.services.GestionnaireCompte;
import fr.grin.tpbanque.entities.CompteBancaire;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import org.primefaces.model.LazyDataModel;

/**
 * Backing bean pour la page qui liste tous les comptes.
 *
 * @author grin
 */
@Named
@ViewScoped
public class ListeComptesLazy implements Serializable {

  @Inject
  private GestionnaireCompte gestionnaireCompte;

  private LazyDataModel<CompteBancaire> model = new LazyComptesDataModel(gestionnaireCompte);

  private List<CompteBancaire> listeComptes;

  /**
   * Creates a new instance of GestionComptesBean
   */
  public ListeComptesLazy() {
  }

  @PostConstruct
  public void init() {
    System.out.println("CREATION LazyComptesDataModel");
    this.model = new LazyComptesDataModel(gestionnaireCompte);
  }

  public LazyDataModel<CompteBancaire> getModel() {
    return model;
  }

//  public List<CompteBancaire> getAllComptes() {
//    if (listeComptes == null) {
//      listeComptes = gestionnaireCompte.getAllComptes();
//    }
//    return listeComptes;
//  }
  /**
   * Retourne si la ligne doit être sélectionnée. Elle le sera si la valeur de
   * son solde est supérieure ou égale à la valeur saisie dans le filtre.
   *
   * @param valeurColonne
   * @param valeurFiltre
   * @param locale
   * @return true si la ligne est sélectionnée.
   */
  public boolean filterBySolde(Object valeurColonne, Object valeurFiltre, Locale locale) {
    String valeurFiltreString = (String) valeurFiltre;
    if (valeurFiltreString.equals("")) {
      return true;
    }
    try {
      return (Integer) valeurColonne >= Integer.valueOf(valeurFiltreString);
    } catch (NumberFormatException e) {
      // On ne fait pas de sélection si le filtre ne contient pas un nombre
      return true;
    }
  }

  public String supprimerCompte(CompteBancaire compte) {
    gestionnaireCompte.supprimer(compte);
    Util.addFlashInfoMessage("Compte de " + compte.getNom() + " supprimé");
    // Redirection pour ne pas voir le client supprimé
    return "listeComptes?faces-redirect=true";
  }

}
