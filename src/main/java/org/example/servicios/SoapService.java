package org.example.servicios;


import jakarta.jws.WebService;
import org.example.encapsulaciones.Url;
import org.example.encapsulaciones.Cliente;

import jakarta.jws.WebMethod;
import java.io.IOException;


@WebService
public class SoapService {

    private UrlService urlService = UrlService.getInstancia();
    private UsuarioService usuarioService = UsuarioService.getInstancia();

    @WebMethod
    public boolean autentificacion(String user,String password){
        return (usuarioService.autenticarUsuario(user,password) != null)?true:false;
    }

    @WebMethod
    public Url[] getEnlaces(String user){
        return crearArreglo(user);
    }

    @WebMethod
    public Url getEnlace(int enlace,String user){

        return generarEnlace(urlService.find(enlace));
    }

    @WebMethod
    public Url registrarEnlace(String url,String usuario) throws IOException {
        return generarEnlace(UrlService.getInstancia().registrarEnlace(url,usuario));
    }

    @WebMethod
    public Cliente[] getClientes(int id){
        Url url = UrlService.getInstancia().find(id);
        return url.getClientes().toArray(new Cliente[url.getClientes().size()]);
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
