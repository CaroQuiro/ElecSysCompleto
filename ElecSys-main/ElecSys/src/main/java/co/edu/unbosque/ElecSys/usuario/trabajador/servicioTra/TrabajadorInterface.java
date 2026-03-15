package co.edu.unbosque.ElecSys.usuario.trabajador.servicioTra;

import co.edu.unbosque.ElecSys.usuario.trabajador.dtoTra.TrabajadorDTO;

import java.util.List;

public interface TrabajadorInterface {

    public String agregarTrabajador(TrabajadorDTO trabajadorDTO);
    public TrabajadorDTO buscarTrabajador(int id);
    public String deshabilitarTrabajador(int id);
    public List<TrabajadorDTO> listarTrabajadores();
    public String actualizarTrabajador(int id, TrabajadorDTO trabajadorDTO);
}
