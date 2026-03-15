package co.edu.unbosque.ElecSys.cotizacion.detalleCotizacion.servicioDetCot;

import co.edu.unbosque.ElecSys.cotizacion.detalleCotizacion.entidadDetCot.DetalleCotizacionEntidad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleCotizacionRepository extends JpaRepository<DetalleCotizacionEntidad, Integer> {
}
