package co.edu.unbosque.ElecSys.lugarTrabajo.controladorLug;

import co.edu.unbosque.ElecSys.config.excepcion.DuplicateResourceException;
import co.edu.unbosque.ElecSys.config.excepcion.InvalidFieldException;
import co.edu.unbosque.ElecSys.config.excepcion.ResourceNotFoundException;
import co.edu.unbosque.ElecSys.lugarTrabajo.dtoLug.LugarTrabajoDTO;
import co.edu.unbosque.ElecSys.lugarTrabajo.servicioLug.LugarTrabajoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST que gestiona los puntos de acceso para los lugares de trabajo.
 * Permite realizar operaciones CRUD y búsquedas personalizadas sobre las sedes o puntos de obra.
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/lugares-trabajo")
public class LugarTrabajoControlador {

    @Autowired
    private LugarTrabajoService lugarTrabajoService;

    /**
     * Recupera la lista completa de todos los lugares de trabajo registrados en el sistema.
     * @return ResponseEntity con la lista de objetos LugarTrabajoDTO.
     */
    @GetMapping("/listar")
    public ResponseEntity<List<LugarTrabajoDTO>> listarLugaresDeTrabajo() {
        return ResponseEntity.ok(lugarTrabajoService.listarLugar());
    }

    /**
     * Registra un nuevo lugar de trabajo validando que el ID no esté duplicado y los campos sean válidos.
     * @param dto Objeto con la información del lugar a agregar.
     * @return ResponseEntity con un mensaje de éxito.
     * @throws InvalidFieldException Si el ID es inválido o faltan campos obligatorios (nombre, dirección).
     * @throws DuplicateResourceException Si el ID ya se encuentra registrado en el sistema.
     */
    @PostMapping("/agregar")
    public ResponseEntity<String> agregarLugarDeTrabajo(@RequestBody LugarTrabajoDTO dto) {

        if (dto.getIdLugar() <= 0) {
            throw new InvalidFieldException("El id_lugar debe ser mayor a 0.");
        }

        if (dto.getNombreLugar() == null || dto.getNombreLugar().isBlank()) {
            throw new InvalidFieldException("El nombre del lugar es obligatorio.");
        }

        if (dto.getDireccion() == null || dto.getDireccion().isBlank()) {
            throw new InvalidFieldException("La dirección es obligatoria.");
        }

        if (lugarTrabajoService.buscarLugar(dto.getIdLugar()) != null) {
            throw new DuplicateResourceException("Ya existe un lugar con el ID: " + dto.getIdLugar());
        }

        String mensaje = lugarTrabajoService.crearLugar(dto);
        return ResponseEntity.ok(mensaje);
    }


    /**
     * Actualiza la información de un lugar de trabajo existente mediante su ID.
     * @param id Identificador único del lugar a actualizar.
     * @param dto Nuevos datos del lugar de trabajo.
     * @return ResponseEntity con mensaje confirmando la edición.
     * @throws InvalidFieldException Si el ID es inválido, faltan campos obligatorios o se intenta cambiar el ID.
     * @throws ResourceNotFoundException Si no existe un lugar con el ID proporcionado.
     */
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<String> actualizarLugarDeTrabajo(
            @PathVariable int id,
            @RequestBody LugarTrabajoDTO dto) {

        if (id <= 0) {
            throw new InvalidFieldException("El ID debe ser mayor que 0.");
        }

        if (lugarTrabajoService.buscarLugar(id) == null) {
            throw new ResourceNotFoundException("No existe un lugar de trabajo con ID: " + id);
        }

        // Validar campos del DTO
        if (dto.getNombreLugar() == null || dto.getNombreLugar().isBlank()) {
            throw new InvalidFieldException("El nombre del lugar es obligatorio.");
        }

        if (dto.getDireccion() == null || dto.getDireccion().isBlank()) {
            throw new InvalidFieldException("La dirección es obligatoria.");
        }

        if (dto.getIdLugar() != 0 && dto.getIdLugar() != id) {
            throw new InvalidFieldException("No se puede modificar el ID del lugar de trabajo.");
        }

        String mensaje = lugarTrabajoService.editarLugar(id, dto);
        return ResponseEntity.ok(mensaje);
    }

    /**
     * Realiza una búsqueda de lugares de trabajo basada en una coincidencia de texto.
     * @param query Texto a buscar en el nombre o dirección del lugar.
     * @return ResponseEntity con la lista de lugares que coinciden con el criterio.
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<LugarTrabajoDTO>> buscarLugar(@RequestParam String query){
        return ResponseEntity.ok(lugarTrabajoService.buscarLugarTexto(query));
    }

    /**
     * Busca y recupera la información detallada de un lugar de trabajo por su identificador numérico.
     * @param id ID del lugar de trabajo.
     * @return ResponseEntity con el objeto LugarTrabajoDTO.
     * @throws ResourceNotFoundException Si no se encuentra un lugar con dicho ID.
     */
    @GetMapping("/buscar/{id}")
    public ResponseEntity<LugarTrabajoDTO> buscarLugarId(@PathVariable int id){
        LugarTrabajoDTO lugar = lugarTrabajoService.buscarLugar(id);
        if (lugar == null) {
            throw new ResourceNotFoundException("No existe lugar con ID: " + id);
        }
        return ResponseEntity.ok(lugar);
    }
}