package co.edu.unbosque.ElecSys.ordenes.ordenDeVisita.detalleOrdenVisita.dtoDetOrdVis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleOrdenVisitaDTO {
    private int idDetalleVisita;
    private int idVisita;
    private String actividad;
    private String observaciones;
    private String duracion;
}
