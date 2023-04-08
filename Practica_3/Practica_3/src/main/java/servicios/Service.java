package servicios;

import org.example.Clases.Producto;
import org.example.Clases.Usuario;
import org.example.Clases.VentasProductos;

import java.util.ArrayList;
import java.util.List;

public class Service {
    private static Service instancia;
    private List<Usuario> usuarios;
    private List<Producto> productos;
    private List<VentasProductos> ventas;
    private int cont;
    private long carrito;

    public Service() {
        usuarios = new ArrayList<>();;
        productos = new ArrayList<>();;
        ventas = new ArrayList<>();;
        cont = 0;
        carrito = 0;
        usuarios.add(new Usuario("admin","admin","admin"));
    }

    public static Service getInstance(){
        if(instancia == null){
            instancia = new Service();
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

    public Usuario autentificarUsuario(String usuario){
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
        Producto tmp = getProductosPorID(producto.getId());
        if(tmp == null){
            throw new RuntimeException("No Existe el estudiante: "+producto.getId());
        }
        tmp.actualizar(producto);
        return tmp;
    }

    public boolean eliminarProducto(int id){
        Producto temp = getProductosPorID(id);
        return productos.remove(temp);
    }
    public Producto getProductosPorID(int id) {
        return productos.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
    }

    public void addVentas(VentasProductos venta) {
        ventas.add(venta);
    }
}

