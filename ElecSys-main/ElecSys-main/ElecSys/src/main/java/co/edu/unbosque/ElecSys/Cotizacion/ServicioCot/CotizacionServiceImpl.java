package co.edu.unbosque.ElecSys.Cotizacion.ServicioCot;

import co.edu.unbosque.ElecSys.Cotizacion.DTOCot.CotizacionDTO;
import co.edu.unbosque.ElecSys.Cotizacion.EntidadCot.CotizacionEntidad;
import co.edu.unbosque.ElecSys.Cotizacion.Analizador.PrediccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CotizacionServiceImpl implements CotizacionInterface{

    @Autowired
    private CotizacionRepository cotizacionRepository;

    @Autowired
    private PrediccionService prediccionService;

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

    @Override
    public String borrarCotizacion(int id) {
        try {
            cotizacionRepository.deleteById(id);
            return "Cotizacion Eliminada";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

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

            cotizacionRepository.save(entidad);
            return "Cotizacion Actualizada Correctamente";
        }
    }


    @Override
    public int cantidadCotizacionesPorCliente(int idCliente) {
        return cotizacionRepository.llamarContarCotizaciones(idCliente);
    }

    public Boolean existirCot(int id){
        return cotizacionRepository.existsById(id);
    }

}
