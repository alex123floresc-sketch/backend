package com.unaj.project.controller.api;

import com.unaj.project.dto.api.MatriculaDTO;
import com.unaj.project.dto.api.PagoDTO;
import com.unaj.project.model.Matricula;
import com.unaj.project.service.MatriculaService;
import com.unaj.project.service.PagoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/matriculas")
public class MatriculaApiController {

    private final MatriculaService matriculaService;
    private final PagoService pagoService;

    public MatriculaApiController(MatriculaService matriculaService, PagoService pagoService) {
        this.matriculaService = matriculaService;
        this.pagoService = pagoService;
    }

    @GetMapping("/{id}/ficha")
    public ResponseEntity<Map<String, Object>> ficha(@PathVariable Long id) {
        Matricula matricula = matriculaService.buscarFichaPorId(id);
        if (matricula == null) {
            return ResponseEntity.notFound().build();
        }
        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("matricula", MatriculaDTO.desde(matricula));
        resultado.put("pagos", pagoService.listarPorMatricula(id).stream().map(PagoDTO::desde).toList());
        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/{id}/anular")
    public ResponseEntity<Void> anular(@PathVariable Long id) {
        matriculaService.anular(id);
        return ResponseEntity.noContent().build();
    }
}
