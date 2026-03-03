package co.edu.unbosque.ElecSys.Notificacion.DTONot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionDTO {

    private long idNotificacion;
    private String titulo;
    private String mensaje;
    private String tipo;
    private String estado;
    private LocalDateTime fechaCreacion;

}
