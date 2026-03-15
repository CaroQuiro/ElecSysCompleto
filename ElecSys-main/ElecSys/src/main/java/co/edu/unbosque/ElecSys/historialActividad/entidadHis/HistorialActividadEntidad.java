package co.edu.unbosque.ElecSys.historialActividad.entidadHis;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;


@Entity
@Table(name = "historial_actividad")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialActividadEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "historial_gen")
    @SequenceGenerator(name = "historial_gen", sequenceName = "historial_actividad_seq", allocationSize = 1)
    @Column(name = "id_historial")
    private int idHistorial;

    @Column(name = "id_trabajador", nullable = false)
    private int idTrabajador;

    @Column(name = "modulo", nullable = false)
    private String moduloSistema;

    @Column(name = "accion_realizada", nullable = false)
    private String accionRealizada;

    @Column(name = "fecha_realizacion", nullable = false)
    private LocalDate fechaRealizacion;

    @Column(name = "hora", nullable = false)
    private LocalTime hora;

}