package co.edu.unbosque.ElecSys.ordenes.ordenDeVisita.detalleOrdenVisita.servicioDetOrdVis;


import co.edu.unbosque.ElecSys.ordenes.ordenDeVisita.detalleOrdenVisita.dtoDetOrdVis.DetalleOrdenVisitaDTO;

import java.util.List;

public interface DetalleOrdenVisitaInterface {
    public String agregarDetalleOrdVis(DetalleOrdenVisitaDTO detalleOrdenvisitaDTO);
    public String borrarDetalleOrdVis(int id);
    public List<DetalleOrdenVisitaDTO> listarDetallesOrdVis();
    public String actualizarDetalleOrdVis(int id, DetalleOrdenVisitaDTO detalle);
}
