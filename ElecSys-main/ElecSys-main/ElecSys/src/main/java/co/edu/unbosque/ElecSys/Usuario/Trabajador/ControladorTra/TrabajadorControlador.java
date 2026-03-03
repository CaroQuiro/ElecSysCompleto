package co.edu.unbosque.ElecSys.Usuario.Trabajador.ControladorTra;

import co.edu.unbosque.ElecSys.Config.Excepcion.*;
import co.edu.unbosque.ElecSys.Usuario.Trabajador.DTOTra.TrabajadorDTO;
import co.edu.unbosque.ElecSys.Usuario.Trabajador.ServicioTra.TrabajadorServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/trabajador")
public class TrabajadorControlador {

    @Autowired
    private TrabajadorServiceImpl trabajadorService;

    private static final int MAX_TELEFONO = 12;

    // =============================
    // LISTAR TRABAJADORES
    // =============================
    @GetMapping("/listar")
    public ResponseEntity<List<TrabajadorDTO>> listarTrabajadores() {
        List<TrabajadorDTO> lista = trabajadorService.listarTrabajadores();

        if (lista.isEmpty()) {
            throw new ResourceNotFoundException("No hay trabajadores registrados.");
        }

        return ResponseEntity.ok(lista);
    }

    // =============================
    // BUSCAR TRABAJADOR POR ID
    // =============================
    @GetMapping("/buscar/{id}")
    public ResponseEntity<TrabajadorDTO> buscarTrabajador(@PathVariable int id) {

        TrabajadorDTO trabajador = trabajadorService.buscarTrabajador(id);

        if (trabajador == null) {
            throw new ResourceNotFoundException("No existe trabajador con ID: " + id);
        }

        return ResponseEntity.ok(trabajador);
    }

    // =============================
    // AGREGAR TRABAJADOR
    // =============================
    @PostMapping("/agregar")
    public ResponseEntity<String> agregarTrabajador(@RequestBody TrabajadorDTO dto) {

        if (trabajadorService.buscarTrabajador(dto.getId_trabajador()) != null) {
            throw new DuplicateResourceException("Ya existe un trabajador con ID: " + dto.getId_trabajador());
        }

        validarTrabajador(dto);

        String msg = trabajadorService.agregarTrabajador(dto);

        return ResponseEntity.ok(msg);
    }

    // =============================
    // BORRAR TRABAJADOR
    // =============================
    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<String> borrarTrabajador(@PathVariable int id) {

        if (trabajadorService.buscarTrabajador(id) == null) {
            throw new ResourceNotFoundException("No existe trabajador con ID: " + id);
        }

        String msg = trabajadorService.deshabilitarTrabajador(id);

        return ResponseEntity.ok(msg);
    }

    // =============================
    // ACTUALIZAR TRABAJADOR
    // =============================
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

        validarTrabajador(dto);

        String msg = trabajadorService.actualizarTrabajador(id, dto);

        return ResponseEntity.ok(msg);
    }


    private static final Set<String> TIPOS_VALIDOS =
            Set.of("ADMINISTRADOR", "USUARIO");


    // =============================
    // VALIDACIONES
    // =============================
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
}

