package co.edu.unbosque.ElecSys.cuentaPorPagar.entidadCuen;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cuenta_pagar")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuentaPorPagarEntidad {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "cuenta_pagar_seq"
    )
    @SequenceGenerator(
            name = "cuenta_pagar_seq",
            sequenceName = "seq_cuenta_pagar_id",
            allocationSize = 1
    )
    @Column(name = "id_cuenta_pagar")
    private Integer id_cuenta_pagar;

    @Column(name = "id_trabajador")
    private int id_trabajador;

    @Column(name = "id_cliente")
    private int id_cliente;

    @Column(name = "nota")
    private String nota;

    @Column(name = "fecha_realizacion")
    private LocalDate fecha_realizacion;

    @Column(name = "monto")
    private BigDecimal monto;

    @Column(name = "estado")
    private String estado;
}
