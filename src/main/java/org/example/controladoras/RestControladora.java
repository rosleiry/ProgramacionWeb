package org.example.controladoras;

import org.example.encapsulaciones.Url;
import org.example.encapsulaciones.Usuario;
import org.example.exceptions.noExistingUser;
import org.example.servicios.UrlService;
import org.example.servicios.UsuarioService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RestControladora {

    private static RestControladora instancia;
    private List<UrlService> listaEstudiante = new ArrayList<>();
    private List<Usuario> listaUsuarios = new ArrayList<>();

    /**
     * Constructor privado.
     */

    public static RestControladora getInstancia(){
        if(instancia==null){
            instancia = new RestControladora();
        }
        return instancia;
    }


    public Usuario buscarUsuario(String usuario){
        List<Usuario> act =  UsuarioService.getInstancia().findAllByUsuario(usuario);
        if(act.size()==0){
            return null; //generar una excepcion...
        }

        for (Usuario user: act
        ) {
            System.out.println(user.getUsuario());
        }
        return act.get(0);
    }

    public Set<Url> getListaUsuarios(String usuario){

        Usuario act= buscarUsuario(usuario);
        if(act== null)
        {
            throw new noExistingUser("No Existe el usuario: "+usuario);
        }
        return act.getMisEnlaces();
    }

    public Url registrarEnlace(String url,String usuario) throws IOException {
        return generarEnlace(UrlService.getInstancia().registrarEnlace(url,usuario));
    }

    public Url[] crearArreglo(String user){
        Url[] enlaces = UrlService.getInstancia().getEnlaces(user);
        for(int i = 0; i < enlaces.length; i++){
            Url aux = generarEnlace(enlaces[i]);
            enlaces[i] = aux;
        }
        return enlaces;
    }

    private Url generarEnlace(Url enlace) {
        Url aux = new Url();
        aux.setIdUrl(enlace.getIdUrl());
        aux.setURL(enlace.getURL());
        aux.setFecha(enlace.getFecha());
        aux.setEnlaceAcortado(enlace.getEnlaceAcortado());
        aux.setCantVecesAccedidas(enlace.getCantVecesAccedidas());
        aux.setImagen64(enlace.getImagen64());
        aux.setClientes(aux.getClientes());
        return aux;
    }

}
