package co.edu.unbosque.ElecSys.ordenes.ordenDeTrabajo.detalleOrdenTrabajo.dtoDetOrdTra;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleOrdenTrabajoDTO {

    private Integer idDetalleTrabajo;
    private Integer idOrden;
    private String actividad;
    private String observaciones;
    private String duracion;
}
