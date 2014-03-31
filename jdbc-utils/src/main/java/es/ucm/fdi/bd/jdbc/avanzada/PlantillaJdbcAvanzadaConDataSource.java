package es.ucm.fdi.bd.jdbc.avanzada;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import es.ucm.fdi.bd.jdbc.PlantillaJdbcConDataSource;

public class PlantillaJdbcAvanzadaConDataSource extends PlantillaJdbcConDataSource implements PlantillaJdbcAvanzada {

    public PlantillaJdbcAvanzadaConDataSource(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void ejecutaSentencia(String sentenciaSQL, Object... params) throws SQLException {
        Connection conn = getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement(sentenciaSQL);
            for(int i = 1; i <= params.length; i++) {
                stmt.setObject(i, params[i-1]);
            }
            try {
                stmt.execute();
                doDespuesSentenciaSQL(conn);
            } catch (SQLException e) {
                doEnCasoDeError(conn, e);
                throw e;
            } finally {
                if (stmt != null) {
                    stmt.close();
                }
            }
        } finally {
            doSiempreDespuesEjecutaSentenciaSQL();
        }
    }

    @Override
    public int ejecutaSentenciaDevuelveNumFilasAfectadas(String sentenciaSQL, Object... params) throws SQLException {
        Connection conn = getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement(sentenciaSQL);
            for(int i = 1; i <= params.length; i++) {
                stmt.setObject(i, params[i-1]);
            }
            try {
                int filasAfectadas = stmt.executeUpdate();
                doDespuesSentenciaSQL(conn);
                return filasAfectadas;
            } catch (SQLException e) {
                doEnCasoDeError(conn, e);
                throw e;
            } finally {
                if (stmt != null) {
                    stmt.close();
                }
            }
        } finally {
            doSiempreDespuesEjecutaSentenciaSQL();
        }
    }

    @Override
    public void ejecutaSentenciaMuestraEnSalidaEstandar(String sentenciaSQL, boolean muestraCabecera, Object... params)
            throws SQLException {
        Connection conn = getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement(sentenciaSQL);
            for(int i = 1; i <= params.length; i++) {
                stmt.setObject(i, params[i-1]);
            }
            try {
                ResultSet rst = stmt.executeQuery();
                try {
                    if (muestraCabecera) {
                        muestraCabeceraSalidaEstandar(rst);
                    }
                    while (rst.next()) {
                        muestraFilaSalidaEstandar(rst);
                    }
                } finally {
                    if (rst != null) {
                        rst.close();
                    }
                }
                doDespuesSentenciaSQL(conn);
            } catch (SQLException e) {
                doEnCasoDeError(conn, e);
                throw e;
            } finally {
                if (stmt != null) {
                    stmt.close();
                }
            }
        } finally {
            doSiempreDespuesEjecutaSentenciaSQL();
        }
    } 

    @Override
    public <T> T ejecutaConsulta(String consulta, ProcesadorResultSet<T> procesador) throws SQLException {
        Connection conn = getConnection();
        T resultado = null;
        try {
            Statement stmt = conn.createStatement();
            try {                
                ResultSet rst = stmt.executeQuery(consulta);
                try {                    
                    resultado = procesador.procesaResultado(rst);
                } finally {
                    if (rst != null) {
                        rst.close();
                    }
                }
                doDespuesSentenciaSQL(conn);
            } catch(SQLException e) {
                doEnCasoDeError(conn, e);
            } finally {
                if (stmt != null) {
                    stmt.close();
                }
            }
        } finally {
            doSiempreDespuesEjecutaSentenciaSQL();
        }
        return resultado;
    }

    @Override
    public <T> T ejecutaConsulta(String consulta, ProcesadorFilas<T> procesador) throws SQLException {
        return ejecutaConsulta(consulta, new ProcesaFilas<T>(procesador));
    }

    @Override
    public <T> T ejecutaConsulta(String consulta, ProcesadorResultSet<T> procesador, Object... params)
            throws SQLException {
        Connection conn = getConnection();
        T resultado = null;
        try {         
            PreparedStatement stmt = conn.prepareStatement(consulta);
            for(int i = 1; i <= params.length; i++) {
                stmt.setObject(i, params[i-1]);
            }
            try {
                ResultSet rst = stmt.executeQuery();
                try {                    
                    resultado = procesador.procesaResultado(rst);
                } finally {
                    if (rst != null) {
                        rst.close();
                    }
                }
                doDespuesSentenciaSQL(conn);
            } catch(SQLException e) {
                doEnCasoDeError(conn, e);
            } finally {
                if (stmt != null) {
                    stmt.close();
                }
            }
        } finally {
            doSiempreDespuesEjecutaSentenciaSQL();
        }
        return resultado;
    }

    @Override
    public <T> T ejecutaConsulta(String consulta, ProcesadorFilas<T> procesador, Object... params) throws SQLException {
        return ejecutaConsulta(consulta, new ProcesaFilas<T>(procesador), params);
    }
}
