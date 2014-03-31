package es.ucm.fdi.bd.jdbc.sgbds;

import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import oracle.jdbc.pool.OracleDataSource;
import es.ucm.fdi.bd.jdbc.PlantillaJdbcConDataSource;
import es.ucm.fdi.bd.utils.PropertiesUtils;

/**
 * Factoría de {@link DataSource} para el SGBD Oracle Database
 * 
 * 
 * @see <a href="http://www.oracle.com/pls/db112/homepage">Sitio de Oracle Database 11gR2</a>
 * 
 * @author Ivan Martinez Ortiz
 */
abstract public class OracleDataSourceFactory {

    /**
     * Nombre del fichero que contiene los parámetros de configuración
     */
    public static final String ORACLE_PROPERTIES = "oracle.properties";

    /**
     * URL por defecto para HSQLDB;
     */
    public static final String DEFAULT_URL = "jdbc:oracle:thin:@localhost:1521/orcl";

    /**
     * Nombre de usuario por defecto.
     */
    public static final String DEFAULT_USERNAME = "SYSTEM";

    /**
     * Contraseña por defecto.
     */
    public static final String DEFAULT_PASSWORD = "oracle";

    /**
     * No se permite crear instancias de la clase.
     */
    private OracleDataSourceFactory() {
    }

    public static DataSource createDataSource() {
        return createDataSource(PropertiesUtils.cargaProperties(ORACLE_PROPERTIES));
    }

    public static DataSource createDataSource(Properties properties) {
        OracleDataSource ds;
        try {
            ds = new OracleDataSource();
        } catch (SQLException e) {
            throw new RuntimeException("Error creando el OracleDataSource", e);
        }
        ds.setURL(properties.getProperty(PlantillaJdbcConDataSource.CONFIG_URL_PROPERTY, DEFAULT_URL));
        ds.setUser(properties.getProperty(PlantillaJdbcConDataSource.CONFIG_USER_PROPERTY, DEFAULT_USERNAME));
        ds.setPassword(properties.getProperty(PlantillaJdbcConDataSource.CONFIG_PASSWORD_PROPERTY, DEFAULT_PASSWORD));
        return ds;
    }
}
