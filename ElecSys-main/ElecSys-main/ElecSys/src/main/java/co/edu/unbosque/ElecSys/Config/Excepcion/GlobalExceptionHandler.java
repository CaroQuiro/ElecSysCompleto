package co.edu.unbosque.ElecSys.Config.Excepcion;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleNotFound(ResourceNotFoundException e) {
        return buildResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidFieldException.class)
    public ResponseEntity<Object> handleInvalidField(InvalidFieldException e) {
        return buildResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Object> handleDuplicate(DuplicateResourceException e) {
        return buildResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PdfGenerationException.class)
    public ResponseEntity<Object> handlePdfError(PdfGenerationException e) {
        return buildResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleOther(Exception e) {
        return buildResponse("Error interno del servidor: " + e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Object> buildResponse(String mensaje, HttpStatus status) {
        Map<String, Object> body = new HashMap<>();
        body.put("mensaje", mensaje);
        body.put("fecha", LocalDateTime.now());
        body.put("codigo", status.value());
        return new ResponseEntity<>(body, status);
    }
}