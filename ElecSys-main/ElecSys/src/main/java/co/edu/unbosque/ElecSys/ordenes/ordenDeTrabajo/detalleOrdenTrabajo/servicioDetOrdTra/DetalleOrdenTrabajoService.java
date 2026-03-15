package co.edu.unbosque.ElecSys.ordenes.ordenDeTrabajo.detalleOrdenTrabajo.servicioDetOrdTra;


import co.edu.unbosque.ElecSys.historialActividad.helperHis.AuditoriaHelper;
import co.edu.unbosque.ElecSys.ordenes.ordenDeTrabajo.detalleOrdenTrabajo.dtoDetOrdTra.DetalleOrdenTrabajoDTO;
import co.edu.unbosque.ElecSys.ordenes.ordenDeTrabajo.detalleOrdenTrabajo.entidadDetOrdTra.DetalleOrdenTrabajoEntidad;
import co.edu.unbosque.ElecSys.ordenes.ordenDeTrabajo.entidadOrdTra.OrdenDeTrabajoEntidad;
import co.edu.unbosque.ElecSys.ordenes.ordenDeTrabajo.servicioOrdTra.OrdenDeTrabajoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio encargado de la lógica de negocio para los detalles de las Órdenes de Trabajo.
 * Maneja la persistencia y consulta de las actividades específicas realizadas en cada orden.
 */
@Service
public class DetalleOrdenTrabajoService implements DetalleOrdenTrabajoInterface{

    @Autowired
    private DetalleOrdenTrabajoRepository detalleRepository;

    @Autowired
    private OrdenDeTrabajoRepository ordenTrabajoRepository;

    @Autowired
    private AuditoriaHelper auditoria;

    /**
     * Registra un nuevo detalle asegurando su vinculación con una orden de trabajo válida.
     * @param dto Datos del detalle a agregar.
     * @return Mensaje de éxito o descripción del error si la orden no existe.
     */
    @Override
    public String agregarDetalleOrdTra(DetalleOrdenTrabajoDTO dto) {
        try {
            OrdenDeTrabajoEntidad orden = ordenTrabajoRepository
                    .findById(dto.getIdOrden())
                    .orElse(null);

            if (orden == null) {
                return "La orden de trabajo no existe";
            }

            DetalleOrdenTrabajoEntidad detalle = new DetalleOrdenTrabajoEntidad();
            detalle.setIdOrden(dto.getIdOrden());
            detalle.setActividad(dto.getActividad());
            detalle.setObservaciones(dto.getObservaciones());
            detalle.setDuracion(dto.getDuracion());

            DetalleOrdenTrabajoEntidad guardado = detalleRepository.save(detalle);
            auditoria.registrarAccion("DETALLE_ORDEN_TRABAJO", "Creación de Detalle",
                    "ID_DETALLE", "N/A", String.valueOf(guardado.getIdDetalleTrabajo()));

            return "Detalle guardado correctamente";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al agregar el detalle de la orden de trabajo";
        }
    }

    /**
     * Elimina un detalle técnico del repositorio.
     * @param id ID del detalle a borrar.
     * @return Mensaje indicando el resultado de la operación.
     */
    @Override
    public String borrarDetalleOrdTra(int id) {
        try {
            if (!detalleRepository.existsById(id)) {
                return "El detalle de orden de trabajo no existe";
            }

            detalleRepository.deleteById(id);
            auditoria.registrarAccion("DETALLE_ORDEN_TRABAJO", "Eliminación de Detalle",
                    "ID_DETALLE", String.valueOf(id), "Eliminado");
            return "Detalle de orden de trabajo eliminado correctamente";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al eliminar el detalle de la orden de trabajo";
        }
    }

    /**
     * Recupera todos los detalles técnicos registrados en el sistema.
     * @return Lista de todos los detalles existentes en formato DTO.
     */
    @Override
    public List<DetalleOrdenTrabajoDTO> listarDetallesOrdTra() {
        return detalleRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Actualiza la información de una actividad o detalle técnico.
     * @param id ID del detalle a modificar.
     * @param dto Nuevos datos (actividad, observaciones, duración).
     * @return Mensaje de éxito o aviso de inexistencia.
     */
    @Override
    public String actualizarDetalleOrdTra(int id, DetalleOrdenTrabajoDTO dto) {
        try {
            DetalleOrdenTrabajoEntidad detalle = detalleRepository
                    .findById(id)
                    .orElse(null);

            if (detalle == null) {
                return "El detalle de orden de trabajo no existe";
            }

            detalle.setActividad(dto.getActividad());
            detalle.setObservaciones(dto.getObservaciones());
            detalle.setDuracion(dto.getDuracion());

            detalleRepository.save(detalle);

            auditoria.registrarAccion("DETALLE_ORDEN_TRABAJO", "Actualización de Detalle",
                    "ID_DETALLE", "Existente", String.valueOf(id));
            return "Detalle de orden de trabajo actualizado correctamente";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al actualizar el detalle de la orden de trabajo";
        }
    }

    /**
     * Convierte una entidad de base de datos en un objeto de transferencia de datos.
     * @param entidad Entidad persistida.
     * @return Objeto DTO mapeado.
     */
    private DetalleOrdenTrabajoDTO mapToDTO(DetalleOrdenTrabajoEntidad entidad) {
        return new DetalleOrdenTrabajoDTO(
                entidad.getIdDetalleTrabajo(),
                entidad.getIdOrden(),
                entidad.getActividad(),
                entidad.getObservaciones(),
                entidad.getDuracion()
        );
    }

    /**
     * Filtra y retorna los detalles que pertenecen exclusivamente a una orden.
     * @param idOrden ID de la orden de trabajo.
     * @return Lista de detalles vinculados a dicha orden.
     */
    public List<DetalleOrdenTrabajoDTO> listarDetallesPorOrden(int idOrden) {
        return detalleRepository.findAll()
                .stream()
                .filter(d -> d.getIdOrden() == idOrden)
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Busca un detalle específico por su identificador.
     * @param idDetalle ID del detalle.
     * @return DTO del detalle o null si no se encuentra.
     */
    public DetalleOrdenTrabajoDTO buscarDetalle(int idDetalle) {
        return detalleRepository.findById(idDetalle)
                .map(this::mapToDTO)
                .orElse(null);
    }

    /**
     * Verifica la existencia de un detalle en la base de datos.
     * @param idDetalle ID a comprobar.
     * @return true si existe, false en caso contrario.
     */
    public boolean existeDetalle(int idDetalle) {
        return detalleRepository.existsById(idDetalle);
    }
}
