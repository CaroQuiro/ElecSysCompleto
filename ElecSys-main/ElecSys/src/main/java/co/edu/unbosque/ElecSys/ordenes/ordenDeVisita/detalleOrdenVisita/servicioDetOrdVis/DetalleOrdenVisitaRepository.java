package co.edu.unbosque.ElecSys.ordenes.ordenDeVisita.detalleOrdenVisita.servicioDetOrdVis;

import co.edu.unbosque.ElecSys.ordenes.ordenDeVisita.detalleOrdenVisita.entidadDetOrdVis.DetalleOrdenVisitaEntidad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleOrdenVisitaRepository extends JpaRepository<DetalleOrdenVisitaEntidad, Integer> {
}
