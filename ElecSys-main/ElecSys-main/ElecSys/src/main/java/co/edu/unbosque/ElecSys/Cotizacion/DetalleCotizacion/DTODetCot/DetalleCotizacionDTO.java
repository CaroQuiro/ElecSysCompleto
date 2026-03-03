package co.edu.unbosque.ElecSys.Cotizacion.DetalleCotizacion.DTODetCot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetalleCotizacionDTO {

    private int id_detalle_cotizacion;
    private int id_cotizacion;
    private String descripcion;
    private int cantidad;
    private BigDecimal valor_unitario;
    private BigDecimal subtotal;

}
