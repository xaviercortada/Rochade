package net.atos.tsb;

import javax.print.attribute.standard.DateTimeAtCompleted;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by a451598 on 28/06/2016.
 */
public class TSBElement extends Object {

    static final int fecha_creacion = 2;
    static final int fecha_modificacion = 3;


    String tipo;
    String nombre;
    String subsistem;
    boolean documentacion;
    Date fechaCreacion;
    Date fechaModi;
    String criticidad;

    public TSBElement(String line){

        String[] s = line.split("#");
        this.nombre = s[0];

        String doc = s[1].substring(0,2);
        this.documentacion = doc.equals("Si");

        if(s.length >= 3) {
            this.fechaCreacion = parseDate(s[fecha_creacion]);

            Calendar cal = Calendar.getInstance();
            cal.setTime(this.fechaCreacion);
            int nyear = cal.get(Calendar.YEAR);
            if (nyear >= 2020) {
                cal.set(Calendar.YEAR, 2000);
                this.fechaCreacion = cal.getTime();
            }
        }else{
            Calendar cal = Calendar.getInstance();
            this.fechaCreacion = cal.getTime();
        }

        if(s.length >= 4) {
            this.fechaModi = parseDate(s[fecha_modificacion]);
        }else{
            Calendar cal = Calendar.getInstance();
            this.fechaModi = cal.getTime();
        }

        if(s.length >= 5) {
            this.criticidad = s[4];
        }else{
            this.criticidad = null;
        }
    }

    public TSBElement(String tipo,String subsistem, String line) {
        this(line);

        this.tipo = tipo;
        this.subsistem = subsistem;

    }

    public TSBElement(String tipo, String line){
        this(line);

        this.tipo = tipo;

        String[] s = line.split("#");
        String text = s[0];

        try {
            this.subsistem = extractSubsistema(text);
        } catch (Exception e) {
            this.subsistem = "ER";
            e.printStackTrace();
        }

    }

    private static String extractSubsistema(String s) throws Exception {
        String sbs = "";
        if(s.charAt(0) == 'J'){
            int i = s.indexOf('.');
            if(i == -1) throw new Exception("Error parseando susbsistema");

            sbs = s.substring(i+2,i+2+2);

        }else{
            int i = s.indexOf('.');
            if(i == -1) throw new Exception("Error parseando susbsistema");

            sbs = s.substring(i+1,i+1+2);

        }

        return sbs;

    }

    private java.util.Date parseDate(String item){
        java.util.Date dFecha = null;
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String sFecha = item.substring(0, Math.min(item.length(), 10));
        sFecha = sFecha.replaceAll("/","-");
        //System.out.println(sFecha);
        try {
            dFecha = df.parse(sFecha);
        } catch (ParseException e) {
            df = new SimpleDateFormat("dd-MM-yy");
            sFecha = item.substring(0, Math.min(item.length(), 8));
            sFecha = sFecha.replaceAll("/","-");
            //System.out.println(sFecha);
            try{
                dFecha = df.parse(sFecha);
            }catch(ParseException pe2){
                pe2.printStackTrace();
                dFecha = Calendar.getInstance().getTime();
            }
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(dFecha);
        int nyear = cal.get(Calendar.YEAR);
        if(nyear < 1000){
            cal.set(Calendar.YEAR, nyear + 2000);
            dFecha = cal.getTime();
        }

        return dFecha;

    }

    public String toString(){
        return String.format("%1s %2s %3s %4b  %5$td-%5$tm-%5$tY %6$td-%6$tm-%6$tY",nombre,tipo,subsistem,documentacion,fechaCreacion,fechaModi);
    }
}
