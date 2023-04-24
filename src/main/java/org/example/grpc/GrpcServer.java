package org.example.grpc;

import io.grpc.BindableService;
import org.example.servicios.GrpcService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GrpcServer {
    private static final int PORT = 7001;
    private Server server;

    public void start() throws IOException{
        server = ServerBuilder.forPort(PORT).addService((BindableService) new GrpcService()).build().start();
    }

    public void blockunitlShutdown() throws InterruptedException{
        if (server == null){
            return;
        }
        server.awaitTermination();
    }
}
