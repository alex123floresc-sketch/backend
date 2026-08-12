package com.unaj.project.controller.api;

import com.unaj.project.dto.api.AlumnoDTO;
import com.unaj.project.dto.api.CursoDTO;
import com.unaj.project.dto.api.ProfesorDTO;
import com.unaj.project.service.AlumnoService;
import com.unaj.project.service.CursoService;
import com.unaj.project.service.ProfesorService;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/busqueda")
public class BusquedaApiController {

    private static final int LIMITE_POR_CATEGORIA = 8;

    private final AlumnoService alumnoService;
    private final ProfesorService profesorService;
    private final CursoService cursoService;

    public BusquedaApiController(AlumnoService alumnoService, ProfesorService profesorService, CursoService cursoService) {
        this.alumnoService = alumnoService;
        this.profesorService = profesorService;
        this.cursoService = cursoService;
    }

    @GetMapping
    public Map<String, Object> buscar(@RequestParam(required = false) String q, Authentication auth) {
        Map<String, Object> resultado = new LinkedHashMap<>();
        boolean puedeVerGestion = tieneAlgunRol(auth, "ROLE_ADMIN", "ROLE_CAJERO");

        if (q == null || q.isBlank()) {
            resultado.put("alumnos", List.of());
            resultado.put("profesores", List.of());
            resultado.put("cursos", List.of());
            return resultado;
        }

        resultado.put("alumnos", alumnoService.buscarPagina(q, PageRequest.of(0, LIMITE_POR_CATEGORIA))
                .getContent().stream().map(AlumnoDTO::desde).toList());
        if (puedeVerGestion) {
            resultado.put("profesores", profesorService.buscarPagina(q, PageRequest.of(0, LIMITE_POR_CATEGORIA))
                    .getContent().stream().map(ProfesorDTO::desde).toList());
            resultado.put("cursos", cursoService.buscarPagina(q, null, null, PageRequest.of(0, LIMITE_POR_CATEGORIA))
                    .getContent().stream().map(CursoDTO::desde).toList());
        } else {
            resultado.put("profesores", List.of());
            resultado.put("cursos", List.of());
        }
        return resultado;
    }

    private boolean tieneAlgunRol(Authentication auth, String... roles) {
        if (auth == null) return false;
        List<String> buscados = List.of(roles);
        for (GrantedAuthority ga : auth.getAuthorities()) {
            if (buscados.contains(ga.getAuthority())) return true;
        }
        return false;
    }
}
