package co.edu.unbosque.ElecSys.cotizacion.detalleCotizacion.servicioDetCot;

import co.edu.unbosque.ElecSys.cotizacion.detalleCotizacion.dtoDetCot.DetalleCotizacionDTO;

import java.util.List;

public interface DetalleCotizacionInterface {
    public String agregarDetalleCot(DetalleCotizacionDTO detalleCotizacionDTO);
    //public CotizacionDTO buscarCotizacion(int id);
    public String borrarDetalleCot(int id);
    public List<DetalleCotizacionDTO> listarDetallesCot();
    public String actualizarDetalle(int id, DetalleCotizacionDTO detalle);
}
