package co.edu.unbosque.ElecSys.usuario.trabajador.servicioTra;


import co.edu.unbosque.ElecSys.historialActividad.helperHis.AuditoriaHelper;
import co.edu.unbosque.ElecSys.usuario.trabajador.dtoTra.TrabajadorDTO;
import co.edu.unbosque.ElecSys.usuario.trabajador.entidadTra.TrabajadorEntidad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Servicio encargado de la lógica operativa de los trabajadores.
 * Implementa el hashing de contraseñas mediante {@link BCryptPasswordEncoder}.
 */
@Service
public class TrabajadorServiceImpl implements TrabajadorInterface{

    @Autowired
    private TrabajadorRepository trabajadorRepository;

    @Autowired
    private AuditoriaHelper auditoria;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * Registra un trabajador cifrando su contraseña antes de la persistencia.
     * @param trabajadorDTO Datos del nuevo trabajador.
     * @return Mensaje de estado de la creación.
     */
    @Override
    public String agregarTrabajador(TrabajadorDTO trabajadorDTO) {

        String passwordHash = encoder.encode(trabajadorDTO.getPassword());

        TrabajadorEntidad nuevoTrabajador = new TrabajadorEntidad(
                trabajadorDTO.getId_trabajador(),
                trabajadorDTO.getNombre(),
                trabajadorDTO.getTelefono(),
                trabajadorDTO.getDireccion(),
                trabajadorDTO.getCorreo(),
                trabajadorDTO.getTipo_usuario(),
                trabajadorDTO.getPassword(),
                trabajadorDTO.getEstado()
        );

        try {
            trabajadorRepository.save(nuevoTrabajador);
            auditoria.registrarAccion("TRABAJADORES", "Creación de Trabajador",
                    "ID_TRABAJADOR", "N/A", String.valueOf(trabajadorDTO.getId_trabajador()));
            return "Trabajador creado exitosamente";
        } catch (Exception e) {
            return "Error al crear trabajador";
        }
    }

    /**
     * Busca un trabajador en la base de datos por su identificador.
     * @param id ID del trabajador.
     * @return DTO del trabajador o null si no se halla.
     */
    @Override
    public TrabajadorDTO buscarTrabajador(int id) {
        TrabajadorEntidad trabajador = trabajadorRepository.findById(id).orElse(null);
        if (trabajador != null){
            return new TrabajadorDTO(
                    trabajador.getId_trabajador(),
                    trabajador.getNombre(),
                    trabajador.getTelefono(),
                    trabajador.getDireccion(),
                    trabajador.getCorreo(),
                    trabajador.getTipo_usuario(),
                    trabajador.getPassword(),
                    trabajador.getEstado()
                    );
        };

        return null;
    }

    /**
     * Modifica el estado del trabajador a "Deshabilitado".
     * @param id ID del trabajador.
     * @return Mensaje de confirmación del cambio.
     */
    @Override
    public String deshabilitarTrabajador(int id) {
        Optional<TrabajadorEntidad> trabajadorExit = trabajadorRepository.findById(id);
        if (trabajadorExit.isEmpty()){
            return "Trabajador no encontrado para deshabilitar";
        } else {
            TrabajadorEntidad entidad = trabajadorExit.get();

            entidad.setEstado("Deshabilitado");

            trabajadorRepository.save(entidad);
            return "Trabajador fue deshabilitado Exitosamente";
        }
    }

    /**
     * Obtiene el listado de todos los trabajadores del sistema.
     * @return Lista de objetos {@link TrabajadorDTO}.
     */
    @Override
    public List<TrabajadorDTO> listarTrabajadores() {
            List<TrabajadorEntidad> trabajador = trabajadorRepository.findAll();
            List<TrabajadorDTO> trabajadorDTOS = new ArrayList<>();

            for (TrabajadorEntidad trabajadores : trabajador){
                trabajadorDTOS.add(new TrabajadorDTO(
                   trabajadores.getId_trabajador(),
                   trabajadores.getNombre(),
                   trabajadores.getTelefono(),
                   trabajadores.getDireccion(),
                   trabajadores.getCorreo(),
                   trabajadores.getTipo_usuario(),
                   trabajadores.getPassword(),
                        trabajadores.getEstado()
                ));
            }

            return trabajadorDTOS;
    }

    /**
     * Actualiza el perfil del trabajador. Si se recibe una contraseña nueva, esta se actualiza.
     * @param id ID del trabajador.
     * @param trabajadorDTO Datos actualizados.
     * @return Mensaje indicando éxito en la actualización.
     */
    @Override
    public String actualizarTrabajador(int id, TrabajadorDTO trabajadorDTO) {

        Optional<TrabajadorEntidad> trabajadorExit = trabajadorRepository.findById(id);

        if (trabajadorExit.isEmpty()) {
            return "Trabajador no encontrado para actualizar";
        }

        TrabajadorEntidad entidad = trabajadorExit.get();

        entidad.setNombre(trabajadorDTO.getNombre());
        entidad.setTelefono(trabajadorDTO.getTelefono());
        entidad.setDireccion(trabajadorDTO.getDireccion());
        entidad.setCorreo(trabajadorDTO.getCorreo());
        entidad.setEstado(trabajadorDTO.getEstado());

        if (trabajadorDTO.getPassword() != null &&
                !trabajadorDTO.getPassword().isBlank()) {

            entidad.setPassword(
                    trabajadorDTO.getPassword()
            );
        }

        trabajadorRepository.save(entidad);
        auditoria.registrarAccion("TRABAJADORES", "Actualización de Perfil",
                "ID_TRABAJADOR", "Existente", String.valueOf(id));
        return "Trabajador actualizado exitosamente";
    }

}
