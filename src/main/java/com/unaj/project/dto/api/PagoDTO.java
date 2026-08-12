package com.unaj.project.dto.api;

import com.unaj.project.model.Pago;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PagoDTO {

    private Long id;
    private Long matriculaId;
    private String concepto;
    private BigDecimal monto;
    private BigDecimal montoPagado;
    private BigDecimal saldo;
    private LocalDate fechaVencimiento;
    private LocalDateTime fechaPago;
    private String estado;
    private String metodo;

    public static PagoDTO desde(Pago p) {
        PagoDTO d = new PagoDTO();
        d.id = p.getId();
        d.matriculaId = p.getMatricula() != null ? p.getMatricula().getId() : null;
        d.concepto = p.getConcepto();
        d.monto = p.getMonto();
        d.montoPagado = p.getMontoPagado();
        d.saldo = p.getSaldo();
        d.fechaVencimiento = p.getFechaVencimiento();
        d.fechaPago = p.getFechaPago();
        d.estado = p.getEstado();
        d.metodo = p.getMetodo();
        return d;
    }

    public Long getId() { return id; }
    public Long getMatriculaId() { return matriculaId; }
    public String getConcepto() { return concepto; }
    public BigDecimal getMonto() { return monto; }
    public BigDecimal getMontoPagado() { return montoPagado; }
    public BigDecimal getSaldo() { return saldo; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public LocalDateTime getFechaPago() { return fechaPago; }
    public String getEstado() { return estado; }
    public String getMetodo() { return metodo; }
}
