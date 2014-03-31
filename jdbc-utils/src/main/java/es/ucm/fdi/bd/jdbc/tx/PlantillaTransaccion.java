package es.ucm.fdi.bd.jdbc.tx;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import es.ucm.fdi.bd.jdbc.avanzada.PlantillaJdbcAvanzadaConDataSource;

/**
 * 
 * @author Ivan Martinez Ortiz
 * 
 */
public class PlantillaTransaccion {

    private DataSource dataSource;

    public PlantillaTransaccion(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> T ejecuta(AccionTransaccional<T> accion) throws SQLException {
        return ejecuta(NivelAislamientoEnum.READ_COMMITED, accion);

    }

    /**
     * Ejecuta la accion especificada dentro de una transacción.
     * 
     * @param nivelAislamiento
     *            Nivel de aislamiento para la transaccion
     * @param accion
     *            Acción a ejecutar.
     * 
     * @return Resultado de la transacción o <code>null</code> si no se devuelve
     *         nada.
     * 
     * @throws SQLException
     *             si hay un error de inicialización, rollback o error del
     *             sistema.
     */
    public <T> T ejecuta(NivelAislamientoEnum nivelAislamiento, AccionTransaccional<T> accion) throws SQLException {
        EstadoTransaccionJdbc estado = new EstadoTransaccionJdbc();
        PlantillaJdbcTransaccional plantilla = new PlantillaJdbcTransaccional(this.dataSource, nivelAislamiento);
        T resultado = null;
        Connection conn = null;
        try {
            resultado = accion.ejecutarEnTransaccion(estado, plantilla);
            conn = plantilla.getConnection();
            if (!estado.isForzarRollback()) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (SQLException e) {
            plantilla.getConnection().rollback();
            throw e;
        } catch (RuntimeException e) {
            plantilla.getConnection().rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return resultado;
    }

    private class EstadoTransaccionJdbc implements EstadoTransaccion {
        private boolean forzarRollback;

        public EstadoTransaccionJdbc() {
            this.forzarRollback = false;
        }

        @Override
        public void setForzarRollback() {
            this.forzarRollback = true;
        }

        @Override
        public boolean isForzarRollback() {
            return this.forzarRollback;
        }
    }

    private class PlantillaJdbcTransaccional extends PlantillaJdbcAvanzadaConDataSource {

        private NivelAislamientoEnum nivelAislamiento;

        private Connection conn;

        public PlantillaJdbcTransaccional(DataSource dataSource, NivelAislamientoEnum nivelAislamiento) {
            super(dataSource);
            this.nivelAislamiento = nivelAislamiento;
        }

        @Override
        protected void configureConnection(Connection conn) throws SQLException {
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(getTranslatedTransacctionIsolationLevel());
        }

        @Override
        protected void doSiempreDespuesEjecutaSentenciaSQL() throws SQLException {
        }

        @Override
        public Connection getConnection() throws SQLException {
            if (this.conn == null) {
                this.conn = super.getConnection();
            }
            return this.conn;
        }

        private int getTranslatedTransacctionIsolationLevel() {
            int result = Connection.TRANSACTION_READ_COMMITTED;
            switch (nivelAislamiento) {
            case READ_UNCOMMITED:
                result = Connection.TRANSACTION_READ_UNCOMMITTED;
                break;
            case REPETIBLE_READ:
                result = Connection.TRANSACTION_REPEATABLE_READ;
                break;
            case SERIALIZABLE:
                result = Connection.TRANSACTION_SERIALIZABLE;
                break;
            case READ_COMMITED:
            default:
                result = Connection.TRANSACTION_READ_COMMITTED;
                break;
            }
            return result;
        }
    }
}
