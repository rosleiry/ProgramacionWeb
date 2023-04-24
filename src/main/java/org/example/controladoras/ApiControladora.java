package org.example.controladoras;

import io.javalin.Javalin;
import org.example.encapsulaciones.Cliente;
import org.example.encapsulaciones.Url;
import org.example.encapsulaciones.Usuario;
import org.example.servicios.ClienteService;
import org.example.servicios.UrlService;
import org.example.servicios.UsuarioService;

import java.net.InetAddress;
import java.util.*;

public class ApiControladora {
    private final Javalin app;
    private final ClienteService clienteService = ClienteService.getInstancia();
    private final UrlService urlService = UrlService.getInstancia();
    private final UsuarioService usuarioService = UsuarioService.getInstancia();
    private Boolean checkLogin= false;
    private Boolean checkRegister= false;


    public ApiControladora(Javalin app) {
        this.app = app;
    }

    public void aplicarRutas() {
        app.routes(() -> {

            //Evitar que Enlaces sea null
            app.before("",ctx -> {
                if(ctx.sessionAttribute("Enlaces") == null) {
                    Set<Url> enlaces = new HashSet<>();
                    ctx.sessionAttribute("Enlaces", enlaces);
                }
            });

            //Pagina Inicial
            //Desde aqui se crean los enlaces cortados
            app.get("/", ctx -> {
                Map<String, Object> aux =  new HashMap<>();
                Set<Url> enlaces;

                Usuario usuario = ctx.sessionAttribute("usuario");
                if(usuario != null){
                    enlaces = usuario.getMisEnlaces();
                }else{
                    enlaces = ctx.sessionAttribute("Enlaces");
                }

                aux.put("usuario",usuario);
                aux.put("links",enlaces);
                ctx.render("/publico/index.vm",aux);
            });

            //crear enlace
            app.post("/acortarEnlace", ctx -> {
                String URL = ctx.formParam("link");

                Usuario usuario = ctx.sessionAttribute("usuario");
                Url act = new Url();
                act.setURL(URL);

                act.setEnlaceAcortado(UrlService.getAcortado());
                act.setImagen64(UrlService.getPreview(URL));

                Set<Url> listaActual;
                if(usuario!=null)
                {
                    act.setUsuario(usuario);
                    listaActual = usuario.getMisEnlaces();
                    listaActual.add(act);
                    usuario.setMisEnlaces(listaActual);
                    urlService.crear(act);
                    usuarioService.editar(usuario);
                }else{
                    listaActual= ctx.sessionAttribute("Enlaces");
                    listaActual.add(act);
                    urlService.crear(act);
                    ctx.sessionAttribute("Enlaces", listaActual);
                }
                ctx.redirect("/");
            });

            //Redireccionar
            app.get("/re/:redirect",ctx -> {
                int id = ctx.pathParam("redirect",Integer.class).get();
                Url aux = urlService.find(id);
                String detalles = getOS(ctx.userAgent().toLowerCase());
                String nav = getNav(ctx.header("sec-ch-ua").toLowerCase());
                Cliente client = new Cliente();
                InetAddress ip= InetAddress.getLocalHost(); //codigo para obtener la ip de la PC ACTUAL
                client.setIp(ip.getHostAddress());
                client.setSistemaOperativo(detalles);
                client.setNavegador(nav);

                aux.setCantVecesAccedidas(aux.getCantVecesAccedidas()+1);
                clienteService.crear(client);

                List<Cliente> clientes = aux.getClientes();
                clientes.add(client);
                aux.setClientes(clientes);
                urlService.editar(aux);

                ctx.redirect(aux.getURL());
            });

            app.get("/ver/:id", ctx -> {
                int id = ctx.pathParam("id",Integer.class).get();
                Url enlace = urlService.find(id);

                Map<String,Object> map = new HashMap<>();
                map.put("usuario",ctx.sessionAttribute("usuario"));
                map.put("enlace",enlace);
                map.put("map",enlace.calcularCantidad());

                ctx.render("/publico/verEnlace.vm",map);
            });

            //carga vista login
            app.get("/login", ctx -> {
                Map<String, Object> modelo = new HashMap<>();
                if(checkLogin)
                {
                    checkLogin=false;
                    modelo.put("check", true);
                }else{
                    modelo.put("check", checkLogin);
                }
                ctx.render("/publico/autentificacion.vm",modelo);
            });

            app.get("/registrarse", ctx -> {
                Map<String, Object> modelo = new HashMap<>();

                if(checkRegister)
                {
                    checkRegister=false;
                    modelo.put("check", true);
                }else{
                    modelo.put("check", checkRegister);
                }
                ctx.render("/publico/registro.vm",modelo);
            });

            //guardar crear usuario
            app.post("/crear/user", ctx -> {
                //obteniendo la información enviada.
                String usuario = ctx.formParam("usuario");
                String nombre = ctx.formParam("nombre");
                String contrasea = ctx.formParam("contrasea");
                Usuario.TipoRole rol = Usuario.TipoRole.ROLE_USUARIO;

                Usuario tmp = new Usuario();
                tmp.setUsuario(usuario.toLowerCase());
                tmp.setNombre(nombre.toLowerCase());
                tmp.setContrasea(contrasea.toLowerCase());
                tmp.setRol(rol);

                if(usuarioService.findAllByUsuario(usuario.toLowerCase()).size() == 0)
                {
                    //el usuario no existe
                    usuarioService.crear(tmp);
                    ctx.sessionAttribute("usuario",tmp);
                    ctx.redirect("/ListarEnlaces");
                    checkRegister= false;
                }else{
                    //el usuario ya existe
                    checkRegister = true;
                    ctx.redirect("/registrarse");
                }
            });

            //guardar editar usuario ROL ADMIN
            app.post("/ascender/:idUsuario", ctx -> {
                //obtengo el usuario
                Usuario tmp = usuarioService.find(ctx.pathParam("idUsuario", Integer.class).get());
                tmp.setRol(Usuario.TipoRole.ROLE_ADMIN);
                usuarioService.editar(tmp);
                ctx.redirect("/ListarUsuarios");
            });


            //guardar editar usuario ROL NORMAL
            app.post("/descender/:idUsuario", ctx -> {
                //obtengo el usuario
                Usuario tmp = usuarioService.find(ctx.pathParam("idUsuario", Integer.class).get());
                tmp.setRol(Usuario.TipoRole.ROLE_USUARIO);
                usuarioService.editar(tmp);
                ctx.redirect("/ListarUsuarios");
            });

            //eliminar usuario
            app.post("/eliminar/:idUsuario", ctx -> {
                //obtengo el usuario
                int id =ctx.pathParam("idUsuario", Integer.class).get();
                usuarioService.eliminar(id);
                ctx.redirect("/ListarUsuarios");
            });


            //INICIO DE SECCION
            app.post("/autenticar", ctx -> {
                //Obteniendo la informacion de la peticion. Pendiente validar los parametros.
                String user = ctx.formParam("usuario");
                String password = ctx.formParam("password");

                //Autenticando el usuario para nuestro ejemplo siempre da una respuesta correcta.
                Usuario usuario = usuarioService.autenticarUsuario(user.toLowerCase(), password.toLowerCase());

                if( usuario != null ){
                    //agregando el usuario en la session...
                    checkLogin=false;
                    ctx.sessionAttribute("usuario", usuario);
                    ctx.redirect("/");
                }else{
                    checkLogin=true;
                    ctx.redirect("/login");
                }

            });

            //cerrar seccion
            app.get("/logout", ctx -> {
                ctx.sessionAttribute("usuario", null);
                ctx.redirect("/");
            });

            //listar usuario
            app.get("/ListarUsuarios", ctx -> {

                //obtenemos los valores del session
                Usuario usuarioTmp = ctx.sessionAttribute("usuario");
                List<Usuario> lista = usuarioService.findAll();

                Map<String, Object> modelo = new HashMap<>();
                modelo.put("usuario", usuarioTmp);
                modelo.put("usuarios",lista);
                //enviando al sistema de plantilla.
                ctx.render("/publico/usuarios.vm",modelo);
            });


            //listar enlaces
            app.get("/ListarEnlaces", ctx -> {

                //obtenemos los valores del session
                Usuario usuario = ctx.sessionAttribute("usuario");

                //paso los parametro
                List<Url> lista = urlService.findAll();
                //enviando al sistema de plantilla.
                Map<String, Object> modelo = new HashMap<>();
                modelo.put("links", lista);
                modelo.put("usuario",usuario);
                ctx.render("/publico/enlaces.vm",modelo);
            });

            //eliminar enlace
            app.post("/eliminar/enlace/:id", ctx -> {
                //obtenemos los valores del session
                int id =ctx.pathParam("id", Integer.class).get();
                Boolean estado = urlService.eliminar(id);

                if(estado)
                {
                    Set<Url> listaEnlaces = ctx.sessionAttribute("Enlaces");
                    Set<Url> newEnlace = urlService.eliminarEnlaceByID(id, listaEnlaces);

                    if(newEnlace != null)
                    {
                        ctx.sessionAttribute("Enlaces",newEnlace);
                    }
                }

                ctx.redirect("/ListarEnlaces");
            });

        });
        app.exception(Exception.class, (exception, ctx) -> {
            ctx.status(500);
            ctx.html("<h1>Error no recuperado:"+exception.getMessage()+"</h1>");
            exception.printStackTrace();
        });
    }

    private String getOS(String user){
        String detalles = "";
        if(user.indexOf("windows") >= 0){
            detalles = "Windows";
        }else if(user.indexOf("mac") >= 0){
            detalles = "MacOs";
        }else if(user.indexOf("x11") >= 0){
            detalles = "Unix";
        }else if(user.indexOf("android") >= 0){
            detalles = "Android";
        }else if(user.indexOf("iphone") >= 0){
            detalles = "IOS";
        }
        return detalles;

    }
    private String getNav(String user){
        String detalles = "";

        if(user.contains("edge")  ){
            detalles = "Edge";
        }else if(user.contains("safari")){
            detalles = "Safari";
        }else if(user.contains("opera") ){
            detalles = "Opera";
        }else if(user.contains("chrome")){
            detalles = "Chrome";
        }else if(user.contains("firefox")){
            detalles = "Firefox";
        }
        return detalles;
    }
}
