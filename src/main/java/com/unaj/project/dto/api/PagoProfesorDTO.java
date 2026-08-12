package com.unaj.project.dto.api;

import com.unaj.project.model.PagoProfesor;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PagoProfesorDTO {

    private Long id;
    private Long profesorId;
    private String tipoPeriodo;
    private LocalDate periodoInicio;
    private LocalDate periodoFin;
    private BigDecimal horasPagadas;
    private BigDecimal monto;
    private LocalDate fechaPago;
    private String metodo;
    private String observaciones;

    public static PagoProfesorDTO desde(PagoProfesor p) {
        PagoProfesorDTO d = new PagoProfesorDTO();
        d.id = p.getId();
        d.profesorId = p.getProfesor() != null ? p.getProfesor().getId() : null;
        d.tipoPeriodo = p.getTipoPeriodo();
        d.periodoInicio = p.getPeriodoInicio();
        d.periodoFin = p.getPeriodoFin();
        d.horasPagadas = p.getHorasPagadas();
        d.monto = p.getMonto();
        d.fechaPago = p.getFechaPago();
        d.metodo = p.getMetodo();
        d.observaciones = p.getObservaciones();
        return d;
    }

    public Long getId() { return id; }
    public Long getProfesorId() { return profesorId; }
    public String getTipoPeriodo() { return tipoPeriodo; }
    public LocalDate getPeriodoInicio() { return periodoInicio; }
    public LocalDate getPeriodoFin() { return periodoFin; }
    public BigDecimal getHorasPagadas() { return horasPagadas; }
    public BigDecimal getMonto() { return monto; }
    public LocalDate getFechaPago() { return fechaPago; }
    public String getMetodo() { return metodo; }
    public String getObservaciones() { return observaciones; }
}
