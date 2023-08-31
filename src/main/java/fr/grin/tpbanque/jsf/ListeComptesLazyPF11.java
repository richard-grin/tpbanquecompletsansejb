package fr.grin.tpbanque.jsf;

import fr.grin.tpbanque.entities.CompteBancaire;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.primefaces.model.JpaLazyDataModel;
import org.primefaces.model.LazyDataModel;

/**
 * Backing bean pour la page qui liste tous les comptes.
 * Pour PrimeFaces version 11, avec utilisation de JpaLazyDataModel.
 *
 * @author grin
 */
@Named
@ViewScoped
public class ListeComptesLazyPF11 implements Serializable {
  
  @PersistenceContext
  private EntityManager em;

  private LazyDataModel<CompteBancaire> model;

  public ListeComptesLazyPF11() {
  }

  @PostConstruct
  public void init() {
    this.model = new JpaLazyDataModel<>(CompteBancaire.class, () -> em, "id");
  }

  public LazyDataModel<CompteBancaire> getModel() {
    return model;
  }

}
