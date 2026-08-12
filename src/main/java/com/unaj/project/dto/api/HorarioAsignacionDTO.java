package com.unaj.project.dto.api;

import com.unaj.project.model.Horario;

public class HorarioAsignacionDTO {

    private Long id;
    private Long bloqueId;
    private String diaSemana;
    private Long cursoId;
    private String cursoCodigo;
    private String cursoNombre;
    private String profesorNombre;
    private String aula;

    public static HorarioAsignacionDTO desde(Horario h) {
        HorarioAsignacionDTO d = new HorarioAsignacionDTO();
        d.id = h.getId();
        d.bloqueId = h.getBloque() != null ? h.getBloque().getId() : null;
        d.diaSemana = h.getDiaSemana() != null ? h.getDiaSemana().name() : null;
        if (h.getCurso() != null) {
            d.cursoId = h.getCurso().getId();
            d.cursoCodigo = h.getCurso().getCodigo();
            d.cursoNombre = h.getCurso().getNombre();
            d.profesorNombre = h.getCurso().getProfesor() != null
                    ? h.getCurso().getProfesor().getNombreCompleto() : null;
        }
        d.aula = h.getAula();
        return d;
    }

    public Long getId() { return id; }
    public Long getBloqueId() { return bloqueId; }
    public String getDiaSemana() { return diaSemana; }
    public Long getCursoId() { return cursoId; }
    public String getCursoCodigo() { return cursoCodigo; }
    public String getCursoNombre() { return cursoNombre; }
    public String getProfesorNombre() { return profesorNombre; }
    public String getAula() { return aula; }
}
