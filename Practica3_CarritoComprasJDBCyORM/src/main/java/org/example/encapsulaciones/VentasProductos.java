package org.example.encapsulaciones;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@Entity
public class VentasProductos implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Temporal(TemporalType.DATE)
    Date fechaCompra = new Date();
    private String nombreCliente;

    @OneToMany(fetch = FetchType.EAGER)
    private List<ProductoComprado> listaProductos;


    public VentasProductos(String nombre) {
        fechaCompra = new Date();
        this.nombreCliente = nombreCliente;
    }

    public VentasProductos() {

    }

    public long getId() {
        return id;
    }

    public List<ProductoComprado> getListaProductos() {
        return this.listaProductos;
    }
    public void setListaProductos(List<ProductoComprado> listaProductos) {
        this.listaProductos = listaProductos;
    }
    public String getNombreCliente() {
        return nombreCliente;
    }
    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getFechaCompra() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY");
        String date = dateFormat.format(this.fechaCompra);
        return date;
    }

    public Integer getTotal(){
        Integer total = 0;
        for (ProductoComprado producto : listaProductos) {
            total += producto.getPrecio()*producto.getCantidad();
        }
        return total;
    }
}
