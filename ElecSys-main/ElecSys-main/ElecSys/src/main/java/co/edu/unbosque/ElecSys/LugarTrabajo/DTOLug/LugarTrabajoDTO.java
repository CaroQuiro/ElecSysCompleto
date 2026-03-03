package co.edu.unbosque.ElecSys.LugarTrabajo.DTOLug;

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
