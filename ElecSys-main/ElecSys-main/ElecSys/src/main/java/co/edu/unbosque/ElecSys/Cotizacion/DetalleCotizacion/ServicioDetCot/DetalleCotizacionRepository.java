package co.edu.unbosque.ElecSys.Cotizacion.DetalleCotizacion.ServicioDetCot;

import co.edu.unbosque.ElecSys.Cotizacion.DetalleCotizacion.EntidadDetCot.DetalleCotizacionEntidad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleCotizacionRepository extends JpaRepository<DetalleCotizacionEntidad, Integer> {
}
