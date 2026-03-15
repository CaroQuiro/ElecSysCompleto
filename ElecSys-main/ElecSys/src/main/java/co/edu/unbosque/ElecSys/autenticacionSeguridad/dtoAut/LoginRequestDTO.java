package co.edu.unbosque.ElecSys.autenticacionSeguridad.dtoAut;

import lombok.Data;

/**
 * DTO para la solicitud de login inicial.
 */
@Data
public class LoginRequestDTO {
    private String usuario;
    private String correo;
    private String password;
}

