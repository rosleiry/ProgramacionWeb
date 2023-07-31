package org.example.encapsulaciones;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
public class ProductoComprado implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int productId;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long ventaID;
    private int cantidad;
    private int precio;
    private String nombre;

    public ProductoComprado(int productId, long ventaID, int cantidad, int precio, String nombre) {
        this.productId = productId;
        this.ventaID = ventaID;
        this.cantidad = cantidad;
        this.precio = precio;
        this.nombre = nombre;
    }

    public ProductoComprado() {

    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public long getVentaID() {
        return ventaID;
    }

    public void setVentaID(long ventaID) {
        this.ventaID = ventaID;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public int total(){
        return cantidad * precio;
    }
}
