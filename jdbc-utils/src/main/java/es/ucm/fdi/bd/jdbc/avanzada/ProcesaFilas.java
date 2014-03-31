package es.ucm.fdi.bd.jdbc.avanzada;

import java.sql.ResultSet;
import java.sql.SQLException;

class ProcesaFilas<T> implements ProcesadorResultSet<T> {

    private ProcesadorFilas<T> procesadorFilas;

    public ProcesaFilas(ProcesadorFilas<T> procesadorFilas) {
        this.procesadorFilas = procesadorFilas;
    }

    @Override
    public T procesaResultado(ResultSet rs) throws SQLException {
        T resultado = null;
        while(rs.next()) {
            resultado = procesadorFilas.procesaFila(rs);
        }
        return resultado;
    }

}
