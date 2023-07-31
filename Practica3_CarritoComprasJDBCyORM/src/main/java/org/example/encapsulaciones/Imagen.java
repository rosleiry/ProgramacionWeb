package org.example.encapsulaciones;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
public class Imagen implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String mimeType;
    @Lob
    private String imagenBase64;

    public Imagen(String nombre, String mimeType, String imagenBase64) {
        this.nombre = nombre;
        this.mimeType = mimeType;
        this.imagenBase64 = imagenBase64;
    }

    public Imagen() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getImagenBase64() {
        return imagenBase64;
    }

    public void setImagenBase64(String imagenBase64) {
        this.imagenBase64 = imagenBase64;
    }
}
