package es.ucm.fdi.bd.jdbc.avanzada;

import java.sql.SQLException;

import es.ucm.fdi.bd.jdbc.PlantillaJdbc;

public interface PlantillaJdbcAvanzada extends PlantillaJdbc {

    public void ejecutaSentencia(String sentenciaSQL, Object... params) throws SQLException;

    public int ejecutaSentenciaDevuelveNumFilasAfectadas(String sentenciaSQL, Object... params) throws SQLException;

    public void ejecutaSentenciaMuestraEnSalidaEstandar(String sentenciaSQL, boolean muestraCabecera, Object... params) throws SQLException;

    public <T> T ejecutaConsulta(String consulta, ProcesadorResultSet<T> procesador) throws SQLException;

    public <T> T ejecutaConsulta(String consulta, ProcesadorFilas<T> procesador) throws SQLException;

    public <T> T ejecutaConsulta(String consulta, ProcesadorResultSet<T> procesador, Object... params) throws SQLException;

    public <T> T ejecutaConsulta(String consulta, ProcesadorFilas<T> procesador, Object... params) throws SQLException;
}
