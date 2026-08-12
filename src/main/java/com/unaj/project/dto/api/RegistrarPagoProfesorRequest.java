package com.unaj.project.dto.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RegistrarPagoProfesorRequest {

    @NotNull(message = "El profesor es obligatorio")
    private Long profesorId;

    @NotBlank(message = "El tipo de período es obligatorio")
    private String tipoPeriodo;

    @NotNull(message = "El inicio del período es obligatorio")
    private LocalDate periodoInicio;

    @NotNull(message = "El fin del período es obligatorio")
    private LocalDate periodoFin;

    @NotNull(message = "Las horas pagadas son obligatorias")
    private BigDecimal horasPagadas;

    @NotNull(message = "El monto es obligatorio")
    private BigDecimal monto;

    private LocalDate fechaPago;
    private String metodo;
    private String observaciones;

    public Long getProfesorId() { return profesorId; }
    public void setProfesorId(Long profesorId) { this.profesorId = profesorId; }

    public String getTipoPeriodo() { return tipoPeriodo; }
    public void setTipoPeriodo(String tipoPeriodo) { this.tipoPeriodo = tipoPeriodo; }

    public LocalDate getPeriodoInicio() { return periodoInicio; }
    public void setPeriodoInicio(LocalDate periodoInicio) { this.periodoInicio = periodoInicio; }

    public LocalDate getPeriodoFin() { return periodoFin; }
    public void setPeriodoFin(LocalDate periodoFin) { this.periodoFin = periodoFin; }

    public BigDecimal getHorasPagadas() { return horasPagadas; }
    public void setHorasPagadas(BigDecimal horasPagadas) { this.horasPagadas = horasPagadas; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public LocalDate getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDate fechaPago) { this.fechaPago = fechaPago; }

    public String getMetodo() { return metodo; }
    public void setMetodo(String metodo) { this.metodo = metodo; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
