package co.edu.unbosque.ElecSys.Usuario.Trabajador.ServicioTra;

import co.edu.unbosque.ElecSys.Usuario.Trabajador.EntidadTra.TrabajadorEntidad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrabajadorRepository extends JpaRepository<TrabajadorEntidad, Integer> {
    TrabajadorEntidad findByCorreo(String correo);
}
