package es.ucm.fdi.bd.jdbc.oracle;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.util.Properties;

class SimpleJDBCVersion2 {
  public static void main (String args[]) throws Exception {

    Properties props = new Properties();
    // Apertura de fichero como recurso
    InputStream fichero = SimpleJDBCVersion2.class.getClassLoader().getResourceAsStream("database.properties");
    props.load(fichero);

    Connection conn = conn = DriverManager.getConnection(props.getProperty("jdbc.url"), props.getProperty("jdbc.username"), props.getProperty("jdbc.password"));
    // Create Oracle DatabaseMetaData object
    DatabaseMetaData meta = conn.getMetaData();
    
    // gets driver info:
    System.out.println("JDBC driver version is " + meta.getDriverVersion());
  }
}