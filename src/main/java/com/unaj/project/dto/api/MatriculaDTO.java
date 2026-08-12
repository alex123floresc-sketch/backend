package com.unaj.project.dto.api;

import com.unaj.project.model.Matricula;
import com.unaj.project.model.MatriculaDetalle;

import java.time.LocalDateTime;
import java.util.List;

public class MatriculaDTO {

    public static class DetalleDTO {
        private Long id;
        private Long cursoId;
        private String cursoCodigo;
        private String cursoNombre;
        private Integer cursoHoras;

        static DetalleDTO desde(MatriculaDetalle d) {
            DetalleDTO dto = new DetalleDTO();
            dto.id = d.getId();
            if (d.getCurso() != null) {
                dto.cursoId = d.getCurso().getId();
                dto.cursoCodigo = d.getCurso().getCodigo();
                dto.cursoNombre = d.getCurso().getNombre();
                dto.cursoHoras = d.getCurso().getHoras();
            }
            return dto;
        }

        public Long getId() { return id; }
        public Long getCursoId() { return cursoId; }
        public String getCursoCodigo() { return cursoCodigo; }
        public String getCursoNombre() { return cursoNombre; }
        public Integer getCursoHoras() { return cursoHoras; }
    }

    private Long id;
    private Long alumnoId;
    private String alumnoNombreCompleto;
    private String alumnoDni;
    private Long cicloId;
    private String cicloNombre;
    private String turno;
    private LocalDateTime fechaMatricula;
    private String estado;
    private int totalHoras;
    private List<DetalleDTO> detalles;

    public static MatriculaDTO desde(Matricula m) {
        MatriculaDTO d = new MatriculaDTO();
        d.id = m.getId();
        if (m.getEstudiante() != null) {
            d.alumnoId = m.getEstudiante().getId();
            d.alumnoNombreCompleto = m.getEstudiante().getNombreCompleto();
            d.alumnoDni = m.getEstudiante().getDni();
        }
        if (m.getSemestre() != null) {
            d.cicloId = m.getSemestre().getId();
            d.cicloNombre = m.getSemestre().getNombre();
        }
        d.turno = m.getTurno() != null ? m.getTurno().name() : null;
        d.fechaMatricula = m.getFechaMatricula();
        d.estado = m.getEstado();
        d.totalHoras = m.getTotalHoras();
        d.detalles = m.getDetalles().stream().map(DetalleDTO::desde).toList();
        return d;
    }

    public Long getId() { return id; }
    public Long getAlumnoId() { return alumnoId; }
    public String getAlumnoNombreCompleto() { return alumnoNombreCompleto; }
    public String getAlumnoDni() { return alumnoDni; }
    public Long getCicloId() { return cicloId; }
    public String getCicloNombre() { return cicloNombre; }
    public String getTurno() { return turno; }
    public LocalDateTime getFechaMatricula() { return fechaMatricula; }
    public String getEstado() { return estado; }
    public int getTotalHoras() { return totalHoras; }
    public List<DetalleDTO> getDetalles() { return detalles; }
}
