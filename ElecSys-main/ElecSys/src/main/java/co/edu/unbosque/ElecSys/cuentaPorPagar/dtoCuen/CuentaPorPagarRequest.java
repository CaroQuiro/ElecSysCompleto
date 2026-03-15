package co.edu.unbosque.ElecSys.cuentaPorPagar.dtoCuen;

import co.edu.unbosque.ElecSys.cuentaPorPagar.detalle_Cuenta.detalleDTO.Detalle_CuentaDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CuentaPorPagarRequest {

    private CuentaPorPagarDTO cuentaPorPagarDTO;
    private String referencia;
    private List<Detalle_CuentaDTO> detalleCuentaDTOS;

}
