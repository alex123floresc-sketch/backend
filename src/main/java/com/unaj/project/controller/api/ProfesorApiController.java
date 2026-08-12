package com.unaj.project.controller.api;

import com.unaj.project.dto.ProfesorForm;
import com.unaj.project.dto.api.ProfesorDTO;
import com.unaj.project.dto.api.ProfesorRequest;
import com.unaj.project.model.Curso;
import com.unaj.project.model.Horario;
import com.unaj.project.model.PagoProfesor;
import com.unaj.project.model.Profesor;
import com.unaj.project.model.RegistroHoras;
import com.unaj.project.repository.CursoRepository;
import com.unaj.project.repository.HorarioRepository;
import com.unaj.project.service.PagoProfesorService;
import com.unaj.project.service.ProfesorService;
import com.unaj.project.service.RegistroHorasService;
import com.unaj.project.util.PeriodoUtil;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profesores")
public class ProfesorApiController {

    private final ProfesorService profesorService;
    private final CursoRepository cursoRepository;
    private final HorarioRepository horarioRepository;
    private final RegistroHorasService registroHorasService;
    private final PagoProfesorService pagoProfesorService;

    public ProfesorApiController(ProfesorService profesorService, CursoRepository cursoRepository,
                                 HorarioRepository horarioRepository, RegistroHorasService registroHorasService,
                                 PagoProfesorService pagoProfesorService) {
        this.profesorService = profesorService;
        this.cursoRepository = cursoRepository;
        this.horarioRepository = horarioRepository;
        this.registroHorasService = registroHorasService;
        this.pagoProfesorService = pagoProfesorService;
    }

    @GetMapping
    public Map<String, Object> listar(@RequestParam(required = false) String q,
                                      @PageableDefault(size = 15) Pageable pageable) {
        Page<Profesor> pagina = profesorService.buscarPagina(q, pageable);
        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("contenido", pagina.getContent().stream().map(ProfesorDTO::desde).toList());
        resultado.put("paginaActual", pagina.getNumber());
        resultado.put("totalPaginas", pagina.getTotalPages());
        resultado.put("totalElementos", pagina.getTotalElements());
        resultado.put("tamanio", pagina.getSize());
        return resultado;
    }

    @GetMapping("/{id}")
    public Map<String, Object> detalle(@PathVariable Long id) {
        Profesor profesor = profesorService.buscarPorId(id);
        List<Curso> cursos = cursoRepository.findByProfesorIdAndEliminadoFalse(id);
        List<Horario> horarios = horarioRepository.findByCursoProfesorId(id).stream()
                .sorted(Comparator.comparing(Horario::getDiaSemana)
                        .thenComparing(Horario::getTurno)
                        .thenComparing(Horario::getHoraInicio))
                .toList();

        PeriodoUtil.Rango rangoQuincena = PeriodoUtil.quincenaDe(LocalDate.now());
        List<RegistroHoras> registrosQuincena = registroHorasService.listarPorProfesorEnRango(
                id, rangoQuincena.inicio(), rangoQuincena.fin());
        BigDecimal horasQuincena = registrosQuincena.stream()
                .map(r -> r.getHoras() != null ? r.getHoras() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal tarifa = profesor.getTarifaHora() != null ? profesor.getTarifaHora() : BigDecimal.ZERO;
        BigDecimal montoEsperadoQuincena = horasQuincena.multiply(tarifa).setScale(2, RoundingMode.HALF_UP);

        List<PagoProfesor> pagos = pagoProfesorService.listarPorProfesor(id);
        BigDecimal montoPagadoQuincena = pagos.stream()
                .filter(p -> "QUINCENAL".equals(p.getTipoPeriodo())
                        && p.getPeriodoInicio().isEqual(rangoQuincena.inicio())
                        && p.getPeriodoFin().isEqual(rangoQuincena.fin()))
                .map(PagoProfesor::getMonto).reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("profesor", ProfesorDTO.desde(profesor));
        resultado.put("cursos", cursos.stream().map(c -> Map.of("id", c.getId(), "nombre", c.getNombre())).toList());
        resultado.put("totalHorarios", horarios.size());
        resultado.put("rangoInicio", rangoQuincena.inicio());
        resultado.put("rangoFin", rangoQuincena.fin());
        resultado.put("horasQuincena", horasQuincena);
        resultado.put("montoEsperadoQuincena", montoEsperadoQuincena);
        resultado.put("montoPagadoQuincena", montoPagadoQuincena);
        resultado.put("pendienteQuincena", montoEsperadoQuincena.subtract(montoPagadoQuincena));
        resultado.put("ultimosPagos", pagos.stream().limit(5).toList());
        return resultado;
    }

    @PostMapping
    public ResponseEntity<ProfesorDTO> crear(@Valid @RequestBody ProfesorRequest req) {
        ProfesorForm form = aForm(req);
        form.setId(null);
        profesorService.guardar(form);
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable Long id, @Valid @RequestBody ProfesorRequest req) {
        ProfesorForm form = aForm(req);
        form.setId(id);
        profesorService.guardar(form);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        profesorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> subirFoto(@PathVariable Long id, @RequestParam("foto") MultipartFile foto) {
        ProfesorForm form = profesorService.buscarFormPorId(id);
        form.setFoto(foto);
        profesorService.guardar(form);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{id}/foto", produces = { MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE })
    public ResponseEntity<byte[]> foto(@PathVariable Long id) {
        Profesor profesor = profesorService.buscarPorId(id);
        if (!profesor.isFotoPresente()) {
            return ResponseEntity.notFound().build();
        }
        MediaType tipo = (profesor.getFotoContentType() != null)
                ? MediaType.parseMediaType(profesor.getFotoContentType())
                : MediaType.IMAGE_JPEG;
        return ResponseEntity.ok().contentType(tipo).body(profesor.getFoto());
    }

    private ProfesorForm aForm(ProfesorRequest req) {
        ProfesorForm form = new ProfesorForm();
        form.setNombre(req.getNombre());
        form.setApellido(req.getApellido());
        form.setEmail(req.getEmail());
        form.setEspecialidad(req.getEspecialidad());
        form.setTarifaHora(req.getTarifaHora());
        form.setNiveles(req.getNiveles());
        form.setDestacadoWeb(req.isDestacadoWeb());
        return form;
    }
}
