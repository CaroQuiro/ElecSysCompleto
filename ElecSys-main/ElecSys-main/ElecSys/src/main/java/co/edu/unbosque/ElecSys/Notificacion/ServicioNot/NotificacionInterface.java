package co.edu.unbosque.ElecSys.Notificacion.ServicioNot;

import co.edu.unbosque.ElecSys.Notificacion.DTONot.NotificacionDTO;

import java.util.List;

public interface NotificacionInterface {

     public String crearNotificacion(NotificacionDTO dto);

    public String editarNotificacion(long idNotificacionAnt, NotificacionDTO notificacionNueva);

    public String borrarNotificacion(long idNotificacion);

    public List<NotificacionDTO> listarNotificaciones();

    public NotificacionDTO buscarNotificacion(long idNotificacion);

    public String desactivarNotificacion(long idNotificacion);

    public void revisarYEnviarNotificacionesprogramadas();

    public void envioNotificacion(NotificacionDTO notificacion);
}
