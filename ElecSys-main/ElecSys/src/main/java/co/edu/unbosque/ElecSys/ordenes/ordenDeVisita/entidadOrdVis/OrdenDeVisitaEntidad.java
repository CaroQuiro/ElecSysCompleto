package co.edu.unbosque.ElecSys.ordenes.ordenDeVisita.entidadOrdVis;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
@Entity
@Table(name = "orden_visita")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenDeVisitaEntidad {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "visita_gen")
    @SequenceGenerator(name = "visita_gen", sequenceName = "seq_orden_visita_id", allocationSize = 1)
    @Column(name = "id_visita")
    private Integer idVisita; // Integer permite que sea null para activar la secuencia // Antes id_visita

    @Column(name = "id_lugar", nullable = false)
    private Integer idLugar;

    @Column(name = "id_cliente", nullable = false)
    private Integer idCliente; // Aquí es donde estaba el error

    @Column(name = "id_trabajador", nullable = false)
    private Integer idTrabajador;

    @Column(name = "fecha_realizacion")
    private Date fechaRealizacion;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "estado")
    private String estado;
}