package co.edu.unbosque.ElecSys.ordenes.ordenDeVisita.servicioOrdVis;

import co.edu.unbosque.ElecSys.historialActividad.helperHis.AuditoriaHelper;
import co.edu.unbosque.ElecSys.ordenes.ordenDeVisita.dtoOrdVis.OrdenDeVisitaDTO;
import co.edu.unbosque.ElecSys.ordenes.ordenDeVisita.entidadOrdVis.OrdenDeVisitaEntidad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio para la gestión de Órdenes de Visita técnica.
 * Procesa la programación de citas técnicas y su seguimiento de estado.
 */
@Service
public class OrdenDeVisitaService implements OrdenDeVisitaInterface{

    @Autowired
    private OrdenVisitaRepository ordenVisitaRepository;

    @Autowired
    private AuditoriaHelper auditoria;

    /**
     * Crea una nueva orden de visita y devuelve el ID generado por la base de datos.
     * @param dto Información de la visita (cliente, lugar, trabajador, etc.).
     * @return El ID (int) de la visita generada.
     * @throws RuntimeException Si falla la creación.
     */
    @Override
    public int agregarOrdenVisita(OrdenDeVisitaDTO dto) {
        try {
            OrdenDeVisitaEntidad orden = new OrdenDeVisitaEntidad();
            orden.setIdLugar(dto.getIdLugar());
            orden.setIdCliente(dto.getIdCliente());
            orden.setIdTrabajador(dto.getIdTrabajador());
            orden.setFechaRealizacion(dto.getFechaRealizacion());
            orden.setDescripcion(dto.getDescripcion());
            orden.setEstado(dto.getEstado());

            OrdenDeVisitaEntidad guardada = ordenVisitaRepository.save(orden);

            auditoria.registrarAccion("ORDEN_VISITA", "Creación de Visita",
                    "ID_VISITA", "N/A", String.valueOf(guardada.getIdVisita()));

            return guardada.getIdVisita();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear la orden de visita");
        }
    }

    /**
     * Actualiza los datos de una orden de visita ya programada.
     * @param idOrdenAnt ID de la visita a modificar.
     * @param ordenNueva Nuevos datos.
     * @return Mensaje de estado de la operación.
     */
    @Override
    public String editarOrdenVisita(int idOrdenAnt, OrdenDeVisitaDTO ordenNueva) {
        try {
            OrdenDeVisitaEntidad orden = ordenVisitaRepository
                    .findById(idOrdenAnt)
                    .orElse(null);

            if (orden == null) {
                return "La orden de visita no existe";
            }

            orden.setIdLugar(ordenNueva.getIdLugar());
            orden.setIdCliente(ordenNueva.getIdCliente());
            orden.setIdTrabajador(ordenNueva.getIdTrabajador());
            orden.setFechaRealizacion(ordenNueva.getFechaRealizacion());
            orden.setDescripcion(ordenNueva.getDescripcion());
            orden.setEstado(ordenNueva.getEstado());

            ordenVisitaRepository.save(orden);

            auditoria.registrarAccion("ORDEN_VISITA", "Edición de Visita",
                    "ID_VISITA", String.valueOf(idOrdenAnt), "Modificada");

            return "Orden de visita actualizada correctamente";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al actualizar la orden de visita";
        }
    }

    /**
     * Borra una orden de visita del sistema.
     * @param idOrden ID de la visita a eliminar.
     * @return Mensaje confirmando la eliminación.
     */
    @Override
    public String borrarOrdenVisita(int idOrden) {
        try {
            if (!ordenVisitaRepository.existsById(idOrden)) {
                return "La orden de visita no existe";
            }

            ordenVisitaRepository.deleteById(idOrden);
            auditoria.registrarAccion("ORDEN_VISITA", "Eliminación de Visita",
                    "ID_VISITA", String.valueOf(idOrden), "Eliminada");

            return "Orden de visita eliminada correctamente";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al eliminar la orden de visita";
        }
    }

    /**
     * Lista todas las visitas técnicas del sistema.
     * @return Lista de {@link OrdenDeVisitaDTO}.
     */
    @Override
    public List<OrdenDeVisitaDTO> listarOrdenVisita() {
        return ordenVisitaRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Obtiene las visitas programadas para un cliente en particular.
     * @param idCliente ID del cliente.
     * @return Lista de visitas asociadas al cliente.
     */
    @Override
    public List<OrdenDeVisitaDTO> listarOrdenVisitaPorCliente(int idCliente) {
        return ordenVisitaRepository.findByIdCliente(idCliente)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Recupera una visita técnica por su identificador único.
     * @param idOrden ID de la visita.
     * @return DTO de la visita o null.
     */
    @Override
    public OrdenDeVisitaDTO buscarOrdenVisita(int idOrden) {
        return ordenVisitaRepository.findById(idOrden)
                .map(this::mapToDTO)
                .orElse(null);
    }

    /**
     * Transforma una entidad de Orden de Visita a su DTO.
     * @param entidad Entidad persistida.
     * @return DTO mapeado.
     */
    private OrdenDeVisitaDTO mapToDTO(OrdenDeVisitaEntidad entidad) {
        return new OrdenDeVisitaDTO(
                entidad.getIdVisita(),
                entidad.getIdLugar(),
                entidad.getIdCliente(),
                entidad.getIdTrabajador(),
                entidad.getFechaRealizacion(),
                entidad.getDescripcion(),
                entidad.getEstado()
        );
    }

    /**
     * Verifica la existencia de una orden de visita.
     * @param idOrden ID de la visita.
     * @return true si la visita existe.
     */
    public boolean existeOrden(int idOrden) {
        return ordenVisitaRepository.existsById(idOrden);
    }
}
