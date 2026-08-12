package com.unaj.project.dto.api;

import com.unaj.project.model.BloqueHorario;

import java.time.LocalTime;

public class BloqueHorarioDTO {

    private Long id;
    private Long cicloId;
    private String turno;
    private String area;
    private String nivel;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String tipo;
    private String tipoEtiqueta;

    public static BloqueHorarioDTO desde(BloqueHorario b) {
        BloqueHorarioDTO d = new BloqueHorarioDTO();
        d.id = b.getId();
        d.cicloId = b.getCiclo() != null ? b.getCiclo().getId() : null;
        d.turno = b.getTurno() != null ? b.getTurno().name() : null;
        d.area = b.getArea();
        d.nivel = b.getNivel() != null ? b.getNivel().name() : null;
        d.horaInicio = b.getHoraInicio();
        d.horaFin = b.getHoraFin();
        if (b.getTipo() != null) {
            d.tipo = b.getTipo().name();
            d.tipoEtiqueta = b.getTipo().getEtiqueta();
        }
        return d;
    }

    public Long getId() { return id; }
    public Long getCicloId() { return cicloId; }
    public String getTurno() { return turno; }
    public String getArea() { return area; }
    public String getNivel() { return nivel; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
    public String getTipo() { return tipo; }
    public String getTipoEtiqueta() { return tipoEtiqueta; }
}
