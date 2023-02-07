package org.example;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    private static Document doc;
    public static void main(String[] args) {

        System.out.println("Ingrese una URL: ");
        Scanner sc = new Scanner(System.in);
        String enlace = sc.nextLine();

        if (isValid(enlace)) {
            System.out.println("URL is valid");
            System.out.println("Total de lineas: " + contarLineas(enlace));
            System.out.println("Total de parrafos: " + contarParrafos());
            System.out.println("Total de imagenes dentro de los párrafos que contiene el archivo HTML: "
                    + contarImagenes());

            try {
                contarForms(enlace);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
            System.out.println("Esta URL no es valida");


    }

    /* Returns true if url is valid
    * Este fragmento de codigo pertenece a https://www.geeksforgeeks.org/check-if-url-is-valid-or-not-in-java/*/
    public static boolean isValid(String url)
    {
        /* Try creating a valid URL */
        try {
            doc = Jsoup.parse(url);
            doc = Jsoup.connect(url).get();
        }
        // If there was an Exception
        // while creating URL object
        catch (IllegalArgumentException | IOException e){
            return false;
        }
        return true;

    }

    //Indicar la cantidad de lineas del recurso retornado
    private static int contarLineas(String url){
        String contenido = "";
        try {
            Connection.Response doc = Jsoup.connect(url).execute();
            contenido = doc.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contenido.split("\n").length;
    }

    //Indicar la cantidad de párrafos (p) que contiene el documento
    // HTML.
    private static int contarParrafos(){
        int total;

        Elements elements;
        elements = doc.select("p");
        total = elements.size();

        return total;

    }

    //Indicar la cantidad de imágenes (img) dentro de los párrafos que
    //contiene el archivo HTML.
    private static int contarImagenes(){
        int total;
        Elements elements;
        elements = doc.select("p img"); //Indicar (img) dentro de p
        total = elements.size();

        return total;
    }

    //indicar la cantidad de formularios (form) que contiene el HTML por
    //categorizando por el método implementado POST o GET (UTILIZO EL METODO POST)
    private static void contarForms(String url) throws IOException{
        int total, formulario=1;

        Elements formElement = doc.select("[method=post]");
        total = formElement.size();
        System.out.println("Cantidad de formularios que contiene el HTML: " + total);

        for (Element element: doc.getElementsByTag("form").forms()) {
            String metodo = element.attr("method");
            Elements tipoPost = element.getElementsByAttributeValueContaining("method", "post");

            //Para cada formulario parseado, identificar que el método de
            //envío del formulario sea POST y enviar una petición al servidor
            //con el parámetro llamado asignatura y valor practica1 y un
            //header llamado matricula-id con el valor correspondiente a
            //matrícula o id asignado.
            for (Element element1: tipoPost ) {

                String dir = element1.absUrl("action");
                try{
                    System.out.println("Forumlario: "+formulario);
                    org.jsoup.nodes.Document docs = Jsoup.connect(dir)
                            .data("asignatura","practica1")
                            .header("matricula-id", "20180627-10134712").post();

                    System.out.println(docs.body().toString());
                }catch (HttpStatusException ignored){}
            }

            // Para cada formulario mostrar los campos del tipo input y su
            //respectivo tipo que contiene en el documento HTML.
            Elements todosInputs = element.select("input");
            for (Element otroelement: todosInputs) {
                System.out.println("Tipo de input: " + otroelement.attr("type"));
            }
            formulario++;
        }
    }
}