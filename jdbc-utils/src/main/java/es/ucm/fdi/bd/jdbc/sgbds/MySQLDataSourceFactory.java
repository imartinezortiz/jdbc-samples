package es.ucm.fdi.bd.jdbc.sgbds;

import java.util.Properties;

import javax.sql.DataSource;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import es.ucm.fdi.bd.jdbc.PlantillaJdbcConDataSource;
import es.ucm.fdi.bd.utils.PropertiesUtils;

/**
 * Factoría de {@link DataSource} para el SGBD MySQL
 * 
 * @see <a href="http://dev.mysql.com">Sitio de MySQL</a>
 * 
 * @author Ivan Martinez Ortiz
 */
abstract public class MySQLDataSourceFactory {

    /**
     * Nombre del fichero que contiene los parámetros de configuración
     */
    public static final String MYSQL_PROPERTIES = "mysql.properties";

    /**
     * URL por defecto para HSQLDB;
     */
    public static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/test";

    /**
     * Nombre de usuario por defecto.
     */
    public static final String DEFAULT_USERNAME = "root";

    /**
     * Contraseña por defecto.
     */
    public static final String DEFAULT_PASSWORD = "root";

    /**
     * No se permite crear instancias de la clase.
     */
    private MySQLDataSourceFactory() {
    }

    public static DataSource createDataSource() {
        return createDataSource(PropertiesUtils.cargaProperties(MYSQL_PROPERTIES));
    }

    public static DataSource createDataSource(Properties properties) {
        MysqlDataSource ds = new MysqlDataSource();
        ds.setUrl(properties.getProperty(PlantillaJdbcConDataSource.CONFIG_URL_PROPERTY, DEFAULT_URL));
        ds.setUser(properties.getProperty(PlantillaJdbcConDataSource.CONFIG_USER_PROPERTY, DEFAULT_USERNAME));
        ds.setPassword(properties.getProperty(PlantillaJdbcConDataSource.CONFIG_PASSWORD_PROPERTY, DEFAULT_PASSWORD));
        return ds;
    }
}