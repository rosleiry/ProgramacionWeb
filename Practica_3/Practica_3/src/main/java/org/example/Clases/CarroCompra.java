package org.example.Clases;

import java.util.ArrayList;

public class CarroCompra {
    private long id;
    public ArrayList<Producto> productos;
    public CarroCompra(long id) {

        this.id = id;
        this.productos = new ArrayList();
    }
    public long getId() {
        return id;
    }
   // public void setId(int id) {this.id = id;}

    public ArrayList<Producto> getProductos() {
        return this.productos;
    }

    public void addProducto(Producto nuevo) {
        this.productos.add(nuevo);
    }


    public Producto getProductosPorID(int id) {
        return productos.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
    }


    public void cambiarProducto(Producto temp, int pos) {
        this.productos.set(pos, temp);
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
        this.productos.remove(pos);
    }

    public void borrarProductos() {
        this.productos = new ArrayList();
    }

}
