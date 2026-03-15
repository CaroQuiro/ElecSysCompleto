package co.edu.unbosque.ElecSys.cuentaPorPagar.servicioCuen;

import co.edu.unbosque.ElecSys.cuentaPorPagar.entidadCuen.CuentaPorPagarEntidad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuentasPorPagarRepository extends JpaRepository<CuentaPorPagarEntidad, Integer> {
}
