package net.atos.tsb;

import javax.jdo.annotations.*;
import javax.persistence.*;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by a451598 on 23/08/2016.
 */
public class RochadeService {

    private EntityManager em;

    public RochadeService(EntityManager em){
        this.em = em;
    }

    public Rochade findElementById(long id){
        Rochade item = em.find(Rochade.class, id);
        return item;
    }


    public Rochade findElementByNombre(String nombre){
        TypedQuery<Rochade> query = em.createNamedQuery("Rochade.findByName", Rochade.class);
        query.setParameter("nombre", nombre);
        List<Rochade> list = query.getResultList();

        if(list.size() > 0)
            return list.get(0);

        return null;
    }

    public int resetElements(){
        int i = 0;

        em.getTransaction().begin();
        try{

            Query query = em.createQuery("UPDATE Rochade r SET r.estado = 'B'");
            i = query.executeUpdate();
            em.getTransaction().commit();
        }catch(Exception e){
            e.printStackTrace();
            em.getTransaction().rollback();
        }



        return i;
    }

    public void update(Rochade item){
        em.merge(item);
    }

    public void insert(Rochade item){
        try{
            em.persist(item);
        }catch (EntityExistsException ee){
            ee.printStackTrace();

        }catch (PersistenceException pe){
            pe.printStackTrace();
        }
    }
}
