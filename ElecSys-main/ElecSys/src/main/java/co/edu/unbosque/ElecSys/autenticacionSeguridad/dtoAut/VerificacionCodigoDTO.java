package co.edu.unbosque.ElecSys.autenticacionSeguridad.dtoAut;

import lombok.Data;

/**
 * DTO para la verificación del código enviado por correo.
 */
@Data
public class VerificacionCodigoDTO {
    private String correo;
    private String codigo;
}

