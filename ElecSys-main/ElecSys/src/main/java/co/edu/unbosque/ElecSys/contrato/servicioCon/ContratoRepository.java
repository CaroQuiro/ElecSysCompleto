package co.edu.unbosque.ElecSys.contrato.servicioCon;

import co.edu.unbosque.ElecSys.contrato.entidadCon.ContratoEntidad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContratoRepository extends JpaRepository<ContratoEntidad, Integer> {
}
