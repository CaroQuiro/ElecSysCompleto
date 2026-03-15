package co.edu.unbosque.ElecSys.lugarTrabajo.servicioLug;

import co.edu.unbosque.ElecSys.lugarTrabajo.entidadLug.LugarTrabajoEntidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LugarTrabajoRepository extends JpaRepository<LugarTrabajoEntidad, Integer> {

    @Query("""
    select l from LugarTrabajoEntidad l where 
        LOWER(l.nombre_lugar) LIKE LOWER(concat('%', :query, '%')) OR 
        l.direccion LIKE CONCAT('%', :query, '%') 
    """)

    List<LugarTrabajoEntidad> buscarLugarTexto(@Param("query") String query);
}
