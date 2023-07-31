package org.example.encapsulaciones;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Entity
public class Producto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nombre;
    private int precio;

    @Transient
    private int cantidad;

    private String desc;
    @Column(columnDefinition = "boolean default true")
    private boolean estado;
    @OneToMany(fetch = FetchType.EAGER)
    private List<Imagen> Imagenes;


    public Producto(String nombre, int precio, String desc) {
        this.nombre = nombre;
        this.precio = precio;
        this.desc = desc;
        estado = true;
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

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }


    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public List<Imagen> getImagenes() {
        return Imagenes;
    }

    public void setImagenes(List<Imagen> imagenes) {
        Imagenes = imagenes;
    }

    public void actualizar(Producto producto) {
        this.nombre = producto.nombre;
        this.precio = producto.precio;
    }

    public int total(){
        return precio * cantidad;
    }

}
