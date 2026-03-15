package co.edu.unbosque.ElecSys.lugarTrabajo.dtoLug;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LugarTrabajoDTO {

    private int idLugar;
    private String nombreLugar;
    private String direccion;

}
