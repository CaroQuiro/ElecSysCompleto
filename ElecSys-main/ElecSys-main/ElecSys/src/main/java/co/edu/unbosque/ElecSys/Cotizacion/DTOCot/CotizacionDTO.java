package co.edu.unbosque.ElecSys.Cotizacion.DTOCot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CotizacionDTO {

    private int id_cotizacion;
    private int id_trabajador;
    private int id_cliente;
    private int id_lugar;
    private LocalDate fecha_realizacion;
    private String referencia;
    private BigDecimal valor_total;
    private String estado;
    private BigDecimal administracion;
    private BigDecimal imprevistos;
    private BigDecimal utilidad;
    private BigDecimal iva;
    private  BigDecimal total_pagar;


}
