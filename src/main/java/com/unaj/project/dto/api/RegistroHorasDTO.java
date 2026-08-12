package com.unaj.project.dto.api;

import com.unaj.project.model.RegistroHoras;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class RegistroHorasDTO {

    private Long id;
    private Long profesorId;
    private Long horarioId;
    private LocalDate fecha;
    private LocalDateTime horaLlegada;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private BigDecimal horas;
    private String observaciones;

    public static RegistroHorasDTO desde(RegistroHoras r) {
        RegistroHorasDTO d = new RegistroHorasDTO();
        d.id = r.getId();
        d.profesorId = r.getProfesor() != null ? r.getProfesor().getId() : null;
        d.horarioId = r.getHorario() != null ? r.getHorario().getId() : null;
        d.fecha = r.getFecha();
        d.horaLlegada = r.getHoraLlegada();
        d.horaInicio = r.getHoraInicio();
        d.horaFin = r.getHoraFin();
        d.horas = r.getHoras();
        d.observaciones = r.getObservaciones();
        return d;
    }

    public Long getId() { return id; }
    public Long getProfesorId() { return profesorId; }
    public Long getHorarioId() { return horarioId; }
    public LocalDate getFecha() { return fecha; }
    public LocalDateTime getHoraLlegada() { return horaLlegada; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
    public BigDecimal getHoras() { return horas; }
    public String getObservaciones() { return observaciones; }
}
