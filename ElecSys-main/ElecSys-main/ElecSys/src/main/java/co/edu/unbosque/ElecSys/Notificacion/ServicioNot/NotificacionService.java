package co.edu.unbosque.ElecSys.Notificacion.ServicioNot;


import co.edu.unbosque.ElecSys.Notificacion.DTONot.NotificacionDTO;
import co.edu.unbosque.ElecSys.Notificacion.EntidadNot.NotificacionDestinatarioEntidad;
import co.edu.unbosque.ElecSys.Notificacion.EntidadNot.NotificacionEntidad;
import co.edu.unbosque.ElecSys.Notificacion.EntidadNot.NotificacionProgramacionEntidad;
import co.edu.unbosque.ElecSys.Notificacion.EnvioEmail.ConfiguracionEmail;
import co.edu.unbosque.ElecSys.Usuario.Cliente.DTOClie.ClienteDTO;
import co.edu.unbosque.ElecSys.Usuario.Cliente.ServicioClie.ClienteServiceImpl;
import co.edu.unbosque.ElecSys.Usuario.Trabajador.DTOTra.TrabajadorDTO;
import co.edu.unbosque.ElecSys.Usuario.Trabajador.ServicioTra.TrabajadorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.sql.Date;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.List;



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

    /* =====================================================
       CREAR NOTIFICACIÓN
       ===================================================== */
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

    /* =====================================================
       EDITAR NOTIFICACIÓN
       ===================================================== */
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

    /* =====================================================
       BORRAR NOTIFICACIÓN
       ===================================================== */
    @Override
    public String borrarNotificacion(long idNotificacion) {
        try {
            notificacionRepository.deleteById((int) idNotificacion);
            return "Notificación eliminada correctamente";
        } catch (Exception e) {
            return "Error al eliminar la notificación";
        }
    }

    /* =====================================================
       LISTAR NOTIFICACIONES
       ===================================================== */
    @Override
    public List<NotificacionDTO> listarNotificaciones() {
        return notificacionRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /* =====================================================
       BUSCAR NOTIFICACIÓN
       ===================================================== */
    @Override
    public NotificacionDTO buscarNotificacion(long idNotificacion) {
        return notificacionRepository.findById((int) idNotificacion)
                .map(this::mapToDTO)
                .orElse(null);
    }

    /* =====================================================
       DESACTIVAR NOTIFICACIÓN
       ===================================================== */
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

    /* =====================================================
       ENVÍO MANUAL DE NOTIFICACIÓN
       ===================================================== */
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

    /* =====================================================
       MAPPER INTERNO
       ===================================================== */
    private NotificacionDTO mapToDTO(NotificacionEntidad entidad) {
        return new NotificacionDTO(
                entidad.getIdNotificacion(),   // ya es Long → se convierte a long
                entidad.getTitulo(),
                entidad.getMensaje(),
                entidad.getTipo(),
                entidad.getEstado(),
                entidad.getFechaCreacion()     // ya es LocalDateTime
        );
    }

    @Transactional
    public String crearNotificacionProgramada(NotificacionDTO dto, String frecuencia,
                                              LocalDateTime fechaInicio, LocalDateTime fechaFin,
                                              List<Integer> idsDestinatarios, String tipoDestinatario) {
        try {
            // 1. Crear la Notificación Base
            NotificacionEntidad notificacion = new NotificacionEntidad();
            notificacion.setTitulo(dto.getTitulo());
            notificacion.setMensaje(dto.getMensaje());
            notificacion.setTipo("RECURRENTE");
            notificacion.setEstado("ACTIVA");
            notificacion.setFechaCreacion(LocalDateTime.now());
            notificacion = notificacionRepository.save(notificacion);

            // 2. Crear los Destinatarios (Cliente o Trabajador)
            for (Integer id : idsDestinatarios) {
                NotificacionDestinatarioEntidad dest = new NotificacionDestinatarioEntidad();
                dest.setNotificacion(notificacion);
                dest.setTipoDestinatario(tipoDestinatario);
                if ("CLIENTE".equals(tipoDestinatario)) {
                    dest.setIdCliente(id);
                    dest.setIdTrabajador(null); // Ahora sí puedes poner null
                } else {
                    dest.setIdTrabajador(id);
                    dest.setIdCliente(null);    // Ahora sí puedes poner null
                }
                dest.setEnviado(false);
                destinatarioRepository.save(dest);
            }

            // 3. Crear la Programación (Frecuencia)
            NotificacionProgramacionEntidad prog = new NotificacionProgramacionEntidad();
            prog.setNotificacion(notificacion);
            prog.setFrecuencia(frecuencia.toUpperCase()); // DIARIA, SEMANAL, MENSUAL, SEMESTRAL, ANUAL
            prog.setFechaInicio(fechaInicio);
            prog.setFechaFin(fechaFin);
            prog.setUltimaEjecucion(null); // No se ha ejecutado aún
            programacionRepository.save(prog);

            return "Notificación programada creada con éxito";
        } catch (Exception e) {
            return "Error al programar: " + e.getMessage();
        }
    }

    @Override
    @Scheduled(cron = "0 0 8 * * *") // Se ejecuta todos los días a las 8:00 AM
    @Transactional
    public void revisarYEnviarNotificacionesprogramadas() {
        List<NotificacionProgramacionEntidad> programadas = programacionRepository.findAll();
        LocalDateTime ahora = LocalDateTime.now();

        for (NotificacionProgramacionEntidad prog : programadas) {
            NotificacionEntidad notificacion = prog.getNotificacion();

            // Validaciones básicas: Estado y Rango de Fechas
            if (!"ACTIVA".equals(notificacion.getEstado())) continue;
            if (ahora.isBefore(prog.getFechaInicio())) continue;
            if (prog.getFechaFin() != null && ahora.isAfter(prog.getFechaFin())) continue;

            if (debeEjecutarse(prog, ahora)) {
                // IMPORTANTE: Como el método 'envioNotificacion' ignora a los que ya tienen enviado=true,
                // debemos resetear el estado de los destinatarios para esta nueva recurrencia.
                List<NotificacionDestinatarioEntidad> destinatarios = destinatarioRepository.findByNotificacion(notificacion);
                for (NotificacionDestinatarioEntidad d : destinatarios) {
                    d.setEnviado(false);
                    destinatarioRepository.save(d);
                }

                // Llamamos al envío (tu método actual se encarga del resto)
                envioNotificacion(mapToDTO(notificacion));

                // Actualizamos la última ejecución
                prog.setUltimaEjecucion(ahora);
                programacionRepository.save(prog);
            }
        }
    }

    /**
     * Lógica para determinar si corresponde enviar hoy según la frecuencia
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


