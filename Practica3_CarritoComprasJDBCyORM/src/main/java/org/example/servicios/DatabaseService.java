package org.example.servicios;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaQuery;

import java.util.List;

public class DatabaseService<T> {
    private static EntityManagerFactory emf;
    private Class<T> clase;

    public DatabaseService(Class<T> clase){
        if(emf == null){
            emf = Persistence.createEntityManagerFactory("MiUnidadPersistencia");
            this.clase = clase;
        }
    }

    public EntityManager getEntityManager(){
        return emf.createEntityManager();
    }

    public T create(T entity) throws IllegalArgumentException, EntityExistsException, PersistenceException {
        EntityManager em = getEntityManager();
        try{
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        }finally {
            em.close();
        }
        return entity;
    }

    public T edit(T entity) throws PersistenceException{
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        try{
            em.merge(entity);
            em.getTransaction().commit();
        }finally {
            em.close();
        }
        return entity;
    }

    public boolean delete(Object entityID) throws PersistenceException{
        boolean ok = false;
        EntityManager em = getEntityManager();
        em.getTransaction().begin();

        try{
            T entity = em.find(clase,entityID);
            em.remove(entity);
            em.getTransaction().commit();

            ok = true;
        }finally {
            em.close();
        }
        return ok;
    }
    public T find(Object id) throws PersistenceException{
        EntityManager em = getEntityManager();

        try {
            return em.find(clase,id);
        }finally {
            em.close();
        }

    }

    public List<T> findAll() throws PersistenceException {
        EntityManager em = getEntityManager();
        try{
            CriteriaQuery<T> criteriaQuery = em.getCriteriaBuilder().createQuery(clase);
            criteriaQuery.select(criteriaQuery.from(clase));
            return em.createQuery(criteriaQuery).getResultList();
        } finally {
            em.close();
        }
    }
}
