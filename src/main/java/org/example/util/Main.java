package org.example.util;

import io.javalin.Javalin;

import io.javalin.rendering.JavalinRenderer;
import io.javalin.rendering.template.JavalinVelocity;
import org.example.controladoras.ApiControladora;
import org.example.controladoras.SoapControladora;
import org.example.encapsulaciones.Usuario;
import org.example.grpc.GrpcServer;
import org.example.servicios.BootstrapServices;
import org.example.servicios.RestService;
import org.example.servicios.UsuarioService;


import java.io.IOException;
import java.util.Random;
import java.util.stream.IntStream;

public class Main {

    private static String modoConexion = "";

    public static void main(String[] args) throws InterruptedException, IOException {

        String mensaje = "Software ORM - JPA";
        System.out.println(mensaje);

        if(args.length >= 1){
            modoConexion = args[0];
            System.out.println("Modo de Operacion: "+modoConexion);
        }

        //Iniciando la base de datos.
        if(modoConexion.isEmpty()) {
            BootstrapServices.getInstancia().init();

            EntrarDatos();
        }

        //Creando la instancia del servidor.
        Javalin app = Javalin.create(config ->{
            config.notifyAll();
        });
        new SoapControladora(app).aplicarRutas();
        new RestService(app).aplicarRutas();
        app.start(getHerokuAssignedPort());
        //creando los endpoint de las rutas.
        new ApiControladora(app).aplicarRutas();
        JavalinRenderer.register(new JavalinVelocity(),".vm");

        GrpcServer server = new GrpcServer();
        server.start();
        server.blockunitlShutdown();
    }

    public static String codeGenerator() {
        int[] arr = {58, 59, 60, 61, 62, 63, 64};
        int leftLimit = 48; // letter 'a'
        int rightLimit = 90; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            if (!IntStream.of(arr).anyMatch(n -> n == randomLimitedInt)) {
                buffer.append((char) randomLimitedInt);
            } else {
                i--;
            }
        }
        String generatedString = buffer.toString();

        return generatedString;
    }
    private static void EntrarDatos() {

        if(UsuarioService.getInstancia().autenticarUsuario("admin","admin") == null)
        {
            //anadiendo los usuarios.
            Usuario usuario1 = new Usuario();
            usuario1.setUsuario("admin");
            usuario1.setNombre("admin");
            usuario1.setRol(Usuario.TipoRole.ROLE_ADMIN);
            usuario1.setContrasea("admin");
            UsuarioService.getInstancia().crear(usuario1);
        }
    }

    public static String getModoConexion() {
        return modoConexion;
    }
    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 7000; //Retorna el puerto por defecto en caso de no estar en Heroku.
    }
}
