package com.unaj.project.dto.api;

import com.unaj.project.model.Rol;
import com.unaj.project.model.Usuario;

import java.util.List;

public class PerfilDTO {

    private Long id;
    private String username;
    private String nombre;
    private String email;
    private String telefono;
    private String cargo;
    private boolean fotoPresente;
    private boolean firmaPresente;
    private List<String> roles;

    public static PerfilDTO desde(Usuario u) {
        PerfilDTO d = new PerfilDTO();
        d.id = u.getId();
        d.username = u.getUsername();
        d.nombre = u.getNombre();
        d.email = u.getEmail();
        d.telefono = u.getTelefono();
        d.cargo = u.getCargo();
        d.fotoPresente = u.isFotoPresente();
        d.firmaPresente = u.isFirmaPresente();
        d.roles = u.getRoles() != null ? u.getRoles().stream().map(Rol::getNombre).toList() : List.of();
        return d;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
    public String getCargo() { return cargo; }
    public boolean isFotoPresente() { return fotoPresente; }
    public boolean isFirmaPresente() { return firmaPresente; }
    public List<String> getRoles() { return roles; }
}
