package co.edu.unbosque.ElecSys.Usuario.Trabajador.EntidadTra;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "trabajador")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrabajadorEntidad {

    @Id
    @Column(name = "id_trabajador")
    private int id_trabajador;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "correo")
    private String correo;

    @Column(name = "tipo_usuario")
    private String tipo_usuario;

    @Column(name = "password")
    private String password;

    @Column(name = "estado")
    private String estado;
}
