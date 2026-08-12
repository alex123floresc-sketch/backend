package com.unaj.project.controller.api;

import com.unaj.project.dto.api.AbonoDTO;
import com.unaj.project.dto.api.AbonoRequest;
import com.unaj.project.dto.api.AlumnoDTO;
import com.unaj.project.dto.api.NuevaCuotaRequest;
import com.unaj.project.dto.api.PagoDTO;
import com.unaj.project.model.Alumno;
import com.unaj.project.model.Pago;
import com.unaj.project.service.MatriculaService;
import com.unaj.project.service.PagoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pagos")
public class PagoApiController {

    private final PagoService pagoService;
    private final MatriculaService matriculaService;

    public PagoApiController(PagoService pagoService, MatriculaService matriculaService) {
        this.pagoService = pagoService;
        this.matriculaService = matriculaService;
    }

    @GetMapping
    public Map<String, Object> listar(@RequestParam(required = false) String q,
                                      @PageableDefault(size = 10) Pageable pageable) {
        Page<Alumno> pagina = pagoService.buscarAlumnosConPagos(q, pageable);
        List<Long> alumnoIds = pagina.getContent().stream().map(Alumno::getId).collect(Collectors.toList());
        Map<Long, List<Pago>> pagosPorAlumno = pagoService.agruparPorAlumno(alumnoIds);

        List<Map<String, Object>> contenido = pagina.getContent().stream().map(a -> {
            Map<String, Object> fila = new LinkedHashMap<>();
            fila.put("alumno", AlumnoDTO.desde(a));
            fila.put("pagos", pagosPorAlumno.getOrDefault(a.getId(), List.of()).stream().map(PagoDTO::desde).toList());
            return fila;
        }).toList();

        List<Pago> todos = pagoService.listarTodos();
        BigDecimal porCobrar = pagoService.totalSaldo(todos, "PENDIENTE").add(pagoService.totalSaldo(todos, "PARCIAL"));

        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("contenido", contenido);
        resultado.put("paginaActual", pagina.getNumber());
        resultado.put("totalPaginas", pagina.getTotalPages());
        resultado.put("totalElementos", pagina.getTotalElements());
        resultado.put("tamanio", pagina.getSize());
        resultado.put("cobrado", pagoService.totalCobrado(todos));
        resultado.put("porCobrar", porCobrar);
        resultado.put("vencido", pagoService.totalSaldo(todos, "VENCIDO"));
        return resultado;
    }

    @GetMapping("/{id}")
    public Map<String, Object> detalle(@PathVariable Long id) {
        Pago pago = pagoService.buscarPorId(id);
        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("pago", PagoDTO.desde(pago));
        resultado.put("abonos", pagoService.listarAbonos(id).stream().map(AbonoDTO::desde).toList());
        return resultado;
    }

    @PostMapping("/{id}/abonos")
    public ResponseEntity<PagoDTO> registrarAbono(@PathVariable Long id, @Valid @RequestBody AbonoRequest req,
                                                   Authentication auth) {
        String username = (auth != null) ? auth.getName() : null;
        Pago pago = pagoService.registrarAbono(id, req.getMonto(), req.getMetodo(), username);
        return ResponseEntity.ok(PagoDTO.desde(pago));
    }

    @PostMapping("/cuotas")
    public ResponseEntity<PagoDTO> nuevaCuota(@Valid @RequestBody NuevaCuotaRequest req) {
        Pago pago = matriculaService.agregarCuota(req.getMatriculaId(), req.getConcepto(),
                req.getMonto(), req.getFechaVencimiento());
        return ResponseEntity.status(201).body(PagoDTO.desde(pago));
    }
}
