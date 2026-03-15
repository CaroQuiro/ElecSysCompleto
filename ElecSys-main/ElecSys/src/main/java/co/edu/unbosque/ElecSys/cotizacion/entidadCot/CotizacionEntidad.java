package co.edu.unbosque.ElecSys.cotizacion.entidadCot;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cotizacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CotizacionEntidad {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "cotizacion_seq"
    )
    @SequenceGenerator(
            name = "cotizacion_seq",
            sequenceName = "seq_cotizacion_id",
            allocationSize = 1
    )
    @Column(name = "id_cotizacion")
    private Integer id_cotizacion;

    @Column(name = "id_trabajador")
    private int id_trabajador;

    @Column(name = "id_cliente")
    private int id_cliente;

    @Column(name = "id_lugar")
    private int id_lugar;

    @Column(name = "fecha_realizacion")
    private LocalDate fecha_realizacion;

    @Column(name = "referencia")
    private String referencia;

    @Column(name = "valor_total")
    private BigDecimal valor_total;

    @Column(columnDefinition = "estado_cotizacion")
    private String estado;

    @Column(name = "administracion")
    private BigDecimal administracion;

    @Column(name = "imprevistos")
    private BigDecimal imprevistos;

    @Column(name = "utilidad")
    private BigDecimal utilidad;

    @Column(name = "iva")
    private BigDecimal iva;

    @Column(name = "total_pagar")
    private BigDecimal total_pagar;

}
