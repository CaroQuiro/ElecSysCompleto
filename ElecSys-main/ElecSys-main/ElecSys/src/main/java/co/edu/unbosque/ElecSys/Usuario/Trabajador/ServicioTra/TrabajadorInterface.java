package co.edu.unbosque.ElecSys.Usuario.Trabajador.ServicioTra;

import co.edu.unbosque.ElecSys.Cotizacion.DTOCot.CotizacionDTO;
import co.edu.unbosque.ElecSys.Usuario.Trabajador.DTOTra.TrabajadorDTO;

import java.util.List;

public interface TrabajadorInterface {

    public String agregarTrabajador(TrabajadorDTO trabajadorDTO);
    public TrabajadorDTO buscarTrabajador(int id);
    public String deshabilitarTrabajador(int id);
    public List<TrabajadorDTO> listarTrabajadores();
    public String actualizarTrabajador(int id, TrabajadorDTO trabajadorDTO);
}
