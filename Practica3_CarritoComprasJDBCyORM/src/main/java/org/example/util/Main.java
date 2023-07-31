package org.example.util;

import io.javalin.Javalin;
import io.javalin.http.UploadedFile;
import io.javalin.rendering.JavalinRenderer;
import io.javalin.rendering.template.JavalinVelocity;
import java.lang.Integer;


import org.example.encapsulaciones.*;
import org.example.servicios.*;
import org.jasypt.util.text.AES256TextEncryptor;

import java.io.IOException;
import java.util.Base64;
import java.util.*;

public class Main {

    //Iniciando la base de datos.
    private static String modoConexion = "";

    public static void main(String[] args) {

        //Inicializacion del servidor
        BootstrapServices.startDB();

        Javalin app = Javalin.create().start(5000);

        //Instanciacion del motor de plantillas a utilizar
        JavalinRenderer.register(new JavalinVelocity(),".vm");


        crearUsuarios();

        //Si el carrito no existe dentro de la sesion entonces se crea y se agrega como un atributo
        app.before(ctx -> {
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            if(carrito == null){
                carrito = new CarroCompra();
            }
            ctx.sessionAttribute("carrito",carrito);
        });

        /*Registra un producto en el sistema a partir de los valores del formulario*/
        app.post("/registrar", ctx -> {
            String nombre = ctx.formParam("nombre");
            String precioParam = ctx.formParam("precio");
            int precio = 0; // Default value for precio

            if (precioParam != null) {
                try {
                    precio = Integer.parseInt(precioParam);
                } catch (NumberFormatException e) {
                    // Handle the case when "precio" cannot be parsed as an integer
                    // You can provide a default value or return an error response
                }
            } else {
                // Handle the case when "precio" parameter is not present in the form
                // You can provide a default value or return an error response
            }

            String desc = ctx.formParam("desc");
            List<Imagen> imagenes = new ArrayList<>();

            UploadedFile uploadedFile = ctx.uploadedFile("img"); // Use "img" as the key
            if (uploadedFile != null) {
                try {
                    byte[] bytes = uploadedFile.content().readAllBytes();
                    String encodedString = Base64.getEncoder().encodeToString(bytes);
                    Imagen imagen = new Imagen(uploadedFile.filename(), uploadedFile.contentType(), encodedString);
                    ImagenServices.getInstancia().create(imagen);
                    imagenes.add(imagen);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // Handle the case when no file is uploaded
            }

            Producto temp = new Producto(nombre, precio, desc);
            temp.setImagenes(imagenes);
            ProductoServices.getInstance().create(temp);
            ctx.redirect("/productos");
        });


        app.get("/ver/{id}",ctx -> {
            String idParam = ctx.pathParam("id");
            int id = Integer.parseInt(idParam);
            Producto temp = ProductoServices.getInstance().find(id);
            List<Comentario> comments = ComentarioServices.getInstancia().findComments(id);
            Map<String, Object> modelo = new HashMap<>();
            String user = ctx.cookie("usuario");
            modelo.put("temp",temp);
            modelo.put("comments",comments);
            modelo.put("user",user);
            ctx.render("/publico/ver.vm",modelo);
        });

        app.get("/logout", ctx -> {
            if(ctx.cookie("usuario")!= null && ctx.cookie("mist")!= null){
                ctx.removeCookie("usuario");
                ctx.removeCookie("mist");
            }
            ctx.redirect("/");
        });

        app.post("/addComment/{id}", ctx->{
            String comment = ctx.formParam("coment");
            String idParam = ctx.pathParam("id");
            int id = Integer.parseInt(idParam);
            Comentario temp = new Comentario(comment,id);
            ComentarioServices.getInstancia().create(temp);
            ctx.redirect("/ver/"+id);
        });

        app.get("/delComent/{id}/{coment}", ctx ->{
            String idParam = ctx.pathParam("id");
            int id = Integer.parseInt(idParam);
            String commentParam = ctx.pathParam("coment");
            int comment = Integer.parseInt(commentParam);
            System.out.println("El id del comentario es: "+comment);
            ComentarioServices.getInstancia().deleteComent(comment);
            ctx.redirect("/ver/"+id);
        });

        /*Ruta raiz
         * Muesta los productos disponibles para agragar al carrito*/
        app.get("/", ctx -> {
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            List<Producto> productos = ProductoServices.getInstance().findProd(0, 10);

            Map<String, Object> modelo = new HashMap<>();
            modelo.put("productos",productos);
            modelo.put("cantidad",carrito.getProductos().size());
            List<String> paginas = getPaginas();
            modelo.put("paginas",paginas);
            ctx.render("/publico/listadoProductos.vm", modelo);
        });

        app.get("/comprar/{id}", ctx -> {
            // Obtener el ID del producto a comprar desde los parámetros de la ruta.
            int id = ctx.pathParamAsClass("id", Integer.class).get();
            // Realizar el cálculo con el ID.
            int pos = id * 10;
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            List<Producto> productos = ProductoServices.getInstance().findProd(pos, pos+10);
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("productos",productos);
            modelo.put("cantidad",carrito.getProductos().size());
            List<String> paginas = getPaginas();
            modelo.put("paginas",paginas);
            ctx.render("/publico/listadoProductos.vm", modelo);
        });


        /*Peticion que agrega un producto al carrito del usuario
         * Si el producto ya está en el carrito entonces se aumenta la cantidad que se quiere*/
        app.post("/comprar", ctx -> {
            CarroCompra carrito = ctx.sessionAttribute("carrito");

            Producto temp = carrito.obtenerProductosPorID(ctx.formParamAsClass("id", Integer.class).get());

            if (temp == null) {
                temp = ProductoServices.getInstance().find(Integer.parseInt(ctx.formParam("id")));
                temp.setCantidad(Integer.parseInt(ctx.formParam("cantidad")));
                carrito.agregarProducto(temp);
                ctx.sessionAttribute("carrito", carrito);
                ctx.redirect("/comprar");
            } else {
                int pos = carrito.getPos(Integer.parseInt(ctx.formParam("id")));
                temp.setCantidad(Integer.parseInt(ctx.formParam("cantidad")) + temp.getCantidad());
                carrito.modificarProducto(temp, pos);
            }


            ctx.sessionAttribute("carrito",carrito);
            ctx.redirect("/comprar");
        });

        app.get("/comprar", ctx -> {
            ctx.redirect("/");
        });

        /*Carga la pestaña con todas las ventas realizadas
         * Si el usuario no se ha logeado entonces se redirige al log-in*/
        app.get("/ventas", ctx -> {
            if( ctx.cookie("usuario") == null || ctx.cookie("mist")== null){
                ctx.redirect("/autenti/ventas");
                return;
            } else{
                AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
                textEncryptor.setPassword("myEncryptionPassword");
                String mist = textEncryptor.decrypt(ctx.cookie("mist"));
                Usuario aux = new Usuario(ctx.cookie("usuario"),mist);
                if(!UsuarioServices.autentificarUsuario(aux).equalsIgnoreCase("ADM")){
                    ctx.redirect("/autenti/ventas");
                    return;
                }
            }
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            List<VentasProductos> ventas = VentasProductosServices.getInstance().getVentas();
            for (VentasProductos venta: ventas) {
                System.out.println(venta.getId());
            }
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("ventas",ventas);
            modelo.put("cantidad",carrito.getProductos().size());

            ctx.render("/publico/ventas.vm",modelo);
        });



        /*Carga la ventana para hacer crud de los productos*/
        app.get("/productos", ctx -> {
            if( ctx.cookie("usuario") == null || ctx.cookie("mist")== null){
                ctx.redirect("/autenti/productos");
                return;
            } else{
                AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
                textEncryptor.setPassword("myEncryptionPassword");
                String mist = textEncryptor.decrypt(ctx.cookie("mist"));
                Usuario aux = new Usuario(ctx.cookie("usuario"),mist);
                if(!UsuarioServices.autentificarUsuario(aux).equalsIgnoreCase("ADM")){
                    ctx.redirect("/autenti/ventas");
                    return;
                }
            }
            List<Producto> productos = ProductoServices.getInstance().findProd(0,0);
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



        /*Remueve un articulo de los disponibles a partir de su id*/
        app.get("/remover/{id}", ctx -> {
            ProductoServices.getInstance().deleteProducto(Integer.parseInt(ctx.pathParam("id")));
            ctx.redirect("/productos");
        });

        /*Permite editar un producto ya agregado
         * Se envia un string para determinar que se realizará una modificación luego del post*/
        app.get("/editar/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Producto temp = ProductoServices.getInstance().find(id);
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("producto", temp);
            modelo.put("accion", "/editar/" + id);

            CarroCompra carrito = ctx.sessionAttribute("carrito");
            modelo.put("cantidad", carrito.getProductos().size());

            ctx.render("/publico/productoCE.vm", modelo);
        });


        /*Post luego del formulario de modificar producto
         * Actualiza los valores a partir de lo enviado en el formulario*/
        app.post("/editar/{id}", ctx -> {
            String nombre = ctx.formParam("nombre");
            String precioStr = ctx.formParam("precio");
            int precio = Integer.parseInt(precioStr);
            String desc = ctx.formParam("desc");
            // String precioStr = ctx.formParam("precio");
            // BigDecimal precio = new BigDecimal(precioStr);

            Producto temp = new Producto(nombre, precio, desc);
            temp.setId(Integer.parseInt(ctx.pathParam("id")));
            ProductoServices.getInstance().edit(temp);

            ctx.redirect("/productos");
        });


        /*Hace render al log-in
         * direc determina a que vista será rediccionado luego de autentificarse correctamente*/
        app.get("/autenti/{direc}", ctx -> {
            String direc = ctx.pathParam("direc");
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("direc",direc);
            ctx.render("/publico/autentificacion.vm",modelo);
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
            Usuario user = new Usuario(usuario,pass);
            //service.autentificarUsuario(user);
            AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
            textEncryptor.setPassword("myEncryptionPassword");
            pass = textEncryptor.encrypt(pass);
            if(recordar != null){
                ctx.cookie("usuario", usuario,(3600*24*7));//Una semana en segundos
                ctx.cookie("mist", pass,(3600*24*7));
            }
            //Encriptar cookie
            ctx.cookie("usuario", usuario);
            ctx.cookie("mist", pass);

            ctx.redirect("/"+temp);

        });

        /*Carga el carrito pasando la lista de productos que se tiene dentro del carro*/
        app.get("/carrito", ctx -> {
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            if(carrito == null){
                carrito = new CarroCompra();
            }
            ctx.sessionAttribute("carrito",carrito);
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("productos",carrito.getProductos());
            modelo.put("cantidad",carrito.getProductos().size());
            ctx.render("/publico/carrito.vm",modelo);
        });
        /*Elimina un producto del carrito a partir de su id*/
        app.get("/eliminar/{id}", ctx -> {
            String idParam = ctx.pathParam("id");
            int id = Integer.parseInt(idParam);
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            carrito.eliminarProductoPorId(id);

            ctx.sessionAttribute("carrito",carrito);
            ctx.redirect("/carrito");
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
            VentasProductos venta = new VentasProductos(nombre);
            List<ProductoComprado> list = ProductoCompradoServices.getInstance().convertProd(carrito.productos,venta.getId());
            venta.setListaProductos(list);
            VentasProductosServices.getInstance().create(venta);
            carrito.borrarProductos();
            ctx.sessionAttribute("carrito",carrito);
            ctx.redirect("/comprar");
        });

        /*Limpia el carrito del usuario*/
        app.get("/limpiar", ctx -> {
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            carrito.borrarProductos();

            ctx.redirect("/comprar");
        });

    }
    private static void crearUsuarios(){
        String nombre;
        int precio;
        String desc;
        List<Imagen> fotos = new ArrayList<Imagen>();
        for(int i = 0 ; i < 19; i++){
            nombre = "producto "+ i;
            precio = 10 * i;
            desc = "Este es el producto "+i;
            Producto temp = new Producto(nombre,precio,desc);
            temp.setImagenes(fotos);
            ProductoServices.getInstance().create(temp);
        }

    }

    private static List<String> getPaginas() {
        int pag = ProductoServices.getInstance().pag();
        List<String> list = new ArrayList<String>();
        for(int i = 0; i <= pag; i++){
            String aux = "<a class=\"page-link\" href=\"/comprar/"+i+"\">"+(i+1)+"</a>";
            list.add(aux);
        }
        return list;
    }

    public static String getConnection(){
        return modoConexion;
    }
}