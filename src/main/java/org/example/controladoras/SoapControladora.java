package org.example.controladoras;

import io.javalin.Javalin;
import jakarta.xml.ws.spi.http.HttpContext;
import org.eclipse.jetty.http.spi.HttpSpiContextHandler;
import org.eclipse.jetty.http.spi.JettyHttpContext;
//import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.http.spi.JettyHttpServer;
import org.example.servicios.SoapService;
import javax.xml.ws.Endpoint;
import java.lang.reflect.Method;

public class SoapControladora {


    protected Javalin app;

    public SoapControladora(Javalin app){
        this.app = app;
    }

    public void aplicarRutas(){
        Server server = app.server().server();
        ContextHandlerCollection contextHandlerCollection = new ContextHandlerCollection();
        server.setHandler(contextHandlerCollection);

        try {
            JettyHttpContext context = build(server, "/ws");

            SoapService userWS = new SoapService();
            Endpoint userEndPoint = javax.xml.ws.Endpoint.create(userWS);

            userEndPoint.publish(context);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private JettyHttpContext build(Server server, String contextString) throws Exception {
        JettyHttpServer jettyHttpServer = new JettyHttpServer(server, true);
        JettyHttpContext ctx = (JettyHttpContext) jettyHttpServer.createContext(contextString);
        Method method = JettyHttpContext.class.getDeclaredMethod("getJettyContextHandler");
        method.setAccessible(true);
        HttpSpiContextHandler contextHandler = (HttpSpiContextHandler) method.invoke(ctx);
        contextHandler.start();
        return ctx;
    }

}
