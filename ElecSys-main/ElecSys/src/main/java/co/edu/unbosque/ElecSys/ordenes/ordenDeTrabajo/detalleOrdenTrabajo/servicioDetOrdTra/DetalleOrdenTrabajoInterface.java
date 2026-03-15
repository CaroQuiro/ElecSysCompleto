package co.edu.unbosque.ElecSys.ordenes.ordenDeTrabajo.detalleOrdenTrabajo.servicioDetOrdTra;


import co.edu.unbosque.ElecSys.ordenes.ordenDeTrabajo.detalleOrdenTrabajo.dtoDetOrdTra.DetalleOrdenTrabajoDTO;

import java.util.List;

public interface DetalleOrdenTrabajoInterface {
    public String agregarDetalleOrdTra(DetalleOrdenTrabajoDTO detalleOrdenTrabajoDTO);
    public String borrarDetalleOrdTra(int id);
    public List<DetalleOrdenTrabajoDTO> listarDetallesOrdTra();
    public String actualizarDetalleOrdTra(int id, DetalleOrdenTrabajoDTO detalle);
}
