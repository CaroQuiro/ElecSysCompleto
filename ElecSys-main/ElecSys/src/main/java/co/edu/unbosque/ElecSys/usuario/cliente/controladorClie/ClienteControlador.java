package co.edu.unbosque.ElecSys.usuario.cliente.controladorClie;

import co.edu.unbosque.ElecSys.config.excepcion.DuplicateResourceException;
import co.edu.unbosque.ElecSys.config.excepcion.InvalidFieldException;
import co.edu.unbosque.ElecSys.config.excepcion.ResourceNotFoundException;
import co.edu.unbosque.ElecSys.usuario.cliente.dtoClie.ClienteDTO;
import co.edu.unbosque.ElecSys.usuario.cliente.servicioClie.ClienteServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de clientes en el sistema ElecSys.
 * Proporciona servicios para el registro, consulta, actualización y deshabilitación
 * de clientes, integrando validaciones de negocio.
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/clientes")
public class ClienteControlador {

    @Autowired
    private ClienteServiceImpl clienteService;

    private static final int MAX_TELEFONO = 12;

    /**
     * Obtiene la lista de todos los clientes registrados.
     * @return ResponseEntity con la lista de {@link ClienteDTO}.
     * @throws ResourceNotFoundException Si no existen clientes en la base de datos.
     */
    @GetMapping("/listar")
    public ResponseEntity<List<ClienteDTO>> listarClientes() {
        List<ClienteDTO> lista = clienteService.listarClientes();

        if (lista.isEmpty()) {
            throw new ResourceNotFoundException("No hay clientes registrados.");
        }
        return ResponseEntity.ok(lista);
    }

    /**
     * Busca clientes mediante un criterio de texto (nombre, correo, etc.).
     * @param query Texto de búsqueda.
     * @return ResponseEntity con la lista de clientes que coinciden con el criterio.
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<ClienteDTO>> buscarCliente(@RequestParam String query){
        return ResponseEntity.ok(clienteService.buscarClienteTexto(query));
    }

    /**
     * Busca un cliente específico por su identificador único.
     * @param id Identificador del cliente.
     * @return ResponseEntity con el {@link ClienteDTO} encontrado.
     * @throws ResourceNotFoundException Si el cliente con el ID proporcionado no existe.
     */
    @GetMapping("/buscar/{id}")
    public ResponseEntity<ClienteDTO> buscarCliente(@PathVariable int id) {
        ClienteDTO cliente = clienteService.buscarCliente(id);
        if (cliente == null) {
            throw new ResourceNotFoundException("No existe cliente con ID: " + id);
        }
        return ResponseEntity.ok(cliente);
    }

    /**
     * Registra un nuevo cliente en el sistema.
     * @param dto Datos del cliente a agregar.
     * @return ResponseEntity con el mensaje de éxito de la operación.
     * @throws DuplicateResourceException Si ya existe un cliente con el mismo ID.
     */
    @PostMapping("/agregar")
    public ResponseEntity<String> agregarCliente(@RequestBody ClienteDTO dto) {

        if (clienteService.buscarCliente(dto.getId_cliente()) != null) {
            throw new DuplicateResourceException("Ya existe un cliente con ID: " + dto.getId_cliente());
        }

        validarCliente(dto);

        String msg = clienteService.agregarCliente(dto);

        return ResponseEntity.ok(msg);
    }

    /**
     * Cambia el estado de un cliente a deshabilitado.
     * @param id Identificador del cliente.
     * @return ResponseEntity con el mensaje de confirmación.
     * @throws ResourceNotFoundException Si el cliente no existe.
     */
    @DeleteMapping("/deshabilitar/{id}")
    public ResponseEntity<String> deshabilitarCliente(@PathVariable int id) {

        if (clienteService.buscarCliente(id) == null) {
            throw new ResourceNotFoundException("No existe cliente con ID: " + id);
        }

        String msg = clienteService.deshabilitarCliente(id);

        return ResponseEntity.ok(msg);
    }

    /**
     * Actualiza la información de un cliente existente.
     * @param id Identificador del cliente.
     * @param dto Nuevos datos del cliente.
     * @return ResponseEntity con el mensaje de éxito.
     * @throws ResourceNotFoundException Si el cliente no existe.
     */
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<String> actualizarCliente(
            @PathVariable int id,
            @RequestBody ClienteDTO dto) {

        ClienteDTO actual = clienteService.buscarCliente(id);

        if (actual == null) {
            throw new ResourceNotFoundException("No existe cliente con ID: " + id);
        }

        // No permitir cambiar ID
        dto.setId_cliente(id);
        validarCliente(dto);

        String msg = clienteService.actualizarCliente(id, dto);

        return ResponseEntity.ok(msg);
    }

    /**
     * Realiza validaciones de integridad de datos para el objeto cliente.
     * @param dto Objeto cliente a validar.
     * @throws InvalidFieldException Si el nombre, correo o estado están ausentes, o si el teléfono excede el límite.
     */
    private void validarCliente(ClienteDTO dto) {

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new InvalidFieldException("El nombre es obligatorio.");
        }

        if (dto.getCorreo() == null || dto.getCorreo().isBlank()) {
            throw new InvalidFieldException("El correo es obligatorio.");
        }

        if (dto.getTelefono() != null && dto.getTelefono().length() > MAX_TELEFONO) {
            throw new InvalidFieldException("El teléfono no puede tener más de 12 caracteres.");
        }

        if (dto.getEstado() == null || dto.getEstado().isBlank()) {
            throw new InvalidFieldException("El estado es obligatorio.");
        }
    }
}


