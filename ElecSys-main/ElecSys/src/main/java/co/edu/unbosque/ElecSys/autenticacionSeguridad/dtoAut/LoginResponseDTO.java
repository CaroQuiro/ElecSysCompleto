package co.edu.unbosque.ElecSys.autenticacionSeguridad.dtoAut;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO de respuesta que contiene la información de la sesión exitosa.
 */
@Data
@AllArgsConstructor
public class LoginResponseDTO {
    private String token;
    private int idTrabajador;
    private String nombre;
    private String cargo;
    private String mensaje;
}

