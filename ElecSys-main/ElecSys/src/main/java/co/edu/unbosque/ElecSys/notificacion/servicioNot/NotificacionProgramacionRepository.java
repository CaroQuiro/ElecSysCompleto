package co.edu.unbosque.ElecSys.notificacion.servicioNot;

import co.edu.unbosque.ElecSys.notificacion.entidadNot.NotificacionProgramacionEntidad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacionProgramacionRepository extends JpaRepository<NotificacionProgramacionEntidad, Integer> {
}
