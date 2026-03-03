package co.edu.unbosque.ElecSys.Ordenes.OrdenDeTrabajo.DetalleOrdenTrabajo.ServicioDetOrdTra;

import co.edu.unbosque.ElecSys.Ordenes.OrdenDeTrabajo.DetalleOrdenTrabajo.EntidadDetOrdTra.DetalleOrdenTrabajoEntidad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleOrdenTrabajoRepository extends JpaRepository<DetalleOrdenTrabajoEntidad, Integer> {
}
