package co.edu.unbosque.ElecSys.usuario.trabajador.servicioTra;

import co.edu.unbosque.ElecSys.usuario.trabajador.entidadTra.TrabajadorEntidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrabajadorRepository extends JpaRepository<TrabajadorEntidad, Integer> {
    Optional<TrabajadorEntidad> findByCorreo(String correo);
}
