package es.ucm.fdi.bd.derby;
import java.math.BigDecimal;
import java.sql.SQLException;

import javax.sql.DataSource;

import es.ucm.fdi.bd.jdbc.PlantillaJdbc;
import es.ucm.fdi.bd.jdbc.PlantillaJdbcConDataSource;
import es.ucm.fdi.bd.jdbc.sgbds.DerbyDataSourceFactory;
import es.ucm.fdi.bd.jdbc.tx.NivelAislamientoEnum;

public class PruebasDerby {

    public static void main(String args[]) throws Exception {
        PruebasDerby test = new PruebasDerby();

        test.pruebaSimple();
        test.prueba1TransaccionTransferencia();
        test.prueba1TransaccionFalloEnMitad();
        test.prueba2TransaccionesBienConfiguradas();
        test.prueba2TransaccionesMalConfiguradas();

    }

    private DataSource ds;

    public PruebasDerby() {
        this.ds = DerbyDataSourceFactory.createDataSource();
        try {
            creaTablasPruebas();
        } catch (SQLException e) {
            throw new RuntimeException("Error creando las tablas", e);
        }
    }

    public void pruebaSimple() throws SQLException {
        System.out.println("## COMIENZO - Prueba Simple");

        PlantillaJdbc plantilla = new PlantillaJdbcConDataSource(ds);
        plantilla.ejecutaSentenciaMuestraEnSalidaEstandar("SELECT 2+2 AS RESULTADO FROM sysibm.sysdummy1", true);

        System.out.println("## FIN - Prueba Simple \n#");
    }

    /**
     * Prueba de una transferencia con 1 única transacción.
     */
    public void prueba1TransaccionTransferencia() throws Exception {
        System.out.println("## COMIENZO - 1 Transaccion simple");
        recreaDatosPruebas();

        System.out.println("Datos antes");
        muestraDatos();

        Runnable transferencia = new TransferenciaSimpleBuilder(ds)
        .cuentaOrigen(1)
        .cuentaDestino(2)
        .cantidad(BigDecimal.valueOf(20l))
        .transferencia();
        System.out.println(transferencia);

        Thread hilo = new Thread(transferencia, "Transferencia 1");
        hilo.start();
        hilo.join();

        System.out.println("Datos despues");
        muestraDatos();

        System.out.println("## FIN - 1 Transaccion simple");
    }

    /**
     * Prueba con una única transacción con fallo en mitad de la transacción (NO
     * HAY CAMBIOS EN EL RESTULTADO)
     */
    public void prueba1TransaccionFalloEnMitad() throws Exception {
        System.out.println("## COMIENZO - 1 Transaccion simple (FALLO FORZADO)");

        recreaDatosPruebas();

        System.out.println("Datos antes");
        muestraDatos();

        Runnable transferencia = new TransferenciaSimpleBuilder(ds)
        .cuentaOrigen(1)
        .cuentaDestino(2)
        .cantidad(BigDecimal.valueOf(20l))
        .retrasoMitad(1)
        .forzarFallo()
        .transferencia();
        System.out.println(transferencia);

        Thread hilo = new Thread(transferencia, "Transferencia 1");
        hilo.start();
        hilo.join();

        System.out.println("Datos despues");
        muestraDatos();

        System.out.println("## FIN - Transaccion simple (FALLO FORZADO)");
    }
    
    /**
     * Prueba con 2 transacciones bien configuradas
     */
    public void prueba2TransaccionesBienConfiguradas() throws Exception {
        System.out.println("## COMIENZO - 2 Transacciones BIEN Configuradas");
        recreaDatosPruebas();

        System.out.println("Datos antes");
        muestraDatos();

        Runnable transferencia1 = new TransferenciaComplejaBuilder(ds)
        .cuentaOrigen(1)
        .cuentaDestino(2)
        .cantidad(BigDecimal.valueOf(20l))
        .retrasoMitad(2)
        .forzarFallo()
        .nivelAislamiento(NivelAislamientoEnum.READ_COMMITED)
        .transferencia();
        System.out.println(transferencia1);
        
        Runnable transferencia2 = new TransferenciaComplejaBuilder(ds)
        .cuentaOrigen(1)
        .cuentaDestino(2)
        .cantidad(BigDecimal.valueOf(40l))
        .retrasoInicial(1)
        .retrasoMitad(3)
        .nivelAislamiento(NivelAislamientoEnum.READ_COMMITED)
        .transferencia();
        System.out.println(transferencia2);

        Thread hilo1 = new Thread(transferencia1, "Transferencia 1");
        Thread hilo2 = new Thread(transferencia2, "Transferencia 2");
        hilo1.start();
        hilo2.start();
        hilo1.join();
        hilo2.join();

        System.out.println("Datos despues");
        muestraDatos();

        System.out.println("## FIN - 2 Transacciones BIEN Configuradas");
    }

    /** 
     * Prueba con 2 transacciones MAL configuradas
     */
    public void prueba2TransaccionesMalConfiguradas() throws Exception {
        System.out.println("## COMIENZO - 2 Transacciones MAL Configuradas");
        recreaDatosPruebas();

        System.out.println("Datos antes");
        muestraDatos();

        Runnable transferencia1 = new TransferenciaComplejaBuilder(ds)
        .cuentaOrigen(1)
        .cuentaDestino(2)
        .cantidad(BigDecimal.valueOf(20l))
        .retrasoMitad(5)
        .forzarFallo()
        .nivelAislamiento(NivelAislamientoEnum.READ_UNCOMMITED)
        .transferencia();
        System.out.println(transferencia1);
                
        Runnable transferencia2 = new TransferenciaComplejaBuilder(ds)
        .cuentaOrigen(1)
        .cuentaDestino(2)
        .cantidad(BigDecimal.valueOf(40l))
        .retrasoInicial(2)
        .retrasoMitad(9)
        .nivelAislamiento(NivelAislamientoEnum.READ_UNCOMMITED)
        .transferencia();
        System.out.println(transferencia2);

        Thread hilo1 = new Thread(transferencia1, "Transferencia 1");
        Thread hilo2 = new Thread(transferencia2, "Transferencia 2");
        
        hilo1.start(); hilo2.start();
        hilo1.join(); hilo2.join();

        System.out.println("Datos despues");
        muestraDatos();

        System.out.println("## FIN - 2 Transacciones MAL Configuradas");
    }
    
    private void creaTablasPruebas() throws SQLException {
        PlantillaJdbc plantilla = new PlantillaJdbcConDataSource(ds);
        try {
            plantilla.ejecutaSentencia("DROP TABLE Cuentas");
        } catch (Throwable ignore) {
            // Se lanza si no existe la tabla A
            // ignore.printStackTrace();
        }
        plantilla
                .ejecutaSentencia("CREATE TABLE Cuentas ( codCuenta INTEGER PRIMARY KEY, saldo NUMERIC(8,2) NOT NULL DEFAULT 0.0, cliente VARCHAR(10) NOT NULL)");
    }

    private void recreaDatosPruebas() throws SQLException {
        PlantillaJdbc plantilla = new PlantillaJdbcConDataSource(ds);
        plantilla.ejecutaSentencia("DELETE FROM Cuentas");
        plantilla.ejecutaSentencia("INSERT INTO Cuentas VALUES (1, 1000, 'C1'), (2, 200, 'C2')");
    }

    private void muestraDatos() throws SQLException {
        PlantillaJdbc plantilla = new PlantillaJdbcConDataSource(ds);
        plantilla.ejecutaSentenciaMuestraEnSalidaEstandar("SELECT * FROM Cuentas", true);
    }
}