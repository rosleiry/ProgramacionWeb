package org.example.encapsulaciones;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class VentasProductos {
    private long id;
    Date fechaCompra = new Date();
    private String nombreCliente;
    private ArrayList<Producto> listaProductos;

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

    public long getId() {
        return id;
    }

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

    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (Producto producto : listaProductos) {
            BigDecimal precio = producto.getPrecio();
            int cantidad = producto.getCantidad();
            BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(cantidad));
            total = total.add(subtotal);
        }
        return total;
    }
}
