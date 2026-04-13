package co.edu.unbosque.ElecSys.ordenes.ordenDeTrabajo.controladorOrdTra;

import co.edu.unbosque.ElecSys.config.excepcion.InvalidFieldException;
import co.edu.unbosque.ElecSys.config.excepcion.PdfGenerationException;
import co.edu.unbosque.ElecSys.config.excepcion.ResourceNotFoundException;
import co.edu.unbosque.ElecSys.lugarTrabajo.dtoLug.LugarTrabajoDTO;
import co.edu.unbosque.ElecSys.lugarTrabajo.servicioLug.LugarTrabajoService;
import co.edu.unbosque.ElecSys.ordenes.ordenDeTrabajo.Archivo.PDF_Orden_Trabajo;
import co.edu.unbosque.ElecSys.ordenes.ordenDeTrabajo.dtoOrdTra.OrdenDeTrabajoDTO;
import co.edu.unbosque.ElecSys.ordenes.ordenDeTrabajo.dtoOrdTra.OrdenDeTrabajoRequest;
import co.edu.unbosque.ElecSys.ordenes.ordenDeTrabajo.detalleOrdenTrabajo.dtoDetOrdTra.DetalleOrdenTrabajoDTO;
import co.edu.unbosque.ElecSys.ordenes.ordenDeTrabajo.detalleOrdenTrabajo.servicioDetOrdTra.DetalleOrdenTrabajoService;
import co.edu.unbosque.ElecSys.ordenes.ordenDeTrabajo.servicioOrdTra.OrdenDeTrabajoService;
import co.edu.unbosque.ElecSys.usuario.cliente.dtoClie.ClienteDTO;
import co.edu.unbosque.ElecSys.usuario.cliente.servicioClie.ClienteServiceImpl;
import co.edu.unbosque.ElecSys.usuario.trabajador.dtoTra.TrabajadorDTO;
import co.edu.unbosque.ElecSys.usuario.trabajador.servicioTra.TrabajadorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * Controlador REST para la gestión de Órdenes de Trabajo.
 * Proporciona endpoints para el ciclo de vida completo de una orden, incluyendo
 * la gestión de sus detalles técnicos y validaciones de estado del cliente.
 */
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/ordenes-trabajo")
public class OrdenDeTrabajoControlador {


    @Autowired
    private OrdenDeTrabajoService ordenDeTrabajoService;

    @Autowired
    private DetalleOrdenTrabajoService detalleOrdenTrabajoService;

    @Autowired
    private ClienteServiceImpl clienteService;

    @Autowired
    private LugarTrabajoService lugarservice;

    @Autowired
    private TrabajadorServiceImpl trabajadorService;

    @Autowired
    private PDF_Orden_Trabajo pdfOrdenTrabajo;

    /**
     * Recupera todas las órdenes de trabajo registradas en el sistema.
     * @return Lista de objetos {@link OrdenDeTrabajoDTO}.
     */
    @GetMapping("/listar")
    public List<OrdenDeTrabajoDTO> listarOrdenTrabajo() {
        return ordenDeTrabajoService.listarOrdenTrabajo();
    }

    /**
     * Busca una orden de trabajo específica por su identificador único.
     * @param id Identificador de la orden.
     * @return El DTO de la orden encontrada.
     * @throws ResourceNotFoundException Si no existe la orden con el ID proporcionado.
     */
    @GetMapping("/buscar/{id}")
    public OrdenDeTrabajoDTO buscarOrdenTrabajo(@PathVariable int id) {

        OrdenDeTrabajoDTO orden = ordenDeTrabajoService.buscarOrdenTrabajo(id);

        if (orden == null)
            throw new ResourceNotFoundException("No existe la orden de trabajo con ID: " + id);

        return orden;
    }

    /**
     * Crea una nueva orden de trabajo junto con sus detalles asociados.
     * Valida que el cliente exista y esté en estado ACTIVO antes de proceder.
     * @param request Objeto que contiene la cabecera de la orden y su lista de detalles.
     * @return Mensaje de éxito con el ID de la orden generada.
     * @throws InvalidFieldException Si la solicitud es nula, el cliente no es apto o faltan actividades en los detalles.
     */
    @PostMapping("/agregar")
    public ResponseEntity<byte[]> agregarOrdenDeTrabajo(@RequestBody OrdenDeTrabajoRequest request) throws IOException {

        TrabajadorDTO trabajador = trabajadorService.buscarTrabajador(request.getOrden().getId_trabajador());
        ClienteDTO clienteEncon = clienteService.buscarCliente(request.getOrden().getId_cliente());
        LugarTrabajoDTO lugar = lugarservice.buscarLugar(request.getOrden().getId_lugar());
        if (request == null || request.getOrden() == null)
            throw new InvalidFieldException("La solicitud no contiene datos.");

        var cliente = clienteService.buscarCliente(request.getOrden().getId_cliente());
        if (cliente == null || !cliente.getEstado().equals("ACTIVO")) {
            throw new InvalidFieldException("No se puede crear la orden: El cliente no existe o no está ACTIVO.");
        }

        OrdenDeTrabajoDTO ordenDTO = request.getOrden();

        int idOrdenGenerado = ordenDeTrabajoService.agregarOrdenTrabajo(ordenDTO);
        ordenDTO.setId_orden(idOrdenGenerado);

        List<DetalleOrdenTrabajoDTO> detalles = request.getDetalles();
        if (detalles != null) {
            for (DetalleOrdenTrabajoDTO d : detalles) {
                if (d.getActividad() == null || d.getActividad().isBlank())
                    throw new InvalidFieldException("Actividad obligatoria en detalles.");

                d.setIdOrden(idOrdenGenerado);
                detalleOrdenTrabajoService.agregarDetalleOrdTra(d);
            }
        }
        byte[] pdf;

        try{
            pdf = pdfOrdenTrabajo.generarArchivo(ordenDTO, clienteEncon, lugar, detalles, trabajador);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        String nombreArchivo = pdfOrdenTrabajo.descargarPDFOrden( idOrdenGenerado ,ordenDTO, pdf);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nombreArchivo)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    /**
     * Actualiza la información de una orden de trabajo existente.
     * Solo permite modificaciones si la orden no está en estado 'REALIZADA' o 'CANCELADA'.
     * @param id ID de la orden a actualizar.
     * @param dto Datos actualizados de la orden.
     * @return Mensaje de éxito de la actualización.
     * @throws ResourceNotFoundException Si la orden no existe.
     * @throws InvalidFieldException Si se intenta cambiar el ID o si la orden ya está finalizada/cancelada.
     */
    @PutMapping("/actualizar/{id}")
    public String actualizarOrdenDeTrabajo(
            @PathVariable int id,
            @RequestBody OrdenDeTrabajoDTO dto) {

        if (!ordenDeTrabajoService.existeOrden(id))
            throw new ResourceNotFoundException("No existe la orden de trabajo con ID: " + id);

        if (dto.getId_orden() != id)
            throw new InvalidFieldException("No se puede cambiar el ID de la orden.");

        OrdenDeTrabajoDTO actual = ordenDeTrabajoService.buscarOrdenTrabajo(id);

        if (actual == null) throw new ResourceNotFoundException("No existe la orden.");
        if (actual.getEstado().equals("REALIZADA") || actual.getEstado().equals("CANCELADA")) {
            throw new InvalidFieldException("La orden #" + id + " ya fue " + actual.getEstado() + " y no puede modificarse.");
        }

        return ordenDeTrabajoService.editarOrdenTrabajo(id, dto);
    }

    /**
     * Elimina una orden de trabajo y todos sus detalles vinculados.
     * @param id ID de la orden a eliminar.
     * @return Mensaje confirmando la eliminación de la orden y sus detalles.
     * @throws ResourceNotFoundException Si la orden no existe.
     * @throws InvalidFieldException Si la orden ya fue realizada o cancelada.
     */
    @DeleteMapping("/borrar/{id}")
    public String borrarOrdenTrabajo(@PathVariable int id) {

        if (!ordenDeTrabajoService.existeOrden(id))
            throw new ResourceNotFoundException("No existe la orden de trabajo con ID: " + id);

        detalleOrdenTrabajoService.listarDetallesPorOrden(id)
                .forEach(d ->
                        detalleOrdenTrabajoService.borrarDetalleOrdTra(
                                d.getIdDetalleTrabajo()));

        OrdenDeTrabajoDTO actual = ordenDeTrabajoService.buscarOrdenTrabajo(id);

        if (actual == null) throw new ResourceNotFoundException("No existe la orden.");
        if (actual.getEstado().equals("REALIZADA") || actual.getEstado().equals("CANCELADA")) {
            throw new InvalidFieldException("La orden #" + id + " ya fue " + actual.getEstado() + " y no puede borrarse.");
        }

        return ordenDeTrabajoService.borrarOrdenTrabajo(id)
                + " + detalles eliminados";
    }

    /**
     * Lista los detalles técnicos asociados a una orden de trabajo específica.
     * @param idOrden ID de la orden de trabajo.
     * @return Lista de {@link DetalleOrdenTrabajoDTO}.
     * @throws ResourceNotFoundException Si la orden de trabajo no existe.
     */
    @GetMapping("/{idOrden}/detalles")
    public List<DetalleOrdenTrabajoDTO> listarDetallesPorOrden(
            @PathVariable int idOrden) {

        if (!ordenDeTrabajoService.existeOrden(idOrden))
            throw new ResourceNotFoundException(
                    "No existe la orden de trabajo con ID: " + idOrden);

        return detalleOrdenTrabajoService.listarDetallesPorOrden(idOrden);
    }

    /**
     * Agrega un nuevo ítem de detalle a una orden de trabajo existente.
     * @param idOrden ID de la orden padre.
     * @param detalle Información del nuevo detalle (actividad, duración, etc.).
     * @return Mensaje de confirmación del servicio de detalles.
     * @throws ResourceNotFoundException Si la orden de trabajo no existe.
     * @throws InvalidFieldException Si la actividad del detalle está vacía.
     */
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

    /**
     * Modifica un detalle técnico específico.
     * @param idDetalle ID del detalle a actualizar.
     * @param detalle Nuevos datos del detalle.
     * @return Mensaje de éxito de la actualización.
     * @throws ResourceNotFoundException Si el detalle no existe.
     */
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

    /**
     * Elimina un detalle técnico individual de la base de datos.
     * @param idDetalle ID del detalle a eliminar.
     * @return Mensaje de éxito de la eliminación.
     * @throws ResourceNotFoundException Si el detalle no existe.
     */
    @DeleteMapping("/detalles/borrar/{idDetalle}")
    public String borrarDetalle(@PathVariable int idDetalle) {

        if (!detalleOrdenTrabajoService.existeDetalle(idDetalle))
            throw new ResourceNotFoundException(
                    "No existe el detalle con ID: " + idDetalle);

        return detalleOrdenTrabajoService.borrarDetalleOrdTra(idDetalle);
    }

    @GetMapping("/descargarOrden-pdf/{id}")
    public ResponseEntity<byte[]> descargarPDF(@PathVariable int id) throws IOException {
        OrdenDeTrabajoDTO orden = ordenDeTrabajoService.buscarOrdenTrabajo(id);

        if (orden == null){
            throw new ResourceNotFoundException("No existe la orden de trabajo con ID: " + id);
        }

        ClienteDTO cliente = clienteService.buscarCliente(orden.getId_cliente());
        LugarTrabajoDTO lugar = lugarservice.buscarLugar(orden.getId_lugar());

        List<DetalleOrdenTrabajoDTO> detalleOrden = detalleOrdenTrabajoService.listarDetallesOrdTra()
                .stream().filter(d -> d.getIdOrden() == id).toList();

        TrabajadorDTO trabajador = trabajadorService.buscarTrabajador(orden.getId_trabajador());

        byte[] pdf;

        try{
            pdf = pdfOrdenTrabajo.generarArchivo(orden, cliente, lugar, detalleOrden, trabajador);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        String nombreArchivo = pdfOrdenTrabajo.descargarPDFOrden(0,orden, pdf);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nombreArchivo)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}

