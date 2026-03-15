package co.edu.unbosque.ElecSys.cotizacion.servicioCot;

import co.edu.unbosque.ElecSys.cotizacion.entidadCot.CotizacionEntidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CotizacionRepository extends JpaRepository<CotizacionEntidad, Integer> {

    @Query(value = "SELECT contar_cotizaciones_por_cliente(:idCliente)", nativeQuery = true)
    int llamarContarCotizaciones(@Param("idCliente") int idCliente);
}
