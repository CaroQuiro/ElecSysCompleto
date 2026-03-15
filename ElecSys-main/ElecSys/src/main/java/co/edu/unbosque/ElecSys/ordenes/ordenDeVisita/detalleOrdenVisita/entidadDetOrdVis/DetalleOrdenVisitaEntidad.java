package co.edu.unbosque.ElecSys.ordenes.ordenDeVisita.detalleOrdenVisita.entidadDetOrdVis;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "detalle_orden_visita")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleOrdenVisitaEntidad {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "det_visita_gen")
    @SequenceGenerator(
            name = "det_visita_gen",
            sequenceName = "seq_detalle_orden_visita_id",
            allocationSize = 1
    )
    @Column(name = "id_detalle_visita")
    private Integer id_detalle_visita;

    @Column(name = "id_visita", nullable = false)
    private int id_visita;

    @Column(name = "actividad", nullable = false)
    private String actividad;

    @Column(name = "observaciones", nullable = false)
    private String observaciones;

    @Column(name = "duracion", nullable = false)
    private String duracion;
}
