package net.atos.tsb;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by a451598 on 28/06/2016.
 */
public class HPQCElement extends Object {
    String tipo;
    String nombre;
    int dossier;

    static final int dossier_id = 3;
    static final int cycle_name = 14;
    static final int bussiness_function = 19;
    static final int test_name = 23;
    static final int test_status = 32;

    public HPQCElement(String line) throws HPQCElementException {
        String cycle = "";
        String status = "";

        String[] s = line.split(";");

        try {
            cycle = s[cycle_name].substring(0, Math.min(s[cycle_name].length(), 100));
        }catch (Exception ex){
            cycle = "";
        }
        if(!cycle.equalsIgnoreCase("Cycle 02")){
            throw new HPQCElementException("Cycle <> 02");
        }

        try {
            status = s[test_status].substring(0, Math.min(s[test_status].length(), 100));
        }catch(Exception ex){
            status = "";
        }
        if(status.equalsIgnoreCase("N/A")){
            throw new HPQCElementException("N/A");
        }
        this.dossier = Integer.parseInt(s[dossier_id].substring(0,Math.min(s[dossier_id].length(),4)));
        this.tipo = s[bussiness_function].substring(0,Math.min(s[bussiness_function].length(),100));
        this.nombre = s[test_name].substring(0,Math.min(s[test_name].length(),100));

    }

    public String toString(){
        return String.format("%1d %2s %3s",dossier,nombre,tipo);
    }
}
