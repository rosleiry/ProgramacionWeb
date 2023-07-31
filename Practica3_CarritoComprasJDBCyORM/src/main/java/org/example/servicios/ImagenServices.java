package org.example.servicios;

import org.example.encapsulaciones.Imagen;

public class ImagenServices extends DatabaseService<Imagen>{
    private static ImagenServices instance;

    private ImagenServices(){
        super(Imagen.class);
    }

    public static ImagenServices getInstancia(){
        if(instance==null){
            instance = new ImagenServices();
        }
        return instance;
    }
}
