package co.edu.unbosque.ElecSys.Usuario.Cliente.ServicioClie;

import co.edu.unbosque.ElecSys.Usuario.Cliente.EntidadClie.ClienteEntidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClienteRepository extends JpaRepository<ClienteEntidad, Integer> {

    @Query("""
    select c from ClienteEntidad c where 
        LOWER(c.nombre) LIKE LOWER(concat('%', :query, '%')) OR 
        LOWER(c.correo) LIKE LOWER(concat('%', :query, '%')) OR 
        c.telefono LIKE CONCAT('%', :query, '%') 
    """)

    List<ClienteEntidad> buscarClienteTexto(@Param("query") String query);
}
