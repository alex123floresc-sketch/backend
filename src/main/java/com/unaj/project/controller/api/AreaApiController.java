package com.unaj.project.controller.api;

import com.unaj.project.dto.AreaResumenDTO;
import com.unaj.project.dto.api.CursoDTO;
import com.unaj.project.model.Areas;
import com.unaj.project.model.Curso;
import com.unaj.project.model.Nivel;
import com.unaj.project.service.AlumnoService;
import com.unaj.project.service.CursoService;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/areas")
public class AreaApiController {

    private final CursoService cursoService;
    private final AlumnoService alumnoService;

    public AreaApiController(CursoService cursoService, AlumnoService alumnoService) {
        this.cursoService = cursoService;
        this.alumnoService = alumnoService;
    }

    /** Conteo de cursos por nivel (equivalente a areas/niveles.html). */
    @GetMapping("/niveles")
    public Map<String, Long> porNivel() {
        Map<Nivel, Long> resumen = cursoService.contarPorNivel();
        Map<String, Long> resultado = new LinkedHashMap<>();
        for (Map.Entry<Nivel, Long> e : resumen.entrySet()) {
            resultado.put(e.getKey().name(), e.getValue());
        }
        return resultado;
    }

    /** Resumen de cursos/profesores/alumnos por área o grado dentro de un nivel. */
    @GetMapping
    public List<AreaResumenDTO> resumen(@RequestParam Nivel nivel) {
        List<String> areasDelNivel = Areas.paraNivel(nivel);
        List<Curso> cursos = cursoService.listarPorNivel(nivel);
        Map<String, Long> alumnosPorArea = alumnoService.contarPorArea(nivel);
        return areasDelNivel.stream()
                .map(a -> new AreaResumenDTO(
                        a,
                        cursos.stream().filter(c -> c.getAreas().contains(a)).count(),
                        cursos.stream().filter(c -> c.getAreas().contains(a) && c.getProfesor() != null)
                                .map(c -> c.getProfesor().getId()).distinct().count(),
                        alumnosPorArea.get(a)))
                .toList();
    }

    /** Cursos de un área/grado concreto (marcando cuáles ya pertenecen a ella). */
    @GetMapping("/cursos")
    public List<CursoDTO> cursosDeArea(@RequestParam Nivel nivel, @RequestParam String area) {
        return cursoService.listarPorNivel(nivel).stream()
                .sorted(java.util.Comparator.comparing((Curso c) -> !c.getAreas().contains(area))
                        .thenComparing(Curso::getNombre, String.CASE_INSENSITIVE_ORDER))
                .map(CursoDTO::desde)
                .toList();
    }

    @PostMapping
    public void guardar(@RequestParam Nivel nivel, @RequestParam String area,
                        @RequestParam(required = false) List<Long> cursoIds) {
        cursoService.establecerCursosDeArea(nivel, area, cursoIds);
    }
}
