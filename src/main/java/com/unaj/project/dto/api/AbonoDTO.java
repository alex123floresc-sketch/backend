package com.unaj.project.dto.api;

import com.unaj.project.model.Abono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AbonoDTO {

    private Long id;
    private BigDecimal monto;
    private LocalDateTime fecha;
    private String metodo;
    private String registradoPorNombre;

    public static AbonoDTO desde(Abono a) {
        AbonoDTO d = new AbonoDTO();
        d.id = a.getId();
        d.monto = a.getMonto();
        d.fecha = a.getFecha();
        d.metodo = a.getMetodo();
        d.registradoPorNombre = a.getRegistradoPor() != null ? a.getRegistradoPor().getNombre() : null;
        return d;
    }

    public Long getId() { return id; }
    public BigDecimal getMonto() { return monto; }
    public LocalDateTime getFecha() { return fecha; }
    public String getMetodo() { return metodo; }
    public String getRegistradoPorNombre() { return registradoPorNombre; }
}
