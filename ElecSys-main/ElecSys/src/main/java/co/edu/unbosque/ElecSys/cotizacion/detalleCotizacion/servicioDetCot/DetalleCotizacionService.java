package co.edu.unbosque.ElecSys.cotizacion.detalleCotizacion.servicioDetCot;

import co.edu.unbosque.ElecSys.cotizacion.detalleCotizacion.dtoDetCot.DetalleCotizacionDTO;
import co.edu.unbosque.ElecSys.cotizacion.detalleCotizacion.entidadDetCot.DetalleCotizacionEntidad;
import co.edu.unbosque.ElecSys.historialActividad.helperHis.AuditoriaHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de persistencia de los ítems individuales (detalles) de una cotización.
 */
@Service
public class DetalleCotizacionService implements DetalleCotizacionInterface{

    @Autowired
    private DetalleCotizacionRepository detalleCotizacionRepository;

    @Autowired
    private AuditoriaHelper auditoria;

    /**
     * Guarda un nuevo detalle de cotización en la base de datos.
     * @param detalleCotizacionDTO Datos del ítem.
     * @return Mensaje confirmando la creación o el error.
     */
    @Override
    public String agregarDetalleCot(DetalleCotizacionDTO detalleCotizacionDTO) {
        DetalleCotizacionEntidad nuevodetalle = new DetalleCotizacionEntidad(
                null,
          detalleCotizacionDTO.getId_cotizacion(),
          detalleCotizacionDTO.getDescripcion(),
          detalleCotizacionDTO.getCantidad(),
          detalleCotizacionDTO.getValor_unitario(),
          detalleCotizacionDTO.getSubtotal()
        );
        try{
            DetalleCotizacionEntidad guardado = detalleCotizacionRepository.save(nuevodetalle);
            auditoria.registrarAccion("DETALLE_COTIZACION", "Creación de Detalle",
                    "ID_DETALLE", "N/A", String.valueOf(guardado.getId_detalle_cotizacion()));            return "Detalle creado";
        } catch (RuntimeException e) {
            return "Error al crear un nuevo detalle";
        }
    }

    /**
     * Borra el detalle especifico por id
     * @param id ID del detalle a modificar.
     * @return Mensaje diciendo si lo elimino o no
     */
    @Override
    public String borrarDetalleCot(int id) {
        try{
            detalleCotizacionRepository.deleteById(id);
            auditoria.registrarAccion("DETALLE_COTIZACION", "Eliminación de Detalle",
                    "ID_DETALLE", String.valueOf(id), "N/A");
            return "Detalle eliminado correctamente";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * Trae todos los detalles en la base de datos
     * @return Una lista con todos los detalles
     */
    @Override
    public List<DetalleCotizacionDTO> listarDetallesCot() {
        List<DetalleCotizacionEntidad> detalle = detalleCotizacionRepository.findAll();
        List<DetalleCotizacionDTO> detalles = new ArrayList<>();

        for (DetalleCotizacionEntidad entidad : detalle){
            detalles.add(new DetalleCotizacionDTO(
               entidad.getId_detalle_cotizacion(),
                    entidad.getId_cotizacion(),
                    entidad.getDescripcion(),
                    entidad.getCantidad(),
                    entidad.getValor_unitario(),
                    entidad.getSubtotal()
            ));
        }
        return detalles;
    }

    /**
     * Actualiza la descripción, cantidad o valores de un detalle existente.
     * @param id ID del detalle a modificar.
     * @param detalle DTO con los nuevos datos.
     * @return Estado de la actualización.
     */
    @Override
    public String actualizarDetalle(int id, DetalleCotizacionDTO detalle) {
        Optional<DetalleCotizacionEntidad> detalleExist = detalleCotizacionRepository.findById(id);
        if (detalleExist.isEmpty()){
            return "Detalle no actualizado";
        }else {
            DetalleCotizacionEntidad entidad = detalleExist.get();
            entidad.setDescripcion(detalle.getDescripcion());
            entidad.setCantidad(detalle.getCantidad());
            entidad.setValor_unitario(detalle.getValor_unitario());
            entidad.setSubtotal(detalle.getSubtotal());

            detalleCotizacionRepository.save(entidad);
            auditoria.registrarAccion("DETALLE_COTIZACION", "Actualización de Detalle",
                    "ID_DETALLE", "Existente", String.valueOf(id));
            return "Detalle Actualizado";
        }
    }
}
