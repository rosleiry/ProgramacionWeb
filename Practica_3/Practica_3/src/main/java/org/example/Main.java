package org.example;

import org.example.Clases.CarroCompra;
import io.javalin.Javalin;

import io.javalin.rendering.JavalinRenderer;
import io.javalin.rendering.template.JavalinFreemarker;
import io.javalin.rendering.template.JavalinThymeleaf;
import io.javalin.rendering.template.JavalinVelocity;
import org.example.Clases.Producto;
import org.example.Clases.Usuario;
import org.example.Clases.VentasProductos;
import org.jasypt.util.text.AES256TextEncryptor;
import servicios.Service;


import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    private static String modoConexion = "";
    public static void main(String[] args throws SQLException){

        if (args.length >= 1) {
            modoConexion = args[0];
            System.out.println("Modo de Operacion: " + modoConexion);
        }

        if (modoConexion.isEmpty()) {
            DatabaseStarter.startDatabase();
        }

        Javalin app = Javalin.create((config) -> {
            config.addStaticFiles((staticFileConfig) -> {
                staticFileConfig.hostedPath = "/";
                staticFileConfig.directory = "/publico";
                staticFileConfig.location = Location.CLASSPATH;
            });
            config.enableCorsForAllOrigins();
        }).start(getHerokuAssignedPort());
        (new ProductoControlador(app)).aplicarRutas();
        (new UsuarioControlador(app)).aplicarRutas();
    }

    public static String getConecction() {
        return modoConexion;
    }

        //Inicializacion del servidor
        Javalin app = Javalin.create().start(7000);
        //Instanciacion del motor de plantillas a utilizar
        JavalinRenderer.register(new JavalinVelocity(), ".vm");
        JavalinRenderer.register(new JavalinThymeleaf(), ".html");
        JavalinRenderer.register(new JavalinFreemarker(), ".ftl");


        //Creacion de la controladora
        Service service = Service.getInstance();

        /*Si el carrito no existe dentro de la sesion entonces se crea y se agrega como un atributo*/
        app.before(ctx -> {
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            if(carrito == null){
                carrito = new CarroCompra(service.getCarrito());
            }
            ctx.sessionAttribute("carrito",carrito);

        });
        /*Ruta raiz
         * Muesta los productos disponibles para agragar al carrito*/
        app.get("/", ctx -> {
            CarroCompra carrito = ctx.sessionAttribute("carrito");

            List<Producto> productos = service.getProductos();
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("productos",productos);
            modelo.put("cantidad",carrito.getProductos().size());
            ctx.render("/publico/listadoProductos.vm", modelo);
        });

        /*Carga el carrito pasando la lista de productos que se tiene dentro del carro*/
        app.get("/carrito", ctx -> {
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            if(carrito == null){
                carrito = new CarroCompra(service.getCarrito());
            }
            ctx.sessionAttribute("carrito",carrito);
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("productos",carrito.getProductos());
            modelo.put("cantidad",carrito.getProductos().size());
            ctx.render("/publico/carrito.vm",modelo);
        });

        /*Peticion que agrega un producto al carrito del usuario
         * Si el producto ya está en el carrito entonces se aumenta la cantidad que se quiere*/
        app.post("/comprar", ctx -> {
            CarroCompra carrito = ctx.sessionAttribute("carrito");

            Producto temp = carrito.getProductosPorID(Integer.parseInt(ctx.formParam("id")));
            if(temp == null){
                temp = service.getProductosPorID(Integer.parseInt(ctx.formParam("id")));
                temp.setCantidad(Integer.parseInt(ctx.formParam("cantidad")));
                carrito.addProducto(temp);
                ctx.sessionAttribute("carrito",carrito);
                ctx.redirect("/comprar");
            }else{
                int pos = carrito.getPos(Integer.parseInt(ctx.formParam("id")));
                temp.setCantidad(Integer.parseInt(ctx.formParam("cantidad")) + temp.getCantidad());
                carrito.cambiarProducto(temp,pos);
            }

            ctx.sessionAttribute("carrito",carrito);
            ctx.redirect("/comprar");
        });

        /*Procesa la compra
         * crea un objeto venta
         * Limpia el carrito del usuario*/
        app.post("/procesar",ctx -> {
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            if(carrito.getProductos().size() < 1){
                ctx.redirect("/carrito");
            }
            String nombre = ctx.formParam("nombre");
            VentasProductos venta = new VentasProductos(service.getVentas().size()+1,nombre,carrito.productos);
            service.addVentas(venta);
            carrito.borrarProductos();
            ctx.sessionAttribute("carrito",carrito);
            ctx.redirect("/comprar");
        });

        /*Hace render al log-in direc determina a que vista será rediccionado luego de autentificarse correctamente*/
        app.get("/autenti/<path>", ctx -> {
            String direc = ctx.pathParam("path");
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("direc",direc);
            ctx.render("/publico/autentificacion.vm",modelo);
        });

        app.get("/comprar", ctx -> {
            ctx.redirect("/");
        });


        /*Carga la pestaña con todas las ventas realizadas
         * Si el usuario no se ha logeado entonces se redirige al log-in*/
        app.get("/ventas", ctx -> {

            if( ctx.cookie("usuario") == null || ctx.cookie("password") == null || !ctx.cookie("usuario").equalsIgnoreCase("admin") || !ctx.cookie("password").equalsIgnoreCase("admin")) {
                ctx.redirect("/autenti/ventas");
                return;
            }
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            List<VentasProductos> ventas = service.getVentas();
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("ventas",ventas);
            modelo.put("cantidad",carrito.getProductos().size());

            ctx.render("/publico/ventas.vm",modelo);
        });


        /*Post de autentificacion
         * redirige a la ventana especificada en el get*/
        app.post("/autenti/{direc}",ctx -> {
            String usuario = ctx.formParam("usuario");
            String pass = ctx.formParam("password");
            String temp = ctx.pathParam("direc");
            String recordar = ctx.formParam("recordar");

            if(usuario == null || pass == null){
                ctx.redirect("/autenti/"+temp);
            }
            Usuario user = new Usuario(
                    usuario,pass);
            service.autentificarUsuario(usuario);
            AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
            textEncryptor.setPassword("myEncryptionPassword");
            pass = textEncryptor.encrypt(pass);
            if(recordar != null){
                ctx.cookie("usuario", usuario,(3600*24*7));//Una semana en segundos
                ctx.cookie("mist", pass,(3600*24*7));
            }
            ctx.cookie("usuario", usuario);
            ctx.cookie("password",pass);

            ctx.redirect("/"+temp);

        });
        /*Carga la ventana para hacer crud de los productos*/
        app.get("/productos", ctx -> {
            if( ctx.cookie("usuario") == null || ctx.cookie("password") == null || !ctx.cookie("usuario").equalsIgnoreCase("admin") || !ctx.cookie("password").equalsIgnoreCase("admin")) {
                ctx.redirect("/autenti/productos");
                return;
            }
            List<Producto> productos = service.getProductos();
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("productos",productos);
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            modelo.put("cantidad",carrito.getProductos().size());
            ctx.render("/publico/productos.vm",modelo);
        });

        /*Carga la ventana para registrar un nuevo producto en el sistema
         * Envia un string accion para poder especificar lo que se va a realizar al momento de hacer post en el formulario
         * ya que se utiliza la misma vista que para editar un producto*/
        app.get("/registrar", ctx -> {
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("accion","/registrar");
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            modelo.put("cantidad",carrito.getProductos().size());
            ctx.render("/publico/productoCE.vm",modelo);
        });

        /*Registra un producto en el sistema a partir de los valores del formulario*/
        app.post("/registrar", ctx -> {
            String nombre = ctx.formParam("nombre");
            int precio = Integer.parseInt(ctx.formParam("precio"));

            Producto temp = new Producto(nombre,precio);
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
            Producto temp = service.getProductosPorID(Integer.parseInt(ctx.pathParam("id")));
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("producto",temp);
            modelo.put("accion", "/editar/" + ctx.pathParam("id"));

            CarroCompra carrito = ctx.sessionAttribute("carrito");
            modelo.put("cantidad",carrito.getProductos().size());
            ctx.render("/publico/productoCE.vm",modelo);
        });

        /*Post luego del formulario de modificar producto
         * Actualiza los valores a partir de lo enviado en el formulario*/
        app.post("/editar/{id}", ctx -> {
            String nombre = ctx.formParam("nombre");
            int precio = Integer.parseInt(ctx.formParam("precio"));

            Producto temp = new Producto(nombre,precio);
            temp.setId(Integer.parseInt(ctx.pathParam("id")));
            service.actualizarProducto(temp);

            ctx.redirect("/productos");
        });


        /*Elimina un producto del carrito a partir de su id*/
        app.get("/eliminar/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            carrito.eliminarProductoPorId(id);

            ctx.sessionAttribute("carrito",carrito);
            ctx.redirect("/carrito");
        });

        /*Limpia el carrito del usuario*/
        app.get("/limpiar", ctx -> {
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            carrito.borrarProductos();

            ctx.redirect("/comprar");
        });

        /*Procesa la compra
         * crea un objeto venta
         * Limpia el carrito del usuario*/
        app.post("/procesar", ctx -> {
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            if(carrito.getProductos().size() < 1){
                ctx.redirect("/carrito");
            }
            String nombre = ctx.formParam("nombre");
            VentasProductos venta = new VentasProductos(service.getVentas().size()+1,nombre,carrito.productos);
            service.addVentas(venta);
            carrito.borrarProductos();
            ctx.sessionAttribute("carrito",carrito);
            ctx.redirect("/comprar");
        });

        app.get("/logout", ctx -> {
            if(ctx.cookie("usuario")!= null && ctx.cookie("mist")!= null){
                ctx.removeCookie("usuario");
                ctx.removeCookie("mist");
            }
            ctx.redirect("/");
        });

    }

}

