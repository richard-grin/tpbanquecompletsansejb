package fr.grin.tpbanque.jsf;

import fr.grin.tpbanque.services.GestionnaireCompte;
import fr.grin.tpbanque.entities.CompteBancaire;
import jakarta.inject.Named;
import java.io.Serializable;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;

/**
 * Backing bean pour la page nouveauNom.xhtml.
 *
 * @author grin
 */
@Named(value = "modifierNom")
@ViewScoped
public class ModifierNom implements Serializable {

  @Inject
  private GestionnaireCompte gestionnaireCompte;

  private Long id;
  private CompteBancaire compte;
  private String nom;
  private String nomAncien;

  public String getNom() {
    return nom;
  }

  public void setMontant(String nom) {
    this.nom = nom;
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
    this.nomAncien = compte.getNom();
  }

  public String enregistrer() {
    compte.ajoutOperation("Changement nom : " + this.nomAncien + " -> " + compte.getNom());
    gestionnaireCompte.update(compte);
    Util.addFlashInfoMessage("Nouveau nom enregistré : " + compte.getNom()
            + " à la place de " + this.nomAncien);
    return "listeComptes?faces-redirect=true";
  }

}
