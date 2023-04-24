package org.example.servicios;

import org.example.encapsulaciones.Cliente;

public class ClienteService extends DatabaseService{

    private static ClienteService instancia;

    private ClienteService(){
        super(Cliente.class);
    }

    public static ClienteService getInstancia(){
        if(instancia==null){
            instancia = new ClienteService();
        }
        return instancia;
    }



}
