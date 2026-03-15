package co.edu.unbosque.ElecSys.ordenes.ordenDeTrabajo.servicioOrdTra;

import co.edu.unbosque.ElecSys.historialActividad.helperHis.AuditoriaHelper;
import co.edu.unbosque.ElecSys.ordenes.ordenDeTrabajo.dtoOrdTra.OrdenDeTrabajoDTO;
import co.edu.unbosque.ElecSys.ordenes.ordenDeTrabajo.entidadOrdTra.OrdenDeTrabajoEntidad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio principal para la gestión de cabeceras de Órdenes de Trabajo.
 * Se encarga de la creación, edición y consulta de las órdenes en el sistema.
 */
@Service
public class OrdenDeTrabajoService implements OrdenDeTrabajoInterface{

    @Autowired
    private OrdenDeTrabajoRepository ordenTrabajoRepository;

    @Autowired
    private AuditoriaHelper auditoria;

    /**
     * Registra una nueva orden de trabajo y retorna el ID generado por el sistema.
     * @param dto Datos de la orden a crear.
     * @return El ID (Integer) asignado a la nueva orden.
     * @throws RuntimeException Si ocurre un fallo durante la persistencia.
     */
    @Override
    public Integer agregarOrdenTrabajo(OrdenDeTrabajoDTO dto) {
        try {
            OrdenDeTrabajoEntidad orden = new OrdenDeTrabajoEntidad();

            orden.setIdOrdenVisita(dto.getId_orden_visita());
            orden.setIdLugar(dto.getId_lugar());
            orden.setIdCliente(dto.getId_cliente());
            orden.setIdTrabajador(dto.getId_trabajador());
            orden.setFechaRealizacion(dto.getFecha_realizacion());
            orden.setEstado(dto.getEstado());

            OrdenDeTrabajoEntidad guardada = ordenTrabajoRepository.save(orden);

            auditoria.registrarAccion("ORDEN_TRABAJO", "Creación de Orden",
                    "ID_ORDEN", "N/A", String.valueOf(guardada.getIdOrden()));

            return guardada.getIdOrden();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear la orden de trabajo en el servidor");
        }
    }

    /**
     * Modifica los datos generales de una orden de trabajo existente.
     * @param idOrdenAnt ID de la orden a editar.
     * @param ordenNueva DTO con la nueva información.
     * @return Mensaje de éxito o error.
     */
    @Override
    public String editarOrdenTrabajo(int idOrdenAnt, OrdenDeTrabajoDTO ordenNueva) {
        try {
            OrdenDeTrabajoEntidad orden = ordenTrabajoRepository
                    .findById(idOrdenAnt)
                    .orElse(null);

            if (orden == null) {
                return "La orden de trabajo no existe";
            }

            orden.setIdOrdenVisita(ordenNueva.getId_orden_visita());
            orden.setIdLugar(ordenNueva.getId_lugar());
            orden.setIdCliente(ordenNueva.getId_cliente());
            orden.setIdTrabajador(ordenNueva.getId_trabajador());
            orden.setFechaRealizacion(ordenNueva.getFecha_realizacion());
            orden.setEstado(ordenNueva.getEstado());

            ordenTrabajoRepository.save(orden);

            auditoria.registrarAccion("ORDEN_TRABAJO", "Edición de Orden",
                    "ID_ORDEN", String.valueOf(idOrdenAnt), "Modificada");

            return "Orden de trabajo actualizada correctamente";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al actualizar la orden de trabajo";
        }
    }

    /**
     * Elimina una orden de trabajo de la base de datos.
     * @param idOrden ID de la orden a borrar.
     * @return Mensaje confirmando la eliminación.
     */
    @Override
    public String borrarOrdenTrabajo(int idOrden) {
        try {
            if (!ordenTrabajoRepository.existsById(idOrden)) {
                return "La orden de trabajo no existe";
            }

            ordenTrabajoRepository.deleteById(idOrden);
            auditoria.registrarAccion("ORDEN_TRABAJO", "Eliminación de Orden",
                    "ID_ORDEN", String.valueOf(idOrden), "Eliminada");
            return "Orden de trabajo eliminada correctamente";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al eliminar la orden de trabajo";
        }
    }

    /**
     * Recupera el listado completo de órdenes de trabajo.
     * @return Lista de {@link OrdenDeTrabajoDTO}.
     */
    @Override
    public List<OrdenDeTrabajoDTO> listarOrdenTrabajo() {
        return ordenTrabajoRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Recupera todas las órdenes asociadas a un cliente específico.
     * @param idCliente ID del cliente.
     * @return Lista de órdenes del cliente.
     */
    @Override
    public List<OrdenDeTrabajoDTO> listarOrdenTrabajoPorCliente(int idCliente) {
        return ordenTrabajoRepository.findByIdCliente(idCliente)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Obtiene los datos detallados de una orden por su ID.
     * @param idOrden ID de búsqueda.
     * @return DTO de la orden o null si no existe.
     */
    @Override
    public OrdenDeTrabajoDTO buscarOrdenTrabajo(int idOrden) {
        return ordenTrabajoRepository.findById(idOrden)
                .map(this::mapToDTO)
                .orElse(null);
    }

    /**
     * Transforma una entidad de Orden de Trabajo a su correspondiente DTO.
     * @param entidad Entidad de base de datos.
     * @return Objeto DTO mapeado.
     */
    private OrdenDeTrabajoDTO mapToDTO(OrdenDeTrabajoEntidad entidad) {
        return new OrdenDeTrabajoDTO(
                entidad.getIdOrden(),
                entidad.getIdOrdenVisita(),
                entidad.getIdLugar(),
                entidad.getIdCliente(),
                entidad.getIdTrabajador(),
                entidad.getFechaRealizacion(),
                entidad.getEstado()
        );
    }

    /**
     * Comprueba si una orden de trabajo existe en el repositorio.
     * @param idOrden ID a verificar.
     * @return true si la orden existe.
     */
    public boolean existeOrden(int idOrden) {
        return ordenTrabajoRepository.existsById(idOrden);
    }
}
