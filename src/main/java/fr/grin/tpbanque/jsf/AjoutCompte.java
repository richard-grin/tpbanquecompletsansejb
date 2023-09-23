package fr.grin.tpbanque.jsf;

import fr.grin.tpbanque.service.GestionnaireCompte;
import fr.grin.tpbanque.entities.CompteBancaire;
import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

/**
 * Backing bean pour la page ajout.xhtml
 * @author grin
 */
@Named(value = "ajout")
@RequestScoped
public class AjoutCompte {
    
  @Inject
  private GestionnaireCompte gestionnaireCompte;
  
  private String nom;
  private int solde;

  public String getNom() {
    return nom;
  }

  public void setNom(String nom) {
    this.nom = nom;
  }

  public int getSolde() {
    return solde;
  }

  public void setSolde(int solde) {
    this.solde = solde;
  }

  public String creer() {
    gestionnaireCompte.creerCompte(new CompteBancaire(nom, solde));
    return "listeComptes?faces-redirect=true";
  }
  
  
}
