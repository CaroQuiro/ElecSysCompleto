package co.edu.unbosque.ElecSys.historialActividad.detalleActividad.servicioDetalleActividad;


import co.edu.unbosque.ElecSys.historialActividad.detalleActividad.dtoDetalleActividad.DetalleActividadDTO;

import java.util.List;

public interface DetalleActividadInterface {

    public String agregarDetalleActividad(DetalleActividadDTO dto);

    public List<DetalleActividadDTO> listarDetalleActividadPorIdHistorial(int idHistorial);

    public DetalleActividadDTO buscarDetalleActividad(int idDetalleActividad);
}
