package co.edu.unbosque.ElecSys.Notificacion.ServicioNot;

import co.edu.unbosque.ElecSys.Notificacion.EntidadNot.NotificacionDestinatarioEntidad;
import co.edu.unbosque.ElecSys.Notificacion.EntidadNot.NotificacionEntidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionDestinatarioRepository extends JpaRepository<NotificacionDestinatarioEntidad, Integer> {
    List<NotificacionDestinatarioEntidad> findByNotificacion(NotificacionEntidad notificacion);
}
