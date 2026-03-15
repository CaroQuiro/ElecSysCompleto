package co.edu.unbosque.ElecSys.ordenes.ordenDeVisita.servicioOrdVis;

import co.edu.unbosque.ElecSys.ordenes.ordenDeVisita.dtoOrdVis.OrdenDeVisitaDTO;

import java.util.List;

public interface OrdenDeVisitaInterface {

    public int agregarOrdenVisita(OrdenDeVisitaDTO dto);

    public String editarOrdenVisita(int idOrdenAnt, OrdenDeVisitaDTO ordenNueva);

    public String borrarOrdenVisita(int idOrden);

    public List<OrdenDeVisitaDTO> listarOrdenVisita();

    public List<OrdenDeVisitaDTO> listarOrdenVisitaPorCliente(int idCliente);

    public OrdenDeVisitaDTO buscarOrdenVisita(int idOrden);


}
