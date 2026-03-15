package co.edu.unbosque.ElecSys.ordenes.ordenDeVisita.controladorOrdVis;

import co.edu.unbosque.ElecSys.config.excepcion.InvalidFieldException;
import co.edu.unbosque.ElecSys.config.excepcion.ResourceNotFoundException;
import co.edu.unbosque.ElecSys.ordenes.ordenDeVisita.dtoOrdVis.OrdenDeVisitaDTO;
import co.edu.unbosque.ElecSys.ordenes.ordenDeVisita.dtoOrdVis.OrdenDeVisitaRequest;
import co.edu.unbosque.ElecSys.ordenes.ordenDeVisita.detalleOrdenVisita.dtoDetOrdVis.DetalleOrdenVisitaDTO;
import co.edu.unbosque.ElecSys.ordenes.ordenDeVisita.detalleOrdenVisita.servicioDetOrdVis.DetalleOrdenVisitaService;
import co.edu.unbosque.ElecSys.ordenes.ordenDeVisita.servicioOrdVis.OrdenDeVisitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import co.edu.unbosque.ElecSys.ordenes.ordenDeTrabajo.servicioOrdTra.OrdenDeTrabajoService;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/ordenes-visita")
public class OrdenDeVisitaControlador {


    @Autowired
    private OrdenDeVisitaService ordenDeVisitaService;

    @Autowired
    private DetalleOrdenVisitaService detalleOrdenVisitaService;

    @Autowired
    private OrdenDeTrabajoService ordenDeTrabajoService;

    /* =====================================================
       LISTAR TODAS LAS ÓRDENES DE VISITA
       ===================================================== */
    @GetMapping("/listar")
    public List<OrdenDeVisitaDTO> listarOrdenesDeVisita() {
        return ordenDeVisitaService.listarOrdenVisita();
    }

    /* =====================================================
       BUSCAR ORDEN DE VISITA POR ID
       ===================================================== */
    @GetMapping("/buscar/{id}")
    public OrdenDeVisitaDTO buscarOrdenDeVisita(@PathVariable int id) {

        OrdenDeVisitaDTO orden = ordenDeVisitaService.buscarOrdenVisita(id);

        if (orden == null)
            throw new ResourceNotFoundException(
                    "No existe la orden de visita con ID: " + id);

        return orden;
    }

    /* =====================================================
       CREAR ORDEN DE VISITA + DETALLES
       ===================================================== */
    @PostMapping("/agregar")
    public String agregarOrdenDeVisita(@RequestBody OrdenDeVisitaRequest request) {
        // 1. Validaciones iniciales
        if (request == null || request.getOrden() == null)
            throw new InvalidFieldException("La solicitud no contiene datos de la orden de visita.");

        OrdenDeVisitaDTO orden = request.getOrden();

        if (orden.getIdCliente() <= 0 || orden.getIdLugar() <= 0 || orden.getIdTrabajador() <= 0)
            throw new InvalidFieldException("Cliente, lugar o trabajador inválido.");

        // 2. GUARDAR ORDEN Y RECUPERAR EL ID (Cambio clave)
        // El servicio ahora debe devolver un int con el ID generado por la secuencia
        int idOrdenGenerado = ordenDeVisitaService.agregarOrdenVisita(orden);

        // 3. Procesar detalles con el ID real
        List<DetalleOrdenVisitaDTO> detalles = request.getDetalles();
        if (detalles == null || detalles.isEmpty())
            throw new InvalidFieldException("Debe enviar al menos un detalle de la visita.");

        for (DetalleOrdenVisitaDTO d : detalles) {
            if (d.getActividad() == null || d.getActividad().isBlank())
                throw new InvalidFieldException("La actividad del detalle es obligatoria.");

            // Vinculamos el detalle al ID que acabamos de recibir del servicio
            d.setIdVisita(idOrdenGenerado);
            detalleOrdenVisitaService.agregarDetalleOrdVis(d);
        }

        return "Orden de visita #" + idOrdenGenerado + " y detalles creados correctamente.";
    }



    /* =====================================================
       ACTUALIZAR ORDEN DE VISITA
       ===================================================== */
    @PutMapping("/actualizar/{id}")
    public String actualizarOrdenDeVisita(
            @PathVariable int id,
            @RequestBody OrdenDeVisitaDTO dto) {

        if (!ordenDeVisitaService.existeOrden(id))
            throw new ResourceNotFoundException(
                    "No existe la orden de visita con ID: " + id);

        if (dto.getIdVisita() != id)
            throw new InvalidFieldException(
                    "No se puede cambiar el ID de la orden.");

        OrdenDeVisitaDTO actual = ordenDeVisitaService.buscarOrdenVisita(id);

        if (actual.getEstado().equals("REALIZADA") || actual.getEstado().equals("CANCELADA")) {
            throw new InvalidFieldException("La orden #" + id + " está en estado " +
                    actual.getEstado() + " y no permite modificaciones.");
        }

        return ordenDeVisitaService.editarOrdenVisita(id, dto);
    }

    /* =====================================================
       BORRAR ORDEN DE VISITA + DETALLES
       ===================================================== */
    @DeleteMapping("/borrar/{id}")
    public String borrarOrdenDeVisita(@PathVariable int id) {

        if (!ordenDeVisitaService.existeOrden(id))
            throw new ResourceNotFoundException(
                    "No existe la orden de visita con ID: " + id);

        OrdenDeVisitaDTO actual = ordenDeVisitaService.buscarOrdenVisita(id);

        if (actual.getEstado().equals("REALIZADA") || actual.getEstado().equals("CANCELADA")) {
            throw new InvalidFieldException("No se puede eliminar la orden #" + id +
                    " porque ya ha sido " + actual.getEstado());
        }

        boolean tieneTrabajoAsociado = ordenDeTrabajoService.listarOrdenTrabajo().stream()
                .anyMatch(t -> t.getId_orden_visita() != null && t.getId_orden_visita() == id);

        if (tieneTrabajoAsociado) {
            throw new InvalidFieldException("Operación denegada: Existe una Orden de Trabajo vinculada a esta visita. " +
                    "Debe eliminar primero la Orden de Trabajo correspondiente.");
        }

        // Borrar detalles primero
        detalleOrdenVisitaService.listarDetallesPorOrden(id)
                .forEach(d ->
                        detalleOrdenVisitaService.borrarDetalleOrdVis(
                                d.getIdDetalleVisita()));

        return ordenDeVisitaService.borrarOrdenVisita(id)
                + " + detalles eliminados";
    }

    /* =====================================================
       LISTAR DETALLES DE UNA ORDEN DE VISITA
       ===================================================== */
    @GetMapping("/{idOrden}/detalles")
    public List<DetalleOrdenVisitaDTO> listarDetallesPorOrden(
            @PathVariable int idOrden) {

        if (!ordenDeVisitaService.existeOrden(idOrden))
            throw new ResourceNotFoundException(
                    "No existe la orden de visita con ID: " + idOrden);

        return detalleOrdenVisitaService.listarDetallesPorOrden(idOrden);
    }

    /* =====================================================
       AGREGAR DETALLE A UNA ORDEN DE VISITA
       ===================================================== */
    @PostMapping("/{idOrden}/detalles/agregar")
    public String agregarDetalle(
            @PathVariable int idOrden,
            @RequestBody DetalleOrdenVisitaDTO detalle) {

        if (!ordenDeVisitaService.existeOrden(idOrden))
            throw new ResourceNotFoundException(
                    "No existe la orden de visita con ID: " + idOrden);

        if (detalle.getActividad() == null || detalle.getActividad().isBlank())
            throw new InvalidFieldException(
                    "La actividad es obligatoria.");

        detalle.setIdVisita(idOrden);
        return detalleOrdenVisitaService.agregarDetalleOrdVis(detalle);
    }

    /* =====================================================
       ACTUALIZAR DETALLE DE VISITA
       ===================================================== */
    @PutMapping("/detalles/actualizar/{idDetalle}")
    public String actualizarDetalle(
            @PathVariable int idDetalle,
            @RequestBody DetalleOrdenVisitaDTO detalle) {

        DetalleOrdenVisitaDTO actual =
                detalleOrdenVisitaService.buscarDetalle(idDetalle);

        if (actual == null)
            throw new ResourceNotFoundException(
                    "No existe el detalle de visita con ID: " + idDetalle);

        return detalleOrdenVisitaService
                .actualizarDetalleOrdVis(idDetalle, detalle);
    }

    /* =====================================================
       BORRAR DETALLE DE VISITA
       ===================================================== */
    @DeleteMapping("/detalles/borrar/{idDetalle}")
    public String borrarDetalle(@PathVariable int idDetalle) {

        if (!detalleOrdenVisitaService.existeDetalle(idDetalle))
            throw new ResourceNotFoundException(
                    "No existe el detalle de visita con ID: " + idDetalle);

        return detalleOrdenVisitaService.borrarDetalleOrdVis(idDetalle);
    }
}
