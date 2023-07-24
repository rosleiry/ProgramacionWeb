package org.example.encapsulaciones;

import java.util.ArrayList;

public class CarroCompra {

    private long id;
    public ArrayList<Producto> listaProductos;
    public CarroCompra(long id) {

        this.id = id;
        this.listaProductos = new ArrayList();
    }
    public long getId() {

        return id;
    }

    public ArrayList<Producto> getListaProductos() {
        return this.listaProductos;
    }

    //crud

    public void agregarProducto(Producto nuevo) {
        this.listaProductos.add(nuevo);
    }


    public Producto obtenerProductosPorID(int id) {
        return listaProductos.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
    }


    public void modificarProducto(Producto temp, int pos) {
        this.listaProductos.set(pos, temp);
    }

    public int getPos(Integer id) {
        int cont = 0;
        while(cont < listaProductos.size()){
            if(listaProductos.get(cont).getId() == id){
                return cont;
            }
            cont++;
        }
        return -1;
    }

    public void eliminarProductoPorId(int id) {
        int pos = this.getPos(id);
        this.listaProductos.remove(pos);
    }

    public void borrarProductos() {
        this.listaProductos = new ArrayList();
    }

}
