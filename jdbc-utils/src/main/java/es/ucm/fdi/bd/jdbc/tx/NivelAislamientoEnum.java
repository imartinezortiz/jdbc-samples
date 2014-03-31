package es.ucm.fdi.bd.jdbc.tx;

/**
 * Niveles de aislamiento para las transacciones.
 * 
 * @author Iván Martínez-Ortiz
 * 
 * @see <href="http://en.wikipedia.org/wiki/Isolation_%28database_systems%29">Niveles de aislamiento</a>
 *
 */
public enum NivelAislamientoEnum {
    READ_UNCOMMITED, READ_COMMITED, REPETIBLE_READ, SERIALIZABLE;
}
