package org.example.Clases;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;

public class Comentario {
    @Id
    @GeneratedValue
    private Long id;
    @Column(
            nullable = false
    )
    private String nombre;
    @Column(
            nullable = false
    )
    private String mensaje;
    @Column(
            nullable = false
    )
    private LocalDate fecha;

    public Comentario(String nombre, String mensaje, LocalDate fecha) {
        this.nombre = nombre;
        this.mensaje = mensaje;
        this.fecha = fecha;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMensaje() {
        return this.mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDate getFecha() {
        return this.fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
}
