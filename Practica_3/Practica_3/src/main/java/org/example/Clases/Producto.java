package org.example.Clases;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Producto implements Serializable {

    @Id
    private int id;
    @Column(
            nullable = false
    )
    private String nombre;
    @Column(
            nullable = false
    )
    private int precio;
    @Column(
            nullable = false
    )
    private int cantidad;

    @OneToMany(
            fetch = FetchType.EAGER
    )
    private Set<Imagen> imagen = new HashSet();

    public Producto( String nombre, int precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    public Producto() {

    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public Set<Imagen> getImagen() {
        return this.imagen;
    }


    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    public void actualizar(Producto producto) {
        this.nombre = producto.nombre;
        this.precio = producto.precio;
    }

    public int total(){
        return precio * cantidad;
    }
}
