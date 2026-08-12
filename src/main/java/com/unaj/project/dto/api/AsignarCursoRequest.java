package com.unaj.project.dto.api;

import com.unaj.project.model.DiaSemana;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class AsignarCursoRequest {

    @NotNull(message = "El bloque es obligatorio")
    private Long bloqueId;

    @NotNull(message = "El día es obligatorio")
    private DiaSemana dia;

    private List<Long> cursoIds;

    public Long getBloqueId() { return bloqueId; }
    public void setBloqueId(Long bloqueId) { this.bloqueId = bloqueId; }

    public DiaSemana getDia() { return dia; }
    public void setDia(DiaSemana dia) { this.dia = dia; }

    public List<Long> getCursoIds() { return cursoIds; }
    public void setCursoIds(List<Long> cursoIds) { this.cursoIds = cursoIds; }
}
