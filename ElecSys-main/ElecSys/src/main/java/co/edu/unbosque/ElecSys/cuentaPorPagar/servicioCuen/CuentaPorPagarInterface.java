package co.edu.unbosque.ElecSys.cuentaPorPagar.servicioCuen;

import co.edu.unbosque.ElecSys.cuentaPorPagar.dtoCuen.CuentaPorPagarDTO;

import java.util.List;

public interface CuentaPorPagarInterface {
    public CuentaPorPagarDTO agregarCuentaPagar(CuentaPorPagarDTO cuenta);
    //public CotizacionDTO buscarCotizacion(int id);
    public String borrarCuentaPagar(int id);
    public List<CuentaPorPagarDTO> listarCuentasPagar();
    public String actualizarCuenta(int id, CuentaPorPagarDTO cuentaPorPagarDTO);
    public Boolean existeCuenta(int id);
    public CuentaPorPagarDTO buscarCuenta(int id);
}
