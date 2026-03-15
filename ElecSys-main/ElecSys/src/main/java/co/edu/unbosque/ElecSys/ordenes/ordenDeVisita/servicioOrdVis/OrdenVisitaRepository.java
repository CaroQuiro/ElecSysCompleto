package co.edu.unbosque.ElecSys.ordenes.ordenDeVisita.servicioOrdVis;

import co.edu.unbosque.ElecSys.ordenes.ordenDeVisita.entidadOrdVis.OrdenDeVisitaEntidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenVisitaRepository extends JpaRepository<OrdenDeVisitaEntidad, Integer> {
    List<OrdenDeVisitaEntidad> findByIdCliente(int idCliente);
}