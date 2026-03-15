package co.edu.unbosque.ElecSys.historialActividad.servicioHis;

import co.edu.unbosque.ElecSys.historialActividad.dtoHis.HistorialActividadDTO;

import java.util.Date;
import java.util.List;

public interface HistorialActividadInterface {

    public String agregarHistorialActividad(HistorialActividadDTO dto);

    public List<HistorialActividadDTO> listarHistorialActividad();

    public HistorialActividadDTO buscarHistorialActividad(int idHistorial);


}
