package co.edu.unbosque.ElecSys.cotizacion.servicioCot;

import co.edu.unbosque.ElecSys.cotizacion.analizador.PrediccionService;
import co.edu.unbosque.ElecSys.cotizacion.dtoCot.CotizacionDTO;
import co.edu.unbosque.ElecSys.cotizacion.entidadCot.CotizacionEntidad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación de los servicios de negocio para las cotizaciones generales.
 */
@Service
public class CotizacionServiceImpl implements CotizacionInterface{

    @Autowired
    private CotizacionRepository cotizacionRepository;

    @Autowired
    private PrediccionService prediccionService;

    /**
     * Persiste una cotización en la base de datos.
     * @param cotizacion DTO con la información de la cotización.
     * @return El DTO guardado incluyendo el ID generado por la secuencia.
     */
    @Override
    public CotizacionDTO agregarCotizacion(CotizacionDTO cotizacion) {
        CotizacionEntidad nuevaCotizacion = new CotizacionEntidad(
                null,
                cotizacion.getId_trabajador(),
                cotizacion.getId_cliente(),
                cotizacion.getId_lugar(),
                cotizacion.getFecha_realizacion(),
                cotizacion.getReferencia(),
                cotizacion.getValor_total(),
                cotizacion.getEstado(),
                cotizacion.getAdministracion(),
                cotizacion.getImprevistos(),
                cotizacion.getUtilidad(),
                cotizacion.getIva(),
                cotizacion.getTotal_pagar()
                );
        try {
            CotizacionEntidad cotGuardada = cotizacionRepository.save(nuevaCotizacion);
            cotizacion.setId_cotizacion(cotGuardada.getId_cotizacion());
            return cotizacion;

        }catch (Exception e){
            return null;
        }
    }

    /**
     * Busca una cotización por su identificador único.
     * @param id ID de la cotización.
     * @return El DTO encontrado o null si no existe.
     */
    @Override
    public CotizacionDTO buscarCotizacion(int id) {
        CotizacionEntidad cotizacion = cotizacionRepository.findById(id).orElse(null);
        if (cotizacion == null){
            return null;
        }else{
            return new CotizacionDTO(
                    cotizacion.getId_cotizacion(),
                    cotizacion.getId_trabajador(),
                    cotizacion.getId_cliente(),
                    cotizacion.getId_lugar(),
                    cotizacion.getFecha_realizacion(),
                    cotizacion.getReferencia(),
                    cotizacion.getValor_total(),
                    cotizacion.getEstado(),
                    cotizacion.getAdministracion(),
                    cotizacion.getImprevistos(),
                    cotizacion.getUtilidad(),cotizacion.getIva(),
                    cotizacion.getTotal_pagar());
        }
    }

    /**
     * Elimina una cotización de la base de datos mediante su ID.
     * @param id El identificador único de la cotización a borrar.
     * @return Un mensaje confirmando la eliminación ("Cotizacion Eliminada") o el mensaje de la excepción si falla.
     */
    @Override
    public String borrarCotizacion(int id) {
        try {
            cotizacionRepository.deleteById(id);
            return "Cotizacion Eliminada";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * Recupera todas las cotizaciones almacenadas en el sistema.
     * @return Una lista (List) de objetos CotizacionDTO con todos los registros de la base de datos.
     */
    @Override
    public List<CotizacionDTO> listarCotizacion() {
        List<CotizacionEntidad> cotizacion = cotizacionRepository.findAll();
        List<CotizacionDTO> cotizacionDTOS = new ArrayList<>();

        for (CotizacionEntidad cotizaciones : cotizacion){
            cotizacionDTOS.add(new CotizacionDTO(
                    cotizaciones.getId_cotizacion(),
                    cotizaciones.getId_trabajador(),
                    cotizaciones.getId_cliente(),
                    cotizaciones.getId_lugar(),
                    cotizaciones.getFecha_realizacion(),
                    cotizaciones.getReferencia(),
                    cotizaciones.getValor_total(),
                    cotizaciones.getEstado(),
                    cotizaciones.getAdministracion(),
                    cotizaciones.getImprevistos(),
                    cotizaciones.getUtilidad(),
                    cotizaciones.getIva(),
                    cotizaciones.getTotal_pagar()
                    ));
        }
        return cotizacionDTOS;
    }

    /**
     * Actualiza los datos de una cotización existente identificada por su ID.
     * @param id El ID de la cotización que se desea modificar.
     * @param cotizacion Objeto DTO con los nuevos datos a guardar.
     * @return Mensaje de éxito ("Cotizacion Actualizada Correctamente") o de error si no se encuentra el registro.
     */
    @Override
    public String actualizarCot(int id, CotizacionDTO cotizacion) {

        Optional<CotizacionEntidad> cotizacionExis = cotizacionRepository.findById(id);
        if (cotizacionExis.isEmpty()){
            return "Cotizacion imposible de actualizar";
        }else {
            CotizacionEntidad entidad = cotizacionExis.get();
            entidad.setId_trabajador(cotizacion.getId_trabajador());
            entidad.setId_cliente(cotizacion.getId_cliente());
            entidad.setId_lugar(cotizacion.getId_lugar());
            entidad.setFecha_realizacion(cotizacion.getFecha_realizacion());
            entidad.setReferencia(cotizacion.getReferencia());
            entidad.setValor_total(cotizacion.getValor_total());
            entidad.setEstado(cotizacion.getEstado());
            entidad.setAdministracion(cotizacion.getAdministracion());
            entidad.setImprevistos(cotizacion.getImprevistos());
            entidad.setUtilidad(cotizacion.getUtilidad());
            entidad.setIva(cotizacion.getIva());
            entidad.setTotal_pagar(cotizacion.getTotal_pagar());

            cotizacionRepository.save(entidad);
            return "Cotizacion Actualizada Correctamente";
        }
    }

    /**
     * Cuenta cuántas cotizaciones ha realizado un cliente específico en la historia de la empresa.
     * @param idCliente ID del cliente.
     * @return Cantidad total de registros encontrados.
     */
    @Override
    public int cantidadCotizacionesPorCliente(int idCliente) {
        return cotizacionRepository.llamarContarCotizaciones(idCliente);
    }

    /**
     * Verifica si una cotización existe en la base de datos sin recuperar el objeto completo.
     * @param id El identificador único de la cotización.
     * @return true si la cotización existe, false en caso contrario.
     */
    public Boolean existirCot(int id){
        return cotizacionRepository.existsById(id);
    }
}
