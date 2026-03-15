package co.edu.unbosque.ElecSys.usuario.trabajador.controladorTra;

import co.edu.unbosque.ElecSys.config.excepcion.DuplicateResourceException;
import co.edu.unbosque.ElecSys.config.excepcion.InvalidFieldException;
import co.edu.unbosque.ElecSys.config.excepcion.ResourceNotFoundException;
import co.edu.unbosque.ElecSys.usuario.trabajador.dtoTra.TrabajadorDTO;
import co.edu.unbosque.ElecSys.usuario.trabajador.servicioTra.TrabajadorServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * Controlador REST para administrar los trabajadores (usuarios del sistema).
 * Incluye lógica para el registro de nuevos usuarios, asignación de roles y
 * validación de credenciales.
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/trabajador")
public class TrabajadorControlador {

    @Autowired
    private TrabajadorServiceImpl trabajadorService;

    private static final int MAX_TELEFONO = 12;

    /**
     * Lista todos los trabajadores registrados en la plataforma.
     * @return ResponseEntity con la lista de {@link TrabajadorDTO}.
     * @throws ResourceNotFoundException Si la lista está vacía.
     */
    @GetMapping("/listar")
    public ResponseEntity<List<TrabajadorDTO>> listarTrabajadores() {
        List<TrabajadorDTO> lista = trabajadorService.listarTrabajadores();

        if (lista.isEmpty()) {
            throw new ResourceNotFoundException("No hay trabajadores registrados.");
        }

        return ResponseEntity.ok(lista);
    }

    /**
     * Busca un trabajador específico por su ID.
     * @param id Identificador del trabajador.
     * @return ResponseEntity con el DTO del trabajador.
     * @throws ResourceNotFoundException Si el trabajador no existe.
     */
    @GetMapping("/buscar/{id}")
    public ResponseEntity<TrabajadorDTO> buscarTrabajador(@PathVariable int id) {

        TrabajadorDTO trabajador = trabajadorService.buscarTrabajador(id);

        if (trabajador == null) {
            throw new ResourceNotFoundException("No existe trabajador con ID: " + id);
        }

        return ResponseEntity.ok(trabajador);
    }

    /**
     * Registra un nuevo trabajador validando roles y duplicados.
     * @param dto Información del trabajador.
     * @return ResponseEntity con mensaje de éxito.
     * @throws DuplicateResourceException Si el ID ya está en uso.
     */
    @PostMapping("/agregar")
    public ResponseEntity<String> agregarTrabajador(@RequestBody TrabajadorDTO dto) {

        if (trabajadorService.buscarTrabajador(dto.getId_trabajador()) != null) {
            throw new DuplicateResourceException("Ya existe un trabajador con ID: " + dto.getId_trabajador());
        }

        validarTrabajador(dto);

        String msg = trabajadorService.agregarTrabajador(dto);

        return ResponseEntity.ok(msg);
    }

    /**
     * Deshabilita a un trabajador del sistema.
     * @param id ID del trabajador.
     * @return ResponseEntity con mensaje de confirmación.
     * @throws ResourceNotFoundException Si el ID no existe.
     */
    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<String> borrarTrabajador(@PathVariable int id) {

        if (trabajadorService.buscarTrabajador(id) == null) {
            throw new ResourceNotFoundException("No existe trabajador con ID: " + id);
        }

        String msg = trabajadorService.deshabilitarTrabajador(id);

        return ResponseEntity.ok(msg);
    }

    /**
     * Actualiza la información de un trabajador, incluyendo su estado y contraseña opcional.
     * @param id ID del trabajador.
     * @param dto Nuevos datos.
     * @return ResponseEntity con el mensaje de éxito.
     * @throws ResourceNotFoundException Si el trabajador no existe.
     */
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<String> actualizarTrabajador(
            @PathVariable int id,
            @RequestBody TrabajadorDTO dto) {

        TrabajadorDTO actual = trabajadorService.buscarTrabajador(id);

        if (actual == null) {
            throw new ResourceNotFoundException("No existe trabajador con ID: " + id);
        }

        // No permitir cambiar ID
        dto.setId_trabajador(id);

        validarTrabajadorParaActualizar(dto);

        String msg = trabajadorService.actualizarTrabajador(id, dto);

        return ResponseEntity.ok(msg);
    }


    private static final Set<String> TIPOS_VALIDOS =
            Set.of("ADMIN", "USUARIO");


    /**
     * Valida los campos requeridos para la creación de un nuevo trabajador.
     * @param dto DTO a validar.
     * @throws InvalidFieldException Si faltan datos críticos o el tipo de usuario no es ADMIN/USUARIO.
     */
    private void validarTrabajador(TrabajadorDTO dto) {

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new InvalidFieldException("El nombre es obligatorio.");
        }

        if (dto.getCorreo() == null || dto.getCorreo().isBlank()) {
            throw new InvalidFieldException("El correo es obligatorio.");
        }

        if (dto.getTipo_usuario() == null || dto.getTipo_usuario().isBlank()) {
            throw new InvalidFieldException("El tipo de usuario es obligatorio.");
        }

        if (!TIPOS_VALIDOS.contains(dto.getTipo_usuario())) {
            throw new InvalidFieldException(
                    "Tipo de usuario inválido. Debe ser uno de: " + TIPOS_VALIDOS
            );
        }

        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new InvalidFieldException("La contraseña es obligatoria.");
        }

        if (dto.getTelefono() != null && dto.getTelefono().length() > MAX_TELEFONO) {
            throw new InvalidFieldException("El teléfono no puede tener más de 12 caracteres.");
        }

        if (dto.getEstado() == null || dto.getEstado().isBlank()) {
            throw new InvalidFieldException("El estado es obligatoria.");
        }
    }

    /**
     * Valida los campos requeridos para la actualización, omitiendo la obligatoriedad de la contraseña.
     * @param dto DTO a validar.
     * @throws InvalidFieldException Si los campos de perfil son incorrectos.
     */
    private void validarTrabajadorParaActualizar(TrabajadorDTO dto) {

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new InvalidFieldException("El nombre es obligatorio.");
        }

        if (dto.getCorreo() == null || dto.getCorreo().isBlank()) {
            throw new InvalidFieldException("El correo es obligatorio.");
        }

        if (dto.getTipo_usuario() == null || dto.getTipo_usuario().isBlank()) {
            throw new InvalidFieldException("El tipo de usuario es obligatorio.");
        }

        if (!TIPOS_VALIDOS.contains(dto.getTipo_usuario())) {
            throw new InvalidFieldException(
                    "Tipo de usuario inválido. Debe ser uno de: " + TIPOS_VALIDOS
            );
        }

        if (dto.getTelefono() != null && dto.getTelefono().length() > MAX_TELEFONO) {
            throw new InvalidFieldException("El teléfono no puede tener más de 12 caracteres.");
        }

        if (dto.getEstado() == null || dto.getEstado().isBlank()) {
            throw new InvalidFieldException("El estado es obligatoria.");
        }
    }
}

