package es.ucm.fdi.bd.derby;
import javax.sql.DataSource;

import es.ucm.fdi.bd.jdbc.tx.AccionTransaccional;
import es.ucm.fdi.bd.jdbc.tx.NivelAislamientoEnum;
import es.ucm.fdi.bd.jdbc.tx.PlantillaTransaccion;


abstract public class AbstractOperacion implements Runnable {
    
    
    private final DataSource ds;
    
    private final NivelAislamientoEnum nivelAislamiento;
    
    protected AbstractOperacion(final NivelAislamientoEnum nivelAislamiento, DataSource dataSource) {
        this.nivelAislamiento = nivelAislamiento;
        this.ds = dataSource;
    }
    
    @Override
    final public void run() {
        PlantillaTransaccion t = new PlantillaTransaccion(ds);
        try {
            t.ejecuta(nivelAislamiento, getAccion());
        } catch (Throwable e) {
            muestraError(e);
        }
    }
    
    abstract protected AccionTransaccional<Void> getAccion();

    protected void muestraError(Throwable e) {
        System.out.println("ERROR: "+ e.getMessage() + " (" + e.getClass().getName() + ") ["+Thread.currentThread().getName()+"]");
    }
    
    protected void espera(int segundos) {
        if (segundos > 0) {
            try {
                Thread.sleep(segundos * 1000l);
            } catch (InterruptedException e) {
                muestraError(e);
            }
        }
    }
}
