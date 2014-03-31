package es.ucm.fdi.bd.jdbc.sgbds;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.derby.jdbc.EmbeddedDataSource40;

import es.ucm.fdi.bd.jdbc.PlantillaJdbcConDataSource;
import es.ucm.fdi.bd.utils.PropertiesUtils;

/**
 * Factoría de {@link DataSource} para el SGBD Apache Derby.
 * 
 * @see <a href="http://db.apache.org/derby">Sitio web de Apache Derby</a>
 * 
 * @author Ivan Martinez Ortiz
 * 
 */
abstract public class DerbyDataSourceFactory {

    /**
     * Nombre del fichero que contiene los parámetros de configuración para HSQLDB
     */
    public static final String DERBY_PROPERTIES = "derby.properties";
    
    /**
     * URL por defecto para HSQLDB;
     */
    public static final String DEFAULT_URL = "jdbc:derby:sample;create=true;";
    
    /**
     * Nombre de usuario por defecto.
     */
    public static final String DEFAULT_USERNAME = "";
    
    /**
     * Contraseña por defecto.
     */
    public static final String DEFAULT_PASSWORD = "";
    
    /**
     * No se permite crear instancias de la clase.
     */
    private DerbyDataSourceFactory() {        
    }
    
    public static DataSource createDataSource() {
        return createDataSource(PropertiesUtils.cargaProperties(DERBY_PROPERTIES));
    }

    public static DataSource createDataSource(Properties properties) {
        EmbeddedDataSource40 ds = new EmbeddedDataSource40();
        
        String url = properties.getProperty(PlantillaJdbcConDataSource.CONFIG_URL_PROPERTY, DEFAULT_URL);
        url = url.substring(url.indexOf(':', 5)+1);
        
        String databaseName = url.substring(0, url.indexOf(';'));        
        ds.setDatabaseName(databaseName);
        Map<String, String> parameters = parseParameters(url.substring(url.indexOf(';')+1));
        if (parameters.containsKey("create") && Boolean.valueOf(parameters.get("create"))) {
            ds.setCreateDatabase("create");
        }
        
        ds.setUser(properties.getProperty(PlantillaJdbcConDataSource.CONFIG_USER_PROPERTY, DEFAULT_USERNAME));
        ds.setPassword(properties.getProperty(PlantillaJdbcConDataSource.CONFIG_PASSWORD_PROPERTY, DEFAULT_PASSWORD));
        return ds;
    }

    private static Map<String, String> parseParameters(String params) {
        Map<String, String> parameters = new HashMap<String, String>();
        String[] paramList = params.split(";");
        for(int i = 0; i < paramList.length; i++) {
            String param = paramList[i];
            int pos = param.indexOf('=');
            parameters.put(param.substring(0, pos), param.substring(pos+1));
        }
        return parameters;
    }
}
