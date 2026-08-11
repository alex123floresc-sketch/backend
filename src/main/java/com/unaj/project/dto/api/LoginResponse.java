package com.unaj.project.dto.api;

import java.util.List;

/** Respuesta del login: el token JWT y datos básicos del usuario para que Angular arme su UI. */
public class LoginResponse {

    private String token;
    private long expiraEnMs;
    private String username;
    private String nombre;
    private List<String> roles;

    public LoginResponse() {}

    public LoginResponse(String token, long expiraEnMs, String username, String nombre, List<String> roles) {
        this.token = token;
        this.expiraEnMs = expiraEnMs;
        this.username = username;
        this.nombre = nombre;
        this.roles = roles;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public long getExpiraEnMs() { return expiraEnMs; }
    public void setExpiraEnMs(long expiraEnMs) { this.expiraEnMs = expiraEnMs; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
}
