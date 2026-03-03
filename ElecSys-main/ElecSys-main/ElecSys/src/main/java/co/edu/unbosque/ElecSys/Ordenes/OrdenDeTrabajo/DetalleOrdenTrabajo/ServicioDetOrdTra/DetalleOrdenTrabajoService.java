package co.edu.unbosque.ElecSys.Ordenes.OrdenDeTrabajo.DetalleOrdenTrabajo.ServicioDetOrdTra;


import co.edu.unbosque.ElecSys.Ordenes.OrdenDeTrabajo.DetalleOrdenTrabajo.DTODetOrdTra.DetalleOrdenTrabajoDTO;
import co.edu.unbosque.ElecSys.Ordenes.OrdenDeTrabajo.DetalleOrdenTrabajo.EntidadDetOrdTra.DetalleOrdenTrabajoEntidad;
import co.edu.unbosque.ElecSys.Ordenes.OrdenDeTrabajo.EntidadOrdTra.OrdenDeTrabajoEntidad;
import co.edu.unbosque.ElecSys.Ordenes.OrdenDeTrabajo.ServicioOrdTra.OrdenDeTrabajoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetalleOrdenTrabajoService implements DetalleOrdenTrabajoInterface{

    @Autowired
    private DetalleOrdenTrabajoRepository detalleRepository;

    @Autowired
    private OrdenDeTrabajoRepository ordenTrabajoRepository;

    /* =====================================================
       AGREGAR DETALLE
       ===================================================== */
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

            detalleRepository.save(detalle);
            return "Detalle guardado correctamente";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al agregar el detalle de la orden de trabajo";
        }
    }

    /* =====================================================
       BORRAR DETALLE
       ===================================================== */
    @Override
    public String borrarDetalleOrdTra(int id) {
        try {
            if (!detalleRepository.existsById(id)) {
                return "El detalle de orden de trabajo no existe";
            }

            detalleRepository.deleteById(id);
            return "Detalle de orden de trabajo eliminado correctamente";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al eliminar el detalle de la orden de trabajo";
        }
    }

    /* =====================================================
       LISTAR DETALLES
       ===================================================== */
    @Override
    public List<DetalleOrdenTrabajoDTO> listarDetallesOrdTra() {
        return detalleRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /* =====================================================
       ACTUALIZAR DETALLE
       ===================================================== */
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

            return "Detalle de orden de trabajo actualizado correctamente";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al actualizar el detalle de la orden de trabajo";
        }
    }

    /* =====================================================
       MAPPER PRIVADO
       ===================================================== */
    private DetalleOrdenTrabajoDTO mapToDTO(DetalleOrdenTrabajoEntidad entidad) {
        return new DetalleOrdenTrabajoDTO(
                entidad.getIdDetalleTrabajo(),
                entidad.getIdOrden(),
                entidad.getActividad(),
                entidad.getObservaciones(),
                entidad.getDuracion()
        );
    }

    /* =====================================================
       LISTAR DETALLES POR ORDEN
       ===================================================== */
    public List<DetalleOrdenTrabajoDTO> listarDetallesPorOrden(int idOrden) {
        return detalleRepository.findAll()
                .stream()
                .filter(d -> d.getIdOrden() == idOrden)
                .map(this::mapToDTO)
                .toList();
    }

    /* =====================================================
       BUSCAR DETALLE POR ID
       ===================================================== */
    public DetalleOrdenTrabajoDTO buscarDetalle(int idDetalle) {
        return detalleRepository.findById(idDetalle)
                .map(this::mapToDTO)
                .orElse(null);
    }

    /* =====================================================
       EXISTE DETALLE
       ===================================================== */
    public boolean existeDetalle(int idDetalle) {
        return detalleRepository.existsById(idDetalle);
    }
}
