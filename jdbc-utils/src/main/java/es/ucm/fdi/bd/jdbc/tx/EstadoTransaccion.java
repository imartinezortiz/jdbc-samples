package es.ucm.fdi.bd.jdbc.tx;

/**
 * 
 * @author Ivan Martinez-Ortiz
 *
 */
public interface EstadoTransaccion {
  
  public void setForzarRollback();
  
  public boolean isForzarRollback();
}
