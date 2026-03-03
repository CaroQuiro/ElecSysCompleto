package co.edu.unbosque.ElecSys.Ordenes.OrdenDeVisita.ServicioOrdVis;

import co.edu.unbosque.ElecSys.Ordenes.OrdenDeVisita.EntidadOrdVis.OrdenDeVisitaEntidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenVisitaRepository extends JpaRepository<OrdenDeVisitaEntidad, Integer> {
    List<OrdenDeVisitaEntidad> findByIdCliente(int idCliente);
}