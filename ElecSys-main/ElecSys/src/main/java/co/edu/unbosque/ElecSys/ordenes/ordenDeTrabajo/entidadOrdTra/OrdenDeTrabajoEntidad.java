package co.edu.unbosque.ElecSys.ordenes.ordenDeTrabajo.entidadOrdTra;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Entity
@Table(name = "orden_trabajo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenDeTrabajoEntidad {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trabajo_gen")
    @SequenceGenerator(name = "trabajo_gen", sequenceName = "seq_orden_trabajo_id", allocationSize = 1)
    @Column(name = "id_orden")
    private Integer idOrden;

    @Column(name = "id_orden_visita", nullable = true)
    private Integer idOrdenVisita;

    @Column(name = "id_lugar", nullable = false)
    private Integer idLugar;

    @Column(name = "id_cliente", nullable = false)
    private Integer idCliente;

    @Column(name = "id_trabajador", nullable = false)
    private Integer idTrabajador;

    @Column(name = "fecha_realizacion")
    private Date fechaRealizacion;

    @Column(name = "estado")
    private String estado;
}
