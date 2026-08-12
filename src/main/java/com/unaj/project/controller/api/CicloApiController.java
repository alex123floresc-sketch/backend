package com.unaj.project.controller.api;

import com.unaj.project.dto.CicloForm;
import com.unaj.project.dto.api.CicloDTO;
import com.unaj.project.model.Ciclo;
import com.unaj.project.service.CicloService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ciclos")
public class CicloApiController {

    private final CicloService cicloService;

    public CicloApiController(CicloService cicloService) {
        this.cicloService = cicloService;
    }

    @GetMapping
    public Map<String, Object> listar(@RequestParam(required = false) String q,
                                      @PageableDefault(size = 15) Pageable pageable) {
        Page<Ciclo> pagina = cicloService.buscarPagina(q, pageable);
        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("contenido", pagina.getContent().stream().map(CicloDTO::desde).toList());
        resultado.put("paginaActual", pagina.getNumber());
        resultado.put("totalPaginas", pagina.getTotalPages());
        resultado.put("totalElementos", pagina.getTotalElements());
        resultado.put("tamanio", pagina.getSize());
        return resultado;
    }

    @GetMapping("/todos")
    public List<CicloDTO> todos() {
        return cicloService.listarTodos().stream().map(CicloDTO::desde).toList();
    }

    @GetMapping("/activo")
    public ResponseEntity<CicloDTO> activo() {
        Ciclo ciclo = cicloService.obtenerActivo();
        return ciclo != null ? ResponseEntity.ok(CicloDTO.desde(ciclo)) : ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public CicloDTO obtener(@PathVariable Long id) {
        return CicloDTO.desde(cicloService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Void> crear(@Valid @RequestBody CicloForm form) {
        form.setId(null);
        validarFechas(form);
        cicloService.guardar(form);
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable Long id, @Valid @RequestBody CicloForm form) {
        form.setId(id);
        validarFechas(form);
        cicloService.guardar(form);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        cicloService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private void validarFechas(CicloForm form) {
        if (form.getFechaInicio() != null && form.getFechaFin() != null
                && !form.getFechaFin().isAfter(form.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la de inicio.");
        }
    }
}
