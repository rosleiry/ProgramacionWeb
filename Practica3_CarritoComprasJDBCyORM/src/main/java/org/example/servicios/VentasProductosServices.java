package org.example.servicios;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.example.encapsulaciones.VentasProductos;

import java.util.List;

public class VentasProductosServices extends DatabaseService<VentasProductos>{
    private static VentasProductosServices instance;

    private VentasProductosServices(){ super(VentasProductos.class);}

    public static VentasProductosServices getInstance(){
        if(instance == null){
            instance = new VentasProductosServices();
        }
        return instance;
    }

    public List<VentasProductos> getVentas(){
        EntityManager em = getEntityManager();
        Query query = em.createNativeQuery("select * from VENTASPRODUCTOS ", VentasProductos.class);
        List<VentasProductos> lista = query.getResultList();
        return lista;
    }
}
