package org.example.servicios;

import org.example.encapsulaciones.Cliente;
import org.example.encapsulaciones.Url;

import java.io.IOException;
import java.util.ArrayList;

public class GrpcService {

    private final UrlService urlService = UrlService.getInstancia();
    private final UsuarioService usuarioService = UsuarioService.getInstancia();

    public void autentificacion(EnlaceRnOuterClass.usuarioRequest request, StreamObserver<EnlaceRnOuterClass.usuarioResponse> responseObserver){
        boolean autentificar = (usuarioService.autenticarUsuario(request.getUsuario(),request.getPassword()) != null) ? true:false;
        System.out.println(autentificar);
        responseObserver.onNext(EnlaceRnOuterClass.usuarioResponse.newBuilder().setOk(autentificar).build());
        responseObserver.onCompleted();
    }
    public void registrarEnlace(EnlaceRnOuterClass.EnlaceRequest request, StreamObserver<EnlaceRnOuterClass.EnlaceResponse> responseObserver){
        try {
            Url aux = urlService.registrarEnlace(request.getUrl(),request.getUsuario());
            responseObserver.onNext(convertir(aux));
            responseObserver.onCompleted();
        } catch (IOException e) {
            responseObserver.onError(e.getCause());
        }

    }

    public void getEnlace(EnlaceRnOuterClass.clientesRequest request, StreamObserver<EnlaceRnOuterClass.EnlaceResponse> responseObserver){
        Url enlace = urlService.find(request.getIdUrl());
        if (enlace != null){
            responseObserver.onNext(convertir(enlace));
            responseObserver.onCompleted();
        }else{
            responseObserver.onError(new noExisteEnlace("No existe el enlace: " + request.getIdUrl()));

        }
    }
    public void getEnlaces(EnlaceRnOuterClass.enlace request,StreamObserver<EnlaceRnOuterClass.ListaEnlace> responseObserver){
        Url[] enlaces = urlService.getEnlaces(request.getUser());
        List<EnlaceRnOuterClass.EnlaceResponse> enlaceResponses = new ArrayList<>();
        for (Url e : enlaces){
            enlaceResponses.add(convertir(e));
        }
        EnlaceRnOuterClass.ListaEnlace build = EnlaceRnOuterClass.ListaEnlace.newBuilder().addAllElace(enlaceResponses).build();
        responseObserver.onNext(build);
        responseObserver.onCompleted();
    }
    public void getClientes(EnlaceRnOuterClass.clientesRequest request, StreamObserver<EnlaceRnOuterClass.clienteReponse> responseObserver){
        List<Cliente> clientes = enlaceService.find(request.getIdEnlace()).getClientes();
        List<EnlaceRnOuterClass.cliente> clientesRN = new ArrayList<>();

        for (Cliente e: clientes){
            clientesRN.add(convertirCliente(e));
        }
        EnlaceRnOuterClass.clienteReponse build = EnlaceRnOuterClass.clienteReponse.newBuilder().addAllClientes(clientesRN).build();
        responseObserver.onNext(build);
        responseObserver.onCompleted();
    }

    private EnlaceRnOuterClass.cliente convertirCliente(Cliente e) {
        return EnlaceRnOuterClass.cliente.newBuilder()
                .setFecha(e.getFecha().toString())
                .setIp(e.getIp())
                .setNavegador(e.getNavegador())
                .setSistema(e.getSistemaOperativo())
                .build();
    }

    private EnlaceRnOuterClass.EnlaceResponse convertir(Url aux) {
        return EnlaceRnOuterClass.EnlaceResponse.newBuilder()
                .setEnlaceId(aux.getIdUrl())
                .setUrl(aux.getURL())
                .setFecha(aux.getFecha().toString())
                .setAcortado(aux.getEnlaceAcortado())
                .setVeces(aux.getCantVecesAccedidas())
                .setFoto(aux.getImagen64())
                .build();
    }


}
