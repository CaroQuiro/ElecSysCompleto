package co.edu.unbosque.ElecSys.cuentaPorPagar.detalle_Cuenta.servicioDetalleCuenta;

import co.edu.unbosque.ElecSys.cuentaPorPagar.detalle_Cuenta.entidadDetalleCuenta.DetalleCuentaEntidad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleCuentaRepository extends JpaRepository<DetalleCuentaEntidad, Integer> {
}
