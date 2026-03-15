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

/**
 * Controlador REST para la gestión de Órdenes de Visita técnica.
 * Permite programar visitas, gestionar sus actividades y validar dependencias con órdenes de trabajo.
 */
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

    /**
     * Lista todas las órdenes de visita técnica registradas.
     * @return Lista de {@link OrdenDeVisitaDTO}.
     */
    @GetMapping("/listar")
    public List<OrdenDeVisitaDTO> listarOrdenesDeVisita() {
        return ordenDeVisitaService.listarOrdenVisita();
    }

    /**
     * Busca una orden de visita por su identificador.
     * @param id ID de la visita.
     * @return DTO de la orden de visita.
     * @throws ResourceNotFoundException Si la orden no existe.
     */
    @GetMapping("/buscar/{id}")
    public OrdenDeVisitaDTO buscarOrdenDeVisita(@PathVariable int id) {

        OrdenDeVisitaDTO orden = ordenDeVisitaService.buscarOrdenVisita(id);

        if (orden == null)
            throw new ResourceNotFoundException(
                    "No existe la orden de visita con ID: " + id);

        return orden;
    }

    /**
     * Crea una orden de visita y sus detalles técnicos iniciales de forma atómica.
     * @param request Solicitud con la cabecera de la visita y sus detalles.
     * @return Mensaje confirmando la creación con el ID generado.
     * @throws InvalidFieldException Si los datos de cliente/lugar/trabajador son inválidos o faltan detalles.
     */
    @PostMapping("/agregar")
    public String agregarOrdenDeVisita(@RequestBody OrdenDeVisitaRequest request) {
        if (request == null || request.getOrden() == null)
            throw new InvalidFieldException("La solicitud no contiene datos de la orden de visita.");

        OrdenDeVisitaDTO orden = request.getOrden();

        if (orden.getIdCliente() <= 0 || orden.getIdLugar() <= 0 || orden.getIdTrabajador() <= 0)
            throw new InvalidFieldException("Cliente, lugar o trabajador inválido.");

        int idOrdenGenerado = ordenDeVisitaService.agregarOrdenVisita(orden);

        List<DetalleOrdenVisitaDTO> detalles = request.getDetalles();
        if (detalles == null || detalles.isEmpty())
            throw new InvalidFieldException("Debe enviar al menos un detalle de la visita.");

        for (DetalleOrdenVisitaDTO d : detalles) {
            if (d.getActividad() == null || d.getActividad().isBlank())
                throw new InvalidFieldException("La actividad del detalle es obligatoria.");

            d.setIdVisita(idOrdenGenerado);
            detalleOrdenVisitaService.agregarDetalleOrdVis(d);
        }

        return "Orden de visita #" + idOrdenGenerado + " y detalles creados correctamente.";
    }



    /**
     * Actualiza una orden de visita siempre que no haya sido finalizada o cancelada.
     * @param id ID de la visita.
     * @param dto Datos actualizados.
     * @return Mensaje de confirmación del servicio.
     * @throws ResourceNotFoundException Si la visita no existe.
     * @throws InvalidFieldException Si se intenta cambiar el ID o la orden ya está en un estado final.
     */
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

    /**
     * Elimina una orden de visita y sus detalles.
     * Valida que no existan Órdenes de Trabajo vinculadas antes de proceder.
     * @param id ID de la visita a eliminar.
     * @return Mensaje de éxito de la eliminación.
     * @throws ResourceNotFoundException Si la visita no existe.
     * @throws InvalidFieldException Si la visita ya fue realizada/cancelada o tiene trabajos asociados.
     */
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

        detalleOrdenVisitaService.listarDetallesPorOrden(id)
                .forEach(d ->
                        detalleOrdenVisitaService.borrarDetalleOrdVis(
                                d.getIdDetalleVisita()));

        return ordenDeVisitaService.borrarOrdenVisita(id)
                + " + detalles eliminados";
    }

    /**
     * Lista los detalles de actividades programadas para una visita.
     * @param idOrden ID de la visita técnica.
     * @return Lista de {@link DetalleOrdenVisitaDTO}.
     * @throws ResourceNotFoundException Si la visita no existe.
     */
    @GetMapping("/{idOrden}/detalles")
    public List<DetalleOrdenVisitaDTO> listarDetallesPorOrden(
            @PathVariable int idOrden) {

        if (!ordenDeVisitaService.existeOrden(idOrden))
            throw new ResourceNotFoundException(
                    "No existe la orden de visita con ID: " + idOrden);

        return detalleOrdenVisitaService.listarDetallesPorOrden(idOrden);
    }

    /**
     * Agrega una actividad específica a una orden de visita ya creada.
     * @param idOrden ID de la visita.
     * @param detalle Información de la actividad.
     * @return Mensaje de éxito del servicio.
     * @throws ResourceNotFoundException Si la visita no existe.
     * @throws InvalidFieldException Si la actividad está vacía.
     */
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

    /**
     * Modifica un detalle de actividad de visita específico.
     * @param idDetalle ID del detalle.
     * @param detalle Nuevos datos.
     * @return Mensaje de éxito de la actualización.
     * @throws ResourceNotFoundException Si el detalle no existe.
     */
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

    /**
     * Borra un detalle de actividad de la visita.
     * @param idDetalle ID del detalle a eliminar.
     * @return Mensaje de éxito.
     * @throws ResourceNotFoundException Si el detalle no existe.
     */
    @DeleteMapping("/detalles/borrar/{idDetalle}")
    public String borrarDetalle(@PathVariable int idDetalle) {

        if (!detalleOrdenVisitaService.existeDetalle(idDetalle))
            throw new ResourceNotFoundException(
                    "No existe el detalle de visita con ID: " + idDetalle);

        return detalleOrdenVisitaService.borrarDetalleOrdVis(idDetalle);
    }
}
