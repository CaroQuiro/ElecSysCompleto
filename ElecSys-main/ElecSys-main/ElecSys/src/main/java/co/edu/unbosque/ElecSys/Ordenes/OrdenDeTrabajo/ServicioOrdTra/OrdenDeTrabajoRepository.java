package co.edu.unbosque.ElecSys.Ordenes.OrdenDeTrabajo.ServicioOrdTra;

import co.edu.unbosque.ElecSys.Ordenes.OrdenDeTrabajo.EntidadOrdTra.OrdenDeTrabajoEntidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenDeTrabajoRepository extends JpaRepository<OrdenDeTrabajoEntidad, Integer> {
    List<OrdenDeTrabajoEntidad> findByIdCliente(int idCliente);
}
