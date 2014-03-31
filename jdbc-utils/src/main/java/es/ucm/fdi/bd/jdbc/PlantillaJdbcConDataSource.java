package es.ucm.fdi.bd.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * Plantilla que ejecuta sentencias SQL con JDBC y obtiene las conexiones con un {@link DataSource}.
 * 
 * @author Ivan Martinez Ortiz
 */
public class PlantillaJdbcConDataSource extends AbstractPlantillaJdbc {

    /**
     * Nombre por defecto del archivo de configuración utilizado para configurar un {@link DataSource}
     */
    public static final String DEFAULT_CONFIG_FILE = "database.properties";

    /**
     * Nombre de la propiedad utilizada para almacenar la URL de conexión.
     */
    public static final String CONFIG_URL_PROPERTY = "jdbc.url";

    /**
     * Nombre de la propiedad utilizada para almacenar el nombre de usuario a utilizar al conectarse
     * al SGBD.
     */
    public static final String CONFIG_USER_PROPERTY = "jdbc.username";

    /**
     * Nombre de la propiedad utilizada para almacenar la contraseña a utilizar al conectarse al SGBD.
     */
    public static final String CONFIG_PASSWORD_PROPERTY = "jdbc.password";

    private DataSource dataSource;

    private Connection conn;

    /**
     * Crea una plantilla para realizar consultas con JDBC.
     * 
     * @param dataSource {@link DataSource} utilizado para obtener conexiones.
     */
    public PlantillaJdbcConDataSource(DataSource dataSource) {
        if (dataSource == null) {
            throw new NullPointerException("dataSource no puede ser nulo.");
        }
        this.dataSource = dataSource;
    }

    @Override
    protected Connection createConnection() throws SQLException {
        conn = dataSource.getConnection();
        return conn;
    }

    @Override
    protected void doSiempreDespuesEjecutaSentenciaSQL() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }
}
