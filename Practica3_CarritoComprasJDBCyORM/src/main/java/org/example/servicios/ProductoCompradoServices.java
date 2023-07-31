package org.example.servicios;

import org.example.encapsulaciones.Producto;
import org.example.encapsulaciones.ProductoComprado;

import java.util.ArrayList;
import java.util.List;

public class ProductoCompradoServices extends DatabaseService<ProductoComprado>{

    private static ProductoCompradoServices instance;

    private ProductoCompradoServices(){ super(ProductoComprado.class);}

    public static ProductoCompradoServices getInstance(){
        if(instance == null){
            instance = new ProductoCompradoServices();
        }
        return instance;
    }

    public List<ProductoComprado> convertProd(List<Producto> productos, long venta){
        List<ProductoComprado> list = new ArrayList<ProductoComprado>();
        for (Producto prod:productos) {
            ProductoComprado temp = new ProductoComprado(prod.getId(),venta,prod.getCantidad(),prod.getPrecio(),prod.getNombre());
            getInstance().create(temp);
            list.add(temp);
        }
        return list;
    }
}
