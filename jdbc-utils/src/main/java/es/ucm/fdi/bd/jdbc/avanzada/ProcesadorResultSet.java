package es.ucm.fdi.bd.jdbc.avanzada;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Interfaz Callback utilizado por los métodos consulta de {@link PlantillaJDBC}.
 * 
 * Las implementaciones de esta interfaz deben extraer los resultados del {@link java.sql.ResultSet} y procesarlos, la
 * gestión de las excepciones se realiza en {@link PlantillaJDBC}
 * 
 * @author Iván Martinez Ortiz
 *
 */
public interface ProcesadorResultSet<T> {
  
  /**
   * Las implementaciones deben implementar este método para procesar todo el resultado.
   * 
   * @param rs El ResulSet sobre el que extraer información
   * @throws SQLException
   */
  public T procesaResultado(ResultSet rs) throws SQLException;

}