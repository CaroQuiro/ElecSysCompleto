package co.edu.unbosque.ElecSys.notificacion.servicioNot;


import co.edu.unbosque.ElecSys.notificacion.dtoNot.NotificacionDTO;
import co.edu.unbosque.ElecSys.notificacion.entidadNot.NotificacionDestinatarioEntidad;
import co.edu.unbosque.ElecSys.notificacion.entidadNot.NotificacionEntidad;
import co.edu.unbosque.ElecSys.notificacion.entidadNot.NotificacionProgramacionEntidad;
import co.edu.unbosque.ElecSys.notificacion.envioEmail.ConfiguracionEmail;
import co.edu.unbosque.ElecSys.usuario.cliente.servicioClie.ClienteServiceImpl;
import co.edu.unbosque.ElecSys.usuario.trabajador.servicioTra.TrabajadorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio principal que gestiona el ciclo de vida de las notificaciones en ElecSys.
 * Incluye lógica para notificaciones directas, envíos automáticos programados y reseteo de estados de envío.
 */
@Service
public class NotificacionService implements NotificacionInterface {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private NotificacionDestinatarioRepository destinatarioRepository;

    @Autowired
    private NotificacionProgramacionRepository programacionRepository;

    @Autowired
    private ConfiguracionEmail configuracionEmail;

    @Autowired
    private TrabajadorServiceImpl trabajadorService;

    @Autowired
    private ClienteServiceImpl clienteService;

    /**
     * Almacena una notificación base en el repositorio con estado inicial ACTIVA.
     * @param dto Información de la notificación.
     * @return Mensaje de estado de la creación.
     */
    @Override
    public String crearNotificacion(NotificacionDTO dto) {
        try {
            NotificacionEntidad notificacion = new NotificacionEntidad();
            notificacion.setTitulo(dto.getTitulo());
            notificacion.setMensaje(dto.getMensaje());
            notificacion.setTipo(dto.getTipo());       // UNICA | RECURRENTE
            notificacion.setEstado("ACTIVA");
            notificacion.setFechaCreacion(LocalDateTime.now());

            notificacionRepository.save(notificacion);

            return "Notificación creada correctamente";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al crear la notificación";
        }
    }

    /**
     * Actualiza los datos de una notificación basándose en su ID.
     * @param idNotificacionAnt ID de la notificación a editar.
     * @param notificacionNueva DTO con los datos actualizados.
     * @return Mensaje indicando el éxito o fallo de la actualización.
     */
    @Override
    public String editarNotificacion(long idNotificacionAnt, NotificacionDTO notificacionNueva) {
        return notificacionRepository.findById((int) idNotificacionAnt)
                .map(notificacion -> {
                    notificacion.setTitulo(notificacionNueva.getTitulo());
                    notificacion.setMensaje(notificacionNueva.getMensaje());
                    notificacion.setTipo(notificacionNueva.getTipo());
                    notificacionRepository.save(notificacion);
                    return "Notificación editada correctamente";
                })
                .orElse("No se encontró la notificación");
    }

    /**
     * Elimina físicamente una notificación del sistema.
     * @param idNotificacion ID de la notificación.
     * @return Mensaje de confirmación.
     */
    @Override
    public String borrarNotificacion(long idNotificacion) {
        try {
            notificacionRepository.deleteById((int) idNotificacion);
            return "Notificación eliminada correctamente";
        } catch (Exception e) {
            return "Error al eliminar la notificación";
        }
    }

    /**
     * Obtiene el listado de todas las notificaciones convirtiéndolas a DTO.
     * @return Lista de objetos NotificacionDTO.
     */
    @Override
    public List<NotificacionDTO> listarNotificaciones() {
        return notificacionRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Busca una notificación por ID y la mapea a su representación DTO.
     * @param idNotificacion ID de búsqueda.
     * @return NotificacionDTO encontrado o null si no existe.
     */
    @Override
    public NotificacionDTO buscarNotificacion(long idNotificacion) {
        return notificacionRepository.findById((int) idNotificacion)
                .map(this::mapToDTO)
                .orElse(null);
    }

    /**
     * Cambia el estado de la notificación a INACTIVA.
     * @param idNotificacion ID de la notificación.
     * @return Mensaje de resultado de la operación.
     */
    @Override
    public String desactivarNotificacion(long idNotificacion) {
        return notificacionRepository.findById((int) idNotificacion)
                .map(notificacion -> {
                    notificacion.setEstado("INACTIVA");
                    notificacionRepository.save(notificacion);
                    return "Notificación desactivada";
                })
                .orElse("No se encontró la notificación");
    }

    /**
     * Cambia el estado de la notificación a ACTIVA.
     * @param idNotificacion ID de la notificación.
     * @return Mensaje de resultado de la operación.
     */
    @Override
    public String activarNotificacion(long idNotificacion) {
        return notificacionRepository.findById((int) idNotificacion)
                .map(notificacion -> {
                    notificacion.setEstado("ACTIVA");
                    notificacionRepository.save(notificacion);
                    return "Notificación activada";
                })
                .orElse("No se encontró la notificación");
    }

    /**
     * Procesa y envía correos electrónicos a todos los destinatarios asociados a una notificación
     * que aún no han recibido el mensaje.
     * @param dto Información de la notificación para el envío.
     */
    @Override
    public void envioNotificacion(NotificacionDTO dto) {
        NotificacionEntidad notificacion = notificacionRepository
                .findById((int) dto.getIdNotificacion())
                .orElseThrow();

        List<NotificacionDestinatarioEntidad> destinatarios =
                destinatarioRepository.findByNotificacion(notificacion);

        for (NotificacionDestinatarioEntidad d : destinatarios) {

            if (d.isEnviado()) continue;

            String correo;
            String nombre;

            if ("CLIENTE".equals(d.getTipoDestinatario())) {
                var cliente = clienteService.buscarCliente(d.getIdCliente());
                correo = cliente.getCorreo();
                nombre = cliente.getNombre();
            } else {
                var trabajador = trabajadorService.buscarTrabajador(d.getIdTrabajador());
                correo = trabajador.getCorreo();
                nombre = trabajador.getNombre();
            }

            configuracionEmail.crearEmail(
                    nombre,
                    correo,
                    notificacion.getTitulo(),
                    notificacion.getMensaje(),
                    null
            );

            configuracionEmail.envioEmail();

            d.setEnviado(true);
            d.setFechaEnvio(LocalDateTime.now());
            destinatarioRepository.save(d);
        }
    }

    /**
     * Método interno para transformar una entidad de base de datos a un objeto de transferencia (DTO).
     * @param entidad Entidad persistida.
     * @return Objeto NotificacionDTO.
     */
    private NotificacionDTO mapToDTO(NotificacionEntidad entidad) {
        return new NotificacionDTO(
                entidad.getIdNotificacion(),
                entidad.getTitulo(),
                entidad.getMensaje(),
                entidad.getTipo(),
                entidad.getEstado(),
                entidad.getFechaCreacion()
        );
    }

    /**
     * Registra una notificación recurrente configurando su frecuencia, rango de fechas y destinatarios.
     * @param dto Información base.
     * @param frecuencia Periodicidad (DIARIA, SEMANAL, etc.).
     * @param fechaInicio Fecha de inicio de la programación.
     * @param fechaFin Fecha de finalización opcional.
     * @param idsDestinatarios Lista de IDs de los sujetos a notificar.
     * @param tipoDestinatario Indica si son Clientes o Trabajadores.
     * @return Mensaje de éxito de la programación.
     */
    @Transactional
    public String crearNotificacionProgramada(NotificacionDTO dto, String frecuencia,
                                              LocalDateTime fechaInicio, LocalDateTime fechaFin,
                                              List<Integer> idsDestinatarios, String tipoDestinatario) {
        try {
            NotificacionEntidad notificacion = new NotificacionEntidad();
            notificacion.setTitulo(dto.getTitulo());
            notificacion.setMensaje(dto.getMensaje());
            notificacion.setTipo("RECURRENTE");
            notificacion.setEstado("ACTIVA");
            notificacion.setFechaCreacion(LocalDateTime.now());
            notificacion = notificacionRepository.save(notificacion);

            for (Integer id : idsDestinatarios) {
                NotificacionDestinatarioEntidad dest = new NotificacionDestinatarioEntidad();
                dest.setNotificacion(notificacion);
                dest.setTipoDestinatario(tipoDestinatario);
                if ("CLIENTE".equals(tipoDestinatario)) {
                    dest.setIdCliente(id);
                    dest.setIdTrabajador(null);
                } else {
                    dest.setIdTrabajador(id);
                    dest.setIdCliente(null);
                }
                dest.setEnviado(false);
                destinatarioRepository.save(dest);
            }

            NotificacionProgramacionEntidad prog = new NotificacionProgramacionEntidad();
            prog.setNotificacion(notificacion);
            prog.setFrecuencia(frecuencia.toUpperCase());
            prog.setFechaInicio(fechaInicio);
            prog.setFechaFin(fechaFin);
            prog.setUltimaEjecucion(null);
            programacionRepository.save(prog);

            return "Notificación programada creada con éxito";
        } catch (Exception e) {
            return "Error al programar: " + e.getMessage();
        }
    }

    /**
     * Tarea programada que se ejecuta diariamente a las 8:00 AM para procesar envíos recurrentes.
     * Evalúa la frecuencia de cada programación y resetea los estados de envío para nuevas ejecuciones.
     */
    @Override
    @Scheduled(cron = "0 0 8 * * *")
    @Transactional
    public void revisarYEnviarNotificacionesprogramadas() {
        List<NotificacionProgramacionEntidad> programadas = programacionRepository.findAll();
        LocalDateTime ahora = LocalDateTime.now();

        for (NotificacionProgramacionEntidad prog : programadas) {
            NotificacionEntidad notificacion = prog.getNotificacion();

            if (!"ACTIVA".equals(notificacion.getEstado())) continue;
            if (ahora.isBefore(prog.getFechaInicio())) continue;
            if (prog.getFechaFin() != null && ahora.isAfter(prog.getFechaFin())) continue;

            if (debeEjecutarse(prog, ahora)) {
                List<NotificacionDestinatarioEntidad> destinatarios = destinatarioRepository.findByNotificacion(notificacion);
                for (NotificacionDestinatarioEntidad d : destinatarios) {
                    d.setEnviado(false);
                    destinatarioRepository.save(d);
                }

                envioNotificacion(mapToDTO(notificacion));

                prog.setUltimaEjecucion(ahora);
                programacionRepository.save(prog);
            }
        }
    }

    /**
     * Calcula si corresponde realizar un envío el día de hoy basándose en la última ejecución y la frecuencia.
     * @param prog Entidad de programación a evaluar.
     * @param ahora Fecha y hora actual del sistema.
     * @return true si debe ejecutarse, false en caso contrario.
     */
    private boolean debeEjecutarse(NotificacionProgramacionEntidad prog, LocalDateTime ahora) {
        if (prog.getUltimaEjecucion() == null) return true;

        LocalDateTime ultima = prog.getUltimaEjecucion();

        return switch (prog.getFrecuencia().toUpperCase()) {
            case "DIARIA"   -> ultima.plusDays(1).isBefore(ahora) || ultima.plusDays(1).isEqual(ahora);
            case "SEMANAL"  -> ultima.plusWeeks(1).isBefore(ahora) || ultima.plusWeeks(1).isEqual(ahora);
            case "MENSUAL"  -> ultima.plusMonths(1).isBefore(ahora) || ultima.plusMonths(1).isEqual(ahora);
            case "SEMESTRAL"-> ultima.plusMonths(6).isBefore(ahora) || ultima.plusMonths(6).isEqual(ahora);
            case "ANUAL"    -> ultima.plusYears(1).isBefore(ahora) || ultima.plusYears(1).isEqual(ahora);
            default -> false;
        };
    }

}


