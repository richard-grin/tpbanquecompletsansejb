package fr.grin.tpbanque.jsf;

import fr.grin.tpbanque.services.GestionnaireCompte;
import fr.grin.tpbanque.entities.CompteBancaire;
import java.util.List;
import java.util.Map;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

/**
 * Backing bean pour la page 
 * @author grin
 */
public class LazyComptesDataModel extends LazyDataModel<CompteBancaire> {

  private GestionnaireCompte gestionnaireCompte;

  public LazyComptesDataModel(GestionnaireCompte gestionnaireCompte) {
    this.gestionnaireCompte = gestionnaireCompte;
  }

  @Override
  public List<CompteBancaire> load(int first, int pageSize, 
          Map<String, SortMeta> sorts, Map<String, FilterMeta> filters) {
    this.setRowCount(gestionnaireCompte.count(filters));
    List<CompteBancaire> comptes
            = gestionnaireCompte.getPageTable(first, pageSize, sorts, filters);
    return comptes;
  }

  @Override
  public int count(Map<String, FilterMeta> filters) {
    return gestionnaireCompte.count(filters);
  }

}
