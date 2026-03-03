package co.edu.unbosque.ElecSys.Ordenes.OrdenDeVisita.DetalleOrdenVisita.ServicioDetOrdVis;


import co.edu.unbosque.ElecSys.Ordenes.OrdenDeVisita.DetalleOrdenVisita.DTODetOrdVis.DetalleOrdenVisitaDTO;

import java.util.List;

public interface DetalleOrdenVisitaInterface {
    public String agregarDetalleOrdVis(DetalleOrdenVisitaDTO detalleOrdenvisitaDTO);
    public String borrarDetalleOrdVis(int id);
    public List<DetalleOrdenVisitaDTO> listarDetallesOrdVis();
    public String actualizarDetalleOrdVis(int id, DetalleOrdenVisitaDTO detalle);
}
