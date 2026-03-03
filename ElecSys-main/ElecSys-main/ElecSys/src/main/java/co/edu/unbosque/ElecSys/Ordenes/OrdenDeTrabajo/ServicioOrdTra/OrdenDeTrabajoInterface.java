package co.edu.unbosque.ElecSys.Ordenes.OrdenDeTrabajo.ServicioOrdTra;

import co.edu.unbosque.ElecSys.Ordenes.OrdenDeTrabajo.DTOOrdTra.OrdenDeTrabajoDTO;

import java.util.List;

public interface OrdenDeTrabajoInterface {

    public Integer agregarOrdenTrabajo(OrdenDeTrabajoDTO dto);

    public String editarOrdenTrabajo(int idOrdenAnt, OrdenDeTrabajoDTO ordenNueva);

    public String borrarOrdenTrabajo(int idOrden);

    public List<OrdenDeTrabajoDTO> listarOrdenTrabajo();

    public List<OrdenDeTrabajoDTO> listarOrdenTrabajoPorCliente(int idCliente);

    public OrdenDeTrabajoDTO buscarOrdenTrabajo(int idOrden);
}
