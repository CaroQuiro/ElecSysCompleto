package co.edu.unbosque.ElecSys.lugarTrabajo.servicioLug;
import co.edu.unbosque.ElecSys.historialActividad.helperHis.AuditoriaHelper;
import co.edu.unbosque.ElecSys.lugarTrabajo.dtoLug.LugarTrabajoDTO;
import co.edu.unbosque.ElecSys.lugarTrabajo.entidadLug.LugarTrabajoEntidad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio encargado de la lógica de negocio para la gestión de lugares de trabajo.
 * Implementa las operaciones de persistencia y consulta interactuando con el repositorio.
 */
@Service
public class LugarTrabajoService implements LugarTrabajoInterface{

    @Autowired
    LugarTrabajoRepository lugarTrabajoRepository;

    @Autowired
    private AuditoriaHelper auditoria;


    /**
     * Crea un nuevo registro de lugar de trabajo en la base de datos.
     * @param lugar DTO que contiene la información del nuevo lugar.
     * @return Mensaje confirmando la creación exitosa o indicando un error.
     */
    @Override
    public String crearLugar(LugarTrabajoDTO lugar) {
        LugarTrabajoEntidad lugarTrabajo = new LugarTrabajoEntidad(
                lugar.getIdLugar(),
                lugar.getNombreLugar(),
                lugar.getDireccion()
        );
        try {
            LugarTrabajoEntidad guardado = lugarTrabajoRepository.save(lugarTrabajo);
            auditoria.registrarAccion("LUGAR_TRABAJO", "Creación de Lugar",
                    "ID_LUGAR", "N/A", String.valueOf(guardado.getId_lugar()));
            return "Se creo el lugar correctamente";
        } catch (Exception e) {
            return "Hubo un error al crear el lugar";
        }

    }

    /**
     * Modifica los datos de un lugar de trabajo ya existente.
     * @param idAnterior Identificador del lugar que se desea editar.
     * @param lugar Objeto con los nuevos datos (Nombre y Dirección).
     * @return Mensaje de estado sobre la operación de edición.
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

                auditoria.registrarAccion("LUGAR_TRABAJO", "Edición de Lugar",
                        "ID_LUGAR", String.valueOf(idAnterior), "Modificado");

                return "El lugar se ha editado correctamente";
            } else {
                return "La id del lugar no se ha encontrado";
            }

        } catch (Exception e) {
            return "Hubo un error al editar el lugar";
        }
    }


    /**
     * Obtiene todos los lugares de trabajo almacenados en el repositorio.
     * @return Lista de LugarTrabajoDTO o una lista vacía en caso de error.
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
     * Busca un lugar de trabajo específico por su ID primario.
     * @param idLugar Identificador único del lugar.
     * @return El objeto LugarTrabajoDTO encontrado o null si no existe.
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

    /**
     * Busca lugares de trabajo mediante un patrón de texto en el nombre o descripción.
     * @param query Cadena de texto para la búsqueda.
     * @return Lista de DTOs que coinciden con el texto o null si no hay resultados.
     */
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
