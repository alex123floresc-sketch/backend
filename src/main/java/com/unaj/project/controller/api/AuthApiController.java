package com.unaj.project.controller.api;

import com.unaj.project.dto.api.LoginRequest;
import com.unaj.project.dto.api.LoginResponse;
import com.unaj.project.model.Usuario;
import com.unaj.project.repository.UsuarioRepository;
import com.unaj.project.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Endpoints de autenticación para el frontend Angular.
 *  POST /api/auth/login  -> valida credenciales y devuelve un JWT
 *  GET  /api/auth/me     -> devuelve los datos del usuario del token actual
 */
@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public AuthApiController(AuthenticationManager authenticationManager,
                             JwtService jwtService,
                             UsuarioRepository usuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));

            UserDetails user = (UserDetails) auth.getPrincipal();
            String token = jwtService.generarToken(user);
            List<String> roles = user.getAuthorities().stream().map(Object::toString).toList();

            Usuario usuario = usuarioRepository.findByUsername(user.getUsername());
            String nombre = usuario != null ? usuario.getNombre() : user.getUsername();

            return ResponseEntity.ok(new LoginResponse(
                    token, jwtService.getExpirationMs(), user.getUsername(), nombre, roles));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body(Map.of("mensaje", "Usuario o contraseña incorrectos"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        String username = authentication.getName();
        Usuario usuario = usuarioRepository.findByUsername(username);
        List<String> roles = authentication.getAuthorities().stream().map(Object::toString).toList();
        return ResponseEntity.ok(Map.of(
                "username", username,
                "nombre", usuario != null ? usuario.getNombre() : username,
                "roles", roles
        ));
    }
}
