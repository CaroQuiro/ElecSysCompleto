package co.edu.unbosque.ElecSys.Cotizacion.DTOCot;

import co.edu.unbosque.ElecSys.Cotizacion.DetalleCotizacion.DTODetCot.DetalleCotizacionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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
