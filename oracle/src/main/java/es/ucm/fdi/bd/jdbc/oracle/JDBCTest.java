package es.ucm.fdi.bd.jdbc.oracle;
// CLASSPATH: jdbc/lib/ojdbc5.jar, jlib/orai18n.jar
//

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class JDBCTest {
  
  private Connection getConnection() {
    Properties props = new Properties();
    // Apertura de fichero como recurso
    try {
      InputStream fichero = JDBCTest.class.getClassLoader().getResourceAsStream("database.properties");
      props.load(fichero);
    }catch(IOException ioe) {
      ioe.printStackTrace();
      throw new RuntimeException("No se ha podido leer el fichero de configuración");
    }
    
    Connection conn = null;
    try {
      conn = DriverManager.getConnection(props.getProperty("jdbc.url"), props.getProperty("jdbc.username"), props.getProperty("jdbc.password"));
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException("No se ha podido conectar a la BD");
    }
    return conn;
  }
  
  public void prueba() {

    Connection conn = getConnection();
    
    // Create Oracle DatabaseMetaData object
    DatabaseMetaData meta = null;
    Statement stmt = null;
    ResultSet rs = null;
    try {
      meta = conn.getMetaData();
      // gets driver info:
      System.out.println("JDBC driver version is " + meta.getDriverVersion());

      stmt = conn.createStatement();
      stmt.execute("SELECT 2+2 FROM DUAL");
      rs = stmt.getResultSet();
      
      while(rs.next()) {
        System.out.println(rs.getString(1));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if ( rs != null ) { rs.close(); }
      } catch (SQLException ignore) {
        ignore.printStackTrace();
      }
      try {
        if ( stmt != null ) { stmt.close(); }
      } catch (SQLException ignore) {
        ignore.printStackTrace();
      }
      try {
        if ( conn != null) { conn.close(); }
      } catch (SQLException ignore) {
        ignore.printStackTrace();
      }
     }
  }
  
  public static void main (String args[]) {

    JDBCTest test = new JDBCTest();
    test.prueba();
    
  }
}