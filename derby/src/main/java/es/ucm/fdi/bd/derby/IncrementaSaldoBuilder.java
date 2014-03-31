package es.ucm.fdi.bd.derby;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import es.ucm.fdi.bd.jdbc.avanzada.PlantillaJdbcAvanzada;
import es.ucm.fdi.bd.jdbc.avanzada.ProcesadorFilas;
import es.ucm.fdi.bd.jdbc.tx.AccionTransaccional;
import es.ucm.fdi.bd.jdbc.tx.EstadoTransaccion;
import es.ucm.fdi.bd.jdbc.tx.NivelAislamientoEnum;

public class IncrementaSaldoBuilder {

    private Integer cuenta;
    
    private BigDecimal cantidad;
    
    private int segundosRetrasoInicial;
    
    private int segundosRetrasoMitad;
    
    private boolean forzarFallo;
    
    private NivelAislamientoEnum nivelAislamiento;
        
    private DataSource ds;
    
    public IncrementaSaldoBuilder(DataSource ds) {
        this.ds = ds;
        this.forzarFallo = false;
        this.nivelAislamiento = NivelAislamientoEnum.READ_COMMITED;
        this.segundosRetrasoInicial = 0;
        this.segundosRetrasoMitad = 0;
    }
    
    public IncrementaSaldoBuilder cuenta (int cuenta) {
        this.cuenta = cuenta;
        return this;
    }
    
    public IncrementaSaldoBuilder cantidad (BigDecimal cantidad) {
        this.cantidad = cantidad;
        return this;
    }
    
    public IncrementaSaldoBuilder retrasoInicial(int segundos) {
        this.segundosRetrasoInicial = segundos;
        return this;
    }
    
    public IncrementaSaldoBuilder retrasoMitad(int segundos) {
        this.segundosRetrasoMitad = segundos;
        return this;
    }
    
    public IncrementaSaldoBuilder forzarFallo() {
        this.forzarFallo = true;
        return this;
    }
    
    public IncrementaSaldoBuilder nivelAislamiento(NivelAislamientoEnum nivel) {
        this.nivelAislamiento = nivel;
        return this;
    }

    public Runnable transferencia() {
        return new IncrementaSaldo(cuenta, cantidad, segundosRetrasoInicial, segundosRetrasoMitad, forzarFallo,
                nivelAislamiento, ds);
    }

    private static class IncrementaSaldo extends AbstractOperacion {

        private final int cuenta;
        
        private final BigDecimal cantidad;
        
        private final int segundosRetrasoInicial;
        
        private final int segundosRetrasoMitad;
        
        private final boolean forzarFallo;

        public IncrementaSaldo(final int cuenta, final BigDecimal cantidad,
                final int segundosRetrasoInicial, final int segundosRetrasoMitad, final boolean forzarFallo,
                final NivelAislamientoEnum nivelAislamiento, DataSource dataSource) {
            super(nivelAislamiento, dataSource);
            this.cuenta = cuenta;
            this.cantidad = cantidad;
            this.segundosRetrasoInicial = segundosRetrasoInicial;
            this.segundosRetrasoMitad = segundosRetrasoMitad;
            this.forzarFallo = forzarFallo;
        }

        @Override
        public String toString() {
            return "IncrementaSaldo [cuenta=" + cuenta + ", cantidad=" + cantidad + "]";
        }

        @Override
        public  AccionTransaccional<Void> getAccion() {
            return new AccionTransaccional<Void>() {
                @Override
                public Void ejecutarEnTransaccion(EstadoTransaccion estado, PlantillaJdbcAvanzada plantilla)
                        throws SQLException {
                    // Forzamos que haya un retraso al comienzo
                    espera(segundosRetrasoInicial);
                    
                    // Obtenemos el saldo actual de la cuenta
                    BigDecimal saldo = plantilla.ejecutaConsulta(
                        "SELECT saldo FROM Cuentas WHERE codCuenta= ?"
                        , new ProcesadorFilas<BigDecimal>() {
                            @Override
                            public BigDecimal procesaFila(ResultSet rs) throws SQLException {
                                return rs.getBigDecimal(1);
                            }
                        }
                        , cuenta);


                    if (forzarFallo) {
                        throw new RuntimeException("Forzando fallo.");
                    }
                    
                    // Forzamos que haya un retraso entre las
                    // operaciones
                    espera(segundosRetrasoMitad);
                    
                    // Incrementamos el saldo de la cuenta
                    BigDecimal nuevoSaldo = saldo.add(cantidad);
                    plantilla.ejecutaSentencia("UPDATE CUENTAS SET saldo = ? WHERE codCuenta= ?"
                            , nuevoSaldo, cuenta);
                    
                    return null;
                }
            };
        }
    }
}
