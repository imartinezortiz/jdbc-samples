package es.ucm.fdi.bd.jdbc.avanzada;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Interfaz Callback utilizado por los métodos consulta de {@link PlantillaJDBC}.
 * 
 * Las implementaciones de esta interfaz gestionan las filas {@link java.sql.ResultSet} una a una, la
 * gestión de las excepciones se realiza en {@link PlantillaJDBC}
 * 
 * @author Iván Martínez-Ortiz
 *
 */
public interface ProcesadorFilas<T> {
  
  /**
   * Las implementaciones deben implementar este método para procesar una fila del resultado.
   * 
   * @param rs El ResulSet sobre el que extraer información
   * 
   * @return Devuelve el último valor devuelve este método al procesar la última fila del resultado.
   * 
   * @throws SQLException
   */
  public T procesaFila(ResultSet rs) throws SQLException;
}
