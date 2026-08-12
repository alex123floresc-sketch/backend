package com.unaj.project.dto.api;

import com.unaj.project.model.RegistroIngreso;

import java.time.LocalDateTime;

public class RegistroIngresoDTO {

    private Long id;
    private Long alumnoId;
    private String alumnoNombreCompleto;
    private String alumnoDni;
    private LocalDateTime horaIngreso;

    public static RegistroIngresoDTO desde(RegistroIngreso r) {
        RegistroIngresoDTO d = new RegistroIngresoDTO();
        d.id = r.getId();
        if (r.getAlumno() != null) {
            d.alumnoId = r.getAlumno().getId();
            d.alumnoNombreCompleto = r.getAlumno().getNombreCompleto();
            d.alumnoDni = r.getAlumno().getDni();
        }
        d.horaIngreso = r.getHoraIngreso();
        return d;
    }

    public Long getId() { return id; }
    public Long getAlumnoId() { return alumnoId; }
    public String getAlumnoNombreCompleto() { return alumnoNombreCompleto; }
    public String getAlumnoDni() { return alumnoDni; }
    public LocalDateTime getHoraIngreso() { return horaIngreso; }
}
