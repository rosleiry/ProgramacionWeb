package org.example.servicios_controladoras;

import org.example.encapsulaciones.Producto;
import org.example.encapsulaciones.Usuario;
import org.example.encapsulaciones.VentasProductos;

import java.util.ArrayList;
import java.util.List;

public class CarritoComprasService {
    private static CarritoComprasService instancia;
    private List<Usuario> usuarios;
    private List<Producto> productos;
    private List<VentasProductos> ventas;
    private int cont;
    private long carrito;

    public CarritoComprasService() {
        usuarios = new ArrayList<>();;
        productos = new ArrayList<>();;
        ventas = new ArrayList<>();;
        cont = 0;
        carrito = 0;
        usuarios.add(new Usuario("admin","admin","admin"));
    }

    public static CarritoComprasService getInstance(){
        if(instancia == null){
            instancia = new CarritoComprasService();
        }
        return instancia;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public List<VentasProductos> getVentas() {
        return ventas;
    }

    public Usuario autentificarUsuario(String usuario, String nombre, String password){
        return new Usuario(usuario,nombre,password);
    }

    public Producto registrarProducto(Producto producto){
        producto.setId(cont++);
        productos.add(producto);
        return producto;
    }

    public long getCarrito(){
        return carrito++;
    }
    public Producto actualizarProducto(Producto producto){
        Producto tmp = obtenerProductosPorID(producto.getId());
        if(tmp == null){
            throw new RuntimeException("No exite el producto: "+producto.getId());
        }
        tmp.actualizar(producto);
        return tmp;
    }

    public boolean eliminarProducto(int id){
        Producto temp = obtenerProductosPorID(id);
        return productos.remove(temp);
    }
    public Producto obtenerProductosPorID(int id) {
        return productos.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
    }

    public void addVentas(VentasProductos venta) {
        ventas.add(venta);
    }
}
