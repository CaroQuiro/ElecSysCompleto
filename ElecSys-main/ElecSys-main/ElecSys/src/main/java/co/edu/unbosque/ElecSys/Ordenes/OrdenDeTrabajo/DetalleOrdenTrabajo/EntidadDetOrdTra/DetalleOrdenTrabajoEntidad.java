package co.edu.unbosque.ElecSys.Ordenes.OrdenDeTrabajo.DetalleOrdenTrabajo.EntidadDetOrdTra;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "detalle_orden_trabajo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleOrdenTrabajoEntidad {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "det_trabajo_gen")
    @SequenceGenerator(name = "det_trabajo_gen", sequenceName = "seq_detalle_orden_trabajo_id", allocationSize = 1)
    @Column(name = "id_detalle_trabajo")
    private Integer idDetalleTrabajo;

    @Column(name = "id_orden", nullable = false)
    private Integer idOrden;

    @Column(name = "actividad", nullable = false)
    private String actividad;

    @Column(name = "observaciones", nullable = false)
    private String observaciones;

    @Column(name = "duracion", nullable = false)
    private String duracion;


}
