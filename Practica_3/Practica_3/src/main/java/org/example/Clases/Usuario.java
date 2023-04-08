package org.example.Clases;


import javax.persistence.*;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private int id;

    @Column(
            nullable = false
    )
    private String usuario;

    @Column(
            nullable = false
    )
    private String nombre;

    @Column(
            nullable = false
    )
    private String password;

    public Usuario(String usuario, String pass) {
        this.usuario = "";
        this.password = "";
    }

    public Usuario(String usuario, String nombre, String password) {
        this.usuario = usuario;
        this.nombre = nombre;
        this.password = password;
    }

    public Usuario() {

    }
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getUsurio() {
        return usuario;
    }

    public void setUsurio(String usurio) {
        this.usuario = usurio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
