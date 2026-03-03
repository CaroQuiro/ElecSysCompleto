package co.edu.unbosque.ElecSys.Usuario.Cliente.ControladorClie;

import co.edu.unbosque.ElecSys.Config.Excepcion.*;
import co.edu.unbosque.ElecSys.Usuario.Cliente.DTOClie.ClienteDTO;
import co.edu.unbosque.ElecSys.Usuario.Cliente.ServicioClie.ClienteServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/clientes")
public class ClienteControlador {

    @Autowired
    private ClienteServiceImpl clienteService;

    private static final int MAX_TELEFONO = 12;

    // =============================
    // LISTAR CLIENTES
    // =============================
    @GetMapping("/listar")
    public ResponseEntity<List<ClienteDTO>> listarClientes() {
        List<ClienteDTO> lista = clienteService.listarClientes();

        if (lista.isEmpty()) {
            throw new ResourceNotFoundException("No hay clientes registrados.");
        }
        return ResponseEntity.ok(lista);
    }

    // =============================
    // BUSCAR CLIENTE POR TEXTO
    // =============================
    @GetMapping("/buscar")
    public ResponseEntity<List<ClienteDTO>> buscarCliente(@RequestParam String query){
        return ResponseEntity.ok(clienteService.buscarClienteTexto(query));
    }

    // =============================
    // BUSCAR CLIENTE POR ID
    // =============================
    @GetMapping("/buscar/{id}")
    public ResponseEntity<ClienteDTO> buscarCliente(@PathVariable int id) {
        ClienteDTO cliente = clienteService.buscarCliente(id);
        if (cliente == null) {
            throw new ResourceNotFoundException("No existe cliente con ID: " + id);
        }
        return ResponseEntity.ok(cliente);
    }

    // =============================
    // AGREGAR CLIENTE
    // =============================
    @PostMapping("/agregar")
    public ResponseEntity<String> agregarCliente(@RequestBody ClienteDTO dto) {

        if (clienteService.buscarCliente(dto.getId_cliente()) != null) {
            throw new DuplicateResourceException("Ya existe un cliente con ID: " + dto.getId_cliente());
        }

        validarCliente(dto);

        String msg = clienteService.agregarCliente(dto);

        return ResponseEntity.ok(msg);
    }

    // =============================
    // BORRAR CLIENTE
    // =============================
    @DeleteMapping("/deshabilitar/{id}")
    public ResponseEntity<String> deshabilitarCliente(@PathVariable int id) {

        if (clienteService.buscarCliente(id) == null) {
            throw new ResourceNotFoundException("No existe cliente con ID: " + id);
        }

        String msg = clienteService.deshabilitarCliente(id);

        return ResponseEntity.ok(msg);
    }

    // =============================
    // ACTUALIZAR CLIENTE
    // =============================
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

    // =============================
    // VALIDACIONES
    // =============================
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


