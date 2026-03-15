package co.edu.unbosque.ElecSys.usuario.trabajador.servicioTra;

import co.edu.unbosque.ElecSys.usuario.trabajador.entidadTra.TrabajadorEntidad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrabajadorRepository extends JpaRepository<TrabajadorEntidad, Integer> {
    TrabajadorEntidad findByCorreo(String correo);
}
