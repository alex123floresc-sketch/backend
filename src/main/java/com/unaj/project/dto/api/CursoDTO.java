package com.unaj.project.dto.api;

import com.unaj.project.model.Curso;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Representación JSON de un Curso para la API REST.
 * Se construye dentro del request (OSIV activo), por lo que el acceso a las
 * asociaciones lazy (profesor, areas) es seguro aquí.
 */
public class CursoDTO {

    private Long id;
    private String codigo;
    private String nombre;
    private Integer horas;
    private String nivel;            // nombre del enum: PRIMARIA / SECUNDARIA / PREUNIVERSITARIO
    private String nivelEtiqueta;    // etiqueta legible
    private Long profesorId;
    private String profesorNombre;
    private Set<String> areas = new LinkedHashSet<>();
    private boolean destacadoWeb;

    public static CursoDTO desde(Curso c) {
        CursoDTO d = new CursoDTO();
        d.id = c.getId();
        d.codigo = c.getCodigo();
        d.nombre = c.getNombre();
        d.horas = c.getHoras();
        if (c.getNivel() != null) {
            d.nivel = c.getNivel().name();
            d.nivelEtiqueta = c.getNivel().getEtiqueta();
        }
        if (c.getProfesor() != null) {
            d.profesorId = c.getProfesor().getId();
            d.profesorNombre = c.getProfesor().getNombre() + " " + c.getProfesor().getApellido();
        }
        if (c.getAreas() != null) {
            d.areas = new LinkedHashSet<>(c.getAreas());
        }
        d.destacadoWeb = c.isDestacadoWeb();
        return d;
    }

    public Long getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public Integer getHoras() { return horas; }
    public String getNivel() { return nivel; }
    public String getNivelEtiqueta() { return nivelEtiqueta; }
    public Long getProfesorId() { return profesorId; }
    public String getProfesorNombre() { return profesorNombre; }
    public Set<String> getAreas() { return areas; }
    public boolean isDestacadoWeb() { return destacadoWeb; }
}
