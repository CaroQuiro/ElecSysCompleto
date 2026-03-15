package co.edu.unbosque.ElecSys.cotizacion.servicioCot;

import co.edu.unbosque.ElecSys.cotizacion.dtoCot.CotizacionDTO;

import java.util.List;

public interface CotizacionInterface {
    public CotizacionDTO agregarCotizacion(CotizacionDTO cotizacion);
    public CotizacionDTO buscarCotizacion(int id);
    public String borrarCotizacion(int id);
    public List<CotizacionDTO> listarCotizacion();
    public String actualizarCot(int id, CotizacionDTO cotizacion);
    public int cantidadCotizacionesPorCliente(int idCliente);
}
