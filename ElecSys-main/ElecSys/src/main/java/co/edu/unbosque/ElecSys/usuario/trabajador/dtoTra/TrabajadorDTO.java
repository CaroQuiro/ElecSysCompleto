package co.edu.unbosque.ElecSys.usuario.trabajador.dtoTra;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrabajadorDTO {

    private int id_trabajador;
    private String nombre;
    private String telefono;
    private String direccion;
    private String correo;
    private String tipo_usuario;
    private String password;
    private String estado;
}
