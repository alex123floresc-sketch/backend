package com.unaj.project.controller.api;

import com.unaj.project.dto.api.HorarioAsignacionDTO;
import com.unaj.project.dto.api.PagoProfesorDTO;
import com.unaj.project.dto.api.RegistrarHorasRequest;
import com.unaj.project.dto.api.RegistrarPagoProfesorRequest;
import com.unaj.project.dto.api.RegistroHorasDTO;
import com.unaj.project.model.Horario;
import com.unaj.project.model.PagoProfesor;
import com.unaj.project.model.Profesor;
import com.unaj.project.model.RegistroHoras;
import com.unaj.project.repository.HorarioRepository;
import com.unaj.project.service.PagoProfesorService;
import com.unaj.project.service.ProfesorService;
import com.unaj.project.service.RegistroHorasService;
import com.unaj.project.util.PeriodoUtil;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/horas-docentes")
public class HorasDocenteApiController {

    private final ProfesorService profesorService;
    private final HorarioRepository horarioRepository;
    private final RegistroHorasService registroHorasService;
    private final PagoProfesorService pagoProfesorService;

    public HorasDocenteApiController(ProfesorService profesorService, HorarioRepository horarioRepository,
                                     RegistroHorasService registroHorasService, PagoProfesorService pagoProfesorService) {
        this.profesorService = profesorService;
        this.horarioRepository = horarioRepository;
        this.registroHorasService = registroHorasService;
        this.pagoProfesorService = pagoProfesorService;
    }

    @GetMapping("/{profesorId}")
    public Map<String, Object> ver(@PathVariable Long profesorId,
                                   @RequestParam(required = false) LocalDate semana,
                                   @RequestParam(required = false) LocalDate quincena) {
        Profesor profesor = profesorService.buscarPorId(profesorId);
        List<Horario> horarios = horarioRepository.findByCursoProfesorId(profesorId).stream()
                .sorted(Comparator.comparing(Horario::getDiaSemana)
                        .thenComparing(Horario::getTurno)
                        .thenComparing(Horario::getHoraInicio))
                .toList();

        PeriodoUtil.Rango rangoSemana = PeriodoUtil.semanaDe(semana != null ? semana : LocalDate.now());
        PeriodoUtil.Rango rangoQuincena = PeriodoUtil.quincenaDe(quincena != null ? quincena : LocalDate.now());

        List<RegistroHoras> registrosSemana = registroHorasService.listarPorProfesorEnRango(
                profesorId, rangoSemana.inicio(), rangoSemana.fin());
        List<RegistroHoras> registrosQuincena = registroHorasService.listarPorProfesorEnRango(
                profesorId, rangoQuincena.inicio(), rangoQuincena.fin());
        List<PagoProfesor> pagos = pagoProfesorService.listarPorProfesor(profesorId);
        Map<Long, RegistroHoras> registrosHoyPorHorario = registroHorasService.buscarDeHoyPorHorarios(
                horarios.stream().map(Horario::getId).toList());

        BigDecimal horasSemana = sumarHoras(registrosSemana);
        BigDecimal horasQuincena = sumarHoras(registrosQuincena);
        BigDecimal tarifa = profesor.getTarifaHora() != null ? profesor.getTarifaHora() : BigDecimal.ZERO;
        BigDecimal montoEsperadoSemana = horasSemana.multiply(tarifa).setScale(2, RoundingMode.HALF_UP);
        BigDecimal montoEsperadoQuincena = horasQuincena.multiply(tarifa).setScale(2, RoundingMode.HALF_UP);
        BigDecimal montoPagadoSemana = sumarPagosDelPeriodo(pagos, "SEMANAL", rangoSemana);
        BigDecimal montoPagadoQuincena = sumarPagosDelPeriodo(pagos, "QUINCENAL", rangoQuincena);

        Map<String, RegistroHorasDTO> registrosHoyDTO = new LinkedHashMap<>();
        registrosHoyPorHorario.forEach((id, r) -> registrosHoyDTO.put(String.valueOf(id), RegistroHorasDTO.desde(r)));

        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("horarios", horarios.stream().map(HorarioAsignacionDTO::desde).toList());
        resultado.put("registrosHoyPorHorario", registrosHoyDTO);
        resultado.put("rangoSemana", rangoSemana);
        resultado.put("rangoQuincena", rangoQuincena);
        resultado.put("registrosSemana", registrosSemana.stream().map(RegistroHorasDTO::desde).toList());
        resultado.put("registrosQuincena", registrosQuincena.stream().map(RegistroHorasDTO::desde).toList());
        resultado.put("horasSemana", horasSemana);
        resultado.put("horasQuincena", horasQuincena);
        resultado.put("montoEsperadoSemana", montoEsperadoSemana);
        resultado.put("montoEsperadoQuincena", montoEsperadoQuincena);
        resultado.put("montoPagadoSemana", montoPagadoSemana);
        resultado.put("montoPagadoQuincena", montoPagadoQuincena);
        resultado.put("pendienteSemana", montoEsperadoSemana.subtract(montoPagadoSemana));
        resultado.put("pendienteQuincena", montoEsperadoQuincena.subtract(montoPagadoQuincena));
        resultado.put("pagos", pagos.stream().map(PagoProfesorDTO::desde).toList());
        return resultado;
    }

    @PostMapping("/llegada")
    public void marcarLlegada(@RequestParam Long horarioId, Authentication auth) {
        String username = (auth != null) ? auth.getName() : null;
        registroHorasService.marcarLlegada(horarioId, LocalDate.now(), username);
    }

    @PostMapping("/registrar")
    public RegistroHorasDTO registrar(@Valid @RequestBody RegistrarHorasRequest req, Authentication auth) {
        String username = (auth != null) ? auth.getName() : null;
        RegistroHoras registro = registroHorasService.registrarHoras(req.getHorarioId(), req.getFecha(),
                req.getHoraInicio(), req.getHoraFin(), req.getObservaciones(), username);
        return RegistroHorasDTO.desde(registro);
    }

    @PostMapping("/pagos")
    public PagoProfesorDTO registrarPago(@Valid @RequestBody RegistrarPagoProfesorRequest req, Authentication auth) {
        String username = (auth != null) ? auth.getName() : null;
        PagoProfesor pago = pagoProfesorService.registrar(req.getProfesorId(), req.getTipoPeriodo(),
                req.getPeriodoInicio(), req.getPeriodoFin(), req.getHorasPagadas(), req.getMonto(),
                req.getFechaPago(), req.getMetodo(), req.getObservaciones(), username);
        return PagoProfesorDTO.desde(pago);
    }

    private BigDecimal sumarHoras(List<RegistroHoras> registros) {
        return registros.stream()
                .map(r -> r.getHoras() != null ? r.getHoras() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumarPagosDelPeriodo(List<PagoProfesor> pagos, String tipo, PeriodoUtil.Rango rango) {
        return pagos.stream()
                .filter(p -> tipo.equals(p.getTipoPeriodo())
                        && p.getPeriodoInicio().isEqual(rango.inicio())
                        && p.getPeriodoFin().isEqual(rango.fin()))
                .map(PagoProfesor::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
