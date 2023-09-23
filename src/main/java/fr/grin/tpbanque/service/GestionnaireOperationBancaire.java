package fr.grin.tpbanque.service;

import fr.grin.tpbanque.entities.OperationBancaire;
import java.util.List;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

/**
 * EJB qui gère les opérations bancaires directement.
 * Utilisé pour tester le problème des N + 1 selects.
 * 
 * @author grin
 */
@RequestScoped
public class GestionnaireOperationBancaire {

    @PersistenceContext(unitName = "prod")
    private EntityManager em;

    /**
     * Récupère toutes les opérations bancaires.
     * @return toutes les opérations bancaires.
     */
    public List<OperationBancaire> findAll() {
        TypedQuery<OperationBancaire> query =
           em.createQuery("select o from OperationBancaire o", OperationBancaire.class);
        return query.getResultList();
    }
    
}
