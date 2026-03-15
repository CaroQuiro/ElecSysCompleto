package co.edu.unbosque.ElecSys.ordenes.ordenDeVisita.dtoOrdVis;

import co.edu.unbosque.ElecSys.ordenes.ordenDeVisita.detalleOrdenVisita.dtoDetOrdVis.DetalleOrdenVisitaDTO;
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
