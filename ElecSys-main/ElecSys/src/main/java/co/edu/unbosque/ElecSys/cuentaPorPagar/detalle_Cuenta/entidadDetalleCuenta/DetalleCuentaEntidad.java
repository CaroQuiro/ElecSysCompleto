package co.edu.unbosque.ElecSys.cuentaPorPagar.detalle_Cuenta.entidadDetalleCuenta;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "detalle_cuenta_pagar")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleCuentaEntidad {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "detallecuenta_pagar_seq"
    )
    @SequenceGenerator(
            name = "detallecuenta_pagar_seq",
            sequenceName = "seq_detallecuenta_pagar_id",
            allocationSize = 1
    )
    @Column(name = "id_detalle_cuenta")
    private Integer id_detalle_cuenta;

    @Column(name = "id_cuenta_pagar")
    private int id_cuenta_pagar;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "valor")
    private BigDecimal valor;

}
