package co.edu.unbosque.ElecSys.Cotizacion.DTOCot;

import co.edu.unbosque.ElecSys.Config.Excepcion.InvalidFieldException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIUDTO {
    private BigDecimal administracion;
    private BigDecimal imprevistos;
    private BigDecimal utilidad;

    public BigDecimal validarPorcentaje(BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) <= 0 || valor.compareTo(BigDecimal.ONE) > 0) {
            throw new InvalidFieldException(" debe estar entre 0 y 1 (ej: 0.07)");
        }
        return valor;
    }
}
