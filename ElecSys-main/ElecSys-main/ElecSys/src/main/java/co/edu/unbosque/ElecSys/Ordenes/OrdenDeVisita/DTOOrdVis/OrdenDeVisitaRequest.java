package co.edu.unbosque.ElecSys.Ordenes.OrdenDeVisita.DTOOrdVis;

import co.edu.unbosque.ElecSys.Ordenes.OrdenDeVisita.DetalleOrdenVisita.DTODetOrdVis.DetalleOrdenVisitaDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenDeVisitaRequest {

    private OrdenDeVisitaDTO orden;
    private List<DetalleOrdenVisitaDTO> detalles;
}
