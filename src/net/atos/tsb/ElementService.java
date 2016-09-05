package net.atos.tsb;

import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.List;

/**
 * Created by a451598 on 23/08/2016.
 */
public class ElementService {

    private EntityManager em;

    public ElementService(EntityManager em){
        this.em = em;
    }

    public Element findElementById(long id){
        Element el = em.find(Element.class, id);
        return el;
    }

    public Element findElementByNombre(String nombre){
        TypedQuery<Element> query = em.createQuery("SELECT e FROM Element e WHERE e.Objeto = :nombre", Element.class);
        query.setParameter("nombre", nombre);
        List<Element> list = query.getResultList();

        if(list.size() > 0)
            return list.get(0);

        return null;
    }

    public int resetElements(){
        int i = 0;

        em.getTransaction().begin();
        try{

            Query query = em.createQuery("DELETE * FROM elements");
            i = query.executeUpdate();
            em.getTransaction().commit();
        }catch(Exception e){
            e.printStackTrace();
            em.getTransaction().rollback();
        }



        return i;
    }


    public void update(Element el){
        em.merge(el);
    }

    public void insert(Element el){
        try{
            em.persist(el);
        }catch (EntityExistsException ee){
            ee.printStackTrace();

        }catch (PersistenceException pe){
            pe.printStackTrace();
        }
    }
}
