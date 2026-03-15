package co.edu.unbosque.ElecSys.historialActividad.dtoHis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialActividadDTO {

    private int idHistorial;
    private int idTrabajador;
    private String moduloSistema;
    private String accionRealizada;
    private LocalDate fechaRealizacion;
    private LocalTime hora;

}
