package co.edu.unbosque.ElecSys.Ordenes.OrdenDeVisita.DetalleOrdenVisita.ServicioDetOrdVis;

import co.edu.unbosque.ElecSys.Ordenes.OrdenDeVisita.DetalleOrdenVisita.EntidadDetOrdVis.DetalleOrdenVisitaEntidad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleOrdenVisitaRepository extends JpaRepository<DetalleOrdenVisitaEntidad, Integer> {
}
