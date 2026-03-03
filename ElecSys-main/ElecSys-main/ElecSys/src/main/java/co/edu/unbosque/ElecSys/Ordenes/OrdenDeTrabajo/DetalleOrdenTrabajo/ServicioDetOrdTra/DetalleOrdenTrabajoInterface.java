package co.edu.unbosque.ElecSys.Ordenes.OrdenDeTrabajo.DetalleOrdenTrabajo.ServicioDetOrdTra;


import co.edu.unbosque.ElecSys.Ordenes.OrdenDeTrabajo.DetalleOrdenTrabajo.DTODetOrdTra.DetalleOrdenTrabajoDTO;

import java.util.List;

public interface DetalleOrdenTrabajoInterface {
    public String agregarDetalleOrdTra(DetalleOrdenTrabajoDTO detalleOrdenTrabajoDTO);
    public String borrarDetalleOrdTra(int id);
    public List<DetalleOrdenTrabajoDTO> listarDetallesOrdTra();
    public String actualizarDetalleOrdTra(int id, DetalleOrdenTrabajoDTO detalle);
}
