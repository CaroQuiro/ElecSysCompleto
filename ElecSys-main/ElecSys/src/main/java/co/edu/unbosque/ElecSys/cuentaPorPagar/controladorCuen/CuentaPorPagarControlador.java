package co.edu.unbosque.ElecSys.cuentaPorPagar.controladorCuen;

import co.edu.unbosque.ElecSys.config.excepcion.InvalidFieldException;
import co.edu.unbosque.ElecSys.config.excepcion.PdfGenerationException;
import co.edu.unbosque.ElecSys.config.excepcion.ResourceNotFoundException;
import co.edu.unbosque.ElecSys.cuentaPorPagar.archivo.PDF_Archivo_Cuenta;
import co.edu.unbosque.ElecSys.cuentaPorPagar.dtoCuen.CuentaPorPagarDTO;
import co.edu.unbosque.ElecSys.cuentaPorPagar.dtoCuen.CuentaPorPagarRequest;
import co.edu.unbosque.ElecSys.cuentaPorPagar.detalle_Cuenta.detalleDTO.Detalle_CuentaDTO;
import co.edu.unbosque.ElecSys.cuentaPorPagar.detalle_Cuenta.servicioDetalleCuenta.DetalleCuentaService;
import co.edu.unbosque.ElecSys.cuentaPorPagar.servicioCuen.CuentaPorPagarImplService;
import co.edu.unbosque.ElecSys.usuario.cliente.dtoClie.ClienteDTO;
import co.edu.unbosque.ElecSys.usuario.cliente.servicioClie.ClienteServiceImpl;
import co.edu.unbosque.ElecSys.usuario.trabajador.servicioTra.TrabajadorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * Controlador REST que expone los endpoints para la gestión de Cuentas por Pagar.
 * Maneja operaciones CRUD, validaciones de negocio y generación de documentos.
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/cuentas_pagar")
public class CuentaPorPagarControlador {

    @Autowired
    private CuentaPorPagarImplService cuentaPorPagarService;

    @Autowired
    private DetalleCuentaService detalleCuentaService;

    @Autowired
    private ClienteServiceImpl clienteService;

    @Autowired
    private TrabajadorServiceImpl trabajadorService;

    @Autowired
    private PDF_Archivo_Cuenta pdfService;

    private static final List<String> ESTADOS_VALIDOS =
            List.of("PAGADO", "PENDIENTE", "EN_PROCESO");

    /**
     * Registra una nueva cuenta de cobro, sus detalles y genera el PDF para descarga inmediata.
     * @param solitud Objeto request con datos de cuenta y lista de detalles.
     * @return ResponseEntity con el PDF adjunto.
     * @throws IOException Si falla la descarga del archivo.
     * @throws InvalidFieldException Si los datos de entrada no cumplen las reglas de negocio.
     * @throws PdfGenerationException Si ocurre un error técnico creando el PDF.
     */
    @PostMapping("/agregar")
    public ResponseEntity<byte[]> agregarCuenta(@RequestBody CuentaPorPagarRequest solitud) throws IOException {

        if (solitud.getCuentaPorPagarDTO() == null ||  solitud.getDetalleCuentaDTOS() == null
                || solitud.getDetalleCuentaDTOS().isEmpty()){
            throw new InvalidFieldException("La solicitud no contiene datos de cuenta de cobro o le faltan detalles.");
        }
        CuentaPorPagarDTO cuenta = solitud.getCuentaPorPagarDTO();

        if (cuenta.getId_trabajador() <= 0) {
            throw new InvalidFieldException("El id_trabajador es obligatorio y relacionarse con la base de datos");
        }

        ClienteDTO cliente = clienteService.buscarCliente(cuenta.getId_cliente());
        if (cliente == null || !"ACTIVO".equalsIgnoreCase(cliente.getEstado())) {
            throw new InvalidFieldException("El cliente no existe o no se encuentra ACTIVO para esta operación.");
        }

        if (cuenta.getMonto() == null || cuenta.getMonto().doubleValue() <= 0) {
            throw new InvalidFieldException("El monto debe ser mayor que 0.");
        }

        List<Detalle_CuentaDTO> detalles = solitud.getDetalleCuentaDTOS();
        if (detalles == null || detalles.isEmpty()){
            throw new InvalidFieldException("Debe enviar al menos un detalle");
        }

        if (cuenta.getEstado() == null || !ESTADOS_VALIDOS.contains(cuenta.getEstado())) {
            throw new InvalidFieldException("Estado inválido. Debe ser: " + ESTADOS_VALIDOS);
        }

        CuentaPorPagarDTO cuentaActual = new CuentaPorPagarDTO(
                0,cuenta.getId_trabajador(), cuenta.getId_cliente(), cuenta.getNota(),
                cuenta.getFecha_realizacion(), cuenta.getMonto(), cuenta.getEstado());

        CuentaPorPagarDTO cuentaAguardar = cuentaPorPagarService.agregarCuentaPagar(cuentaActual);

        for (Detalle_CuentaDTO detalleCuentaDTO: detalles){
            detalleCuentaDTO.setId_cuenta_pagar(cuentaAguardar.getId_cuenta_pagar());
            detalleCuentaService.agregarDetalleCuenta(detalleCuentaDTO);
        }

        byte[] pdf;
        try{
            pdf = pdfService.generarArchivoCuenta(cuentaAguardar, cliente, detalles);
        } catch (Exception e) {
            throw new PdfGenerationException("Error al generar el PDF: " + e.getMessage());
        }

        String nombreArchivo = pdfService.descargarCuentaPDF(cuentaAguardar, solitud.getReferencia(), pdf);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+nombreArchivo)
                .contentType(MediaType.APPLICATION_PDF).body(pdf);

    }

    /**
     * Obtiene el listado de todas las cuentas por pagar registradas.
     * @return Lista de DTOs de cuentas por pagar.
     * @throws ResourceNotFoundException Si la lista está vacía.
     */
    @GetMapping("/listar")
    public ResponseEntity<List<CuentaPorPagarDTO>> listarCuentas() {
        List<CuentaPorPagarDTO> lista = cuentaPorPagarService.listarCuentasPagar();

        if (lista.isEmpty()) {
            throw new ResourceNotFoundException("No hay cuentas por pagar registradas.");
        }

        return ResponseEntity.ok(lista);
    }

    /**
     * Elimina una cuenta y sus detalles asociados siempre que no esté en estado PAGADO.
     * @param id ID de la cuenta a eliminar.
     * @return Mensaje de confirmación.
     * @throws ResourceNotFoundException Si el ID no existe.
     * @throws InvalidFieldException Si la cuenta ya fue pagada.
     */
    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<String> borrarCuenta(@PathVariable int id) {

        if (!cuentaPorPagarService.existeCuenta(id)) {
            throw new ResourceNotFoundException("No existe la cuenta por pagar con ID: " + id);
        }

        CuentaPorPagarDTO actual = cuentaPorPagarService.buscarCuenta(id);

        if (actual.getEstado().equalsIgnoreCase("PAGADO")) {
            throw new InvalidFieldException("No se puede eliminar una cuenta que ya ha sido PAGADA.");
        }

        detalleCuentaService.listarDetallesCuentas()
                .stream()
                .filter((d -> d.getId_cuenta_pagar() == id))
                .forEach(d -> detalleCuentaService.borrarDetalleCuenta(d.getId_detalle_cuenta()));

        String mensaje = cuentaPorPagarService.borrarCuentaPagar(id);
        return ResponseEntity.ok(mensaje);
    }

    /**
     * Actualiza la información de una cuenta existente.
     * @param id ID de la cuenta.
     * @param dto Nuevos datos de la cuenta.
     * @return Mensaje de éxito.
     * @throws ResourceNotFoundException Si la cuenta no existe.
     * @throws InvalidFieldException Si se intenta modificar el ID o una cuenta pagada.
     */
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<String> actualizarCuenta(
            @PathVariable int id,
            @RequestBody CuentaPorPagarDTO dto) {

        System.out.println("CLIENTE RECIBIDO: " + dto.getId_cliente());

        if (!cuentaPorPagarService.existeCuenta(id)) {
            throw new ResourceNotFoundException("No existe la cuenta por pagar con ID: " + id);
        }

        // Validaciones
        if (dto.getId_cuenta_pagar() != id) {
            throw new InvalidFieldException("El id_cuenta_pagar no se puede modificar.");
        }

        if (dto.getMonto() == null || dto.getMonto().doubleValue() <= 0) {
            throw new InvalidFieldException("El monto debe ser mayor que 0.");
        }

        if (dto.getEstado() == null || !ESTADOS_VALIDOS.contains(dto.getEstado())) {
            throw new InvalidFieldException("Estado inválido. Debe ser: " + ESTADOS_VALIDOS);
        }

        CuentaPorPagarDTO actual = cuentaPorPagarService.buscarCuenta(id);

        if (actual.getEstado().equalsIgnoreCase("PAGADO")) {
            throw new InvalidFieldException("No se pueden modificar datos de una cuenta en estado PAGADO.");
        }

        ClienteDTO cliente = clienteService.buscarCliente(dto.getId_cliente());
        if (cliente == null || !"ACTIVO".equalsIgnoreCase(cliente.getEstado())) {
            throw new InvalidFieldException("No se puede asignar la cuenta a un cliente inactivo.");
        }

        String mensaje = cuentaPorPagarService.actualizarCuenta(id, dto);
        return ResponseEntity.ok(mensaje);
    }

    /**
     * Busca una cuenta de cobro específica por su ID.
     * @param id ID de la cuenta.
     * @return DTO de la cuenta encontrada.
     * @throws ResourceNotFoundException Si no se encuentra el registro.
     */
        @GetMapping("/buscar/{id}")
        public ResponseEntity<CuentaPorPagarDTO> buscarCuenta(@PathVariable int id) {
            CuentaPorPagarDTO dto = cuentaPorPagarService.buscarCuenta(id);

            if (dto == null) {
                throw new ResourceNotFoundException("No existe la cuenta por pagar con ID: " + id);
            }

            return ResponseEntity.ok(dto);
        }

    /**
     * Lista los detalles específicos asociados a una cuenta de cobro.
     * @param id ID de la cuenta.
     * @return Lista de Detalle_CuentaDTO.
     * @throws ResourceNotFoundException Si la cuenta no existe.
     */
    @GetMapping("/{id}/detallesCuentas")
    public List<Detalle_CuentaDTO> listarDetallesCuentas(@PathVariable int id){
        if (!cuentaPorPagarService.existeCuenta(id)){
            throw new ResourceNotFoundException("No existe la cuenta de cobro con ID: "+ id );
        }

        return detalleCuentaService.listarDetallesCuentas().stream()
                .filter(detalle -> detalle.getId_cuenta_pagar() == id)
                .toList();
    }


    /**
     * Elimina un detalle específico de una cuenta.
     * @param idCuenta ID de la cuenta padre.
     * @param idDetalle ID del detalle a eliminar.
     * @return Mensaje de confirmación.
     * @throws ResourceNotFoundException Si la cuenta o detalle no existen.
     * @throws InvalidFieldException Si el detalle no pertenece a la cuenta o es el último detalle.
     */
    @DeleteMapping("/borrar/{idCuenta}/detalle/{idDetalle}")
    public String borrarDetalleCuenta(@PathVariable int idCuenta, @PathVariable int idDetalle){

        if (!cuentaPorPagarService.existeCuenta(idCuenta)){
            throw  new ResourceNotFoundException("No existe la Cotizacion con ID: "+ idCuenta);
        }

        Detalle_CuentaDTO detalle = detalleCuentaService.listarDetallesCuentas().stream()
                .filter( d -> d.getId_detalle_cuenta() == idDetalle)
                .findFirst().orElseThrow( () -> new ResourceNotFoundException("El detalle no existe"));

        if (detalle.getId_cuenta_pagar() != idCuenta){
            throw  new InvalidFieldException("Ese detalle no pertenece a la Cuenta por pagar indicada");
        }

        List<Detalle_CuentaDTO> todos = detalleCuentaService.listarDetallesCuentas().stream()
                .filter(d -> d.getId_cuenta_pagar() == idCuenta).toList();

        if (todos.size() <= 1) {
            throw new InvalidFieldException("La cuenta no puede quedarse sin detalles. Elimine la cuenta completa si es necesario.");
        }

        detalleCuentaService.borrarDetalleCuenta(idDetalle);
        return "Detalle Cuenta eliminado Correctamente";
    }


    /**
     * Agrega un nuevo concepto o detalle a una cuenta de cobro existente.
     * @param idCuenta ID de la cuenta.
     * @param detalledto Datos del nuevo detalle.
     * @return ResponseEntity con mensaje de éxito.
     */
    @PostMapping("Crear/{idCuenta}/detalle")
    public ResponseEntity<String> agregarDetalle(@PathVariable int idCuenta, @RequestBody Detalle_CuentaDTO detalledto){

        if (!cuentaPorPagarService.existeCuenta(idCuenta)){
            throw  new ResourceNotFoundException("No existe la cuenta con ID: "+ idCuenta);
        }

        if (detalledto.getDescripcion() == null){
            throw  new InvalidFieldException("La descripcion no puede estar vacia");
        }

        detalledto.setId_cuenta_pagar(idCuenta);

        detalleCuentaService.agregarDetalleCuenta(detalledto);

        return ResponseEntity.ok("Detalle agregado correctamente");
    }

    /**
     * Modifica la descripción o valor de un detalle de cuenta existente.
     * @param idCuenta ID de la cuenta padre.
     * @param idDetalle ID del detalle a modificar.
     * @param detalle Nuevos datos del detalle.
     * @return Mensaje de estado de la operación.
     * @throws ResourceNotFoundException Si no se encuentra el registro.
     */
    @PutMapping("actualizar/{idCuenta}/detalle-cuenta/{idDetalle}")
    public String actualizarDetalleCuenta(@PathVariable int idCuenta, @PathVariable int idDetalle, @RequestBody Detalle_CuentaDTO detalle){

        if (!cuentaPorPagarService.existeCuenta(idCuenta)){
            throw new ResourceNotFoundException("No existe la cuenta por pagar con ID: " + idCuenta);
        }

        Detalle_CuentaDTO actual = detalleCuentaService.listarDetallesCuentas().stream()
                .filter(d -> d.getId_detalle_cuenta() == idDetalle)
                .findFirst().orElseThrow( () -> new ResourceNotFoundException("No existe el detalle con ID: " + idDetalle));

        if (detalle.getDescripcion() == null || detalle.getDescripcion().isBlank()){
            throw  new InvalidFieldException("El Detalle no puede estar en vacio");
        }

        String mensaje = detalleCuentaService.actualizarDetalleCuenta(idDetalle, detalle);
        return mensaje;
    }

}
