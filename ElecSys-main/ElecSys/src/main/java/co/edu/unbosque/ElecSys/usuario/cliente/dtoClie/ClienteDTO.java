package co.edu.unbosque.ElecSys.usuario.cliente.dtoClie;

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
