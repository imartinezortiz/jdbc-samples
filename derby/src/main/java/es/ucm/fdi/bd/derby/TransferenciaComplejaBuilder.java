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

public class TransferenciaComplejaBuilder {

    private Integer cuentaOrigen;
    
    private Integer cuentaDestino;
    
    private BigDecimal cantidad;
    
    private int segundosRetrasoInicial;
    
    private int segundosRetrasoMitad;
    
    private boolean forzarFallo;
    
    private NivelAislamientoEnum nivelAislamiento;
        
    private DataSource ds;
    
    public TransferenciaComplejaBuilder(DataSource ds) {
        this.ds = ds;
        this.forzarFallo = false;
        this.nivelAislamiento = NivelAislamientoEnum.READ_COMMITED;
        this.segundosRetrasoInicial = 0;
        this.segundosRetrasoMitad = 0;
    }
    
    public TransferenciaComplejaBuilder cuentaOrigen (int cuentaOrigen) {
        this.cuentaOrigen = cuentaOrigen;
        return this;
    }
    
    public TransferenciaComplejaBuilder cuentaDestino (int cuentaDestino) {
        this.cuentaDestino = cuentaDestino;
        return this;
    }
    
    public TransferenciaComplejaBuilder cantidad (BigDecimal cantidad) {
        this.cantidad = cantidad;
        return this;
    }
    
    public TransferenciaComplejaBuilder retrasoInicial(int segundos) {
        this.segundosRetrasoInicial = segundos;
        return this;
    }
    
    public TransferenciaComplejaBuilder retrasoMitad(int segundos) {
        this.segundosRetrasoMitad = segundos;
        return this;
    }
    
    public TransferenciaComplejaBuilder forzarFallo() {
        this.forzarFallo = true;
        return this;
    }
    
    public TransferenciaComplejaBuilder nivelAislamiento(NivelAislamientoEnum nivel) {
        this.nivelAislamiento = nivel;
        return this;
    }

    public Runnable transferencia() {
        return new TransferenciaCompleja(cuentaOrigen, cuentaDestino, cantidad, segundosRetrasoInicial, segundosRetrasoMitad, forzarFallo,
                nivelAislamiento, ds);
    }

    private static class TransferenciaCompleja extends AbstractOperacion {

        private final int cuentaOrigen;
        
        private final int cuentaDestino;
        
        private final BigDecimal cantidad;
        
        private final int segundosRetrasoInicial;
        
        private final int segundosRetrasoMitad;
        
        private final boolean forzarFallo;

        public TransferenciaCompleja(final int cuentaOrigen, final int cuentaDestino, final BigDecimal cantidad,
                final int segundosRetrasoInicial, final int segundosRetrasoMitad, final boolean forzarFallo,
                final NivelAislamientoEnum nivelAislamiento, DataSource dataSource) {
            super(nivelAislamiento, dataSource);
            this.cuentaOrigen = cuentaOrigen;
            this.cuentaDestino = cuentaDestino;
            this.cantidad = cantidad;
            this.segundosRetrasoInicial = segundosRetrasoInicial;
            this.segundosRetrasoMitad = segundosRetrasoMitad;
            this.forzarFallo = forzarFallo;
        }

        @Override
        public String toString() {
            return "TransferenciaCompleja [cuentaOrigen=" + cuentaOrigen + ", cuentaDestino=" + cuentaDestino
                    + ", cantidad=" + cantidad + "]";
        }

        @Override
        public AccionTransaccional<Void> getAccion() {
            return new AccionTransaccional<Void>() {
                @Override
                public Void ejecutarEnTransaccion(EstadoTransaccion estado, PlantillaJdbcAvanzada plantilla)
                        throws SQLException {
                    // Forzamos que haya un retraso al comienzo
                    espera(segundosRetrasoInicial);
                    
                    // Consultamos saldo de la cuenta origen
                    BigDecimal saldoOrigen = plantilla.ejecutaConsulta(
                            "SELECT saldo FROM Cuentas WHERE codCuenta= ?"
                            , new ProcesadorFilas<BigDecimal>() {
                                @Override
                                public BigDecimal procesaFila(ResultSet rs) throws SQLException {
                                    return rs.getBigDecimal(1);
                                }
                            }
                            , cuentaOrigen);

                    // Calculamos el nuevo saldo de la cuenta origen
                    saldoOrigen = saldoOrigen.subtract(cantidad);
                    
                    // Consultamos saldo de la cuenta destino
                    BigDecimal saldoDestino = plantilla.ejecutaConsulta(
                            "SELECT saldo FROM Cuentas WHERE codCuenta= ?"
                            , new ProcesadorFilas<BigDecimal>() {
                                @Override
                                public BigDecimal procesaFila(ResultSet rs) throws SQLException {
                                    return rs.getBigDecimal(1);
                                }
                            }
                            , cuentaDestino);
                    
                    // Calculamos el nuevo saldo para la cuenta destino
                    saldoDestino = saldoDestino.add(cantidad);                        
                    
                    // Actualizamos el saldo de la cuenta origen
                    plantilla.ejecutaSentencia("UPDATE Cuentas SET saldo = ? WHERE codCuenta= ?"
                            , saldoOrigen, cuentaOrigen);
                    
                    espera(segundosRetrasoMitad);

                    if (forzarFallo) {
                        throw new RuntimeException("Forzando fallo.");
                    }

                    // Actualizamos el saldo de la cuenta destino
                    plantilla.ejecutaSentencia("UPDATE Cuentas SET saldo = ? WHERE codCuenta= ?"
                            , saldoDestino, cuentaDestino);
                    
                    return null;
                }
            };
        }
    }
}
