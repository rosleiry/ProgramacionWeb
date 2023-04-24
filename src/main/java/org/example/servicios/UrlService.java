package org.example.servicios;

import kong.unirest.Unirest;
import org.example.encapsulaciones.Url;
import org.example.encapsulaciones.Usuario;
import org.example.util.Main;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.Set;

public class UrlService extends DatabaseService<Url>{

    private static UrlService instancia;

    private UrlService() {
        super(Url.class);
    }

    public static UrlService getInstancia(){
        if(instancia==null){
            instancia = new UrlService();
        }
        return instancia;
    }

    public static boolean verificarCod(String cod) {
        EntityManager em = getEntityManager();
        boolean res = false;
        try {
            TypedQuery<Url> query = em.createQuery("select e from Url e where e.enlaceAcortado like :cod", Url.class);
            query.setParameter("cod",cod+"%");
            res = query.getResultList().isEmpty();
        } catch (Exception e) {
            res = true;
        }
        System.out.println(res);
        return res;
    }

    /**
     *
     * @param
     * @return
     */

    public List<Url> consultaNativa(){
        EntityManager em = getEntityManager();
        javax.persistence.Query query = em.createNativeQuery("select * from Url ", Url.class);

        List<Url> lista = query.getResultList();
        return lista;
    }



    public Set<Url> eliminarEnlaceByID(Integer actual, Set<Url> enlace)
    {
        for (Url Eactual : enlace) {
            if(Eactual.getIdUrl() == actual)
            {
                enlace.remove(Eactual);
                return enlace;
            }
        }

        return null;
    }


    public Url findEnlace(String path) {
        EntityManager em = getEntityManager();
        TypedQuery<Url> query = em.createQuery("select e from Url e where e.enlaceAcortado like :cod" , Url.class);
        query.setParameter("cod","'%"+path+"%'");
        List<Url> enlaces  = query.getResultList();
        return enlaces.get(0);
    }


    public String getPreview(String url) {

        String response = Unirest.get("https://api.microlink.io?url="+url+"&screenshot=true&meta=false")
                .asJson().getBody().getObject().getJSONObject("data")
                .getJSONObject("screenshot").get("url").toString();

        try {
            java.net.URL aux = new URL(response);
            BufferedImage image = ImageIO.read(aux);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(image,"png",bos);
            response = Base64.getEncoder().encodeToString(bos.toByteArray());
        }catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public String getAcortado() {
        boolean res = false;
        String cod = "";

        while(!res){
            cod = Main.codeGenerator();
            res = instancia.verificarCod(cod);
        }
        return cod;
    }

    public Url[] getEnlaces(String user){
        Usuario usuario = UsuarioService.getInstancia().findAllByUsuario(user).get(0);
        Url[] enlaces = new Url[][usuario.getMisEnlaces().size()];
        usuario.getMisEnlaces().toArray(enlaces);
        System.out.println(enlaces[0].getURL());
        return enlaces;
    }

    public Url registrarEnlace(String enlace,String usuario) throws IOException {
        System.out.println(usuario);
        HttpURLConnection connection = (HttpURLConnection) new URL(enlace).openConnection();
        connection.setRequestMethod("HEAD");
        int responseCode = connection.getResponseCode();
        System.out.println(responseCode);
        connection.disconnect();
        if (200 <= responseCode && responseCode <= 399 || responseCode == 403) {
            Url url = new Url();
            if(!usuario.equalsIgnoreCase("anonimo")) {
                Usuario user = UsuarioService.getInstancia().findAllByUsuario(usuario).get(0);
                url.setUsuario(user);
            }

            String preview = UrlService.getInstancia().getPreview(enlace);
            String acortado = UrlService.getInstancia().getAcortado();

            url.setImagen64(preview);
            url.setEnlace(enlace);
            url.setEnlaceAcortado(acortado);

            url = UrlService.getInstancia().crear(url);
            System.out.println(enlace.getIdUrl());
            return url;

        }
        return null;
    }


}
