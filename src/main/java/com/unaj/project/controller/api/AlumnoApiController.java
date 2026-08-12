package com.unaj.project.controller.api;

import com.unaj.project.dto.AlumnoForm;
import com.unaj.project.dto.api.AlumnoDTO;
import com.unaj.project.dto.api.AlumnoRequest;
import com.unaj.project.dto.api.MatriculaDTO;
import com.unaj.project.dto.api.MatricularRequest;
import com.unaj.project.dto.api.PagoDTO;
import com.unaj.project.model.Alumno;
import com.unaj.project.model.Areas;
import com.unaj.project.model.Matricula;
import com.unaj.project.model.Nivel;
import com.unaj.project.model.Pago;
import com.unaj.project.repository.MatriculaRepository;
import com.unaj.project.repository.PagoRepository;
import com.unaj.project.service.AlumnoService;
import com.unaj.project.service.MatriculaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * API REST del módulo Alumnos, consumida por el frontend Angular.
 * Reutiliza AlumnoService/MatriculaService (misma lógica de negocio que el
 * controlador Thymeleaf), solo cambia la capa de presentación.
 */
@RestController
@RequestMapping("/api/alumnos")
public class AlumnoApiController {

    private final AlumnoService alumnoService;
    private final PagoRepository pagoRepository;
    private final MatriculaRepository matriculaRepository;
    private final MatriculaService matriculaService;

    public AlumnoApiController(AlumnoService alumnoService, PagoRepository pagoRepository,
                               MatriculaRepository matriculaRepository, MatriculaService matriculaService) {
        this.alumnoService = alumnoService;
        this.pagoRepository = pagoRepository;
        this.matriculaRepository = matriculaRepository;
        this.matriculaService = matriculaService;
    }

    @GetMapping
    public Map<String, Object> listar(@RequestParam(required = false) String q,
                                      @RequestParam(required = false) Nivel nivel,
                                      @RequestParam(required = false) String area,
                                      @PageableDefault(size = 15, sort = "apellido") Pageable pageable) {
        Nivel nivelSel = (nivel != null) ? nivel : Nivel.PREUNIVERSITARIO;
        List<String> areasDelNivel = Areas.paraNivel(nivelSel);
        String areaSel = (area != null && areasDelNivel.contains(area)) ? area : null;

        Map<Long, Long> deudaConteo = new LinkedHashMap<>();
        for (Object[] fila : pagoRepository.contarDeudaPorAlumno()) {
            deudaConteo.put((Long) fila[0], (Long) fila[1]);
        }

        Page<Alumno> pagina = alumnoService.buscarPagina(q, nivelSel, areaSel, pageable);
        List<AlumnoDTO> contenido = pagina.getContent().stream().map(AlumnoDTO::desde).toList();
        Map<Long, Boolean> deuda = new LinkedHashMap<>();
        for (Alumno a : pagina.getContent()) {
            deuda.put(a.getId(), deudaConteo.getOrDefault(a.getId(), 0L) > 0);
        }

        long totalAlumnos = alumnoService.listarTodos().stream().filter(a -> a.getNivel() == nivelSel).count();
        long totalConDeuda = alumnoService.listarTodos().stream()
                .filter(a -> a.getNivel() == nivelSel)
                .filter(a -> deudaConteo.getOrDefault(a.getId(), 0L) > 0)
                .count();

        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("contenido", contenido);
        resultado.put("paginaActual", pagina.getNumber());
        resultado.put("totalPaginas", pagina.getTotalPages());
        resultado.put("totalElementos", pagina.getTotalElements());
        resultado.put("tamanio", pagina.getSize());
        resultado.put("deuda", deuda);
        resultado.put("totalAlumnos", totalAlumnos);
        resultado.put("totalConDeuda", totalConDeuda);
        resultado.put("totalAlDia", totalAlumnos - totalConDeuda);
        return resultado;
    }

    @GetMapping("/{id}")
    public AlumnoDTO obtener(@PathVariable Long id) {
        return AlumnoDTO.desde(alumnoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<AlumnoDTO> crear(@Valid @RequestBody AlumnoRequest req) {
        AlumnoForm form = aForm(req);
        form.setId(null);
        Alumno alumno = alumnoService.guardar(form);

        MatricularRequest m = req.getMatriculaInicial();
        if (m != null) {
            matriculaService.matricular(alumno.getId(), m.getCicloId(), m.getTurno(), req.getArea(),
                    m.getConceptoMatricula(), m.getMontoMatricula(), m.getConceptoPension(),
                    m.getMontoPension(), m.getNumeroCuotas());
        }
        return ResponseEntity.status(201).body(AlumnoDTO.desde(alumno));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlumnoDTO> actualizar(@PathVariable Long id, @Valid @RequestBody AlumnoRequest req) {
        AlumnoForm form = aForm(req);
        form.setId(id);
        Alumno alumno = alumnoService.guardar(form);
        return ResponseEntity.ok(AlumnoDTO.desde(alumno));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        alumnoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AlumnoDTO> subirFoto(@PathVariable Long id, @RequestParam("foto") MultipartFile foto) {
        AlumnoForm form = alumnoService.buscarFormPorId(id);
        form.setFoto(foto);
        Alumno alumno = alumnoService.guardar(form);
        return ResponseEntity.ok(AlumnoDTO.desde(alumno));
    }

    @GetMapping(value = "/{id}/foto", produces = { MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE })
    public ResponseEntity<byte[]> foto(@PathVariable Long id) {
        Alumno alumno = alumnoService.buscarPorId(id);
        if (!alumno.isFotoPresente()) {
            return ResponseEntity.notFound().build();
        }
        MediaType tipo = (alumno.getFotoContentType() != null)
                ? MediaType.parseMediaType(alumno.getFotoContentType())
                : MediaType.IMAGE_JPEG;
        return ResponseEntity.ok().contentType(tipo).body(alumno.getFoto());
    }

    @GetMapping("/niveles")
    public List<Map<String, String>> niveles() {
        return Arrays.stream(Nivel.values())
                .map(n -> Map.of("valor", n.name(), "etiqueta", n.getEtiqueta()))
                .toList();
    }

    @GetMapping("/areas")
    public List<String> areas(@RequestParam Nivel nivel) {
        return Areas.paraNivel(nivel);
    }

    @PostMapping("/{id}/matricular")
    public ResponseEntity<MatriculaDTO> matricular(@PathVariable Long id, @Valid @RequestBody MatricularRequest req) {
        Matricula matricula = matriculaService.matricular(id, req.getCicloId(), req.getTurno(), req.getArea(),
                req.getConceptoMatricula(), req.getMontoMatricula(), req.getConceptoPension(),
                req.getMontoPension(), req.getNumeroCuotas());
        return ResponseEntity.status(201).body(MatriculaDTO.desde(matricula));
    }

    @GetMapping("/{id}/expediente")
    public Map<String, Object> expediente(@PathVariable Long id) {
        Alumno alumno = alumnoService.buscarPorId(id);
        List<Matricula> matriculas = matriculaRepository.findByEstudianteIdConDetalle(id);

        List<Map<String, Object>> matriculasConPagos = matriculas.stream().map(m -> {
            List<Pago> pagos = pagoRepository.findByMatriculaId(m.getId());
            Map<String, Object> fila = new LinkedHashMap<>();
            fila.put("matricula", MatriculaDTO.desde(m));
            fila.put("pagos", pagos.stream().map(PagoDTO::desde).toList());
            return fila;
        }).toList();

        BigDecimal totalPagado = BigDecimal.ZERO;
        BigDecimal totalPendiente = BigDecimal.ZERO;
        long matriculasActivas = 0;
        for (Matricula m : matriculas) {
            for (Pago p : pagoRepository.findByMatriculaId(m.getId())) {
                totalPagado = totalPagado.add(p.getMontoPagado());
                totalPendiente = totalPendiente.add(p.getSaldo());
            }
            if ("ACTIVA".equals(m.getEstado())) {
                matriculasActivas++;
            }
        }

        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("alumno", AlumnoDTO.desde(alumno));
        resultado.put("matriculas", matriculasConPagos);
        resultado.put("totalPagado", totalPagado);
        resultado.put("totalPendiente", totalPendiente);
        resultado.put("matriculasActivas", matriculasActivas);
        return resultado;
    }

    private AlumnoForm aForm(AlumnoRequest req) {
        AlumnoForm form = new AlumnoForm();
        form.setNombre(req.getNombre());
        form.setApellido(req.getApellido());
        form.setEmail(req.getEmail());
        form.setCelular(req.getCelular());
        form.setDni(req.getDni());
        form.setNombrePadre(req.getNombrePadre());
        form.setTelefonoPadre(req.getTelefonoPadre());
        form.setArea(req.getArea());
        form.setNivel(req.getNivel());
        return form;
    }
}
