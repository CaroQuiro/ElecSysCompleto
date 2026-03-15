package co.edu.unbosque.ElecSys.cotizacion.dtoCot;

import co.edu.unbosque.ElecSys.cotizacion.detalleCotizacion.dtoDetCot.DetalleCotizacionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CotizacionRequest {

    private CotizacionDTO cotizacion;
    private List<DetalleCotizacionDTO> detalleCotizacionDTOS;
    private Boolean existIva;
    private AIUDTO aiudto;

}
