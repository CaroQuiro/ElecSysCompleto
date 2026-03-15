package co.edu.unbosque.ElecSys.notificacion.dtoNot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramadasRequest {
    public NotificacionDTO notificacion;
    public String frecuencia;
    public java.time.LocalDateTime fechaInicio;
    public java.time.LocalDateTime fechaFin;
    public List<Integer> idsDestinatarios;
    public String tipoDestinatario;
}
