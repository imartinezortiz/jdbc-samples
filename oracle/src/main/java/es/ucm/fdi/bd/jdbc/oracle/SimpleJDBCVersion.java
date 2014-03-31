package es.ucm.fdi.bd.jdbc.oracle;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

class SimpleJDBCVersion {
  public static void main (String args[]) throws SQLException {

    Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@tania.fdi.ucm.es:1521/dbBDgris", "ADMINUSER", "ADMINUSER");
    // Create Oracle DatabaseMetaData object
    DatabaseMetaData meta = null;
    meta = conn.getMetaData();
    
    // gets driver info:
    System.out.println("JDBC driver version is " + meta.getDriverVersion());
  }
}