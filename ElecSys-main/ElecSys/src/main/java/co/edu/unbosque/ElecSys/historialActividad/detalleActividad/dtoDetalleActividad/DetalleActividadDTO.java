package co.edu.unbosque.ElecSys.historialActividad.detalleActividad.dtoDetalleActividad;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleActividadDTO {

    private int idDetalleActividad;
    private int idHistorial;
    private String campoAfectado;
    private String valorAnterior;
    private String valorNuevo;

}
