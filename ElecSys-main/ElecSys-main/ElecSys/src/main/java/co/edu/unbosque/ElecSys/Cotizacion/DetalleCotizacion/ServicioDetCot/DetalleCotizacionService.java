package co.edu.unbosque.ElecSys.Cotizacion.DetalleCotizacion.ServicioDetCot;

import co.edu.unbosque.ElecSys.Cotizacion.DetalleCotizacion.DTODetCot.DetalleCotizacionDTO;
import co.edu.unbosque.ElecSys.Cotizacion.DetalleCotizacion.EntidadDetCot.DetalleCotizacionEntidad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DetalleCotizacionService implements DetalleCotizacionInterface{

    @Autowired
    private DetalleCotizacionRepository detalleCotizacionRepository;

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
            detalleCotizacionRepository.save(nuevodetalle);
            return "Detalle creado";
        } catch (RuntimeException e) {
            return "Error al crear un nuevo detalle";
        }
    }

    @Override
    public String borrarDetalleCot(int id) {
        try{
            detalleCotizacionRepository.deleteById(id);
            return "Detalle eliminado correctamente";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

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
            return "Detalle Actualizado";
        }
    }
}
