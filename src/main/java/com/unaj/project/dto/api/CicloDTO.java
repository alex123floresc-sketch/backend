package com.unaj.project.dto.api;

import com.unaj.project.model.Ciclo;

import java.time.LocalDate;

/**
 * Representación JSON de un Ciclo. Aunque la entidad es "plana" (sin relaciones),
 * Hibernate puede devolverla envuelta en un proxy bytebuddy (p.ej. accedida a través
 * de otra asociación lazy en la misma sesión) que Jackson no sabe serializar
 * directamente — de ahí que, igual que con Curso, se use un DTO explícito.
 */
public class CicloDTO {

    private Long id;
    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private boolean activo;

    public static CicloDTO desde(Ciclo c) {
        CicloDTO d = new CicloDTO();
        d.id = c.getId();
        d.nombre = c.getNombre();
        d.fechaInicio = c.getFechaInicio();
        d.fechaFin = c.getFechaFin();
        d.activo = c.isActivo();
        return d;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public boolean isActivo() { return activo; }
}
