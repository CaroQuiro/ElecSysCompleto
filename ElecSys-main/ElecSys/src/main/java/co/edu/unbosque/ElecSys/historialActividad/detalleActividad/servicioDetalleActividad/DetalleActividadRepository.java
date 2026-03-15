package co.edu.unbosque.ElecSys.historialActividad.detalleActividad.servicioDetalleActividad;

import co.edu.unbosque.ElecSys.historialActividad.detalleActividad.entidadDetalleActividad.DetalleActividadEntidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleActividadRepository extends JpaRepository<DetalleActividadEntidad, Integer> {
    List<DetalleActividadEntidad> findAllByIdHistorial(int idHistorial);
}
