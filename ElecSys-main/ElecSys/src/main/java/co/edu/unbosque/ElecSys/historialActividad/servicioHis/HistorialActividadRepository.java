package co.edu.unbosque.ElecSys.historialActividad.servicioHis;

import co.edu.unbosque.ElecSys.historialActividad.entidadHis.HistorialActividadEntidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface HistorialActividadRepository extends JpaRepository<HistorialActividadEntidad, Integer> {

}
