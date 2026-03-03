package co.edu.unbosque.ElecSys.Ordenes.OrdenDeTrabajo.ControladorOrdTra;

import co.edu.unbosque.ElecSys.Config.Excepcion.InvalidFieldException;
import co.edu.unbosque.ElecSys.Config.Excepcion.ResourceNotFoundException;
import co.edu.unbosque.ElecSys.Ordenes.OrdenDeTrabajo.DTOOrdTra.OrdenDeTrabajoDTO;
import co.edu.unbosque.ElecSys.Ordenes.OrdenDeTrabajo.DTOOrdTra.OrdenDeTrabajoRequest;
import co.edu.unbosque.ElecSys.Ordenes.OrdenDeTrabajo.DetalleOrdenTrabajo.DTODetOrdTra.DetalleOrdenTrabajoDTO;
import co.edu.unbosque.ElecSys.Ordenes.OrdenDeTrabajo.DetalleOrdenTrabajo.ServicioDetOrdTra.DetalleOrdenTrabajoService;
import co.edu.unbosque.ElecSys.Ordenes.OrdenDeTrabajo.ServicioOrdTra.OrdenDeTrabajoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/ordenes-trabajo")
public class OrdenDeTrabajoControlador {


    @Autowired
    private OrdenDeTrabajoService ordenDeTrabajoService;

    @Autowired
    private DetalleOrdenTrabajoService detalleOrdenTrabajoService;

    /* =====================================================
       LISTAR TODAS LAS ÓRDENES
       ===================================================== */
    @GetMapping("/listar")
    public List<OrdenDeTrabajoDTO> listarOrdenTrabajo() {
        return ordenDeTrabajoService.listarOrdenTrabajo();
    }

    /* =====================================================
       BUSCAR ORDEN POR ID
       ===================================================== */
    @GetMapping("/buscar/{id}")
    public OrdenDeTrabajoDTO buscarOrdenTrabajo(@PathVariable int id) {

        OrdenDeTrabajoDTO orden = ordenDeTrabajoService.buscarOrdenTrabajo(id);

        if (orden == null)
            throw new ResourceNotFoundException("No existe la orden de trabajo con ID: " + id);

        return orden;
    }

    /* =====================================================
   CREAR ORDEN + DETALLES (CORREGIDO)
   ===================================================== */
    @PostMapping("/agregar")
    public String agregarOrdenDeTrabajo(@RequestBody OrdenDeTrabajoRequest request) {
        // 1. Validaciones (se mantienen igual)
        if (request == null || request.getOrden() == null)
            throw new InvalidFieldException("La solicitud no contiene datos.");

        OrdenDeTrabajoDTO ordenDTO = request.getOrden();

        // 2. GUARDAR ORDEN Y RECUPERAR EL ID REAL (Cambio Crítico)
        // El servicio ahora devuelve un entero con el ID de la secuencia
        int idOrdenGenerado = ordenDeTrabajoService.agregarOrdenTrabajo(ordenDTO);

        // 3. Procesar detalles con el ID real
        List<DetalleOrdenTrabajoDTO> detalles = request.getDetalles();
        if (detalles != null) {
            for (DetalleOrdenTrabajoDTO d : detalles) {
                if (d.getActividad() == null || d.getActividad().isBlank())
                    throw new InvalidFieldException("Actividad obligatoria en detalles.");

                // VINCULAMOS AL ID REAL QUE ACABAMOS DE CREAR
                d.setIdOrden(idOrdenGenerado);
                detalleOrdenTrabajoService.agregarDetalleOrdTra(d);
            }
        }

        return "Orden de Trabajo #" + idOrdenGenerado + " y detalles creados correctamente.";
    }

    /* =====================================================
       ACTUALIZAR ORDEN
       ===================================================== */
    @PutMapping("/actualizar/{id}")
    public String actualizarOrdenDeTrabajo(
            @PathVariable int id,
            @RequestBody OrdenDeTrabajoDTO dto) {

        if (!ordenDeTrabajoService.existeOrden(id))
            throw new ResourceNotFoundException("No existe la orden de trabajo con ID: " + id);

        if (dto.getId_orden() != id)
            throw new InvalidFieldException("No se puede cambiar el ID de la orden.");

        return ordenDeTrabajoService.editarOrdenTrabajo(id, dto);
    }

    /* =====================================================
       BORRAR ORDEN + DETALLES
       ===================================================== */
    @DeleteMapping("/borrar/{id}")
    public String borrarOrdenTrabajo(@PathVariable int id) {

        if (!ordenDeTrabajoService.existeOrden(id))
            throw new ResourceNotFoundException("No existe la orden de trabajo con ID: " + id);

        // Borrar detalles primero
        detalleOrdenTrabajoService.listarDetallesPorOrden(id)
                .forEach(d ->
                        detalleOrdenTrabajoService.borrarDetalleOrdTra(
                                d.getIdDetalleTrabajo()));

        return ordenDeTrabajoService.borrarOrdenTrabajo(id)
                + " + detalles eliminados";
    }

    /* =====================================================
       LISTAR DETALLES DE UNA ORDEN
       ===================================================== */
    @GetMapping("/{idOrden}/detalles")
    public List<DetalleOrdenTrabajoDTO> listarDetallesPorOrden(
            @PathVariable int idOrden) {

        if (!ordenDeTrabajoService.existeOrden(idOrden))
            throw new ResourceNotFoundException(
                    "No existe la orden de trabajo con ID: " + idOrden);

        return detalleOrdenTrabajoService.listarDetallesPorOrden(idOrden);
    }

    /* =====================================================
       AGREGAR DETALLE A UNA ORDEN
       ===================================================== */
    @PostMapping("/{idOrden}/detalles/agregar")
    public String agregarDetalle(
            @PathVariable int idOrden,
            @RequestBody DetalleOrdenTrabajoDTO detalle) {

        if (!ordenDeTrabajoService.existeOrden(idOrden))
            throw new ResourceNotFoundException(
                    "No existe la orden de trabajo con ID: " + idOrden);

        if (detalle.getActividad() == null || detalle.getActividad().isBlank())
            throw new InvalidFieldException("La actividad es obligatoria.");

        detalle.setIdOrden(idOrden);
        return detalleOrdenTrabajoService.agregarDetalleOrdTra(detalle);
    }

    /* =====================================================
       ACTUALIZAR DETALLE
       ===================================================== */
    @PutMapping("/detalles/actualizar/{idDetalle}")
    public String actualizarDetalle(
            @PathVariable int idDetalle,
            @RequestBody DetalleOrdenTrabajoDTO detalle) {

        DetalleOrdenTrabajoDTO actual =
                detalleOrdenTrabajoService.buscarDetalle(idDetalle);

        if (actual == null)
            throw new ResourceNotFoundException(
                    "No existe el detalle con ID: " + idDetalle);

        return detalleOrdenTrabajoService.actualizarDetalleOrdTra(idDetalle, detalle);
    }

    /* =====================================================
       BORRAR DETALLE
       ===================================================== */
    @DeleteMapping("/detalles/borrar/{idDetalle}")
    public String borrarDetalle(@PathVariable int idDetalle) {

        if (!detalleOrdenTrabajoService.existeDetalle(idDetalle))
            throw new ResourceNotFoundException(
                    "No existe el detalle con ID: " + idDetalle);

        return detalleOrdenTrabajoService.borrarDetalleOrdTra(idDetalle);
    }
}

