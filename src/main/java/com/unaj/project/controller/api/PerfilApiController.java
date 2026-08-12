package com.unaj.project.controller.api;

import com.unaj.project.dto.PerfilForm;
import com.unaj.project.dto.api.PerfilDTO;
import com.unaj.project.dto.api.PerfilRequest;
import com.unaj.project.model.Usuario;
import com.unaj.project.service.PerfilService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/perfil")
public class PerfilApiController {

    private final PerfilService perfilService;

    public PerfilApiController(PerfilService perfilService) {
        this.perfilService = perfilService;
    }

    @GetMapping
    public PerfilDTO ver(Authentication auth) {
        return PerfilDTO.desde(perfilService.obtenerPropio(auth.getName()));
    }

    @PutMapping
    public ResponseEntity<PerfilDTO> actualizar(@Valid @RequestBody PerfilRequest req, Authentication auth) {
        PerfilForm form = perfilService.formPropio(auth.getName());
        form.setNombre(req.getNombre());
        form.setEmail(req.getEmail());
        form.setTelefono(req.getTelefono());
        form.setCargo(req.getCargo());
        form.setPasswordActual(req.getPasswordActual());
        form.setPasswordNueva(req.getPasswordNueva());
        form.setPasswordNuevaConfirmar(req.getPasswordNuevaConfirmar());
        perfilService.actualizarPropio(auth.getName(), form);
        return ResponseEntity.ok(PerfilDTO.desde(perfilService.obtenerPropio(auth.getName())));
    }

    @PostMapping(value = "/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> subirFoto(@RequestParam("foto") MultipartFile foto, Authentication auth) {
        PerfilForm form = perfilService.formPropio(auth.getName());
        form.setFoto(foto);
        perfilService.actualizarPropio(auth.getName(), form);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/foto")
    public ResponseEntity<Void> quitarFoto(Authentication auth) {
        PerfilForm form = perfilService.formPropio(auth.getName());
        form.setQuitarFoto(true);
        perfilService.actualizarPropio(auth.getName(), form);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/firma", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> subirFirma(@RequestParam("firma") MultipartFile firma, Authentication auth) {
        PerfilForm form = perfilService.formPropio(auth.getName());
        form.setFirma(firma);
        perfilService.actualizarPropio(auth.getName(), form);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/foto", produces = { MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE })
    public ResponseEntity<byte[]> foto(Authentication auth) {
        Usuario usuario = perfilService.obtenerPropio(auth.getName());
        if (!usuario.isFotoPresente()) {
            return ResponseEntity.notFound().build();
        }
        MediaType tipo = (usuario.getFotoContentType() != null)
                ? MediaType.parseMediaType(usuario.getFotoContentType())
                : MediaType.IMAGE_JPEG;
        return ResponseEntity.ok().contentType(tipo).body(usuario.getFoto());
    }

    @GetMapping(value = "/firma", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> firma(Authentication auth) {
        Usuario usuario = perfilService.obtenerPropio(auth.getName());
        if (!usuario.isFirmaPresente()) {
            return ResponseEntity.notFound().build();
        }
        MediaType tipo = (usuario.getFirmaContentType() != null)
                ? MediaType.parseMediaType(usuario.getFirmaContentType())
                : MediaType.IMAGE_PNG;
        return ResponseEntity.ok().contentType(tipo).body(usuario.getFirma());
    }
}
