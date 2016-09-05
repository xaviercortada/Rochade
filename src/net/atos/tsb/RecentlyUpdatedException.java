package net.atos.tsb;

/**
 * Created by a451598 on 09/08/2016.
 */
public class RecentlyUpdatedException extends Exception {
    public RecentlyUpdatedException(){
        super("Subsistema actualizado hoy");
    }
}
