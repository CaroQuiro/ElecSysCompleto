package co.edu.unbosque.ElecSys.Usuario.Trabajador.ServicioTra;


import co.edu.unbosque.ElecSys.Usuario.Cliente.DTOClie.ClienteDTO;
import co.edu.unbosque.ElecSys.Usuario.Cliente.EntidadClie.ClienteEntidad;
import co.edu.unbosque.ElecSys.Usuario.Trabajador.DTOTra.TrabajadorDTO;
import co.edu.unbosque.ElecSys.Usuario.Trabajador.EntidadTra.TrabajadorEntidad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TrabajadorServiceImpl implements TrabajadorInterface{

    @Autowired
    private TrabajadorRepository trabajadorRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

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
                passwordHash, // 🔐 HASH
                trabajadorDTO.getEstado()
        );

        try {
            trabajadorRepository.save(nuevoTrabajador);
            return "Trabajador creado exitosamente";
        } catch (Exception e) {
            return "Error al crear trabajador";
        }
    }

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

        // ⚠️ SOLO hashear si viene una nueva contraseña
        if (trabajadorDTO.getPassword() != null &&
                !trabajadorDTO.getPassword().isBlank()) {

            entidad.setPassword(
                    encoder.encode(trabajadorDTO.getPassword())
            );
        }

        trabajadorRepository.save(entidad);
        return "Trabajador actualizado exitosamente";
    }

}
