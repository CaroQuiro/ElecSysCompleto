package co.edu.unbosque.ElecSys.ordenes.ordenDeTrabajo.detalleOrdenTrabajo.servicioDetOrdTra;

import co.edu.unbosque.ElecSys.ordenes.ordenDeTrabajo.detalleOrdenTrabajo.entidadDetOrdTra.DetalleOrdenTrabajoEntidad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleOrdenTrabajoRepository extends JpaRepository<DetalleOrdenTrabajoEntidad, Integer> {
}
