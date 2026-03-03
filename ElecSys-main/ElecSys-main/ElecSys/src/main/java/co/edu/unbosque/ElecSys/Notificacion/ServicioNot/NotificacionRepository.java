package co.edu.unbosque.ElecSys.Notificacion.ServicioNot;

import co.edu.unbosque.ElecSys.Notificacion.EntidadNot.NotificacionEntidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionRepository extends JpaRepository<NotificacionEntidad, Integer> {
    //List<NotificacionEntidad> findAllByIdUsuario(int idUsuario);
}
