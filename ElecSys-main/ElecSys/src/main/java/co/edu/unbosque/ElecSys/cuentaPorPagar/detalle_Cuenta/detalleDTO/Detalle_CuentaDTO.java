package co.edu.unbosque.ElecSys.cuentaPorPagar.detalle_Cuenta.detalleDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Detalle_CuentaDTO {

    private int id_detalle_cuenta;
    private int id_cuenta_pagar;
    private String descripcion;
    private BigDecimal valor;

}
