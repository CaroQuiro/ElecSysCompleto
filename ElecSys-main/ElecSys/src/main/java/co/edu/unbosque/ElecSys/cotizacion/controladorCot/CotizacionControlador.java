package co.edu.unbosque.ElecSys.cotizacion.controladorCot;

import co.edu.unbosque.ElecSys.config.excepcion.InvalidFieldException;
import co.edu.unbosque.ElecSys.config.excepcion.PdfGenerationException;
import co.edu.unbosque.ElecSys.config.excepcion.ResourceNotFoundException;
import co.edu.unbosque.ElecSys.cotizacion.analizador.CotizacionReentrenarRequest;
import co.edu.unbosque.ElecSys.cotizacion.analizador.PrediccionService;
import co.edu.unbosque.ElecSys.cotizacion.archivo.Pdf_Cotizacion;
import co.edu.unbosque.ElecSys.cotizacion.dtoCot.AIUDTO;
import co.edu.unbosque.ElecSys.cotizacion.dtoCot.CotizacionRequest;
import co.edu.unbosque.ElecSys.cotizacion.detalleCotizacion.dtoDetCot.DetalleCotizacionDTO;
import co.edu.unbosque.ElecSys.cotizacion.detalleCotizacion.servicioDetCot.DetalleCotizacionService;
import co.edu.unbosque.ElecSys.cotizacion.servicioCot.CotizacionServiceImpl;
import co.edu.unbosque.ElecSys.lugarTrabajo.dtoLug.LugarTrabajoDTO;
import co.edu.unbosque.ElecSys.lugarTrabajo.servicioLug.LugarTrabajoService;
import co.edu.unbosque.ElecSys.usuario.cliente.dtoClie.ClienteDTO;
import co.edu.unbosque.ElecSys.usuario.cliente.servicioClie.ClienteServiceImpl;
import co.edu.unbosque.ElecSys.cotizacion.dtoCot.CotizacionDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controlador REST para la gestión integral de cotizaciones en ElecSys.
 * Maneja la lógica de cálculos de IVA/AIU, generación de documentos, CRUD y procesos de analisis.
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/cotizaciones")
public class CotizacionControlador {

    @Autowired
    private CotizacionServiceImpl service;

    @Autowired
    private ClienteServiceImpl clienteService;

    @Autowired
    private DetalleCotizacionService detalleService;

    @Autowired
    private LugarTrabajoService lugarService;

    @Autowired
    private Pdf_Cotizacion pdfService;

    @Autowired
    private PrediccionService prediccionService;


    /**
     * Procesa la creación de una cotización completa, realiza cálculos de impuestos
     * (IVA simple o AIU), la persiste en BD y genera el PDF de descarga.
     * * @param solicitud DTO complejo con cotización, detalles y configuración de impuestos.
     * @return ResponseEntity conteniendo el binario del PDF para descarga.
     * @throws IOException Si falla la escritura del archivo físico.
     * @throws InvalidFieldException Si los datos de entrada no cumplen las reglas de negocio.
     * @throws ResourceNotFoundException Si el cliente o lugar no existen.
     * @throws PdfGenerationException Si ocurre un error técnico creando el PDF.
     */
    @PostMapping("/agregar")
    public ResponseEntity<byte[]> agregarCotizacion(@RequestBody CotizacionRequest solicitud) throws IOException {

        if (solicitud.getCotizacion() == null)
            throw new InvalidFieldException("La solicitud no contiene datos de cotización.");

        CotizacionDTO cot = solicitud.getCotizacion();

        // Validaciones de IDs
        if (cot.getId_cliente() <= 0 || cot.getId_lugar() <= 0)
            throw new InvalidFieldException("Cliente o lugar inválido.");

        ClienteDTO cliente = clienteService.buscarCliente(cot.getId_cliente());
        if (cliente == null || !cliente.getEstado().equalsIgnoreCase("ACTIVO")) {
            throw new InvalidFieldException("El cliente debe existir y estar en estado ACTIVO.");
        }

        LugarTrabajoDTO lugar = lugarService.buscarLugar(cot.getId_lugar());
        if (lugar == null)
            throw new ResourceNotFoundException("El lugar no existe.");

        // Validación de detalles
        List<DetalleCotizacionDTO> detalles = solicitud.getDetalleCotizacionDTOS();
        if (detalles == null || detalles.isEmpty())
            throw new InvalidFieldException("Debe enviar al menos un detalle.");

        // -------------------------------
        // Cálculo del valor total
        // -------------------------------
        BigDecimal valorTotal = BigDecimal.ZERO;

        for (DetalleCotizacionDTO d : detalles) {

            if (d.getCantidad() <= 0)
                throw new InvalidFieldException("La cantidad debe ser mayor que cero.");

            if (d.getValor_unitario().compareTo(BigDecimal.ZERO) <= 0)
                throw new InvalidFieldException("El valor unitario debe ser mayor a cero.");

            BigDecimal subtotal =
                    d.getValor_unitario().multiply(new BigDecimal(d.getCantidad()));

            d.setSubtotal(subtotal);
            valorTotal = valorTotal.add(subtotal);
        }

        if (valorTotal.compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidFieldException("El valor total debe ser mayor a cero.");

        // -------------------------------
        // Cálculo de IVA o AIU
        // -------------------------------
        CotizacionDTO cotFull;
        BigDecimal administracion = BigDecimal.ZERO;
        BigDecimal imprevistos = BigDecimal.ZERO;
        BigDecimal utilidades = BigDecimal.ZERO;
        BigDecimal iva = BigDecimal.ZERO;
        BigDecimal totalPagar = BigDecimal.ZERO;

        if (Boolean.TRUE.equals(solicitud.getExistIva())) {

            iva = valorTotal.multiply(new BigDecimal("0.19"));
            totalPagar = valorTotal.add(iva);

            cotFull = new CotizacionDTO(
                    cot.getId_cotizacion(),
                    cot.getId_trabajador(),
                    cot.getId_cliente(),
                    cot.getId_lugar(),
                    cot.getFecha_realizacion(),
                    cot.getReferencia(),
                    valorTotal,
                    cot.getEstado(),
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    iva,
                    totalPagar
            );

        } else {
            AIUDTO aiu = solicitud.getAiudto();

            BigDecimal adminPct = BigDecimal.ZERO;
            BigDecimal imprePct = BigDecimal.ZERO;
            BigDecimal utilPct  = BigDecimal.ZERO;

            if (aiu != null) {
                if (aiu.getAdministracion() != null)
                    adminPct = aiu.validarPorcentaje(aiu.getAdministracion());

                if (aiu.getImprevistos() != null)
                    imprePct = aiu.validarPorcentaje(aiu.getImprevistos());

                if (aiu.getUtilidad() != null)
                    utilPct = aiu.validarPorcentaje(aiu.getUtilidad());
            }

            administracion = valorTotal.multiply(adminPct);
            imprevistos = valorTotal.multiply(imprePct);
            utilidades = valorTotal.multiply(utilPct);
            iva = utilidades.multiply(new BigDecimal("0.19"));

            totalPagar = valorTotal
                    .add(administracion)
                    .add(imprevistos)
                    .add(utilidades)
                    .add(iva);

            cot.setEstado("PENDIENTE");


            cotFull = new CotizacionDTO(
                    cot.getId_cotizacion(),
                    cot.getId_trabajador(),
                    cot.getId_cliente(),
                    cot.getId_lugar(),
                    cot.getFecha_realizacion(),
                    cot.getReferencia(),
                    valorTotal,
                    cot.getEstado(),
                    administracion,
                    imprevistos,
                    utilidades,
                    iva,
                    totalPagar
            );
        }


        cotFull = service.agregarCotizacion(cotFull);


        for (DetalleCotizacionDTO d : detalles) {
            d.setId_cotizacion(cotFull.getId_cotizacion());
            detalleService.agregarDetalleCot(d);
        }


        byte[] pdf;

        try {
            pdf = pdfService.generarArchivo(cotFull, cliente, lugar, detalles);
        } catch (Exception e) {
            throw new PdfGenerationException("Error al generar el PDF: " + e.getMessage());
        }

        String nombreArchivo = pdfService.descargarPDF(cotFull, pdf);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nombreArchivo)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }


    /**
     * Retorna la lista de todas las cotizaciones registradas.
     * @return Lista de CotizacionDTO.
     */
    @GetMapping("/listar")
    public List<CotizacionDTO> listarCotizaciones() {
        return service.listarCotizacion();
    }



    /**
     * Obtiene una cotización específica filtrada por su ID.
     * @param id ID de la cotización.
     * @return informacion de esa cotizacion especifica
     * @throws ResourceNotFoundException Si la cotización no existe.
     */
    @GetMapping("/buscar/{id}")
    public CotizacionDTO buscarCotizacion(@PathVariable int id) {
        CotizacionDTO cot = service.buscarCotizacion(id);
        if (cot == null)
            throw new ResourceNotFoundException("No existe la cotización con ID: " + id);
        return cot;
    }


    /**
     * Obtiene los detalles de una cotización específica filtrados por su ID.
     * @param id ID de la cotización.
     * @return Lista de detalles asociados.
     * @throws ResourceNotFoundException Si la cotización no existe.
     */
    @GetMapping("/{id}/detalles")
    public List<DetalleCotizacionDTO> obtenerDetalles(@PathVariable int id) {
        if (!service.existirCot(id))
            throw new ResourceNotFoundException("No existe la cotización con ID: " + id);

        return detalleService.listarDetallesCot()
                .stream()
                .filter(d -> d.getId_cotizacion() == id)
                .toList();
    }



    /**
     * Borra una cotización específica filtrada por su ID.
     * @param id ID de la cotización.
     * @return mensaje de que la cotizacion fue borrada
     * @throws ResourceNotFoundException Si la cotización no existe.
     * @throws InvalidFieldException la cotizacion ya fue aceptada o rechazada y no se puede borrar
     */
    @DeleteMapping("/borrar/{id}")
    public String borrar(@PathVariable int id) {

        if (!service.existirCot(id))
            throw new ResourceNotFoundException("No existe la cotización con ID: " + id);

        CotizacionDTO actual = service.buscarCotizacion(id);
        if (!actual.getEstado().equalsIgnoreCase("PENDIENTE")) {
            throw new InvalidFieldException("No se puede eliminar una cotización que ya ha sido " + actual.getEstado());
        }

        detalleService.listarDetallesCot()
                .stream()
                .filter(d -> d.getId_cotizacion() == id)
                .forEach(d -> detalleService.borrarDetalleCot(d.getId_detalle_cotizacion()));

        return service.borrarCotizacion(id) + " + detalles eliminados";
    }


    /**
     * Actualiza una cotización específica filtrada por su ID.
     * @param id ID de la cotización.
     * @return mensaje de que la cotizacion fue actualizada
     * @throws ResourceNotFoundException Si la cotización no existe.
     * @throws InvalidFieldException la cotizacion ya fue aceptada o rechazada y no se puede cambiar sus datos
     */
    @PutMapping("/actualizar/{id}")
    public String actualizar(@PathVariable int id, @RequestBody CotizacionDTO dto) {

        if (dto.getId_cotizacion() != id)
            throw new InvalidFieldException("No se puede cambiar el ID de la cotización.");

        List<String> estados = List.of("ACTIVO", "PENDIENTE", "RECHAZADO");

        if (!estados.contains(dto.getEstado()))
            throw new InvalidFieldException("Estado inválido. Solo se permite: " + estados);

        CotizacionDTO actual = service.buscarCotizacion(id);
        if (!actual.getEstado().equalsIgnoreCase("PENDIENTE")) {
            throw new InvalidFieldException("No se puede modificar una cotización que ya ha sido " + actual.getEstado());
        }

        return service.actualizarCot(id, dto);
    }


    /**
     * Borra un detalle de una cotización específica filtrada por su ID.
     * @param idCot ID de la cotización.
     * @param idDetalle ID del detalle especifico de esa cotización.
     * @return mensaje de que el detalle de la cotizacion fue borrada
     * @throws ResourceNotFoundException Si la cotización no existe.
     * @throws InvalidFieldException El detalle es de otra cotizacion
     * @throws InvalidFieldException una cotizacion no puede quedarse sin detalles
     */
    @DeleteMapping("/borrar/{idCot}/detalle/{idDetalle}")
    public String borrarDetalle(@PathVariable int idCot, @PathVariable int idDetalle) {
        if (!service.existirCot(idCot))
            throw new ResourceNotFoundException("No existe la cotización con ID: " + idCot);

        DetalleCotizacionDTO det = detalleService.listarDetallesCot()
                .stream()
                .filter(d -> d.getId_detalle_cotizacion() == idDetalle)
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException("El detalle no existe."));

        if (det.getId_cotizacion() != idCot)
            throw new InvalidFieldException("Ese detalle no pertenece a la cotización indicada.");

        List<DetalleCotizacionDTO> todos = detalleService.listarDetallesCot()
                .stream().filter(d -> d.getId_cotizacion() == idCot).toList();

        if (todos.size() <= 1) {
            throw new InvalidFieldException("La cotización no puede quedarse sin detalles. Elimine la cotización completa si es necesario.");
        }

        detalleService.borrarDetalleCot(idDetalle);

        return "Detalle eliminado correctamente.";
    }


    /**
     * Actualizar un detalle de una cotización específica filtrada por su ID.
     * @param idCot ID de la cotización.
     * @param idDetalle ID del detalle especifico de esa cotización.
     * @param dto nuevos datos del detalle.
     * @return mensaje de que los detalles fueron actualizados
     * @throws ResourceNotFoundException Si la cotización no existe.
     * @throws InvalidFieldException El detalle es de otra cotizacion
     * @throws InvalidFieldException No se pueden cambiar los detalles si la cotizacion asociada ya fue aceptada.
     */
    @PutMapping("/actualizar/{idCot}/detalle/{idDetalle}")
    public String actualizarDetalle(@PathVariable int idCot,
                                    @PathVariable int idDetalle,
                                    @RequestBody DetalleCotizacionDTO dto) {

        System.out.println("ID COTIZACION ACTUAL: " + idCot);

        if (!service.existirCot(idCot))
            throw new ResourceNotFoundException("No existe la cotización con ID: " + idCot);

        DetalleCotizacionDTO actual = detalleService.listarDetallesCot()
                .stream()
                .filter(d -> d.getId_detalle_cotizacion() == idDetalle)
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException("No existe el detalle con ID: " + idDetalle));

        if (actual.getId_cotizacion() != idCot)
            throw new InvalidFieldException("El detalle no pertenece a esta cotización.");

        CotizacionDTO cot = service.buscarCotizacion(idCot);
        if (!cot.getEstado().equalsIgnoreCase("PENDIENTE")) {
            throw new InvalidFieldException("No se pueden modificar detalles de una cotización finalizada.");
        }

        if (dto.getCantidad() <= 0)
            throw new InvalidFieldException("La cantidad debe ser mayor a cero.");

        if (dto.getValor_unitario().compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidFieldException("El valor unitario debe ser mayor a cero.");

        dto.setSubtotal(
                dto.getValor_unitario().multiply(new BigDecimal(dto.getCantidad()))
        );

        return detalleService.actualizarDetalle(idDetalle, dto);
    }


    /**
     * Agrega un detalle a una cotización específica filtrada por su ID.
     * @param idCot ID de la cotización.
     * @param dto nuevodetalle.
     * @return mensaje de que la cotizacion fue borrada
     * @throws ResourceNotFoundException Si la cotización no existe.
     * @throws InvalidFieldException El detalle es de otra cotizacion
     * @throws InvalidFieldException No se pueden cambiar los detalles si la cotizacion asociada ya fue aceptada.
     */
    @PostMapping("/{idCot}/detalle")
    public ResponseEntity<String> agregarDetalle(
            @PathVariable int idCot,
            @RequestBody DetalleCotizacionDTO dto) {

        if (!service.existirCot(idCot))
            throw new ResourceNotFoundException("No existe la cotización con ID: " + idCot);

        if (dto.getCantidad() <= 0)
            throw new InvalidFieldException("La cantidad debe ser mayor a cero.");

        if (dto.getValor_unitario().compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidFieldException("El valor unitario debe ser mayor a cero.");

        dto.setId_cotizacion(idCot);

        dto.setSubtotal(
                dto.getValor_unitario().multiply(new BigDecimal(dto.getCantidad()))
        );

        detalleService.agregarDetalleCot(dto);

        return ResponseEntity.ok("Detalle agregado correctamente.");
    }


    /**
     * Consulta al servicio de IA para predecir la probabilidad de éxito de una cotización existente.
     * @param id ID de la cotización a analizar.
     * @return Mapa con la probabilidad y un mensaje informativo.
     * @throws ResourceNotFoundException Si la cotización no existe.
     */
    @GetMapping("/{id}/probabilidad")
    public ResponseEntity<Map<String, Object>> obtenerProbabilidad(@PathVariable int id) {
        CotizacionDTO cot = service.buscarCotizacion(id);
        if (cot == null)
            throw new ResourceNotFoundException("No existe la cotización con ID: " + id);


        List<DetalleCotizacionDTO> detalles = detalleService.listarDetallesCot()
                .stream()
                .filter(d -> d.getId_cotizacion() == id)
                .toList();


        ClienteDTO cliente = clienteService.buscarCliente(cot.getId_cliente());
        int historial = service.cantidadCotizacionesPorCliente(cot.getId_cliente());
        String esNuevo = "";
        if (historial>0){
            esNuevo = "no";
        }else{
            esNuevo="si";
        }

        double total = cot.getTotal_pagar().doubleValue();
        int items = detalles.size();

        int materiales = detalles.stream()
                .mapToInt(DetalleCotizacionDTO::getCantidad)
                .sum();

        String tieneTramites = cot.getReferencia().toLowerCase().contains("codensa") ||
                cot.getReferencia().toLowerCase().contains("tramite") ? "si" : "no";

        Double porcentaje = prediccionService.obtenerProbabilidad(total, materiales, items, esNuevo, tieneTramites);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("id_cotizacion", id);
        respuesta.put("probabilidad_aceptacion", porcentaje);
        respuesta.put("mensaje", "Cálculo basado en historial de VC Eléctricos");

        return ResponseEntity.ok(respuesta);
    }

    /**
     * Tarea programada (Cron) que se ejecuta el primer día de cada mes para
     * re-entrenar el modelo de IA si hay suficientes datos nuevos (Aceptados/Rechazados).
     */
    @Scheduled(cron = "0 0 0 1 * *")
    public void verificarYEntrenarModeloAutomatico() {
        System.out.println("Iniciando proceso mensual de re-entrenamiento...");

        List<CotizacionDTO> todas = service.listarCotizacion();

        List<CotizacionDTO> validasParaEntrenar = todas.stream()
                .filter(c -> c.getEstado().equalsIgnoreCase("Aceptado") ||
                        c.getEstado().equalsIgnoreCase("Rechazado"))
                .collect(Collectors.toList());

        if (validasParaEntrenar.size() > 100) {

            List<DetalleCotizacionDTO> todosDetalles = detalleService.listarDetallesCot();
            Map<Integer, List<DetalleCotizacionDTO>> detallesPorId =
                    todosDetalles.stream().collect(Collectors.groupingBy(DetalleCotizacionDTO::getId_cotizacion));


            List<CotizacionReentrenarRequest> listaParaPython = validasParaEntrenar.stream()
                    .map(dto -> {
                        List<DetalleCotizacionDTO> misDetalles =
                                detallesPorId.getOrDefault(dto.getId_cotizacion(), new ArrayList<>());

                        int totalMateriales = misDetalles.stream().mapToInt(DetalleCotizacionDTO::getCantidad).sum();
                        int totalItems = misDetalles.size();

                        int historial = service.cantidadCotizacionesPorCliente(dto.getId_cliente());
                        String esNuevo = (historial <= 1) ? "si" : "no";

                        String ref = (dto.getReferencia() != null) ? dto.getReferencia().toLowerCase() : "";
                        String tieneTramites = (ref.contains("tramite") || ref.contains("legalizacion")) ? "si" : "no";

                        return new CotizacionReentrenarRequest(
                                dto.getTotal_pagar().doubleValue(),
                                totalMateriales,
                                totalItems,
                                esNuevo,
                                tieneTramites,
                                dto.getEstado()
                        );
                    }).collect(Collectors.toList());

            try {
                prediccionService.entrenarModelo(listaParaPython);
                System.out.println("Motor de IA actualizado con éxito.");
            } catch (Exception e) {
                System.err.println("Error al contactar con Python: " + e.getMessage());
            }
        }
    }
}
