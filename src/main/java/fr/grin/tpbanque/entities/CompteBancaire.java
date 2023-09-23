package fr.grin.tpbanque.entities;

import fr.grin.tpbanque.service.CompteException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;

/**
 * Un compte bancaire.
 *
 * @author grin
 */
@NamedQuery(name = "CompteBancaire.findAll", query = "select c from CompteBancaire c")
@Entity
public class CompteBancaire implements Serializable {

  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nom;
  private int solde;
  @Version
  private int version;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private List<OperationBancaire> operations = new ArrayList<>();
  
  
  public CompteBancaire(String nom, int solde) {
    this.nom = nom;
    this.solde = solde;
    operations.add(new OperationBancaire("Création du compte", solde));
  }

  public CompteBancaire() {
  }

  public Long getId() {
    return id;
  }

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
  
  public List<OperationBancaire> getOperations() {
    return operations;
  }

  public void deposer(int montant) {
    solde += montant;
    operations.add(new OperationBancaire("Crédit", montant));
  }

  public void retirer(int montant) throws CompteException {
    if (montant <= solde) {
      solde -= montant;
    } else {
      throw new CompteException("Solde du compte de " + this.nom 
              + " est " + solde
              + " ; insuffisant pour un retrait de " + montant);
    }
    operations.add(new OperationBancaire("Débit", montant));
  }
  
  /**
   * Pour ajouter une opération qui n'a pas de montant.
   * Par exemple un changement de nom.
   * @param description description de l'opération.
   */
  public void ajoutOperation(String description) {
    operations.add(new OperationBancaire(description, 0));
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (id != null ? id.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object
  ) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof CompteBancaire)) {
      return false;
    }
    CompteBancaire other = (CompteBancaire) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "fr.grin.tpbanque.entities.CompteBancaire[ id=" + id + " ]";
  }

}
