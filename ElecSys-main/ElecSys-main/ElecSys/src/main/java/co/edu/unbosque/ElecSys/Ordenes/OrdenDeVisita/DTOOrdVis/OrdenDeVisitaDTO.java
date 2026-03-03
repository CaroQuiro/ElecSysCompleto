package co.edu.unbosque.ElecSys.Ordenes.OrdenDeVisita.DTOOrdVis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenDeVisitaDTO {

    private int idVisita;
    private int idLugar;
    private int idCliente;
    private int idTrabajador;
    private Date fechaRealizacion;
    private String descripcion;
    private String estado;
}
