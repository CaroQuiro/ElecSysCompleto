package co.edu.unbosque.ElecSys.Notificacion.EntidadNot;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificacion_programacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionProgramacionEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prog_gen")
    @SequenceGenerator(name = "prog_gen", sequenceName = "seq_notificacion_programacion_id", allocationSize = 1)
    @Column(name = "id_programacion")
    private long idProgramacion;

    @OneToOne(optional = false)
    @JoinColumn(name = "id_notificacion", nullable = false)
    private NotificacionEntidad notificacion;

    @Column(name = "frecuencia", nullable = false)
    private String frecuencia;
    // DIARIA | SEMANAL | MENSUAL

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column(name = "ultima_ejecucion")
    private LocalDateTime ultimaEjecucion;
}

