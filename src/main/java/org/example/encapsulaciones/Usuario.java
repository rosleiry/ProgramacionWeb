package org.example.encapsulaciones;


import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@NamedQueries({
        @NamedQuery(name = "Usuario.findAllByUsuario", query = "select u from Usuario u where u.usuario = :user"),
        @NamedQuery(name = "Usuario.autenticarUsuario", query = "select u from Usuario u where u.usuario = :user and u.contrasea = :pass")})
public class Usuario implements Serializable {
    public enum TipoRole{
        ROLE_USUARIO, ROLE_ADMIN
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idUsuario;

    @PrimaryKeyJoinColumn
    private String usuario;

    private String nombre;
    private String contrasea;
    private TipoRole rol;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.EAGER)
    private Set<Url> enlaces = new HashSet<Url>();

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContrasea() {
        return contrasea;
    }

    public void setContrasea(String contrasea) {
        this.contrasea = contrasea;
    }

    public TipoRole getRol() {
        return rol;
    }

    public void setRol(TipoRole rol) {
        this.rol = rol;
    }

    public Set<Url> getEnlaces() {
        return enlaces;
    }

    public void setEnlaces(Set<Url> enlaces) {
        this.enlaces = enlaces;
    }
}
