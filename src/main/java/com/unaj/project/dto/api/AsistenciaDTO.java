package com.unaj.project.dto.api;

import com.unaj.project.model.Asistencia;

import java.time.LocalDateTime;

public class AsistenciaDTO {

    private Long id;
    private Long alumnoId;
    private String alumnoNombreCompleto;
    private String alumnoDni;
    private LocalDateTime horaRegistro;

    public static AsistenciaDTO desde(Asistencia a) {
        AsistenciaDTO d = new AsistenciaDTO();
        d.id = a.getId();
        if (a.getAlumno() != null) {
            d.alumnoId = a.getAlumno().getId();
            d.alumnoNombreCompleto = a.getAlumno().getNombreCompleto();
            d.alumnoDni = a.getAlumno().getDni();
        }
        d.horaRegistro = a.getHoraRegistro();
        return d;
    }

    public Long getId() { return id; }
    public Long getAlumnoId() { return alumnoId; }
    public String getAlumnoNombreCompleto() { return alumnoNombreCompleto; }
    public String getAlumnoDni() { return alumnoDni; }
    public LocalDateTime getHoraRegistro() { return horaRegistro; }
}
