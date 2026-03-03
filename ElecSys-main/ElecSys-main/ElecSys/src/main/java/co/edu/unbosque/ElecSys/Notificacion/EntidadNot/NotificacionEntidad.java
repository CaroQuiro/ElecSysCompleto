package co.edu.unbosque.ElecSys.Notificacion.EntidadNot;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "notificacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notificacion_gen")
    @SequenceGenerator(name = "notificacion_gen", sequenceName = "seq_notificacion_id", allocationSize = 1)
    @Column(name = "id_notificacion")
    private long idNotificacion;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "mensaje", nullable = false)
    private String mensaje;

    @Column(name = "tipo", nullable = false)
    private String tipo; // UNICA | RECURRENTE

    @Column(name = "estado", nullable = false)
    private String estado;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    /* ================= RELACIONES ================= */

    @OneToMany(
            mappedBy = "notificacion",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<NotificacionDestinatarioEntidad> destinatarios = new ArrayList<>();

    @OneToOne(
            mappedBy = "notificacion",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private NotificacionProgramacionEntidad programacion;
}

