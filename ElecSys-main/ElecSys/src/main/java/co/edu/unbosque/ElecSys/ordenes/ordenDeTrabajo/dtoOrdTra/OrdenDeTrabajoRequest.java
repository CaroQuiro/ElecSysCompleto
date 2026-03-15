package co.edu.unbosque.ElecSys.ordenes.ordenDeTrabajo.dtoOrdTra;

import co.edu.unbosque.ElecSys.ordenes.ordenDeTrabajo.detalleOrdenTrabajo.dtoDetOrdTra.DetalleOrdenTrabajoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdenDeTrabajoRequest {

    private OrdenDeTrabajoDTO orden;
    private List<DetalleOrdenTrabajoDTO> detalles;

}
