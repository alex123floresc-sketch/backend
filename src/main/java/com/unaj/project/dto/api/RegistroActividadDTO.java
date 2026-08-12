package com.unaj.project.dto.api;

import com.unaj.project.model.RegistroActividad;

import java.time.LocalDateTime;

public class RegistroActividadDTO {

    private Long id;
    private String username;
    private String accion;
    private String accionEtiqueta;
    private String modulo;
    private Long entidadId;
    private String descripcion;
    private LocalDateTime fecha;

    public static RegistroActividadDTO desde(RegistroActividad r) {
        RegistroActividadDTO d = new RegistroActividadDTO();
        d.id = r.getId();
        d.username = r.getUsername();
        if (r.getAccion() != null) {
            d.accion = r.getAccion().name();
            d.accionEtiqueta = r.getAccion().getEtiqueta();
        }
        d.modulo = r.getModulo();
        d.entidadId = r.getEntidadId();
        d.descripcion = r.getDescripcion();
        d.fecha = r.getFecha();
        return d;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getAccion() { return accion; }
    public String getAccionEtiqueta() { return accionEtiqueta; }
    public String getModulo() { return modulo; }
    public Long getEntidadId() { return entidadId; }
    public String getDescripcion() { return descripcion; }
    public LocalDateTime getFecha() { return fecha; }
}
