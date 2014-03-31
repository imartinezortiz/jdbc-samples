package es.ucm.fdi.bd.jdbc.tx;

import java.sql.SQLException;

import es.ucm.fdi.bd.jdbc.PlantillaJdbc;
import es.ucm.fdi.bd.jdbc.avanzada.PlantillaJdbcAvanzada;

/**
 * Accion a ejecutar en un contexto transaccional
 * 
 * @author Iván Martínez-Ortiz
 * 
 * @param <T> Tipo del resultado de la ejecución de la acción.
 */
public interface AccionTransaccional<T> {

    /**
     * Todas las llamadas a métodos de {@literal plantillaJdbc} se realizan bajo
     * la misma transacción.
     * 
     * 
     * @param estado Permite consultar y controlar el estado de la transacción.
     *            
     * @param plantillaJdbc {@link PlantillaJdbc} a utilizar transaccionalmente.
     * 
     * @return Devuelve el resultado de la transacción o <code>null</code> si no
     *         hace falta.
     * 
     * @see PlantillaTransaccion#ejecuta(NivelAislamientoEnum, AccionTransaccional)
     * 
     * @throws SQLException Si ocurre algún problema durante la ejecución de la transaccion.
     */
    public T ejecutarEnTransaccion(EstadoTransaccion estado, PlantillaJdbcAvanzada plantillaJdbc) throws SQLException;
}
