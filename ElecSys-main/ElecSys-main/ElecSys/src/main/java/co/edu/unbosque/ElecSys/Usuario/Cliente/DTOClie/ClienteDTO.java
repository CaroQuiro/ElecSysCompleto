package co.edu.unbosque.ElecSys.Usuario.Cliente.DTOClie;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {

    private int id_cliente;
    private String nombre;
    private String telefono;
    private String direccion;
    private String correo;
    private String estado;
}
