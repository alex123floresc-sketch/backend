package com.unaj.project.controller.api;

import com.unaj.project.dto.FilaHorarioDTO;
import com.unaj.project.dto.api.AsignarCursoRequest;
import com.unaj.project.dto.api.BloqueHorarioDTO;
import com.unaj.project.dto.api.CrearBloqueRequest;
import com.unaj.project.dto.api.CursoDTO;
import com.unaj.project.dto.api.HorarioAsignacionDTO;
import com.unaj.project.model.BloqueHorario;
import com.unaj.project.model.DiaSemana;
import com.unaj.project.model.Horario;
import com.unaj.project.model.Nivel;
import com.unaj.project.model.Turno;
import com.unaj.project.repository.HorarioRepository;
import com.unaj.project.service.CursoService;
import com.unaj.project.service.HorarioService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/horarios")
public class HorarioApiController {

    private final HorarioService horarioService;
    private final CursoService cursoService;
    private final HorarioRepository horarioRepository;

    public HorarioApiController(HorarioService horarioService, CursoService cursoService,
                                HorarioRepository horarioRepository) {
        this.horarioService = horarioService;
        this.cursoService = cursoService;
        this.horarioRepository = horarioRepository;
    }

    @GetMapping("/niveles")
    public Map<String, Long> porNivel(@RequestParam(required = false) Long cicloId) {
        Map<Nivel, Long> resumen = horarioService.contarBloquesPorNivel(cicloId);
        Map<String, Long> resultado = new LinkedHashMap<>();
        resumen.forEach((n, c) -> resultado.put(n.name(), c));
        return resultado;
    }

    @GetMapping("/areas")
    public Map<String, Long> porArea(@RequestParam(required = false) Long cicloId, @RequestParam Nivel nivel) {
        return horarioService.contarBloquesPorArea(cicloId, nivel);
    }

    @GetMapping("/grilla")
    public Map<String, Object> grilla(@RequestParam Long cicloId, @RequestParam Nivel nivel, @RequestParam String area) {
        Map<Turno, List<FilaHorarioDTO>> grilla = horarioService.agruparParaGrilla(cicloId, nivel, area);

        Map<String, Object> porTurno = new LinkedHashMap<>();
        long totalBloques = 0;
        long totalAsignaciones = 0;
        for (Turno turno : Turno.values()) {
            List<FilaHorarioDTO> filas = grilla.getOrDefault(turno, List.of());
            totalBloques += filas.size();
            List<Map<String, Object>> filasDTO = filas.stream().map(f -> {
                Map<String, Object> fila = new LinkedHashMap<>();
                fila.put("bloque", BloqueHorarioDTO.desde(f.bloque()));
                Map<String, List<HorarioAsignacionDTO>> porDia = new LinkedHashMap<>();
                for (DiaSemana dia : DiaSemana.values()) {
                    porDia.put(dia.name(), f.cursosDe(dia).stream().map(HorarioAsignacionDTO::desde).toList());
                }
                fila.put("porDia", porDia);
                return fila;
            }).toList();
            porTurno.put(turno.name(), filasDTO);
            totalAsignaciones += filas.stream().flatMap(f -> f.porDia().values().stream()).mapToLong(List::size).sum();
        }

        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("turnos", porTurno);
        resultado.put("totalBloques", totalBloques);
        resultado.put("totalAsignaciones", totalAsignaciones);
        return resultado;
    }

    @PostMapping("/bloques")
    public void crearBloque(@Valid @RequestBody CrearBloqueRequest req) {
        horarioService.crearBloque(req.getCicloId(), req.getNivel(), req.getTurno(),
                req.getHoraInicio(), req.getHoraFin(), req.getTipo(), req.getArea());
    }

    @DeleteMapping("/bloques/{bloqueId}")
    public void eliminarBloque(@PathVariable Long bloqueId) {
        horarioService.eliminarBloque(bloqueId);
    }

    @GetMapping("/bloques/{bloqueId}/dia/{dia}/cursos")
    public Map<String, Object> cursosParaAsignar(@PathVariable Long bloqueId, @PathVariable DiaSemana dia) {
        BloqueHorario bloque = horarioService.buscarBloque(bloqueId);
        List<CursoDTO> cursosDisponibles = cursoService.listarPorNivelYArea(bloque.getNivel(), bloque.getArea())
                .stream().map(CursoDTO::desde).toList();
        List<Horario> cursosAgregados = horarioRepository.findByBloqueIdAndDiaSemana(bloqueId, dia);

        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("bloque", BloqueHorarioDTO.desde(bloque));
        resultado.put("cursosDisponibles", cursosDisponibles);
        resultado.put("cursoIdsAgregados", cursosAgregados.stream().map(h -> h.getCurso().getId()).collect(Collectors.toList()));
        return resultado;
    }

    @PostMapping("/asignar")
    public void asignar(@Valid @RequestBody AsignarCursoRequest req) {
        horarioService.asignarCurso(req.getBloqueId(), req.getDia(), req.getCursoIds());
    }

    @DeleteMapping("/{horarioId}")
    public void quitarCurso(@PathVariable Long horarioId) {
        horarioService.quitarCurso(horarioId);
    }
}
