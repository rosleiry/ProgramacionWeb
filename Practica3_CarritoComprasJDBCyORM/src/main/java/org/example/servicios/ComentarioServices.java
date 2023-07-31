package org.example.servicios;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import org.example.encapsulaciones.Comentario;
import org.hibernate.Session;

import java.util.List;

public class ComentarioServices extends DatabaseService<Comentario> {
    private static ComentarioServices instance;

    private ComentarioServices() {
        super(Comentario.class);
    }

    public static ComentarioServices getInstancia() {
        if (instance == null) {
            instance = new ComentarioServices();
        }
        return instance;
    }

    public List<Comentario> findComments(int id) {
        EntityManager em = getEntityManager();
        Query query = em.createQuery("select e from Comentario e where e.estado = true and e.productoId = :id");
        query.setParameter("id", id);
        List<Comentario> lista = query.getResultList();
        return lista;
    }

    public void deleteComent(int id) throws PersistenceException {
        Session session = getEntityManager().unwrap(Session.class);
        session.beginTransaction();
        Query query = session.createQuery("delete from Comentario where id = :id");
        query.setParameter("id", id);
        int rows = query.executeUpdate();
        session.getTransaction().commit();
        session.close();
    }
}
