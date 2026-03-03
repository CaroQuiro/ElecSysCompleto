package co.edu.unbosque.ElecSys.Ordenes.OrdenDeTrabajo.DTOOrdTra;

import co.edu.unbosque.ElecSys.Ordenes.OrdenDeTrabajo.DetalleOrdenTrabajo.DTODetOrdTra.DetalleOrdenTrabajoDTO;
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
