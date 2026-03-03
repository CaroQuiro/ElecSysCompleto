package co.edu.unbosque.ElecSys.Notificacion.ServicioNot;

import co.edu.unbosque.ElecSys.Notificacion.EntidadNot.NotificacionProgramacionEntidad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacionProgramacionRepository extends JpaRepository<NotificacionProgramacionEntidad, Integer> {
}
