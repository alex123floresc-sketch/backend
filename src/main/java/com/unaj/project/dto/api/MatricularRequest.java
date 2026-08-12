package com.unaj.project.dto.api;

import com.unaj.project.model.Turno;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/** Cuerpo JSON para matricular (o re-matricular) a un alumno en un ciclo. */
public class MatricularRequest {

    @NotNull(message = "El ciclo es obligatorio")
    private Long cicloId;

    @NotNull(message = "El turno es obligatorio")
    private Turno turno;

    @NotBlank(message = "El área es obligatoria")
    private String area;

    private String conceptoMatricula;
    private BigDecimal montoMatricula;
    private String conceptoPension;
    private BigDecimal montoPension;
    private Integer numeroCuotas;

    public Long getCicloId() { return cicloId; }
    public void setCicloId(Long cicloId) { this.cicloId = cicloId; }

    public Turno getTurno() { return turno; }
    public void setTurno(Turno turno) { this.turno = turno; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public String getConceptoMatricula() { return conceptoMatricula; }
    public void setConceptoMatricula(String conceptoMatricula) { this.conceptoMatricula = conceptoMatricula; }

    public BigDecimal getMontoMatricula() { return montoMatricula; }
    public void setMontoMatricula(BigDecimal montoMatricula) { this.montoMatricula = montoMatricula; }

    public String getConceptoPension() { return conceptoPension; }
    public void setConceptoPension(String conceptoPension) { this.conceptoPension = conceptoPension; }

    public BigDecimal getMontoPension() { return montoPension; }
    public void setMontoPension(BigDecimal montoPension) { this.montoPension = montoPension; }

    public Integer getNumeroCuotas() { return numeroCuotas; }
    public void setNumeroCuotas(Integer numeroCuotas) { this.numeroCuotas = numeroCuotas; }
}
