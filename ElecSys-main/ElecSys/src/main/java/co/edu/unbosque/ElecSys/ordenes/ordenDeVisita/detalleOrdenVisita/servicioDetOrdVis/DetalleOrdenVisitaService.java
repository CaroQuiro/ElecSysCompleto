package co.edu.unbosque.ElecSys.ordenes.ordenDeVisita.detalleOrdenVisita.servicioDetOrdVis;


import co.edu.unbosque.ElecSys.ordenes.ordenDeVisita.detalleOrdenVisita.dtoDetOrdVis.DetalleOrdenVisitaDTO;
import co.edu.unbosque.ElecSys.ordenes.ordenDeVisita.detalleOrdenVisita.entidadDetOrdVis.DetalleOrdenVisitaEntidad;
import co.edu.unbosque.ElecSys.ordenes.ordenDeVisita.entidadOrdVis.OrdenDeVisitaEntidad;
import co.edu.unbosque.ElecSys.ordenes.ordenDeVisita.servicioOrdVis.OrdenVisitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetalleOrdenVisitaService implements DetalleOrdenVisitaInterface{

    @Autowired
    private DetalleOrdenVisitaRepository detalleOrdenVisitaRepository;

    @Autowired
    private OrdenVisitaRepository ordenVisitaRepository;

    /* =====================================================
       AGREGAR DETALLE
       ===================================================== */
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

            detalleOrdenVisitaRepository.save(detalle);

            return "Detalle de orden de visita agregado correctamente";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al agregar el detalle de la orden de visita";
        }
    }

    /* =====================================================
       BORRAR DETALLE
       ===================================================== */
    @Override
    public String borrarDetalleOrdVis(int id) {
        try {
            if (!detalleOrdenVisitaRepository.existsById(id)) {
                return "El detalle de orden de visita no existe";
            }

            detalleOrdenVisitaRepository.deleteById(id);
            return "Detalle de orden de visita eliminado correctamente";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al eliminar el detalle de la orden de visita";
        }
    }

    /* =====================================================
       LISTAR TODOS LOS DETALLES
       ===================================================== */
    @Override
    public List<DetalleOrdenVisitaDTO> listarDetallesOrdVis() {
        return detalleOrdenVisitaRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /* =====================================================
       ACTUALIZAR DETALLE
       ===================================================== */
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

            return "Detalle de orden de visita actualizado correctamente";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al actualizar el detalle de la orden de visita";
        }
    }

    /* =====================================================
       LISTAR DETALLES POR ORDEN DE VISITA
       ===================================================== */
    public List<DetalleOrdenVisitaDTO> listarDetallesPorOrden(int idOrdenVisita) {
        return detalleOrdenVisitaRepository.findAll()
                .stream()
                .filter(d -> d.getId_visita() == idOrdenVisita)
                .map(this::mapToDTO)
                .toList();
    }

    /* =====================================================
       BUSCAR DETALLE POR ID
       ===================================================== */
    public DetalleOrdenVisitaDTO buscarDetalle(int idDetalle) {
        return detalleOrdenVisitaRepository.findById(idDetalle)
                .map(this::mapToDTO)
                .orElse(null);
    }

    /* =====================================================
       EXISTE DETALLE
       ===================================================== */
    public boolean existeDetalle(int idDetalle) {
        return detalleOrdenVisitaRepository.existsById(idDetalle);
    }

    /* =====================================================
       MAPPER PRIVADO
       ===================================================== */
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
