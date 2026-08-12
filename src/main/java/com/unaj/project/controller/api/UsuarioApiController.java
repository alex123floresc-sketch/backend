package com.unaj.project.controller.api;

import com.unaj.project.dto.UsuarioForm;
import com.unaj.project.dto.api.UsuarioDTO;
import com.unaj.project.dto.api.UsuarioRequest;
import com.unaj.project.model.Rol;
import com.unaj.project.model.Usuario;
import com.unaj.project.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioApiController {

    private static final String USERNAME_DESARROLLADOR = "desarrollador";

    private final UsuarioService usuarioService;

    public UsuarioApiController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public Map<String, Object> listar(@RequestParam(required = false) String q,
                                      @PageableDefault(size = 15) Pageable pageable,
                                      Authentication auth) {
        Page<Usuario> pagina = usuarioService.buscarPagina(q, auth.getName(), pageable);
        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("contenido", pagina.getContent().stream().map(UsuarioDTO::desde).toList());
        resultado.put("paginaActual", pagina.getNumber());
        resultado.put("totalPaginas", pagina.getTotalPages());
        resultado.put("totalElementos", pagina.getTotalElements());
        resultado.put("tamanio", pagina.getSize());
        resultado.put("esDesarrollador", USERNAME_DESARROLLADOR.equals(auth.getName()));
        return resultado;
    }

    @GetMapping("/{id}")
    public UsuarioDTO obtener(@PathVariable Long id, Authentication auth) {
        return UsuarioDTO.desde(usuarioService.buscarVisiblePorId(id, auth.getName()));
    }

    @GetMapping("/roles")
    public List<Map<String, Object>> roles(Authentication auth) {
        return usuarioService.listarRolesAsignablesPor(auth.getName()).stream()
                .map(r -> Map.<String, Object>of("id", r.getId(), "nombre", r.getNombre()))
                .toList();
    }

    @PostMapping
    public ResponseEntity<Void> crear(@Valid @RequestBody UsuarioRequest req, Authentication auth) {
        if (req.getPasswordPlano() == null || req.getPasswordPlano().isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria al crear un usuario.");
        }
        UsuarioForm form = aForm(req);
        form.setId(null);
        usuarioService.guardar(form, auth.getName());
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequest req, Authentication auth) {
        UsuarioForm form = aForm(req);
        form.setId(id);
        usuarioService.guardar(form, auth.getName());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, Authentication auth) {
        usuarioService.eliminar(id, auth.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{id}/foto", produces = { MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE })
    public ResponseEntity<byte[]> foto(@PathVariable Long id, Authentication auth) {
        Usuario usuario = usuarioService.buscarVisiblePorId(id, auth.getName());
        if (!usuario.isFotoPresente()) {
            return ResponseEntity.notFound().build();
        }
        MediaType tipo = (usuario.getFotoContentType() != null)
                ? MediaType.parseMediaType(usuario.getFotoContentType())
                : MediaType.IMAGE_JPEG;
        return ResponseEntity.ok().contentType(tipo).body(usuario.getFoto());
    }

    private UsuarioForm aForm(UsuarioRequest req) {
        UsuarioForm form = new UsuarioForm();
        form.setUsername(req.getUsername());
        form.setNombre(req.getNombre());
        form.setPasswordPlano(req.getPasswordPlano());
        form.setRolId(req.getRolId());
        form.setActivo(req.isActivo());
        return form;
    }
}
