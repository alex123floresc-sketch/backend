package com.unaj.project.dto.api;

import com.unaj.project.model.Rol;
import com.unaj.project.model.Usuario;

import java.util.List;

public class UsuarioDTO {

    private Long id;
    private String username;
    private String nombre;
    private boolean activo;
    private List<String> roles;
    private String email;
    private String telefono;
    private String cargo;
    private boolean fotoPresente;

    public static UsuarioDTO desde(Usuario u) {
        UsuarioDTO d = new UsuarioDTO();
        d.id = u.getId();
        d.username = u.getUsername();
        d.nombre = u.getNombre();
        d.activo = u.isActivo();
        d.roles = u.getRoles() != null ? u.getRoles().stream().map(Rol::getNombre).toList() : List.of();
        d.email = u.getEmail();
        d.telefono = u.getTelefono();
        d.cargo = u.getCargo();
        d.fotoPresente = u.isFotoPresente();
        return d;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getNombre() { return nombre; }
    public boolean isActivo() { return activo; }
    public List<String> getRoles() { return roles; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
    public String getCargo() { return cargo; }
    public boolean isFotoPresente() { return fotoPresente; }
}
