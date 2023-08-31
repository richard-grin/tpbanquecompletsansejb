package fr.grin.tpbanque.services;

import fr.grin.tpbanque.entities.CompteBancaire;
import jakarta.annotation.PostConstruct;
import java.util.List;
import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.ejb.EJBTransactionRolledbackException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

/**
 * Gère les comptes bancaires.
 *
 * @author grin
 */
@DataSourceDefinition(
        className = "com.mysql.cj.jdbc.MysqlDataSource",
        name = "java:app/jdbc/banque",
        serverName = "localhost", // host.docker.internal" si Docker, localhost si pas dans container Docker
        portNumber = 3306,
        user = "titi", // nom et
        password = "titi", // mot de passe que vous avez donnés lors de la création de la base de données
        databaseName = "banque",
        properties = {
          // "zeroDateTimeBehavior=CONVERT_TO_NULL",
          "useSSL=false",
          "allowPublicKeyRetrieval=true"
        }
)
@ApplicationScoped
public class GestionnaireCompte {

  @Inject
  private Logger logger;

  @PersistenceContext
  private EntityManager em;

  @PostConstruct
  public void init() {
    logger.log(Level.INFO, "****Répertoire courant : {0}", System.getProperty("user.dir"));
  }

  @Transactional
  public void creerCompte(CompteBancaire compte) {
    em.persist(compte);
  }

  @Transactional
  public List<CompteBancaire> getAllComptes() {
    Query q = em.createQuery("select c from CompteBancaire c");
    List<CompteBancaire> l = q.getResultList();
    TypedQuery query
            = em.createNamedQuery("CompteBancaire.findAll", CompteBancaire.class);
    return query.getResultList();
  }

  public long nbComptes() {
    TypedQuery<Long> query
            = em.createQuery("select count(c) from CompteBancaire c", Long.class);
    return query.getSingleResult();
  }

  @Transactional
  public void transferer(CompteBancaire source, CompteBancaire destination,
          int montant) {
    try {
      source.retirer(montant);
      destination.deposer(montant);
      update(source);
      update(destination);
      logger.log(Level.INFO, "Transfert de {0} de {1} vers {2}",
              new Object[]{montant, source.getNom(), destination.getNom()});
    } catch (CompteException ex) {
      // Juste pour tester. A la place de ex il faudrait mettre l'EJBTransactionRolledbackException.
      logger.throwing("GestionnaireCompte", "tranferer", ex);
      // Relance cette exception pour provoquer un rollback de la transaction
      throw new EJBTransactionRolledbackException(ex.getLocalizedMessage(), ex);
    }
  }

  @Transactional
  public CompteBancaire update(CompteBancaire compteBancaire) {
    return em.merge(compteBancaire);
  }

  @Transactional
  public void supprimer(CompteBancaire compte) {
    em.remove(em.merge(compte));
  }

  public CompteBancaire findById(long id) {
    return em.find(CompteBancaire.class, id);
  }

  /**
   * Dépôt d'argent sur un compte bancaire.
   *
   * @param compteBancaire
   * @param montant
   */
  @Transactional
  public void deposer(CompteBancaire compteBancaire, int montant) {
    compteBancaire.deposer(montant);
    update(compteBancaire);
    logger.log(Level.INFO, "Dépôt de {0} sur compte de {1}",
            new Object[]{montant, compteBancaire.getNom()});
  }

  /**
   * Retrait d'argent sur un compte bancaire.
   *
   * @param compteBancaire
   * @param montant
   */
  @Transactional
  public void retirer(CompteBancaire compteBancaire, int montant) {
    try {
      compteBancaire.retirer(montant);
      update(compteBancaire);
    } catch (CompteException ex) {
      // Pour provoquer un rollback de la transaction en cours.
      throw new EJBTransactionRolledbackException(ex.getLocalizedMessage(), ex);
    }
  }

  // Le reste des méthodes pour le lazy loading.
  // Je pense qu'elles ne servent plus à rien depuis les dernières versions 
  // de PrimeFaces (à partir de la version 11.0). A VOIR.
  /**
   * Méthode utilisée par une table PrimeFaces pour le lazy loading.La méthode
 qui charge les données, juste pour une page de la datatable.Elle doit
 tenir compte du numéro de la page mais aussi des filtres (y compris d'un
 éventuel filtre global ?) mis sur la page et du tri des données de la
 table.
   *
   * @param first première ligne à récupérer
   * @param pageSize taille d'une page de la table
   * @param filterMetas les filtres qui ont été mis par l'utilisateur sur les
   * données de la table
   * @return les lignes à afficher dans la table.
   */
  public List<CompteBancaire> getPageTable(int first, int pageSize,
          Map<String, SortMeta> sortMetas, Map<String, FilterMeta> filterMetas) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<CompteBancaire> cq = cb.createQuery(CompteBancaire.class);
    Root<CompteBancaire> compteBancaire = cq.from(CompteBancaire.class);
    // Applique les filtres
    cq.where(getFilterCondition(cb, compteBancaire, filterMetas));
    // Prise en compte des tris
    // Simplification pour le moment en supposant un seul champ de tri
    // TODO: cas où plusieurs clés de tri
    applySort(cb, cq, compteBancaire, sortMetas);
//    if (sortMetas != null) {
//      Collection<SortMeta> values = sortMetas.values();
//      if (!values.isEmpty()) {
//        SortMeta sortMeta = values.iterator().next();
//        String sortField = sortMeta.getField();
//        SortOrder sortOrder = sortMeta.getOrder();
//        if (sortOrder == SortOrder.ASCENDING) {
//          cq.orderBy(cb.asc(compteBancaire.get(sortField)));
//        } else if (sortOrder == SortOrder.DESCENDING) {
//          cq.orderBy(cb.desc(compteBancaire.get(sortField)));
//        }
//      }
//    }
    List<CompteBancaire> comptes
            = em.createQuery(cq).setFirstResult(first).setMaxResults(pageSize)
                    .getResultList();
    System.out.println("+=+=+=Fin de getPageTable comptes=" + comptes);
    return comptes;
  }

  public int count(Map<String, FilterMeta> filters) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<CompteBancaire> compteBancaire = cq.from(CompteBancaire.class);
    cq.where(getFilterCondition(cb, compteBancaire, filters));
    cq.select(cb.count(compteBancaire));
    return em.createQuery(cq).getSingleResult().intValue();
  }

  /**
   * Appliquer les tris.
   *
   * @param cb
   * @param sortMetas
   */
  private void applySort(CriteriaBuilder cb, CriteriaQuery<CompteBancaire> cq,
          Root<CompteBancaire> compteBancaire, Map<String, SortMeta> sortMetas) {
    if (sortMetas == null) {
      return;
    }
    // Trier les tris par ordre décroissant d'importance
    // Code faux ; TODO utiliser plutôt CriteriaBuilder.orderBy(List<Order> liste)
    // Pour créer un Order : criteriaBuilder.asc(criteriaQuery.get(champ))
    // ou bien criteriaBuilder.desc(criteriaQuery.get(champ))
    Collection<SortMeta> values = sortMetas.values();
    List<SortMeta> listeSortMeta
            = values.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
    for (SortMeta sortMeta : listeSortMeta) {
      String sortField = sortMeta.getField();
      SortOrder sortOrder = sortMeta.getOrder();
      if (sortOrder == SortOrder.ASCENDING) {
        cq.orderBy(cb.asc(compteBancaire.get(sortField)));
      } else if (sortOrder == SortOrder.DESCENDING) {
        cq.orderBy(cb.desc(compteBancaire.get(sortField)));
      }
    }
  }

  /**
   * Retourne un prédicat qui correspond aux filtres. Les filtres sont
   * représentés par une map dont la clé est le champ utilisé pour le filtre et
   * la valeur est la valeur que l'on veut pour ce champ. Dans cette version,
   * les filtres sont supposés du type "startsWith" qui est le type par défaut
   * de PrimeFaces.
   *
   * Les autres types de filtres seront pris en compte si une version ultérieure
   * de PrimeFaces fournit une possibilité générique pour obtenir le type de
   * filtre. Pour le moment ça n'est pas le cas et il faut donc écrire du code
   * particulier à chaque application et à chaque champ qui est filtré. Un
   * exemple est donné pour le champ solde avec un filtre de type ">" alors que
   * pour le champ "nom" le filtre est de type "contains". TODO: Tri des nombres
   * pour le solde.
   *
   * @param cb CriteriaBuilder sur lequel on applique les filtres.
   * @param compteBancaire racine de la requête
   * @param filtersMeta les filtres
   * @return prédicat qui correspond aux filtres passés en paramètre.
   */
  private Predicate getFilterCondition(CriteriaBuilder cb,
          Root<CompteBancaire> compteBancaire, Map<String, FilterMeta> filtersMeta) {
    Predicate filterCondition = cb.conjunction();
    if (filtersMeta == null) {
      return filterCondition;
    }
    String wildCard = "%";
    for (FilterMeta meta : filtersMeta.values()) {
      // meta.getFilterValue() est de type Object
      // Valeur entrée dans le champ de la table par l'utilisateur
      String valeurFiltre = (String) meta.getFilterValue();
      if (valeurFiltre == null) {
        // Si la valeur du champ de sélection est vide (ou null ??), 
        // passer au filtre suivant
        continue;
      }

      // Traitement du filtre
      String champ = meta.getField();
      if (champ.equals("solde")) { // Si le filtre concerne le solde
        // Filtre de type > ; sélection des lignes dont le solde est supérieur
        // à la valeur saisie par l'utilisateur dans le champ de sélection PrimeFaces
        // Il faut tout convertir en entiers.
        int valeurInt = Integer.valueOf(valeurFiltre);
        // Integer car le type de solde est int
        Path<Integer> path = compteBancaire.get(champ);
        filterCondition = cb.and(filterCondition, cb.gt(path, valeurInt));
      } else if (champ.equals("nom")) { // Si le filtre concerne le nom
        // On utilise "contains".
        // Pour un filtre de type "startsWith (valeur par défaut) ça serait "
        // TODO: voir les autres types de filtres.
        // String value = filter.getValue() + wildCard;
        // Pour filtre de type "contains" et ne pas tenir compte des majuscules
        String value = (wildCard + valeurFiltre + wildCard).toLowerCase();;
        // Chemin vers le nom qui est de type String
        Path<String> path = compteBancaire.get(champ);
        filterCondition = cb.and(filterCondition, cb.like(cb.lower(path), value));
      }

    }
    return filterCondition;
  }
}
