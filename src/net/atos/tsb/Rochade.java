package net.atos.tsb;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by a451598 on 23/08/2016.
 */

@Entity
@Table(name = "rochade")
@NamedQueries({
        @NamedQuery(name = "Rochade.findByName",
        query = "SELECT r FROM Rochade r WHERE r.nombre = :nombre")
})
public class Rochade implements Serializable {

    @Id @GenericGenerator(name = "gen", strategy = "increment") @GeneratedValue(generator = "gen")
    private long idrochade;

    private String nombre;
    private int documento;
    private Date fecha_creacion;
    private Date fecha_modificacion;
    private String tipo;
    private String subsistem;
    private char estado;

    public Rochade(){

    }

    public Rochade(TSBElement el) {
        this.nombre = el.nombre;
        this.tipo = el.tipo;
        this.documento = el.documentacion ? 1 : 0;
        this.estado = 'N';
        this.fecha_creacion = el.fechaCreacion;
        this.fecha_modificacion = el.fechaModi;
        this.subsistem = el.subsistem;
    }

    public long getIdrochade() {
        return idrochade;
    }

    public void setIdrochade(long idrochade) {
        this.idrochade = idrochade;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getDocumento() {
        return documento;
    }

    public void setDocumento(int documento) {
        this.documento = documento;
    }

    public Date getFecha_creacion() {
        return fecha_creacion;
    }

    public void setFecha_creacion(Date fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }

    public Date getFecha_modificacion() {
        return fecha_modificacion;
    }

    public void setFecha_modificacion(Date fecha_modificacion) {
        this.fecha_modificacion = fecha_modificacion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getSubsistem() {
        return subsistem;
    }

    public void setSubsistem(String subsistem) {
        this.subsistem = subsistem;
    }

    public char getEstado() {
        return estado;
    }

    public void setEstado(char estado) {
        this.estado = estado;
    }
}
