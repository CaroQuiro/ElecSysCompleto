package co.edu.unbosque.ElecSys.contrato.servicioCon;

import co.edu.unbosque.ElecSys.contrato.dtoCon.ContratoDTO;

import java.util.List;

public interface ContratoInterface {
    public ContratoDTO agregarContrato(ContratoDTO contrato);
    public List<ContratoDTO> listarcontratos();
    public ContratoDTO buscarContrato(int id);
}
