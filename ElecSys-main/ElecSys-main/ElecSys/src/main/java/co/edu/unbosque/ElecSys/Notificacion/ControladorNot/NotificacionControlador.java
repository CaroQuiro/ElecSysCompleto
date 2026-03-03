package co.edu.unbosque.ElecSys.Notificacion.ControladorNot;

import co.edu.unbosque.ElecSys.Config.Excepcion.InvalidFieldException;
import co.edu.unbosque.ElecSys.Config.Excepcion.ResourceNotFoundException;
import co.edu.unbosque.ElecSys.Notificacion.DTONot.NotificacionDTO;
import co.edu.unbosque.ElecSys.Notificacion.DTONot.ProgramadasRequest;
import co.edu.unbosque.ElecSys.Notificacion.EnvioEmail.ConfiguracionEmail;
import co.edu.unbosque.ElecSys.Notificacion.ServicioNot.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
@CrossOrigin(origins = "http://localhost:4200")
public class NotificacionControlador {

    @Autowired
    private NotificacionService notificacionService;

    @Autowired
    private ConfiguracionEmail configuracionEmail;

    /* =====================================================
       LISTAR NOTIFICACIONES
       ===================================================== */
    @GetMapping("/listar")
    public List<NotificacionDTO> listarNotificaciones() {
        return notificacionService.listarNotificaciones();
    }

    /* =====================================================
       BUSCAR NOTIFICACIÓN POR ID
       ===================================================== */
    @GetMapping("/buscar/{id}")
    public NotificacionDTO buscarNotificacion(@PathVariable int id) {

        NotificacionDTO notificacion =
                notificacionService.buscarNotificacion(id);

        if (notificacion == null)
            throw new ResourceNotFoundException(
                    "No existe la notificación con ID: " + id);

        return notificacion;
    }

    /* =====================================================
       CREAR NOTIFICACIÓN
       ===================================================== */
    @PostMapping("/agregar")
    public String agregarNotificacion(@RequestBody NotificacionDTO dto) {

        if (dto.getTitulo() == null || dto.getTitulo().isBlank())
            throw new InvalidFieldException("El título es obligatorio.");

        if (dto.getMensaje() == null || dto.getMensaje().isBlank())
            throw new InvalidFieldException("El mensaje es obligatorio.");

        if (dto.getTipo() == null ||
                (!dto.getTipo().equals("UNICA")
                        && !dto.getTipo().equals("RECURRENTE")))
            throw new InvalidFieldException(
                    "El tipo debe ser UNICA o RECURRENTE.");

        return notificacionService.crearNotificacion(dto);
    }

    /* =====================================================
       ACTUALIZAR NOTIFICACIÓN
       ===================================================== */
    @PutMapping("/actualizar/{id}")
    public String actualizarNotificacion(
            @PathVariable int id,
            @RequestBody NotificacionDTO dto) {

        NotificacionDTO actual =
                notificacionService.buscarNotificacion(id);

        if (actual == null)
            throw new ResourceNotFoundException(
                    "No existe la notificación con ID: " + id);

        if (dto.getIdNotificacion() != id)
            throw new InvalidFieldException(
                    "No se puede cambiar el ID de la notificación.");

        return notificacionService.editarNotificacion(id, dto);
    }

    /* =====================================================
       BORRAR NOTIFICACIÓN
       ===================================================== */
    @DeleteMapping("/borrar/{id}")
    public String borrarNotificacion(@PathVariable int id) {

        NotificacionDTO notificacion =
                notificacionService.buscarNotificacion(id);

        if (notificacion == null)
            throw new ResourceNotFoundException(
                    "No existe la notificación con ID: " + id);

        return notificacionService.borrarNotificacion(id);
    }

    /* =====================================================
       ENVÍO DE CORREO ÚNICO (REGISTRA NOTIFICACIÓN)
       ===================================================== */
    @PostMapping(value = "/enviar-correo",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> enviarCorreoUnico(
            @RequestParam String nombreUsuario,
            @RequestParam String correo,
            @RequestParam String asunto,
            @RequestParam String mensaje,
            @RequestPart(required = false) MultipartFile archivo) {

        Map<String, String> respuesta = new HashMap<>();

        try {
            /* ================= VALIDACIONES ================= */
            if (nombreUsuario.isBlank() ||
                    correo.isBlank() ||
                    asunto.isBlank() ||
                    mensaje.isBlank())
                throw new InvalidFieldException(
                        "Todos los campos son obligatorios.");

            /* ================= CREAR NOTIFICACIÓN ================= */
            NotificacionDTO notificacionDTO = new NotificacionDTO();
            notificacionDTO.setTitulo(asunto);
            notificacionDTO.setMensaje(mensaje);
            notificacionDTO.setTipo("UNICA");
            notificacionDTO.setEstado("ACTIVA");

            notificacionService.crearNotificacion(notificacionDTO);

            /* ================= CREAR EMAIL ================= */
            configuracionEmail.crearEmail(
                    nombreUsuario,
                    correo,
                    asunto,
                    mensaje,
                    archivo
            );

            configuracionEmail.envioEmail();

            respuesta.put("mensaje", "Correo enviado y notificación registrada correctamente.");
            return ResponseEntity.ok(respuesta); // Esto devuelve un JSON { "mensaje": "..." } con Status 200

        } catch (Exception e) {
            e.printStackTrace();
            respuesta.put("error", "Error al enviar el correo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta); // Devuelve Status 500
        }
    }

    @PostMapping("/programar")
    public ResponseEntity<String> programarNotificacion(@RequestBody ProgramadasRequest request) {

        // Validaciones básicas
        if (request.notificacion == null || request.notificacion.getTitulo().isBlank())
            throw new InvalidFieldException("Los datos de la notificación son obligatorios.");

        if (request.frecuencia == null || request.frecuencia.isBlank())
            throw new InvalidFieldException("La frecuencia es obligatoria (DIARIA, SEMANAL, etc.).");

        if (request.idsDestinatarios == null || request.idsDestinatarios.isEmpty())
            throw new InvalidFieldException("Debe incluir al menos un destinatario.");

        if (request.tipoDestinatario == null ||
                (!request.tipoDestinatario.equals("CLIENTE") && !request.tipoDestinatario.equals("TRABAJADOR")))
            throw new InvalidFieldException("El tipo de destinatario debe ser CLIENTE o TRABAJADOR.");

        // Llamada al método del servicio que creamos anteriormente
        String resultado = notificacionService.crearNotificacionProgramada(
                request.notificacion,
                request.frecuencia,
                request.fechaInicio,
                request.fechaFin,
                request.idsDestinatarios,
                request.tipoDestinatario
        );

        return ResponseEntity.ok(resultado);
    }

    /**
     * Método para cambiar el estado de ACTIVA a INACTIVA
     */
    @PutMapping("/desactivar/{id}")
    public ResponseEntity<String> desactivar(@PathVariable int id) {

        NotificacionDTO actual = notificacionService.buscarNotificacion(id);

        if (actual == null)
            throw new ResourceNotFoundException("No existe la notificación con ID: " + id);

        String mensaje = notificacionService.desactivarNotificacion(id);

        return ResponseEntity.ok(mensaje);
    }

}
