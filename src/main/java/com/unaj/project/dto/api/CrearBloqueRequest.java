package com.unaj.project.dto.api;

import com.unaj.project.model.Nivel;
import com.unaj.project.model.TipoBloque;
import com.unaj.project.model.Turno;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public class CrearBloqueRequest {

    @NotNull(message = "El ciclo es obligatorio")
    private Long cicloId;

    @NotNull(message = "El nivel es obligatorio")
    private Nivel nivel;

    @NotNull(message = "El turno es obligatorio")
    private Turno turno;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horaFin;

    private TipoBloque tipo = TipoBloque.CLASE;

    @NotBlank(message = "El área/grado es obligatorio")
    private String area;

    public Long getCicloId() { return cicloId; }
    public void setCicloId(Long cicloId) { this.cicloId = cicloId; }

    public Nivel getNivel() { return nivel; }
    public void setNivel(Nivel nivel) { this.nivel = nivel; }

    public Turno getTurno() { return turno; }
    public void setTurno(Turno turno) { this.turno = turno; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

    public TipoBloque getTipo() { return tipo; }
    public void setTipo(TipoBloque tipo) { this.tipo = tipo; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
}
