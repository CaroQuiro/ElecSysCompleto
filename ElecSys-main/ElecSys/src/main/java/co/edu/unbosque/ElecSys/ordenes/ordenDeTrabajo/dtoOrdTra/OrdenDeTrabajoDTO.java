package co.edu.unbosque.ElecSys.ordenes.ordenDeTrabajo.dtoOrdTra;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenDeTrabajoDTO {

    private Integer id_orden;
    private Integer id_orden_visita;
    private Integer id_lugar;
    private Integer id_cliente;
    private Integer id_trabajador;
    private Date fecha_realizacion;
    private String estado;

}
