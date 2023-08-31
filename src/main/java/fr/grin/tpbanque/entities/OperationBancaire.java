package fr.grin.tpbanque.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 *
 * @author grin
 */
@Entity
public class OperationBancaire implements Serializable {

  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String description;
  private LocalDateTime dateOperation;
  private int montant;

  public OperationBancaire() {
  }

  public OperationBancaire(String description, int montant) {
    this.description = description;
    this.montant = montant;
    this.dateOperation = LocalDateTime.now();
  }

  public Long getId() {
    return id;
  }

  public String getDescription() {
    return description;
  }

  public LocalDateTime getDateOperation() {
    return dateOperation;
  }

  public int getMontant() {
    return montant;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (id != null ? id.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof OperationBancaire)) {
      return false;
    }
    OperationBancaire other = (OperationBancaire) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "fr.grin.tpbanque.entities.OperationBancaire[ id=" + id + " ]";
  }
  
}
