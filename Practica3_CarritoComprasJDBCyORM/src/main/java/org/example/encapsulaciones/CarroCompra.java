package org.example.encapsulaciones;

import java.util.ArrayList;

public class CarroCompra {

    private long id;
    public ArrayList<Producto> productos;
    public CarroCompra() {
        this.productos = new ArrayList<Producto>();
    }
    public long getId() {

        return id;
    }

    public ArrayList<Producto> getProductos() {
        return productos;
    }

    //crud

    public void agregarProducto(Producto nuevo) {
        this.productos.add(nuevo);
    }


    public Producto obtenerProductosPorID(int id) {
        return productos.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
    }


    public void modificarProducto(Producto temp, int pos) {
        productos.set(pos, temp);
    }

    public int getPos(Integer id) {
        int cont = 0;
        while(cont < productos.size()){
            if(productos.get(cont).getId() == id){
                return cont;
            }
            cont++;
        }
        return -1;
    }

    public void eliminarProductoPorId(int id) {
        int pos = this.getPos(id);
        productos.remove(pos);
    }

    public void borrarProductos() {
        this.productos = new ArrayList<Producto>();
    }

}
