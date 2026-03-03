package co.edu.unbosque.ElecSys.LugarTrabajo.ControladorLug;

import co.edu.unbosque.ElecSys.LugarTrabajo.DTOLug.LugarTrabajoDTO;
import co.edu.unbosque.ElecSys.LugarTrabajo.ServicioLug.LugarTrabajoService;
import co.edu.unbosque.ElecSys.Config.Excepcion.*;
import co.edu.unbosque.ElecSys.Usuario.Cliente.DTOClie.ClienteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/lugares-trabajo")
public class LugarTrabajoControlador {

    @Autowired
    private LugarTrabajoService lugarTrabajoService;

    // =========================================================
    // LISTAR
    // =========================================================
    @GetMapping("/listar")
    public ResponseEntity<List<LugarTrabajoDTO>> listarLugaresDeTrabajo() {
        return ResponseEntity.ok(lugarTrabajoService.listarLugar());
    }

    // =========================================================
    // AGREGAR
    // =========================================================
    @PostMapping("/agregar")
    public ResponseEntity<String> agregarLugarDeTrabajo(@RequestBody LugarTrabajoDTO dto) {

        // Validaciones
        if (dto.getIdLugar() <= 0) {
            throw new InvalidFieldException("El id_lugar debe ser mayor a 0.");
        }

        if (dto.getNombreLugar() == null || dto.getNombreLugar().isBlank()) {
            throw new InvalidFieldException("El nombre del lugar es obligatorio.");
        }

        if (dto.getDireccion() == null || dto.getDireccion().isBlank()) {
            throw new InvalidFieldException("La dirección es obligatoria.");
        }

        // Validar que no exista el ID
        if (lugarTrabajoService.buscarLugar(dto.getIdLugar()) != null) {
            throw new DuplicateResourceException("Ya existe un lugar con el ID: " + dto.getIdLugar());
        }

        String mensaje = lugarTrabajoService.crearLugar(dto);
        return ResponseEntity.ok(mensaje);
    }


    // =========================================================
    // ACTUALIZAR
    // =========================================================
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

        // No permitir actualizar el ID desde el body
        if (dto.getIdLugar() != 0 && dto.getIdLugar() != id) {
            throw new InvalidFieldException("No se puede modificar el ID del lugar de trabajo.");
        }

        String mensaje = lugarTrabajoService.editarLugar(id, dto);
        return ResponseEntity.ok(mensaje);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<LugarTrabajoDTO>> buscarLugar(@RequestParam String query){
        return ResponseEntity.ok(lugarTrabajoService.buscarLugarTexto(query));
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<LugarTrabajoDTO> buscarLugarId(@PathVariable int id){
        LugarTrabajoDTO lugar = lugarTrabajoService.buscarLugar(id);
        if (lugar == null) {
            throw new ResourceNotFoundException("No existe lugar con ID: " + id);
        }
        return ResponseEntity.ok(lugar);
    }
}