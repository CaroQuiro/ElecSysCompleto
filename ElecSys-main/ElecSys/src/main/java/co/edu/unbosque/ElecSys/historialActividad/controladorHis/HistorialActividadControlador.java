package co.edu.unbosque.ElecSys.historialActividad.controladorHis;

import co.edu.unbosque.ElecSys.config.excepcion.DuplicateResourceException;
import co.edu.unbosque.ElecSys.config.excepcion.InvalidFieldException;
import co.edu.unbosque.ElecSys.config.excepcion.ResourceNotFoundException;
import co.edu.unbosque.ElecSys.historialActividad.dtoHis.HistorialActividadDTO;
import co.edu.unbosque.ElecSys.historialActividad.detalleActividad.dtoDetalleActividad.DetalleActividadDTO;
import co.edu.unbosque.ElecSys.historialActividad.servicioHis.HistorialActividadService;
import co.edu.unbosque.ElecSys.historialActividad.detalleActividad.servicioDetalleActividad.DetalleActividadService;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

/**
 * Controlador REST encargado de gestionar el registro y consulta de la auditoría del sistema.
 * Permite realizar el seguimiento de las acciones ejecutadas por los trabajadores,
 * incluyendo detalles específicos de los cambios realizados en los datos.
 */
@RestController
@RequestMapping("/api/internal/historial-actividad")
@CrossOrigin(origins = "http://localhost:4200")
public class HistorialActividadControlador {

    @Autowired
    private HistorialActividadService historialService;

    @Autowired
    private DetalleActividadService detalleService;

    @Data
    public static class HistorialConDetallesDTO {
        private HistorialActividadDTO historial;
        private List<DetalleActividadDTO> detalles;
    }


    /**
     * Recupera el listado completo de todas las actividades registradas en el sistema.
     * @return ResponseEntity con la lista de objetos {@link HistorialActividadDTO}.
     * @throws ResourceNotFoundException Si la base de datos de historial está vacía.
     */
    @GetMapping("/listar")
    public ResponseEntity<List<HistorialActividadDTO>> listarHistorialActividad() {
        List<HistorialActividadDTO> lista = historialService.listarHistorialActividad();

        if (lista.isEmpty()) {
            throw new ResourceNotFoundException("No hay registros de historial.");
        }

        return ResponseEntity.ok(lista);
    }


    /**
     * Busca un registro de historial específico mediante su identificador único.
     * @param id Identificador del registro de historial.
     * @return ResponseEntity con el objeto {@link HistorialActividadDTO} encontrado.
     * @throws ResourceNotFoundException Si no existe un registro con el ID proporcionado.
     */
    @GetMapping("/buscar/{id}")
    public ResponseEntity<HistorialActividadDTO> buscarPorId(@PathVariable int id) {

        HistorialActividadDTO historial = historialService.buscarHistorialActividad(id);

        if (historial == null) {
            throw new ResourceNotFoundException("No existe historial con ID: " + id);
        }

        return ResponseEntity.ok(historial);
    }

    /**
     * Recupera los detalles técnicos (valores anteriores y nuevos) asociados a un historial.
     * @param idHistorial ID del registro de historial padre.
     * @return ResponseEntity con la lista de detalles técnicos.
     * @throws ResourceNotFoundException Si el historial no posee detalles registrados.
     */
    @GetMapping("/detalle/listar/{idHistorial}")
    public ResponseEntity<List<DetalleActividadDTO>> listarDetallePorHistorial(
            @PathVariable int idHistorial) {

        List<DetalleActividadDTO> lista = detalleService.listarDetalleActividadPorIdHistorial(idHistorial);

        if (lista.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No hay detalles para el historial con ID: " + idHistorial
            );
        }

        return ResponseEntity.ok(lista);
    }


    /**
     * Busca un detalle de actividad específico por su ID único.
     * @param idDetalle ID del detalle técnico.
     * @return ResponseEntity con el objeto {@link DetalleActividadDTO}.
     * @throws ResourceNotFoundException Si no existe el detalle con el ID proporcionado.
     */
    @GetMapping("/detalle/buscar/{idDetalle}")
    public ResponseEntity<DetalleActividadDTO> buscarDetallePorId(
            @PathVariable int idDetalle) {

        DetalleActividadDTO detalle = detalleService.buscarDetalleActividad(idDetalle);

        if (detalle == null) {
            throw new ResourceNotFoundException("No existe detalle con ID: " + idDetalle);
        }

        return ResponseEntity.ok(detalle);
    }
}


