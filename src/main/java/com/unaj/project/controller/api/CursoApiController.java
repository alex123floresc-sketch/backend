package com.unaj.project.controller.api;

import com.unaj.project.dto.CursoForm;
import com.unaj.project.dto.api.CursoDTO;
import com.unaj.project.model.Areas;
import com.unaj.project.model.Curso;
import com.unaj.project.model.Nivel;
import com.unaj.project.service.CursoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * API REST del módulo Cursos, consumida por el frontend Angular.
 * Reutiliza el mismo CursoService que ya usa el controlador Thymeleaf: la lógica de
 * negocio no se duplica, solo cambia la capa de presentación (JSON en vez de vistas).
 */
@RestController
@RequestMapping("/api/cursos")
public class CursoApiController {

    private final CursoService cursoService;

    public CursoApiController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    /** Listado paginado con filtros opcionales por texto, nivel y área. */
    @GetMapping
    public Map<String, Object> listar(@RequestParam(required = false) String q,
                                      @RequestParam(required = false) Nivel nivel,
                                      @RequestParam(required = false) String area,
                                      @PageableDefault(size = 15) Pageable pageable) {
        Page<Curso> pagina = cursoService.buscarPagina(q, nivel, area, pageable);
        List<CursoDTO> contenido = pagina.getContent().stream().map(CursoDTO::desde).toList();
        return Map.of(
                "contenido", contenido,
                "paginaActual", pagina.getNumber(),
                "totalPaginas", pagina.getTotalPages(),
                "totalElementos", pagina.getTotalElements(),
                "tamanio", pagina.getSize()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CursoDTO> obtener(@PathVariable Long id) {
        Curso curso = cursoService.buscarPorId(id);
        return ResponseEntity.ok(CursoDTO.desde(curso));
    }

    @PostMapping
    public ResponseEntity<CursoDTO> crear(@Valid @RequestBody CursoForm form) {
        form.setId(null);
        cursoService.guardar(form);
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable Long id, @Valid @RequestBody CursoForm form) {
        form.setId(id);
        cursoService.guardar(form);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        cursoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /** Catálogo de niveles (para poblar selectores en Angular). */
    @GetMapping("/niveles")
    public List<Map<String, String>> niveles() {
        return Arrays.stream(Nivel.values())
                .map(n -> Map.of("valor", n.name(), "etiqueta", n.getEtiqueta()))
                .toList();
    }

    /** Áreas/grados válidos para un nivel dado. */
    @GetMapping("/areas")
    public List<String> areas(@RequestParam Nivel nivel) {
        return Areas.paraNivel(nivel);
    }
}
