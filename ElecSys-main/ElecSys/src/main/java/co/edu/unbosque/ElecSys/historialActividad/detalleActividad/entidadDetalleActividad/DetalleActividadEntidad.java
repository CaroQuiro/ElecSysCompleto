package co.edu.unbosque.ElecSys.historialActividad.detalleActividad.entidadDetalleActividad;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "detalle_actividad")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleActividadEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "detalle_gen")
    @SequenceGenerator(name = "detalle_gen", sequenceName = "detalle_actividad_seq", allocationSize = 1)
    @Column(name = "id_detalle_actividad")
    private int idDetalleActividad;

    @Column(name = "id_historial", nullable = false)
    private int idHistorial;

    @Column(name = "campo_afectado", length = 200)
    private String campoAfectado;

    @Column(name = "valor_anterior", columnDefinition = "TEXT")
    private String valorAnterior;

    @Column(name = "valor_nuevo", columnDefinition = "TEXT")
    private String valorNuevo;

}
