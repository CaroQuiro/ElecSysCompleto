package co.edu.unbosque.ElecSys.LugarTrabajo.EntidadLug;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lugar")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LugarTrabajoEntidad {

    @Id
    @Column(name = "id_lugar")
    private int id_lugar;

    @Column(name = "nombre_lugar")
    private String nombre_lugar;

    @Column(name = "direccion")
    private String direccion;

}
