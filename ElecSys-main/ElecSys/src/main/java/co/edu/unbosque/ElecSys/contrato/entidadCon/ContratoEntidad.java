package co.edu.unbosque.ElecSys.contrato.entidadCon;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table (name = "contrato")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContratoEntidad {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "contrato_seq"
    )
    @SequenceGenerator(
            name = "contrato_seq",
            sequenceName = "seq_contrato_id",
            allocationSize = 1
    )
    @Column(name = "id_contrato")
    private Integer id_contrato;

    @Column(name = "id_trabajador")
    private int id_trabajador;

    @Column(name = "sueldo")
    private BigDecimal sueldo;

    @Column(name = "fecha_expedicion")
    private LocalDate fecha_expedicion;

    @Column(name = "fecha_iniciacion")
    private LocalDate fecha_iniciacion;

    @Column(name = "id_trabajador_encargado")
    private int id_trabajador_encargado;

    @Column(name = "cargo")
    private String cargo;

    @Column(name = "tipo_contrato")
    private String tipo_contrato;

    @Column(name = "estado")
    private String estado;

    @Column(name = "fecha_nacimiento")
    private LocalDate fecha_nacimiento;

    @Column(name = "lugar_nacimiento")
    private String lugar_nacimiento;

    @Column(name = "edad")
    private int edad;

    @Column(name = "estadocivil")
    private String estadoCivil;
}
