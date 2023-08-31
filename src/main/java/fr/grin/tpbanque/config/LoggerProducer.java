package fr.grin.tpbanque.config;

import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;
import java.util.logging.Logger;

/**
 * Producteur de Logger pour injection dans autres classes.
 * @author grin
 */
public class LoggerProducer {
    @Produces
    public Logger getLogger(InjectionPoint injectionPoint) {
      return Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
    }
}
