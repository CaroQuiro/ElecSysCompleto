package co.edu.unbosque.ElecSys.ordenes.ordenDeVisita.detalleOrdenVisita.servicioDetOrdVis;


import co.edu.unbosque.ElecSys.historialActividad.helperHis.AuditoriaHelper;
import co.edu.unbosque.ElecSys.ordenes.ordenDeVisita.detalleOrdenVisita.dtoDetOrdVis.DetalleOrdenVisitaDTO;
import co.edu.unbosque.ElecSys.ordenes.ordenDeVisita.detalleOrdenVisita.entidadDetOrdVis.DetalleOrdenVisitaEntidad;
import co.edu.unbosque.ElecSys.ordenes.ordenDeVisita.entidadOrdVis.OrdenDeVisitaEntidad;
import co.edu.unbosque.ElecSys.ordenes.ordenDeVisita.servicioOrdVis.OrdenVisitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio para la gestión de las actividades detalladas en las Órdenes de Visita.
 * Controla qué se debe hacer en cada inspección o visita técnica.
 */
@Service
public class DetalleOrdenVisitaService implements DetalleOrdenVisitaInterface{

    @Autowired
    private DetalleOrdenVisitaRepository detalleOrdenVisitaRepository;

    @Autowired
    private OrdenVisitaRepository ordenVisitaRepository;

    @Autowired
    private AuditoriaHelper auditoria;

    /**
     * Agrega una nueva actividad a una visita validando que la visita padre exista.
     * @param dto Datos de la actividad técnica.
     * @return Mensaje de éxito o error.
     */
    @Override
    public String agregarDetalleOrdVis(DetalleOrdenVisitaDTO dto) {
        try {
            OrdenDeVisitaEntidad ordenVisita = ordenVisitaRepository
                    .findById(dto.getIdVisita())
                    .orElse(null);

            if (ordenVisita == null) {
                return "La orden de visita no existe";
            }

            DetalleOrdenVisitaEntidad detalle = new DetalleOrdenVisitaEntidad();
            detalle.setId_visita(ordenVisita.getIdVisita());
            detalle.setActividad(dto.getActividad());
            detalle.setObservaciones(dto.getObservaciones());
            detalle.setDuracion(dto.getDuracion());

            DetalleOrdenVisitaEntidad guardado = detalleOrdenVisitaRepository.save(detalle);

            auditoria.registrarAccion("DETALLE_ORDEN_VISITA", "Creación de Detalle Visita",
                    "ID_DETALLE_VISITA", "N/A", String.valueOf(guardado.getId_detalle_visita()));

            return "Detalle de orden de visita agregado correctamente";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al agregar el detalle de la orden de visita";
        }
    }

    /**
     * Elimina una actividad técnica del registro.
     * @param id ID del detalle de visita.
     * @return Mensaje indicando el resultado.
     */
    @Override
    public String borrarDetalleOrdVis(int id) {
        try {
            if (!detalleOrdenVisitaRepository.existsById(id)) {
                return "El detalle de orden de visita no existe";
            }

            detalleOrdenVisitaRepository.deleteById(id);

            auditoria.registrarAccion("DETALLE_ORDEN_VISITA", "Eliminación de Detalle Visita",
                    "ID_DETALLE_VISITA", String.valueOf(id), "Eliminado");
            return "Detalle de orden de visita eliminado correctamente";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al eliminar el detalle de la orden de visita";
        }
    }

    /**
     * Lista todos los detalles de visitas registrados en el sistema.
     * @return Lista de DTOs de detalles de visita.
     */
    @Override
    public List<DetalleOrdenVisitaDTO> listarDetallesOrdVis() {
        return detalleOrdenVisitaRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Actualiza la descripción o duración de una actividad de visita.
     * @param id ID del detalle a actualizar.
     * @param dto Nuevos datos.
     * @return Mensaje de éxito o error.
     */
    @Override
    public String actualizarDetalleOrdVis(int id, DetalleOrdenVisitaDTO dto) {
        try {
            DetalleOrdenVisitaEntidad detalle = detalleOrdenVisitaRepository
                    .findById(id)
                    .orElse(null);

            if (detalle == null) {
                return "El detalle de orden de visita no existe";
            }

            detalle.setActividad(dto.getActividad());
            detalle.setObservaciones(dto.getObservaciones());
            detalle.setDuracion(dto.getDuracion());

            detalleOrdenVisitaRepository.save(detalle);

            auditoria.registrarAccion("DETALLE_ORDEN_VISITA", "Actualización de Detalle Visita",
                    "ID_DETALLE_VISITA", "Existente", String.valueOf(id));

            return "Detalle de orden de visita actualizado correctamente";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al actualizar el detalle de la orden de visita";
        }
    }

    /**
     * Filtra los detalles de actividades pertenecientes a una visita técnica específica.
     * @param idOrdenVisita ID de la visita.
     * @return Lista de detalles asociados.
     */
    public List<DetalleOrdenVisitaDTO> listarDetallesPorOrden(int idOrdenVisita) {
        return detalleOrdenVisitaRepository.findAll()
                .stream()
                .filter(d -> d.getId_visita() == idOrdenVisita)
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Busca un detalle de visita por su ID primario.
     * @param idDetalle ID del detalle.
     * @return DTO del detalle o null.
     */
    public DetalleOrdenVisitaDTO buscarDetalle(int idDetalle) {
        return detalleOrdenVisitaRepository.findById(idDetalle)
                .map(this::mapToDTO)
                .orElse(null);
    }

    /**
     * Verifica si existe un detalle de visita específico.
     * @param idDetalle ID a consultar.
     * @return true si el registro existe.
     */
    public boolean existeDetalle(int idDetalle) {
        return detalleOrdenVisitaRepository.existsById(idDetalle);
    }

    /**
     * Mapea una entidad de detalle de visita a un DTO.
     * @param entidad Entidad de base de datos.
     * @return DTO resultante.
     */
    private DetalleOrdenVisitaDTO mapToDTO(DetalleOrdenVisitaEntidad entidad) {
        return new DetalleOrdenVisitaDTO(
                entidad.getId_detalle_visita(),
                entidad.getId_visita(),
                entidad.getActividad(),
                entidad.getObservaciones(),
                entidad.getDuracion()
        );
    }
}
