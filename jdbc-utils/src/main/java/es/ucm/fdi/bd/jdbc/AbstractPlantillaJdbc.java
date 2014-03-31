package es.ucm.fdi.bd.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.ucm.fdi.bd.jdbc.sql.LectorSentenciasSQL;

/**
 * Implementación parcial de la plantilla para ejecutar sentencias SQL con JDBC.
 * 
 * @author Ivan Martinez Ortiz
 * 
 */
abstract public class AbstractPlantillaJdbc implements PlantillaJdbc {

    /**
     * Nombre del {@link Logger} utilizado en la clase.
     */
    public static final String NOMBRE_LOGGER = AbstractPlantillaJdbc.class.getCanonicalName();

    private static final Logger log = Logger.getLogger(NOMBRE_LOGGER);

    protected AbstractPlantillaJdbc() {

    }

    @Override
    final public void ejecutaSentenciasSQL(InputStream is) throws IOException {
        LectorSentenciasSQL parser = new LectorSentenciasSQL(is);
        String instr = null;
        while ((instr = parser.siguienteInstruccion()) != null) {
            try {
                ejecutaSentencia(instr);
            } catch (SQLException e) {
                log.log(Level.SEVERE, "Error al ejecutar: " + instr, e);
            }
        }
    }

    @Override
    final public void ejecutaSentencia(String sentenciaSQL) throws SQLException {
        Connection conn = getConnection();
        try {
            Statement stmt = conn.createStatement();
            try {
                stmt.execute(sentenciaSQL);
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

    /**
     * Método invocado tras ejecutarse una sentencia SQL.
     * 
     * @param conn
     *            {@link Connection} al SGBD.
     * 
     * @throws SQLException
     *             Se lanza si ocurre algún error con el SGBD durante la
     *             ejecución del método.
     */
    protected void doDespuesSentenciaSQL(Connection conn) throws SQLException {
    }

    /**
     * Método invocado en caso de error al ejecutar una sentencia SQL.
     * 
     * @param conn
     *            {@link Connection} al SGBD.
     * 
     * @throws SQLException
     *             Se lanza si ocurre algún error con el SGBD durante la
     *             ejecución del método.
     */
    protected void doEnCasoDeError(Connection conn, SQLException e) throws SQLException {
    }

    /**
     * Método invocado tras ejecutarse una sentencia SQL. Este método se invoca
     * en último ejecución de una sentencia SQL.
     * 
     * @throws SQLException
     *             Se lanza si ocurre algún error con el SGBD durante la
     *             ejecución del método.
     */
    protected void doSiempreDespuesEjecutaSentenciaSQL() throws SQLException {
    }

    /**
     * Devuelve una conexión configurada al SGBD.
     * 
     * @return la {@link Connection} al SGBD.
     * 
     * @throws SQLException
     *             Si hay algún problema al obtener la conexión al SGBD.
     *             
     * @see #configureConnection(Connection)
     */
    protected Connection getConnection() throws SQLException {
        Connection conn = createConnection();
        configureConnection(conn);
        return conn;
    }

    /**
     * Crea una conexión al SGBD.
     * 
     * @return la {@link Connection} al SGBD.
     * 
     * @throws SQLException
     *             Si hay algún problema al obtener la conexión al SGBD.
     */
    abstract protected Connection createConnection() throws SQLException;

    /**
     * Permite configurar la {@link Connection} antes de ser utilizada.
     * 
     * @param conn {@link Connection} a configurar.
     * 
     * @throws SQLException si ocurre algún error con el SGBD al configurar {@literal conn}
     */
    protected void configureConnection(Connection conn) throws SQLException {
    }
    
    @Override
    final public int ejecutaSentenciaDevuelveNumFilasAfectadas(String sentenciaSQL) throws SQLException {
        Connection conn = getConnection();
        try {
            Statement stmt = conn.createStatement();
            try {
                int filasAfectadas = stmt.executeUpdate(sentenciaSQL);
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
    public void ejecutaSentenciaMuestraEnSalidaEstandar(String sentenciaSQL, boolean muestraCabecera)
            throws SQLException {
        Connection conn = getConnection();
        try {
            Statement stmt = conn.createStatement();
            try {
                ResultSet rst = stmt.executeQuery(sentenciaSQL);
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

    protected void muestraCabeceraSalidaEstandar(ResultSet rst) throws SQLException {
        ResultSetMetaData metadata = rst.getMetaData();
        int numColumnas = metadata.getColumnCount();
        for (int i = 1; i <= numColumnas; i++) {
            if (i > 1) {
                System.out.print(", ");
            }
            System.out.print(metadata.getColumnName(i));
            System.out.print(" (");
            System.out.print(tipoACadena(metadata.getColumnType(i)));
            System.out.print(")");
        }
        System.out.print("\n\n");
    }

    private String tipoACadena(int type) {
        String tipo = "";
        switch (type) {
        case Types.ARRAY:
            tipo = "Array";
            break;
        case Types.BIGINT:
            tipo = "BigInt";
            break;
        case Types.BINARY:
            tipo = "BINARY";
            break;
        case Types.BIT:
            tipo = "Big";
            break;
        case Types.BLOB:
            tipo = "Blob";
            break;
        case Types.BOOLEAN:
            tipo = "Boolean";
            break;
        case Types.CHAR:
            tipo = "Char";
            break;
        case Types.CLOB:
            tipo = "Clob";
            break;
        case Types.DATE:
            tipo = "Date";
            break;
        case Types.DECIMAL:
            tipo = "Decimal";
            break;
        case Types.DOUBLE:
            tipo = "Double";
            break;
        case Types.FLOAT:
            tipo = "Float";
            break;
        case Types.INTEGER:
            tipo = "Integer";
            break;
        case Types.LONGNVARCHAR:
            tipo = "LongNvarchar";
            break;
        case Types.LONGVARBINARY:
            tipo = "LongVarBinary";
            break;
        case Types.LONGVARCHAR:
            tipo = "LongVarchar";
            break;
        case Types.NCHAR:
            tipo = "NChar";
            break;
        case Types.NCLOB:
            tipo = "NClob";
            break;
        case Types.NULL:
            tipo = "Null";
            break;
        case Types.NUMERIC:
            tipo = "Numeric";
            break;
        case Types.NVARCHAR:
            tipo = "NVarchar";
            break;
        case Types.REAL:
            tipo = "Real";
            break;
        case Types.SMALLINT:
            tipo = "Smallint";
            break;
        case Types.TIME:
            tipo = "Time";
            break;
        case Types.TIMESTAMP:
            tipo = "Timestamp";
            break;
        case Types.TINYINT:
            tipo = "TinyInt";
            break;
        case Types.VARBINARY:
            tipo = "VarBinary";
            break;
        case Types.VARCHAR:
            tipo = "VarChar";
            break;
        default:
            tipo = "Otro(" + type + ")";
            break;
        }

        return tipo;
    }

    protected void muestraFilaSalidaEstandar(ResultSet rst) throws SQLException {
        ResultSetMetaData metadata = rst.getMetaData();
        int numColumnas = metadata.getColumnCount();
        for (int i = 1; i <= numColumnas; i++) {
            if (i > 1) {
                System.out.print(", ");
            }
            Object o = rst.getObject(i);
            if (o != null) {
                System.out.print(o);
            } else {
                System.out.print("null");
            }
        }
        System.out.println();
    }
}
