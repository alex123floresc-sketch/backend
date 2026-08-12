package com.unaj.project.controller.api;

import com.unaj.project.dto.api.RegistroActividadDTO;
import com.unaj.project.model.RegistroActividad;
import com.unaj.project.model.TipoAccion;
import com.unaj.project.service.RegistroActividadService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/actividad")
public class RegistroActividadApiController {

    private final RegistroActividadService registroActividadService;

    public RegistroActividadApiController(RegistroActividadService registroActividadService) {
        this.registroActividadService = registroActividadService;
    }

    @GetMapping
    public Map<String, Object> listar(@RequestParam(required = false) String q,
                                      @RequestParam(required = false) String username,
                                      @RequestParam(required = false) String modulo,
                                      @RequestParam(required = false) TipoAccion accion,
                                      @PageableDefault(size = 20) Pageable pageable) {
        Page<RegistroActividad> pagina = registroActividadService.buscarPagina(username, modulo, accion, q, pageable);

        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
        LocalDateTime inicioSemana = LocalDate.now().minusDays(6).atStartOfDay();

        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("contenido", pagina.getContent().stream().map(RegistroActividadDTO::desde).toList());
        resultado.put("paginaActual", pagina.getNumber());
        resultado.put("totalPaginas", pagina.getTotalPages());
        resultado.put("totalElementos", pagina.getTotalElements());
        resultado.put("tamanio", pagina.getSize());
        resultado.put("totalHoy", registroActividadService.contarDesde(inicioHoy));
        resultado.put("totalSemana", registroActividadService.contarDesde(inicioSemana));
        return resultado;
    }

    @GetMapping("/modulos")
    public List<String> modulos() {
        return registroActividadService.modulos();
    }

    @GetMapping("/acciones")
    public List<Map<String, String>> acciones() {
        return java.util.Arrays.stream(TipoAccion.values())
                .map(a -> Map.of("valor", a.name(), "etiqueta", a.getEtiqueta()))
                .toList();
    }
}
