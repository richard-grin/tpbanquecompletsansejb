package fr.grin.tpbanque.jsf;

import fr.grin.tpbanque.service.GestionnaireOperationBancaire;
import fr.grin.tpbanque.entities.OperationBancaire;
import java.util.List;
import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

/**
 * Backing bean pour la page qui liste les opérations bancaires de tous les comptes.
 * @author grin
 */
@Named(value = "listeOperationsDirect")
@RequestScoped
public class ListeToutesLesOperations {

    @Inject
    private GestionnaireOperationBancaire gestionnaireOperationBancaire;

    /**
     * 
     * @return la liste de toutes les opérations bancaires.
     */
    public List<OperationBancaire> getAllOperations() {
        return gestionnaireOperationBancaire.findAll();
    }
    
}
