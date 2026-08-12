package com.unaj.project.dto.api;

import com.unaj.project.model.Profesor;

import java.math.BigDecimal;
import java.util.List;

public class ProfesorDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String nombreCompleto;
    private String email;
    private String especialidad;
    private BigDecimal tarifaHora;
    private List<String> niveles;
    private boolean fotoPresente;
    private boolean destacadoWeb;

    public static ProfesorDTO desde(Profesor p) {
        ProfesorDTO d = new ProfesorDTO();
        d.id = p.getId();
        d.nombre = p.getNombre();
        d.apellido = p.getApellido();
        d.nombreCompleto = p.getNombreCompleto();
        d.email = p.getEmail();
        d.especialidad = p.getEspecialidad();
        d.tarifaHora = p.getTarifaHora();
        d.niveles = p.getNiveles().stream().map(Enum::name).toList();
        d.fotoPresente = p.isFotoPresente();
        d.destacadoWeb = p.isDestacadoWeb();
        return d;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getEmail() { return email; }
    public String getEspecialidad() { return especialidad; }
    public BigDecimal getTarifaHora() { return tarifaHora; }
    public List<String> getNiveles() { return niveles; }
    public boolean isFotoPresente() { return fotoPresente; }
    public boolean isDestacadoWeb() { return destacadoWeb; }
}
