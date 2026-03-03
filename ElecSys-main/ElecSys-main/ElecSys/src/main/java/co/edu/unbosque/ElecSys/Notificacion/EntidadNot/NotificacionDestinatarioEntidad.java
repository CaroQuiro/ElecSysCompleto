package co.edu.unbosque.ElecSys.Notificacion.EntidadNot;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificacion_destinatario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionDestinatarioEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dest_gen")
    @SequenceGenerator(name = "dest_gen", sequenceName = "seq_notificacion_destinatario_id", allocationSize = 1)
    @Column(name = "id_notificacion_destinatario")
    private long idNotificacionDestinatario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_notificacion", nullable = false)
    private NotificacionEntidad notificacion;

    @Column(name = "tipo_destinatario", nullable = false)
    private String tipoDestinatario; // CLIENTE | TRABAJADOR

    @Column(name = "id_cliente")
    private Integer idCliente;

    @Column(name = "id_trabajador")
    private Integer idTrabajador;

    @Column(name = "enviado", nullable = false)
    private boolean enviado;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;
}

