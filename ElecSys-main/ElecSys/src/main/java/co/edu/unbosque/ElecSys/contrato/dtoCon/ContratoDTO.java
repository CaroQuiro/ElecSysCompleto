package co.edu.unbosque.ElecSys.contrato.dtoCon;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContratoDTO {

    private int id_contrato;
    private int id_trabajador;
    private BigDecimal sueldo;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha_expedicion;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha_iniciacion;

    private int id_trabajador_encargado;
    private String cargo;
    private String tipo_contrato;
    private String estado;
}
