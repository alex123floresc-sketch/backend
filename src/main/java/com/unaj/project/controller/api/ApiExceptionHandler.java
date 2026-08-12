package com.unaj.project.controller.api;

import com.unaj.project.exception.RecursoNoEncontradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manejador de excepciones exclusivo de la API REST (/api/**), separado del
 * GlobalExceptionHandler de Thymeleaf (que devuelve "redirect:..." — si una
 * excepción no controlada aquí llegara hasta él, el cliente JSON recibiría un
 * redirect en vez de un error, y terminaría en /login).
 *
 * @Order(HIGHEST_PRECEDENCE) + basePackages asegura que este advice se evalúe
 * antes que GlobalExceptionHandler para cualquier controlador de este paquete.
 */
@RestControllerAdvice(basePackages = "com.unaj.project.controller.api")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> noEncontrado(RecursoNoEncontradoException ex) {
        return cuerpo(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<Map<String, Object>> noEncontradoEnBorrado(EmptyResultDataAccessException ex) {
        return cuerpo(HttpStatus.NOT_FOUND, "El registro ya no existe.");
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, Object>> argumentoInvalido(RuntimeException ex) {
        return cuerpo(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> cuerpoIlegible(HttpMessageNotReadableException ex) {
        return cuerpo(HttpStatus.BAD_REQUEST, "El cuerpo de la petición no es un JSON válido.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validacionInvalida(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe ->
                errores.put(fe.getField(), fe.getDefaultMessage()));
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("mensaje", "Hay campos inválidos en la solicitud.");
        cuerpo.put("errores", errores);
        return ResponseEntity.badRequest().body(cuerpo);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> integridad(DataIntegrityViolationException ex) {
        return cuerpo(HttpStatus.CONFLICT, "No se pudo completar la operación: el registro está en uso por " +
                "otros datos del sistema (pagos, asistencias u otra información relacionada).");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> inesperado(Exception ex) {
        log.error("Error inesperado en la API", ex);
        return cuerpo(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado. Intenta de nuevo.");
    }

    private ResponseEntity<Map<String, Object>> cuerpo(HttpStatus estado, String mensaje) {
        return ResponseEntity.status(estado).body(Map.of("mensaje", mensaje != null ? mensaje : estado.getReasonPhrase()));
    }
}
