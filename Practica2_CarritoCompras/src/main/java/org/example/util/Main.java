package org.example.util;

import io.javalin.Javalin;
import io.javalin.rendering.JavalinRenderer;
import io.javalin.rendering.template.JavalinVelocity;

import org.example.encapsulaciones.CarroCompra;
import org.example.encapsulaciones.Producto;
import org.example.encapsulaciones.VentasProductos;
import org.example.servicios_controladoras.CarritoComprasService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        Javalin app = Javalin.create().start(7000);
        JavalinRenderer.register(new JavalinVelocity(), ".vm");
        CarritoComprasService service = CarritoComprasService.getInstance();

        //para manejar la sesión y el atributo del carrito
        app.before(ctx -> {
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            if (carrito == null) {
                carrito = new CarroCompra(service.getCarrito());
                ctx.sessionAttribute("carrito", carrito);
            }
        });

        /*Productos disponibles ruta raiz*/
        app.get("/", ctx -> {
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            List<Producto> productos = service.getProductos();

            ctx.render("/publico/listadoProductos.vm",
                    Map.of("productos", productos, "cantidad", carrito.getListaProductos().size()));
        });


        /* Carga el carrito cn la lista de productos que se tiene dentro del carro*/
        app.get("/carrito", ctx -> {
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            if (carrito == null) {
                carrito = new CarroCompra(service.getCarrito());
                ctx.sessionAttribute("carrito", carrito);
            }

            List<Producto> listaProductos = carrito.getListaProductos();
            int cantidadProductos = listaProductos.size();

            ctx.render("/publico/carrito.vm", Map.of("productos", listaProductos, "cantidad", cantidadProductos));
        });


        //Peticion que agrega un producto al carrito del usuario
        app.post("/comprar", ctx -> {
            CarroCompra carrito = ctx.sessionAttribute("carrito");

            // Obtener el producto actual a partir del ID enviado en el formulario
            Producto temp = carrito.obtenerProductosPorID(Integer.parseInt(ctx.formParam("id")));

            if (temp == null) {
                // Si el producto no está en el carrito, se agrega con la cantidad especificada en el formulario
                temp = service.obtenerProductosPorID(Integer.parseInt(ctx.formParam("id")));
                temp.setCantidad(Integer.parseInt(ctx.formParam("cantidad")));
                carrito.agregarProducto(temp);
            } else {
                // Si el producto ya está en el carrito, se incrementa la cantidad en la cantidad especificada en el formulario
                int pos = carrito.getPos(Integer.parseInt(ctx.formParam("id")));
                temp.setCantidad(Integer.parseInt(ctx.formParam("cantidad")) + temp.getCantidad());
                carrito.modificarProducto(temp, pos);
            }

            // Actualizar el carrito en la sesión
            ctx.sessionAttribute("carrito", carrito);

            // Redireccionar al listado de productos para continuar comprando
            ctx.redirect("/comprar");
        });


        /* Procesa la compra realizada por el usuario */
        app.post("/procesar", ctx -> {
            // Obtener el carrito de compras actual desde la sesión
            CarroCompra carrito = ctx.sessionAttribute("carrito");

            // Verificar si el carrito está vacío
            if (carrito.getListaProductos().isEmpty()) {
                // Si el carrito está vacío, redireccionar al carrito de compras
                ctx.redirect("/carrito");
                return;
            }

            // Obtener el nombre de la compra a partir del formulario
            String nombre = ctx.formParam("nombre");

            // Crear un objeto de tipo VentasProductos con los productos del carrito y el nombre de la compra
            VentasProductos venta = new VentasProductos(service.getVentas().size() + 1, nombre, carrito.listaProductos);

            // Agregar la venta a la lista de ventas en el servicio
            service.addVentas(venta);

            // Limpiar el carrito de compras del usuario
            carrito.borrarProductos();

            // Actualizar el carrito en la sesión
            ctx.sessionAttribute("carrito", carrito);

            // Redireccionar a la página de compra para seguir comprando
            ctx.redirect("/comprar");
        });

        /* Renderiza la vista de autenticación con la dirección de redirección después de autenticarse */
        app.get("/autenti/<path>", ctx -> {
            String direc = ctx.pathParam("path");
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("direc", direc);
            ctx.render("/publico/autentificacion.vm", modelo);
        });

        /* Redirecciona a la página de inicio de compras */
        app.get("/comprar", ctx -> {
            ctx.redirect("/");
        });



        /*
         * Ruta para cargar la página con todas las ventas realizadas.
         * Si el usuario no ha iniciado sesión como administrador, se redirige a la página de inicio de sesión.
         */
        app.get("/ventas", ctx -> {
            // Verificar si el usuario ha iniciado sesión como administrador
            if( ctx.cookie("usuario") == null || ctx.cookie("password") == null || !ctx.cookie("usuario").equalsIgnoreCase("admin") || !ctx.cookie("password").equalsIgnoreCase("admin")) {
                // Redirigir a la página de inicio de sesión si no es un administrador
                ctx.redirect("/autenti/ventas");
                return;
            }

            // Obtener el carrito de compras del usuario de la sesión
            CarroCompra carrito = ctx.sessionAttribute("carrito");

            // Obtener la lista de ventas realizadas
            List<VentasProductos> ventas = service.getVentas();

            // Crear un modelo de datos para pasar a la plantilla de la página
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("ventas", ventas);
            modelo.put("cantidad", carrito.getListaProductos().size());

            // Renderizar la página de ventas con el modelo de datos
            ctx.render("/publico/ventas.vm", modelo);
        });


        /*
         * Maneja la solicitud POST de autenticación de usuario.
         * Redirige a la ventana especificada en la ruta después de la autenticación.
         */
        app.post("/autenti/{direc}", ctx -> {
            // Obtener los parámetros de usuario y contraseña del formulario de inicio de sesión
            String usuario = ctx.formParam("usuario");
            String pass = ctx.formParam("password");
            String temp = ctx.pathParam("direc");

            // Verificar si los parámetros de usuario y contraseña no son nulos
            if(usuario == null || pass == null){
                // Redirigir a la página de inicio de sesión si los parámetros son nulos
                ctx.redirect("/autenti/"+temp);
            }

            // Establecer cookies para el usuario y contraseña proporcionados
            ctx.cookie("usuario", usuario);
            ctx.cookie("password", pass);

            // Redirigir a la ventana especificada en la ruta después de la autenticación
            ctx.redirect("/"+temp);
        });

        /*Carga la ventana para hacer crud de los productos*/
        //PENDIENTE
        app.get("/productos", ctx -> {
            if( ctx.cookie("usuario") == null || ctx.cookie("password") == null || !ctx.cookie("usuario").equalsIgnoreCase("admin") || !ctx.cookie("password").equalsIgnoreCase("admin")) {
                ctx.redirect("/autenti/productos");
                return;
            }
            List<Producto> productos = service.getProductos();
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("productos",productos);
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            modelo.put("cantidad",carrito.getListaProductos().size());
            ctx.render("/publico/productos.vm",modelo);
        });

        /*Carga la ventana para registrar un nuevo producto en el sistema
         * Envia un string accion para poder especificar lo que se va a realizar al momento de hacer post en el formulario
         * ya que se utiliza la misma vista que para editar un producto*/
        app.get("/registrar", ctx -> {
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("accion","/registrar");
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            modelo.put("cantidad",carrito.getListaProductos().size());
            ctx.render("/publico/productoCE.vm",modelo);
        });

        /*Registra un producto en el sistema a partir de los valores del formulario*/
        app.post("/registrar", ctx -> {
            String nombre = ctx.formParam("nombre");
            int precio = Integer.parseInt(ctx.formParam("precio"));
            BigDecimal precioDecimal = BigDecimal.valueOf(precio); // Convert int to BigDecimal
            Producto temp = new Producto(nombre, precioDecimal);
            service.registrarProducto(temp);
            ctx.redirect("/productos");
        });

        /*Remueve un articulo de los disponibles a partir de su id*/
        app.get("/remover/{id}", ctx -> {
            service.eliminarProducto(Integer.parseInt(ctx.pathParam("id")));
            ctx.redirect("/productos");
        });

        /*Permite editar un producto ya agregado
         * Se envia un string para determinar que se realizará una modificación luego del post*/
        app.get("/editar/{id}", ctx -> {
            Producto temp = service.obtenerProductosPorID(Integer.parseInt(ctx.pathParam("id")));
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("producto",temp);
            modelo.put("accion", "/editar/" + ctx.pathParam("id"));

            CarroCompra carrito = ctx.sessionAttribute("carrito");
            modelo.put("cantidad",carrito.getListaProductos().size());
            ctx.render("/publico/productoCE.vm",modelo);
        });

        /*Post luego del formulario de modificar producto
         * Actualiza los valores a partir de lo enviado en el formulario*/
        app.post("/editar/{id}", ctx -> {
            String nombre = ctx.formParam("nombre");
            int precio = Integer.parseInt(ctx.formParam("precio"));
            BigDecimal precioDecimal = BigDecimal.valueOf(precio); // Convert int to BigDecimal
            Producto temp = new Producto(nombre, precioDecimal);

            temp.setId(Integer.parseInt(ctx.pathParam("id")));
            service.actualizarProducto(temp);

            ctx.redirect("/productos");
        });


        //Maneja la solicitud GET para eliminar un producto del carrito a partir de su ID.

        app.get("/eliminar/{id}", ctx -> {
            // Obtener el ID del producto a eliminar desde los parámetros de la ruta
            int id = Integer.parseInt(ctx.pathParam("id"));

            // Obtener el carrito de compras del usuario de la sesión
            CarroCompra carrito = ctx.sessionAttribute("carrito");

            // Eliminar el producto del carrito usando su ID
            carrito.eliminarProductoPorId(id);

            // Actualizar el carrito en la sesión
            ctx.sessionAttribute("carrito", carrito);

            // Redirigir al carrito de compras después de eliminar el producto
            ctx.redirect("/carrito");
        });

/*
 * Maneja la solicitud GET para limpiar el carrito del usuario.
 */
        app.get("/limpiar", ctx -> {
            // Obtener el carrito de compras del usuario de la sesión
            CarroCompra carrito = ctx.sessionAttribute("carrito");

            // Borrar todos los productos del carrito
            carrito.borrarProductos();

            // Actualizar el carrito en la sesión
            ctx.sessionAttribute("carrito", carrito);

            // Redirigir a la página de compras después de limpiar el carrito
            ctx.redirect("/comprar");
        });



    }
}