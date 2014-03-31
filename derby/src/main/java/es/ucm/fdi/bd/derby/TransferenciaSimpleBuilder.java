package es.ucm.fdi.bd.derby;
import java.math.BigDecimal;
import java.sql.SQLException;

import javax.sql.DataSource;

import es.ucm.fdi.bd.jdbc.avanzada.PlantillaJdbcAvanzada;
import es.ucm.fdi.bd.jdbc.tx.AccionTransaccional;
import es.ucm.fdi.bd.jdbc.tx.EstadoTransaccion;
import es.ucm.fdi.bd.jdbc.tx.NivelAislamientoEnum;

public class TransferenciaSimpleBuilder {

    private Integer cuentaOrigen;
    
    private Integer cuentaDestino;
    
    private BigDecimal cantidad;
    
    private int segundosRetrasoInicial;
    
    private int segundosRetrasoMitad;
    
    private boolean forzarFallo;
    
    private NivelAislamientoEnum nivelAislamiento;
        
    private DataSource ds;
    
    public TransferenciaSimpleBuilder(DataSource ds) {
        this.ds = ds;
        this.forzarFallo = false;
        this.nivelAislamiento = NivelAislamientoEnum.READ_COMMITED;
        this.segundosRetrasoInicial = 0;
        this.segundosRetrasoMitad = 0;
    }
    
    public TransferenciaSimpleBuilder cuentaOrigen (int cuentaOrigen) {
        this.cuentaOrigen = cuentaOrigen;
        return this;
    }
    
    public TransferenciaSimpleBuilder cuentaDestino (int cuentaDestino) {
        this.cuentaDestino = cuentaDestino;
        return this;
    }
    
    public TransferenciaSimpleBuilder cantidad (BigDecimal cantidad) {
        this.cantidad = cantidad;
        return this;
    }
    
    public TransferenciaSimpleBuilder retrasoInicial(int segundos) {
        this.segundosRetrasoInicial = segundos;
        return this;
    }
    
    public TransferenciaSimpleBuilder retrasoMitad(int segundos) {
        this.segundosRetrasoMitad = segundos;
        return this;
    }
    
    public TransferenciaSimpleBuilder forzarFallo() {
        this.forzarFallo = true;
        return this;
    }
    
    public TransferenciaSimpleBuilder nivelAislamiento(NivelAislamientoEnum nivel) {
        this.nivelAislamiento = nivel;
        return this;
    }

    public Runnable transferencia() {
        return new TransferenciaSimple(cuentaOrigen, cuentaDestino, cantidad, segundosRetrasoInicial, segundosRetrasoMitad, forzarFallo,
                nivelAislamiento, ds);
    }

    private static class TransferenciaSimple extends AbstractOperacion {

        private final int cuentaOrigen;
        
        private final int cuentaDestino;
        
        private final BigDecimal cantidad;
        
        private final int segundosRetrasoInicial;
        
        private final int segundosRetrasoMitad;
        
        private final boolean forzarFallo;


        public TransferenciaSimple(final int cuentaOrigen, final int cuentaDestino, final BigDecimal cantidad,
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
            return "TransferenciaSimple [cuentaOrigen=" + cuentaOrigen + ", cuentaDestino=" + cuentaDestino
                    + ", cantidad=" + cantidad + "]";
        }

        @Override
        protected AccionTransaccional<Void> getAccion () {
            return new AccionTransaccional<Void>() {
                @Override
                public Void ejecutarEnTransaccion(EstadoTransaccion estado, PlantillaJdbcAvanzada plantilla)
                        throws SQLException {
                    // Forzamos que haya un retraso al comienzo
                    espera(segundosRetrasoInicial);

                    // Quitamos el dinero a la cuenta origen
                    plantilla.ejecutaSentencia("UPDATE Cuentas SET saldo = saldo - ? WHERE codCuenta = ?"
                                                , cantidad , cuentaOrigen);

                    if (forzarFallo) {
                        throw new RuntimeException("Forzando fallo.");
                    }

                    // Forzamos que haya un retraso entre las
                    // operaciones
                    espera(segundosRetrasoMitad);

                    // Añadimos el dinero a la cuenta destino
                    plantilla.ejecutaSentencia("UPDATE Cuentas SET saldo = saldo + ? WHERE codCuenta = ?"
                                                , cantidad, cuentaDestino);

                    return null;
                }
            };
        }
    }
}
