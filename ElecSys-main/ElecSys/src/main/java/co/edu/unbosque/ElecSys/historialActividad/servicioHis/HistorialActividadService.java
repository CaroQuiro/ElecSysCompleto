package co.edu.unbosque.ElecSys.historialActividad.servicioHis;

import co.edu.unbosque.ElecSys.historialActividad.dtoHis.HistorialActividadDTO;
import co.edu.unbosque.ElecSys.historialActividad.entidadHis.HistorialActividadEntidad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Servicio de negocio encargado de gestionar el flujo principal de auditoría.
 * Centraliza las operaciones de registro de eventos globales del sistema ElecSys.
 */
@Service
public class HistorialActividadService implements HistorialActividadInterface{

    @Autowired
    private HistorialActividadRepository historialActividadRepository;


    /**
     * Registra un evento de actividad global en el repositorio de historial.
     * @param dto Información del evento (trabajador, módulo, acción, fecha y hora).
     * @return Mensaje confirmando la creación exitosa del registro.
     */
    @Override
    public String agregarHistorialActividad(HistorialActividadDTO dto) {
        HistorialActividadEntidad entidad = new HistorialActividadEntidad();
        entidad.setIdTrabajador(dto.getIdTrabajador());
        entidad.setModuloSistema(dto.getModuloSistema());
        entidad.setAccionRealizada(dto.getAccionRealizada());
        entidad.setFechaRealizacion(dto.getFechaRealizacion());
        entidad.setHora(dto.getHora());
        try {
            HistorialActividadEntidad guardado = historialActividadRepository.save(entidad);
            return String.valueOf(guardado.getIdHistorial());
        } catch (Exception e) {
            return "Hubo un error al crear el historial de actividad";
        }
    }

    /**
     * Recupera el historial completo de actividades registradas.
     * @return Lista de todos los registros de historial existentes.
     */
    @Override
    public List<HistorialActividadDTO> listarHistorialActividad() {
        try {
            List<HistorialActividadEntidad> historialActEntidades = historialActividadRepository.findAll();
            List<HistorialActividadDTO> historialActDtos = new ArrayList<>();

            for(HistorialActividadEntidad historialActividad : historialActEntidades){
                historialActDtos.add(new HistorialActividadDTO(
                        historialActividad.getIdHistorial(),
                        historialActividad.getIdTrabajador(),
                        historialActividad.getModuloSistema(),
                        historialActividad.getAccionRealizada(),
                        historialActividad.getFechaRealizacion(),
                        historialActividad.getHora()
                ));
            };
            return historialActDtos;
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Busca la cabecera de un historial de actividad por su identificador único.
     * @param idHistorial ID del historial de búsqueda.
     * @return DTO del historial encontrado o null si no existe.
     */
    @Override
    public HistorialActividadDTO buscarHistorialActividad(int idHistorial) {
        try {
            HistorialActividadEntidad entidadOpt = historialActividadRepository.findById(idHistorial).orElse(null);

            if(entidadOpt == null){
                return null;
            }else{
                return new HistorialActividadDTO(entidadOpt.getIdHistorial(),
                        entidadOpt.getIdTrabajador(),
                        entidadOpt.getModuloSistema(),
                        entidadOpt.getAccionRealizada(),
                        entidadOpt.getFechaRealizacion(),
                        entidadOpt.getHora());
            }
        } catch (Exception e) {
            return new HistorialActividadDTO();
        }
    }
}
