package org.example.servicios;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import org.example.encapsulaciones.Producto;

import java.util.List;

public class ProductoServices extends DatabaseService<Producto>{
    private static ProductoServices instance;

    private ProductoServices(){
        super(Producto.class);
    }

    public static ProductoServices getInstance(){
        if(instance == null){
            instance = new ProductoServices();
        }
        return instance;
    }

    public void deleteProducto(Object id){
        Producto entity = find(id);
        entity.setEstado(false);
        entity = edit(entity);
    }


    public List<Producto> findProd(int ini, int fin) throws PersistenceException {
        EntityManager em = getEntityManager();
        Query query = em.createNativeQuery("select * from PRODUCTO WHERE ESTADO = true ", Producto.class);
        query.setFirstResult(ini);
        if(fin != 0) {
            query.setMaxResults(fin);
        }
        List<Producto> lista = query.getResultList();
        return lista;    }

    public int pag() {
        int pageSize = 10;
        EntityManager em = getEntityManager();
        Query query = em.createNativeQuery("select * from PRODUCTO WHERE ESTADO = true ", Producto.class);
        int countResults = query.getResultList().size();
        int lastPageNumber = (int) (Math.ceil(countResults / pageSize));
        System.out.println(countResults);
        return  lastPageNumber;
    }
}
