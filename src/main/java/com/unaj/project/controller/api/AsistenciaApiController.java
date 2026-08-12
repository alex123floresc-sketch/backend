package com.unaj.project.controller.api;

import com.unaj.project.dto.AsistenciaResultadoDTO;
import com.unaj.project.dto.RegistroAsistenciaRequest;
import com.unaj.project.dto.RegistroIngresoRequest;
import com.unaj.project.dto.api.AsistenciaDTO;
import com.unaj.project.dto.api.HorarioAsignacionDTO;
import com.unaj.project.dto.api.RegistroHorasDTO;
import com.unaj.project.dto.api.RegistroIngresoDTO;
import com.unaj.project.model.Ciclo;
import com.unaj.project.model.DiaSemana;
import com.unaj.project.model.Horario;
import com.unaj.project.model.RegistroHoras;
import com.unaj.project.model.Turno;
import com.unaj.project.repository.HorarioRepository;
import com.unaj.project.service.AsistenciaService;
import com.unaj.project.service.CicloService;
import com.unaj.project.service.HorarioService;
import com.unaj.project.service.RegistroHorasService;
import com.unaj.project.service.RegistroIngresoService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/asistencias")
public class AsistenciaApiController {

    private final RegistroIngresoService registroIngresoService;
    private final AsistenciaService asistenciaService;
    private final CicloService cicloService;
    private final HorarioService horarioService;
    private final HorarioRepository horarioRepository;
    private final RegistroHorasService registroHorasService;

    public AsistenciaApiController(RegistroIngresoService registroIngresoService,
                                   AsistenciaService asistenciaService,
                                   CicloService cicloService,
                                   HorarioService horarioService,
                                   HorarioRepository horarioRepository,
                                   RegistroHorasService registroHorasService) {
        this.registroIngresoService = registroIngresoService;
        this.asistenciaService = asistenciaService;
        this.cicloService = cicloService;
        this.horarioService = horarioService;
        this.horarioRepository = horarioRepository;
        this.registroHorasService = registroHorasService;
    }

    @GetMapping("/estudiantes")
    public Map<String, Object> ingreso() {
        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("registrosHoy", registroIngresoService.listarDeHoy().stream().map(RegistroIngresoDTO::desde).toList());
        resultado.put("totalHoy", registroIngresoService.contarDeHoy());
        return resultado;
    }

    @PostMapping("/estudiantes/registrar")
    public AsistenciaResultadoDTO registrarIngreso(@RequestBody RegistroIngresoRequest body, Authentication auth) {
        String username = (auth != null) ? auth.getName() : null;
        return registroIngresoService.registrar(body.codigo(), username);
    }

    @GetMapping("/docentes")
    public Map<String, Object> docentes(@RequestParam(required = false) Turno turno) {
        Ciclo cicloActivo = cicloService.obtenerActivo();
        DiaSemana diaHoy = DiaSemana.desde(LocalDate.now().getDayOfWeek());
        Turno turnoSel = (turno != null) ? turno : turnoActual();

        List<Horario> horariosHoy = List.of();
        if (cicloActivo != null && diaHoy != null) {
            horariosHoy = horarioRepository.findByCicloId(cicloActivo.getId()).stream()
                    .filter(h -> h.getDiaSemana() == diaHoy && h.getTurno() == turnoSel)
                    .sorted(Comparator.comparing(Horario::getHoraInicio)
                            .thenComparing(h -> h.getCurso().getProfesor() != null
                                    ? h.getCurso().getProfesor().getNombreCompleto() : ""))
                    .toList();
        }

        Map<Long, RegistroHoras> registrosHoy = registroHorasService.buscarDeHoyPorHorarios(
                horariosHoy.stream().map(Horario::getId).toList());
        Map<String, RegistroHorasDTO> registrosDTO = new LinkedHashMap<>();
        registrosHoy.forEach((id, r) -> registrosDTO.put(String.valueOf(id), RegistroHorasDTO.desde(r)));

        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("ciclo", cicloActivo != null ? cicloActivo.getNombre() : null);
        resultado.put("diaHoy", diaHoy != null ? diaHoy.name() : null);
        resultado.put("turnoSel", turnoSel.name());
        resultado.put("horarios", horariosHoy.stream().map(HorarioAsignacionDTO::desde).toList());
        resultado.put("registrosHoy", registrosDTO);
        return resultado;
    }

    @PostMapping("/docentes/marcar")
    public void docentesMarcar(@RequestParam Long horarioId, Authentication auth) {
        String username = (auth != null) ? auth.getName() : null;
        registroHorasService.marcarLlegada(horarioId, LocalDate.now(), username);
    }

    private Turno turnoActual() {
        LocalTime ahora = LocalTime.now();
        if (ahora.isBefore(LocalTime.of(13, 0))) return Turno.MANANA;
        if (ahora.isBefore(LocalTime.of(19, 0))) return Turno.TARDE;
        return Turno.NOCHE;
    }

    @GetMapping("/cursos")
    public Map<String, Object> cursosLista(@RequestParam(required = false) Long cicloId,
                                           @RequestParam(required = false) Turno turno,
                                           @RequestParam(required = false) DiaSemana dia) {
        Ciclo cicloSel = (cicloId != null) ? cicloService.buscarPorId(cicloId) : cicloService.obtenerActivo();
        Turno turnoSel = (turno != null) ? turno : Turno.MANANA;
        DiaSemana diaSel = (dia != null) ? dia : DiaSemana.desde(LocalDate.now().getDayOfWeek());

        List<Horario> horarios = (cicloSel != null)
                ? horarioService.listarPorCicloTurnoDia(cicloSel.getId(), turnoSel, diaSel)
                : List.of();

        Map<String, Long> conteos = new LinkedHashMap<>();
        for (Horario h : horarios) {
            conteos.put(String.valueOf(h.getId()), asistenciaService.contarDeHoy(h.getId()));
        }

        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("cicloSel", cicloSel != null ? cicloSel.getId() : null);
        resultado.put("turnoSel", turnoSel.name());
        resultado.put("diaSel", diaSel != null ? diaSel.name() : null);
        resultado.put("horarios", horarios.stream().map(HorarioAsignacionDTO::desde).toList());
        resultado.put("conteos", conteos);
        return resultado;
    }

    @GetMapping("/cursos/{horarioId}")
    public Map<String, Object> cursosEscanear(@PathVariable Long horarioId) {
        Horario horario = horarioService.buscarPorId(horarioId);
        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("horario", HorarioAsignacionDTO.desde(horario));
        resultado.put("registrosHoy", asistenciaService.listarDeHoy(horarioId).stream().map(AsistenciaDTO::desde).toList());
        return resultado;
    }

    @PostMapping("/cursos/registrar")
    public AsistenciaResultadoDTO cursosRegistrar(@RequestBody RegistroAsistenciaRequest body, Authentication auth) {
        String username = (auth != null) ? auth.getName() : null;
        return asistenciaService.registrar(body.horarioId(), body.codigo(), username);
    }
}
