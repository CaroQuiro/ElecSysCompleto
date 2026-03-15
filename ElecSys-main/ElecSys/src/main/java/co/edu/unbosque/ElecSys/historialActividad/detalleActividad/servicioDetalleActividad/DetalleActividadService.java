package co.edu.unbosque.ElecSys.historialActividad.detalleActividad.servicioDetalleActividad;

import co.edu.unbosque.ElecSys.historialActividad.detalleActividad.dtoDetalleActividad.DetalleActividadDTO;
import co.edu.unbosque.ElecSys.historialActividad.detalleActividad.entidadDetalleActividad.DetalleActividadEntidad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio encargado de la lógica de persistencia para los pormenores de las actividades.
 * Registra qué campos específicos cambiaron y cuáles eran sus valores previos.
 */
@Service
public class DetalleActividadService implements DetalleActividadInterface{

    @Autowired
    private DetalleActividadRepository detalleActividadRepository;

    /**
     * Almacena un nuevo detalle de actividad en la base de datos.
     * @param dto Objeto con la información del campo afectado y los valores de cambio.
     * @return Mensaje de éxito o descripción del error en caso de fallo.
     */
    @Override
    public String agregarDetalleActividad(DetalleActividadDTO dto) {
        DetalleActividadEntidad detalleActEntidad = new DetalleActividadEntidad();
                detalleActEntidad.setIdHistorial(dto.getIdHistorial());
                detalleActEntidad.setCampoAfectado(dto.getCampoAfectado());
                detalleActEntidad.setValorAnterior(dto.getValorAnterior());
                detalleActEntidad.setValorNuevo(dto.getValorNuevo());
        try {
            detalleActividadRepository.save(detalleActEntidad);
            return "Detalle de actividad guardado correctamente";
        } catch (Exception e) {
            return "Error al guardar el detalle de la actividad";
        }
    }

    /**
     * Obtiene todos los detalles técnicos vinculados a un historial de actividad específico.
     * @param idHistorial Identificador del historial padre.
     * @return Lista de {@link DetalleActividadDTO} asociados o lista vacía si ocurre un error.
     */
    @Override
    public List<DetalleActividadDTO> listarDetalleActividadPorIdHistorial(int idHistorial) {
        try {
            List<DetalleActividadEntidad> detalleActEntidades = detalleActividadRepository.findAllByIdHistorial(idHistorial);
            List<DetalleActividadDTO> detalleActDtos = new ArrayList<>();

            for(DetalleActividadEntidad detalleActividad : detalleActEntidades){
                detalleActDtos.add(new DetalleActividadDTO(
                        detalleActividad.getIdDetalleActividad(),
                        detalleActividad.getIdHistorial(),
                        detalleActividad.getCampoAfectado(),
                        detalleActividad.getValorAnterior(),
                        detalleActividad.getValorNuevo()
                ));
            };
            return detalleActDtos;
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Consulta la información de un detalle de actividad individual mediante su ID.
     * @param idDetalleActividad Identificador único del detalle.
     * @return Objeto {@link DetalleActividadDTO} o null si no se encuentra.
     */
    @Override
    public DetalleActividadDTO buscarDetalleActividad(int idDetalleActividad) {
        try {
            DetalleActividadEntidad entidadOpt = detalleActividadRepository.findById(idDetalleActividad).orElse(null);

            if(entidadOpt == null){
                return null;
            }else{
                return new DetalleActividadDTO(entidadOpt.getIdDetalleActividad(),
                        entidadOpt.getIdHistorial(),
                        entidadOpt.getCampoAfectado(),
                        entidadOpt.getValorAnterior(),
                        entidadOpt.getValorNuevo());
            }
        } catch (Exception e) {
            return new DetalleActividadDTO();
        }
    }
}
