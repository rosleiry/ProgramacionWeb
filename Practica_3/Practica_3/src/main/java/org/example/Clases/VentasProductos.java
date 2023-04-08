package org.example.Clases;

import org.example.Clases.Producto;

import javax.persistence.*;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Entity
public class VentasProductos implements Serializable{
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private long id;

    @Column(
            nullable = false
    )
    private String nombreCliente;

    @OneToMany(
            fetch = FetchType.EAGER
    )
    private ArrayList<Producto> listaProductos;

    @Column(
            nullable = false
    )
    Date fechaCompra = new Date();
    public VentasProductos(String nombre, ArrayList<Producto> productos) {
        this.nombreCliente = nombre;
        this.listaProductos = productos;
    }
    public VentasProductos(long id, String nombreCliente, ArrayList<Producto> productos) {
        this.id = id;
        this.fechaCompra = new Date();
        this.nombreCliente = nombreCliente;
        this.listaProductos = productos;
    }

    public VentasProductos() {

    }

    public long getId() {
        return id;
    }

   // public void setId(int id) {this.id = id;}

    public ArrayList<Producto> getListaProductos() {
        return this.listaProductos;
    }
    public void setListaProductos(ArrayList<Producto> listaProductos) {
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
            for (Producto producto : listaProductos) {
                total += producto.getPrecio()*producto.getCantidad();
            }
            return total;
        }



}
