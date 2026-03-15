package co.edu.unbosque.ElecSys.notificacion.servicioNot;

import co.edu.unbosque.ElecSys.notificacion.entidadNot.NotificacionDestinatarioEntidad;
import co.edu.unbosque.ElecSys.notificacion.entidadNot.NotificacionEntidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionDestinatarioRepository extends JpaRepository<NotificacionDestinatarioEntidad, Integer> {
    List<NotificacionDestinatarioEntidad> findByNotificacion(NotificacionEntidad notificacion);
}
