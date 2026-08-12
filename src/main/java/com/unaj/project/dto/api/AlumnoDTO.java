package com.unaj.project.dto.api;

import com.unaj.project.model.Alumno;

/**
 * Representación JSON de un Alumno para la API REST (sin los bytes de la foto:
 * esa se sirve aparte por GET /api/alumnos/{id}/foto).
 */
public class AlumnoDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String nombreCompleto;
    private String email;
    private String celular;
    private String dni;
    private String nombrePadre;
    private String telefonoPadre;
    private String area;
    private String nivel;
    private String nivelEtiqueta;
    private boolean fotoPresente;

    public static AlumnoDTO desde(Alumno a) {
        AlumnoDTO d = new AlumnoDTO();
        d.id = a.getId();
        d.nombre = a.getNombre();
        d.apellido = a.getApellido();
        d.nombreCompleto = a.getNombreCompleto();
        d.email = a.getEmail();
        d.celular = a.getCelular();
        d.dni = a.getDni();
        d.nombrePadre = a.getNombrePadre();
        d.telefonoPadre = a.getTelefonoPadre();
        d.area = a.getArea();
        if (a.getNivel() != null) {
            d.nivel = a.getNivel().name();
            d.nivelEtiqueta = a.getNivel().getEtiqueta();
        }
        d.fotoPresente = a.isFotoPresente();
        return d;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getEmail() { return email; }
    public String getCelular() { return celular; }
    public String getDni() { return dni; }
    public String getNombrePadre() { return nombrePadre; }
    public String getTelefonoPadre() { return telefonoPadre; }
    public String getArea() { return area; }
    public String getNivel() { return nivel; }
    public String getNivelEtiqueta() { return nivelEtiqueta; }
    public boolean isFotoPresente() { return fotoPresente; }
}
