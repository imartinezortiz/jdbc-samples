package es.ucm.fdi.bd.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import es.ucm.fdi.bd.jdbc.sql.LectorSentenciasSQL;

/**
 * Plantilla para ejecutar sentencias SQL con JDBC.
 * 
 * @author Ivan Martinez Ortiz
 *
 */
public interface PlantillaJdbc {

    /**
     * Ejecuta todas las sentencias SQL que se encuentra en el {@link InputStream}. Si ocurre algún error
     * durante la ejecución de alguna de las instrucciones SQL, se notificará el problema utilizando el
     * mecanismo de {@link Logger} de Java.
     * 
     * @param is {@InputStream} que contiene las sentencias SQL.
     * 
     * @see LectorSentenciasSQL
     * @see #NOMBRE_LOGGER
     * 
     * @throws IOException Si ocurre algún error durante el procesamiento del {@link InputStream}.
     */
    public void ejecutaSentenciasSQL(InputStream is) throws IOException;

    /**
     * Ejecuta una sentencia SQL (DDL o DCL). Este método está pensado para la ejecución de sentencias SQL
     * pertenenciantes al grupo de &quot;Data Definition Language&quot; (DDL) y &quot;Data Control Language&quot; (DCL).
     * Los pasos de ejecución de una sentencia SQL son:
     * <ol>
     * <li>Obtener una conexión al SGBD ({@link #getConnection()})</li>
     * <li>Crear la instrucción SQL ({@link Statement})</li>
     * <li>Ejecutar la instrucción SQL (@link Statement#execute(String)}).</li>
     * <li>Cerrar la instrucción SQL {@link Statement#close()} (también se realiza en caso de error).</li>
     * <li>Llamar {@link #doSiempreDespuesEjecutaSentenciaSQL()} (también se realiza en caso de error).</li>
     * </ol>
     * 
     * La clase que invoca este método es encargada de cerrar la {@link Connection} con el SGBD.
     * 
     * @param sentenciaSQL Sentencia SQL a ejecutar.
     * 
     * @throws SQLException Se lanza si ha habido algún problema con el SGBD al ejecutar {@literal sentenciaSQL}.
     */
    public void ejecutaSentencia(String sentenciaSQL) throws SQLException;

    /**
     * Ejecuta una sentencia SQL (DML: @literal INSERT}, {@literal UPDATE} o {@literal DELETE}). Este método está pensado para la ejecución de sentencias SQL
     * pertenenciantes al grupo de &quot;Data Manipulation Language&quot; (DML), en particular
     * sentencias {@literal INSERT}, {@literal UPDATE} o {@literal DELETE}. Los pasos para ejecutar
     * la instrucción SQL son los mismo que con {@link #ejecutaSentencia(String)}.
     * 
     * @param sentenciaSQL Sentencia SQL a ejecutar.
     * 
     * @return Devuelve el número de filas afectadas por la sentencia SQL {@literal sentenciaSQL}.
     * 
     * @throws SQLException Se lanza si ha habido algún problema con el SGBD al ejecutar {@literal sentenciaSQL}.
     * 
     * @see #ejecutaSentencia(String) Pasos en la ejecución de una sentencia SQL
     */
    public int ejecutaSentenciaDevuelveNumFilasAfectadas(String sentenciaSQL) throws SQLException;

    /**
     * Ejecuta una sentencia SQL (DML: @literal SELECT}). Este método está pensado para la ejecución de sentencias SQL
     * pertenenciantes al grupo de &quot;Data Manipulation Language&quot; (DML), en particular
     * sentencias {@literal SELECT} cuyos resultados se mostrarán en la salida estándar del sistema
     * {@link System#out}. Los pasos para ejecutar la instrucción SQL son los mismo que con
     * {@link #ejecutaSentencia(String)}.
     * 
     * El formato por la salida estándar es:
     * <ol>
     * <li>Si {@code muestraCabecer == true} se muestra el nombre de las columnas (separadas por comas) y su tipo de datos asociado.</li>
     * <li>Línea en blanco.</li>
     * <li>Una línea por cada.</li>
     * </ol>
     * 
     * @param sentenciaSQL Sentencia SQL a ejecutar.
     * 
     * @param muestraCabecera booleano para indicar si se mostrarán los nombres de columnas
     * 
     * @throws SQLException Se lanza si ha habido algún problema con el SGBD al ejecutar {@literal sentenciaSQL}.
     * 
     * @see #ejecutaSentencia(String) Pasos en la ejecución de una sentencia SQL
     */
    public void ejecutaSentenciaMuestraEnSalidaEstandar(String sentenciaSQL, boolean muestraCabecera) throws SQLException;
}