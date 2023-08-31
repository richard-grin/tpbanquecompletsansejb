package fr.grin.tpbanque.services;

/**
 * Exception lancée en cas de problème sur les comptes bancaires.
 *
 * @author grin
 */
public class CompteException extends Exception {

  public CompteException(String message) {
    super(message);
  }

  public CompteException(String message, Throwable cause) {
    super(message, cause);
  }

  public CompteException(Throwable cause) {
    super(cause);
  }
}
