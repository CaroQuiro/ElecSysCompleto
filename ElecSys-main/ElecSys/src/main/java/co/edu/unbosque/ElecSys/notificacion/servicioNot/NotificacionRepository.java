package co.edu.unbosque.ElecSys.notificacion.servicioNot;

import co.edu.unbosque.ElecSys.notificacion.entidadNot.NotificacionEntidad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacionRepository extends JpaRepository<NotificacionEntidad, Integer> {
    //List<NotificacionEntidad> findAllByIdUsuario(int idUsuario);
}
