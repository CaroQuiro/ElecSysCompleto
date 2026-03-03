package co.edu.unbosque.ElecSys.Ordenes.OrdenDeVisita.ServicioOrdVis;

import co.edu.unbosque.ElecSys.Ordenes.OrdenDeVisita.DTOOrdVis.OrdenDeVisitaDTO;
import co.edu.unbosque.ElecSys.Ordenes.OrdenDeVisita.EntidadOrdVis.OrdenDeVisitaEntidad;

import java.util.List;

public interface OrdenDeVisitaInterface {

    public int agregarOrdenVisita(OrdenDeVisitaDTO dto);

    public String editarOrdenVisita(int idOrdenAnt, OrdenDeVisitaDTO ordenNueva);

    public String borrarOrdenVisita(int idOrden);

    public List<OrdenDeVisitaDTO> listarOrdenVisita();

    public List<OrdenDeVisitaDTO> listarOrdenVisitaPorCliente(int idCliente);

    public OrdenDeVisitaDTO buscarOrdenVisita(int idOrden);


}
