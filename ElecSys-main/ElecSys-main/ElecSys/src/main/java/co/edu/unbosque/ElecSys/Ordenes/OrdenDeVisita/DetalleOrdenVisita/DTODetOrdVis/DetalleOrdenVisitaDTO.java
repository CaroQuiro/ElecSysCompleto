package co.edu.unbosque.ElecSys.Ordenes.OrdenDeVisita.DetalleOrdenVisita.DTODetOrdVis;

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
