package co.edu.unbosque.ElecSys.LugarTrabajo.ServicioLug;
import co.edu.unbosque.ElecSys.LugarTrabajo.DTOLug.LugarTrabajoDTO;
import co.edu.unbosque.ElecSys.LugarTrabajo.EntidadLug.LugarTrabajoEntidad;
import co.edu.unbosque.ElecSys.Usuario.Cliente.DTOClie.ClienteDTO;
import co.edu.unbosque.ElecSys.Usuario.Cliente.EntidadClie.ClienteEntidad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LugarTrabajoService implements LugarTrabajoInterface{

    @Autowired
    LugarTrabajoRepository lugarTrabajoRepository;


    /**
     * @param lugar
     * @return
     * private int idLugar;
     *     private String nombreLugar;
     *     private String direccion;
     */
    @Override
    public String crearLugar(LugarTrabajoDTO lugar) {
        LugarTrabajoEntidad lugarTrabajo = new LugarTrabajoEntidad(
                lugar.getIdLugar(),
                lugar.getNombreLugar(),
                lugar.getDireccion()
        );
        try {
            lugarTrabajoRepository.save(lugarTrabajo);
            return "Se creo el lugar correctamente";
        } catch (Exception e) {
            return "Hubo un error al crear el lugar";
        }

    }

    /**
     * @param idAnterior
     * @param lugar
     * @return
     */
    @Override
    public String editarLugar(int idAnterior, LugarTrabajoDTO lugar) {
        try {
            Optional<LugarTrabajoEntidad> optionalLugar = lugarTrabajoRepository.findById(idAnterior);

            if (optionalLugar.isPresent()) {
                LugarTrabajoEntidad lugarExistente = optionalLugar.get();

                lugarExistente.setNombre_lugar(lugar.getNombreLugar());
                lugarExistente.setDireccion(lugar.getDireccion());

                lugarTrabajoRepository.save(lugarExistente);

                return "El lugar se ha editado correctamente";
            } else {
                return "La id del lugar no se ha encontrado";
            }

        } catch (Exception e) {
            return "Hubo un error al editar el lugar";
        }
    }


    /**
     * @return
     */
    @Override
    public List<LugarTrabajoDTO> listarLugar() {
        try {
            List<LugarTrabajoEntidad> lugarEntidades = lugarTrabajoRepository.findAll();
            List<LugarTrabajoDTO> lugarDtos = new ArrayList<>();

            for(LugarTrabajoEntidad lugar : lugarEntidades){
                lugarDtos.add(new LugarTrabajoDTO(
                        lugar.getId_lugar(),
                        lugar.getNombre_lugar(),
                        lugar.getDireccion()));
            };
            return lugarDtos;
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * @return
     */
    @Override
    public LugarTrabajoDTO buscarLugar(int idLugar) {
        try {
            LugarTrabajoEntidad lugar = lugarTrabajoRepository.findById(idLugar).orElse(null);
            if(lugar == null){
                return null;
            }else{
                return new LugarTrabajoDTO(
                        lugar.getId_lugar(),
                        lugar.getNombre_lugar(),
                        lugar.getDireccion()
                );
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<LugarTrabajoDTO> buscarLugarTexto(String query) {
        List<LugarTrabajoEntidad> lugar = lugarTrabajoRepository.buscarLugarTexto(query);
        if (lugar != null){
            return lugar.stream().map( l -> new LugarTrabajoDTO(
                    l.getId_lugar(),
                    l.getNombre_lugar(),
                    l.getDireccion()
            )).toList();
        }
        return null;
    }
}
