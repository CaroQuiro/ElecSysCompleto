package co.edu.unbosque.ElecSys.notificacion.controladorNot;

import co.edu.unbosque.ElecSys.config.excepcion.InvalidFieldException;
import co.edu.unbosque.ElecSys.config.excepcion.ResourceNotFoundException;
import co.edu.unbosque.ElecSys.notificacion.dtoNot.NotificacionDTO;
import co.edu.unbosque.ElecSys.notificacion.dtoNot.ProgramadasRequest;
import co.edu.unbosque.ElecSys.notificacion.envioEmail.ConfiguracionEmail;
import co.edu.unbosque.ElecSys.notificacion.servicioNot.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para la gestión de notificaciones del sistema y envío de correos electrónicos.
 * Soporta notificaciones únicas, recurrentes y envíos de emails con archivos adjuntos.
 */
@RestController
@RequestMapping("/api/notificaciones")
@CrossOrigin(origins = "http://localhost:4200")
public class NotificacionControlador {

    @Autowired
    private NotificacionService notificacionService;

    @Autowired
    private ConfiguracionEmail configuracionEmail;

    /**
     * Retorna el listado completo de notificaciones registradas.
     * @return Lista de NotificacionDTO.
     */
    @GetMapping("/listar")
    public List<NotificacionDTO> listarNotificaciones() {
        return notificacionService.listarNotificaciones();
    }

    /**
     * Recupera una notificación específica mediante su identificador.
     * @param id ID de la notificación.
     * @return El objeto NotificacionDTO encontrado.
     * @throws ResourceNotFoundException Si la notificación no existe.
     */
    @GetMapping("/buscar/{id}")
    public NotificacionDTO buscarNotificacion(@PathVariable int id) {

        NotificacionDTO notificacion =
                notificacionService.buscarNotificacion(id);

        if (notificacion == null)
            throw new ResourceNotFoundException(
                    "No existe la notificación con ID: " + id);

        return notificacion;
    }

    /**
     * Crea una nueva notificación validando los campos de título, mensaje y tipo.
     * @param dto Datos de la nueva notificación.
     * @return Mensaje de confirmación del servicio.
     * @throws InvalidFieldException Si faltan campos obligatorios o el tipo no es válido.
     */
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

    /**
     * Actualiza el contenido de una notificación existente.
     * @param id ID de la notificación a editar.
     * @param dto Nuevos datos de la notificación.
     * @return Mensaje de confirmación de la edición.
     * @throws ResourceNotFoundException Si el ID no existe.
     * @throws InvalidFieldException Si se intenta modificar el identificador original.
     */
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

    /**
     * Elimina una notificación del sistema permanentemente.
     * @param id ID de la notificación a borrar.
     * @return Mensaje de éxito de la operación.
     * @throws ResourceNotFoundException Si la notificación no existe.
     */
    @DeleteMapping("/borrar/{id}")
    public String borrarNotificacion(@PathVariable int id) {

        NotificacionDTO notificacion =
                notificacionService.buscarNotificacion(id);

        if (notificacion == null)
            throw new ResourceNotFoundException(
                    "No existe la notificación con ID: " + id);

        return notificacionService.borrarNotificacion(id);
    }

    /**
     * Realiza el envío de un correo electrónico único y registra el evento como una notificación.
     * @param nombreUsuario Nombre del destinatario para el saludo.
     * @param correo Dirección de correo electrónico de destino.
     * @param asunto Título del email.
     * @param mensaje Cuerpo del mensaje en formato HTML.
     * @param archivo Archivo opcional adjunto a la comunicación.
     * @return ResponseEntity con un mapa de respuesta exitosa o error interno.
     * @throws InvalidFieldException Si alguno de los campos de envío está vacío.
     */
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
            if (nombreUsuario.isBlank() ||
                    correo.isBlank() ||
                    asunto.isBlank() ||
                    mensaje.isBlank())
                throw new InvalidFieldException(
                        "Todos los campos son obligatorios.");

            NotificacionDTO notificacionDTO = new NotificacionDTO();
            notificacionDTO.setTitulo(asunto);
            notificacionDTO.setMensaje(mensaje);
            notificacionDTO.setTipo("UNICA");
            notificacionDTO.setEstado("ACTIVA");

            notificacionService.crearNotificacion(notificacionDTO);

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

    /**
     * Programa una notificación recurrente (diaria, semanal, etc.) para un grupo de destinatarios.
     * @param request Objeto que contiene la notificación, frecuencia, fechas y lista de IDs de destinatarios.
     * @return ResponseEntity con el resultado de la programación.
     * @throws InvalidFieldException Si los datos de programación o destinatarios son incorrectos.
     */
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
     * Cambia el estado de una notificación a INACTIVA para pausar su ejecución o visibilidad.
     * @param id ID de la notificación.
     * @return ResponseEntity con el mensaje de desactivación.
     * @throws ResourceNotFoundException Si la notificación no existe.
     */
    @PutMapping("/desactivar/{id}")
    public ResponseEntity<String> desactivar(@PathVariable int id) {

        NotificacionDTO actual = notificacionService.buscarNotificacion(id);

        if (actual == null)
            throw new ResourceNotFoundException("No existe la notificación con ID: " + id);

        String mensaje = notificacionService.desactivarNotificacion(id);

        return ResponseEntity.ok(mensaje);
    }

    /**
     * Cambia el estado de una notificación a ACTIVA.
     * @param id ID de la notificación.
     * @return ResponseEntity con el mensaje de activación.
     * @throws ResourceNotFoundException Si la notificación no existe.
     */
    @PutMapping("/activar/{id}")
    public ResponseEntity<String> activar(@PathVariable int id) {

        NotificacionDTO actual = notificacionService.buscarNotificacion(id);

        if (actual == null)
            throw new ResourceNotFoundException("No existe la notificación con ID: " + id);

        String mensaje = notificacionService.activarNotificacion(id);

        return ResponseEntity.ok(mensaje);
    }

}
