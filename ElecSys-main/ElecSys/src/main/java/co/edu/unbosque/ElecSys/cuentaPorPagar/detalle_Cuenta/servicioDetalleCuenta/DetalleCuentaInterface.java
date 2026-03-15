package co.edu.unbosque.ElecSys.cuentaPorPagar.detalle_Cuenta.servicioDetalleCuenta;


import co.edu.unbosque.ElecSys.cuentaPorPagar.detalle_Cuenta.detalleDTO.Detalle_CuentaDTO;

import java.util.List;

public interface DetalleCuentaInterface {

    public Detalle_CuentaDTO agregarDetalleCuenta(Detalle_CuentaDTO detalle);
    public String borrarDetalleCuenta(int id);
    public List<Detalle_CuentaDTO> listarDetallesCuentas();
    public String actualizarDetalleCuenta(int id, Detalle_CuentaDTO detalle);
    public Detalle_CuentaDTO buscarDetallesCuentas(int id);
}
