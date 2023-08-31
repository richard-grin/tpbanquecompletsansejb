package fr.grin.tpbanque.entities;

import fr.grin.tpbanque.services.CompteException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author grin
 */
public class CompteBancaireTest {

  public CompteBancaireTest() {
  }

  @BeforeAll
  public static void setUpClass() {
  }

  @AfterAll
  public static void tearDownClass() {
  }

  @BeforeEach
  public void setUp() {
  }

  @AfterEach
  public void tearDown() {
  }

  @Test
  public void testConstructor() {
    int montant = 100;
    CompteBancaire instance = new CompteBancaire("toto", montant);
    assertEquals(instance.getNom(), "toto", "Nom titulaire compte mal enregistré dans l'instance");
    assertEquals(instance.getSolde(), montant, "Montant mal enregistré dans l'instance");
  }

  /**
   * Test of deposer method, of class CompteBancaire.
   */
  @Test
  public void testDeposer() {
    System.out.println("deposer");
    int montant = 100;
    CompteBancaire instance = new CompteBancaire();
    instance.deposer(montant);
    // TODO review the generated test code and remove the default call to fail.
    assertEquals(instance.getSolde(), montant, "Mauvais calcul du solde après un dépôt");
  }

  /**
   * Test of retirer method, of class CompteBancaire.
   */
  @Test
  public void testRetirerTrop() throws Exception {
    System.out.println("retirer trop");
    int montant = 100;
    int montantRetrait = 150;
    CompteBancaire instance = new CompteBancaire("toto", montant);
    assertThrows(
            CompteException.class,
            () -> {
              instance.retirer(montantRetrait);
            },
            "Le retrait de " + montantRetrait + " ne lance pas une CompteException");
  }

  /**
   * Test of retirer method, of class CompteBancaire.
   */
  @Test
  public void testRetirer() throws Exception {
    System.out.println("retirer");
    int montant = 100;
    int montantRetrait = 50;
    CompteBancaire instance = new CompteBancaire("toto", montant);
    instance.retirer(montantRetrait);
    assertEquals(montant - montantRetrait, instance.getSolde(),
            "Mauvais calcul du solde après un retrait");
  }

  @Test
  public void testViderCompte() throws Exception {
    System.out.println("vider un compte");
    int montant = 100;
    CompteBancaire instance = new CompteBancaire("toto", montant);
    assertDoesNotThrow(
            () -> {
              instance.retirer(montant);
            },
            "Le retrait de " + montant + " lance une CompteException");

  }

}
