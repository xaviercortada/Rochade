package net.atos.tsb;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by a451598 on 22/08/2016.
 */

@Entity
@Table(name = "elements")
public class Element implements Serializable {

    @Id @GenericGenerator(name = "gen", strategy = "increment") @GeneratedValue(generator = "gen")
    private long elements_id;

    private long Dossier;
    private String Tipo;
    private String Objeto;

    public Element() {

    }

    public Element(String nombre, int dossier, String tipo) {
        this.Objeto = nombre;
        this.Dossier = dossier;
        this.Tipo = tipo;
    }


    public void setDossier(long dossier) {
        Dossier = dossier;
    }

    public long getDossier() {
        return Dossier;
    }

    public String getTipo() {
        return Tipo;
    }

    public void setTipo(String tipo) {
        Tipo = tipo;
    }

    public String getObjeto() {
        return Objeto;
    }

    public void setObjeto(String objeto) {
        Objeto = objeto;
    }

    public long getElements_id() {
        return elements_id;
    }

    @Override
    public String toString() {
        return String.format("(%d, %s, %s)", this.getDossier(), this.getTipo(), this.getObjeto());
    }
}
