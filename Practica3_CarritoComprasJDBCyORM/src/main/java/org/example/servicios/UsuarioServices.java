package org.example.servicios;

import org.example.encapsulaciones.Usuario;

public class UsuarioServices extends DatabaseService<Usuario>{
    private static UsuarioServices instance;

    private UsuarioServices(){
        super(Usuario.class);
    }

    public static UsuarioServices getInstancia(){
        if(instance==null){
            instance = new UsuarioServices();
        }
        return instance;
    }

    public static String autentificarUsuario(Usuario aux) {
        return "ADM";
    }
}
