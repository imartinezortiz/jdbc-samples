package es.ucm.fdi.bd.jdbc.sgbds;

import java.util.Properties;

import javax.sql.DataSource;

import org.hsqldb.jdbc.JDBCDataSource;

import es.ucm.fdi.bd.jdbc.PlantillaJdbcConDataSource;
import es.ucm.fdi.bd.utils.PropertiesUtils;

/**
 * Factoría de {@link DataSource} para el SGBD HSQLDB.
 * 
 * @see <a href="http://hsqldb.org">Sitio web de HSQLDB</a>
 * 
 * @author Ivan Martinez Ortiz
 * 
 */
abstract public class HsqldbDataSourceFactory {

    /**
     * Nombre del fichero que contiene los parámetros de configuración para HSQLDB
     */
    public static final String HSQLDB_PROPERTIES = "hsqldb.properties";
    
    /**
     * URL por defecto para HSQLDB;
     */
    public static final String DEFAULT_URL = "jdbc:hsqldb:hsql://localhost/xdb";
    
    /**
     * Nombre de usuario por defecto.
     */
    public static final String DEFAULT_USERNAME = "sa";
    
    /**
     * Contraseña por defecto.
     */
    public static final String DEFAULT_PASSWORD = "";
    
    /**
     * No se permite crear instancias de la clase.
     */
    private HsqldbDataSourceFactory() {        
    }
    
    public static DataSource createDataSource() {
        return createDataSource(PropertiesUtils.cargaProperties(HSQLDB_PROPERTIES));
    }

    public static DataSource createDataSource(Properties properties) {
        JDBCDataSource ds = new JDBCDataSource();
        ds.setUrl(properties.getProperty(PlantillaJdbcConDataSource.CONFIG_URL_PROPERTY, DEFAULT_URL));
        ds.setUser(properties.getProperty(PlantillaJdbcConDataSource.CONFIG_USER_PROPERTY, DEFAULT_USERNAME));
        ds.setPassword(properties.getProperty(PlantillaJdbcConDataSource.CONFIG_PASSWORD_PROPERTY, DEFAULT_PASSWORD));
        return ds;
    }
}
