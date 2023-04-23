package org.example.encapsulaciones;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
public class Url implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idUrl;
    private String enlace;
    private Date fecha = new Date();
    private String enlaceAcortado;
    private int cantVecesAccedidas = 0;

    @Lob
    private String imagen64;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Cliente> clientes;

    @ManyToOne(optional = true)
    private Usuario usuario;

    public int getIdUrl() {
        return idUrl;
    }

    public void setIdUrl(int idUrl) {
        this.idUrl = idUrl;
    }

    public String getEnlace() {
        return enlace;
    }

    public void setEnlace(String enlace) {
        this.enlace = enlace;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getEnlaceAcortado() {
        return enlaceAcortado;
    }

    public void setEnlaceAcortado(String enlaceAcortado) {
        this.enlaceAcortado = enlaceAcortado;
    }

    public int getCantVecesAccedidas() {
        return cantVecesAccedidas;
    }

    public void setCantVecesAccedidas(int cantVecesAccedidas) {
        this.cantVecesAccedidas = cantVecesAccedidas;
    }

    public String getImagen64() {
        return imagen64;
    }

    public void setImagen64(String imagen64) {
        this.imagen64 = imagen64;
    }

    public List<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Map<String, Integer> calcularCantidad() {
        List<Cliente> clientes1 = new ArrayList<>(clientes);
        if (!clientes1.isEmpty()) {
            Cliente primero = clientes1.get(0);

            Map<String, Integer> cantPorDia = new HashMap<String, Integer>();
            String fechaActual = primero.getFechaFormat();
            int aux = 1;
            int i = 1;
            while (i < clientes1.size()) {
                Cliente client = clientes1.get(i);
                if (!client.getFechaFormat().equalsIgnoreCase(fechaActual)) {
                    cantPorDia.put(fechaActual, aux);
                    aux = 1;
                    fechaActual = client.getFechaFormat();
                } else {
                    aux++;
                }
                i++;
            }
            cantPorDia.put(fechaActual, aux);

            return cantPorDia;
        }
        return null;
    }


}
