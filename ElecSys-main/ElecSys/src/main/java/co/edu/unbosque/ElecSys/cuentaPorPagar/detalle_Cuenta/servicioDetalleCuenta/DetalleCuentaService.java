package co.edu.unbosque.ElecSys.cuentaPorPagar.detalle_Cuenta.servicioDetalleCuenta;

import co.edu.unbosque.ElecSys.cuentaPorPagar.detalle_Cuenta.detalleDTO.Detalle_CuentaDTO;
import co.edu.unbosque.ElecSys.cuentaPorPagar.detalle_Cuenta.entidadDetalleCuenta.DetalleCuentaEntidad;
import co.edu.unbosque.ElecSys.historialActividad.helperHis.AuditoriaHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación de la interfaz de servicios para la gestión de detalles de cuentas por pagar.
 */
@Service
public class DetalleCuentaService implements DetalleCuentaInterface{

    @Autowired
    private DetalleCuentaRepository DetalleCuentaRepository;

    @Autowired
    private AuditoriaHelper auditoria;


    /**
     * Registra un nuevo detalle vinculado a una cuenta de cobro.
     * @param detalle DTO con la información del detalle.
     * @return El DTO guardado con su ID generado.
     */
    @Override
    public Detalle_CuentaDTO agregarDetalleCuenta(Detalle_CuentaDTO detalle) {
        DetalleCuentaEntidad detallecuentaNueva = new DetalleCuentaEntidad(
                null,
                detalle.getId_cuenta_pagar(),
                detalle.getDescripcion(),
                detalle.getValor()
        );

        try {
            DetalleCuentaEntidad detallecuentaguardado = DetalleCuentaRepository.save(detallecuentaNueva);
            detalle.setId_detalle_cuenta(detallecuentaguardado.getId_detalle_cuenta());
            auditoria.registrarAccion("DETALLE_CUENTA_PAGAR", "Creación de Detalle Cuenta",
                    "ID_DETALLE_CUENTA", "N/A", String.valueOf(detallecuentaguardado.getId_detalle_cuenta()));
            return detalle;
        } catch (Exception e){
            return null;
        }
    }

    /**
     * Elimina un detalle del repositorio.
     * @param id ID del detalle.
     * @return Mensaje de éxito o error.
     */
    @Override
    public String borrarDetalleCuenta(int id) {
        try {
            DetalleCuentaRepository.deleteById(id);
            auditoria.registrarAccion("DETALLE_CUENTA_PAGAR", "Eliminación de Detalle Cuenta",
                    "ID_DETALLE_CUENTA", String.valueOf(id), "N/A");
            return "Detalle de Cuenta Eliminado Exitosamente";
        } catch (Exception e){
            return e.getMessage();
        }
    }

    /**
     * Recupera todos los detalles de cuentas existentes.
     * @return Lista completa de detalles.
     */
    @Override
    public List<Detalle_CuentaDTO> listarDetallesCuentas() {
        List<DetalleCuentaEntidad> detalleCuenta = DetalleCuentaRepository.findAll();
        List<Detalle_CuentaDTO> detallesCuentasDTOS = new ArrayList<>();

        for (DetalleCuentaEntidad detalle : detalleCuenta){
            detallesCuentasDTOS.add(new Detalle_CuentaDTO(
                    detalle.getId_detalle_cuenta(),
                    detalle.getId_cuenta_pagar(),
                    detalle.getDescripcion(),
                    detalle.getValor()
            ));
        }

        return detallesCuentasDTOS;
    }

    /**
     * Actualiza los campos de descripción y valor de un detalle.
     * @param id ID del detalle.
     * @param detalle Nuevos datos.
     * @return Mensaje de confirmación.
     */
    @Override
    public String actualizarDetalleCuenta(int id, Detalle_CuentaDTO detalle) {
        Optional<DetalleCuentaEntidad> detalleCuentaExit = DetalleCuentaRepository.findById(id);
        if (detalleCuentaExit.isEmpty()){
            return "Detalle Cuenta No encontrada para actualizar";
        } else{
            DetalleCuentaEntidad entidad = detalleCuentaExit.get();

            entidad.setDescripcion(detalle.getDescripcion());
            entidad.setValor(detalle.getValor());

            DetalleCuentaRepository.save(entidad);
            auditoria.registrarAccion("DETALLE_CUENTA_PAGAR", "Actualización de Detalle Cuenta",
                    "ID_DETALLE_CUENTA", "Existente", String.valueOf(id));
            return "Detalle Cuenta Actualizada Correctamente";
        }
    }

    /**
     * Busca un detalle específico por su ID.
     * @param id Identificador único.
     * @return DTO del detalle o null si no existe.
     */
    @Override
    public Detalle_CuentaDTO buscarDetallesCuentas(int id) {
        Optional<DetalleCuentaEntidad> detalleExit = DetalleCuentaRepository.findById(id);

        if (detalleExit.isEmpty()){
            return null;
        }

        DetalleCuentaEntidad entidad = detalleExit.get();

        return new Detalle_CuentaDTO(
                entidad.getId_detalle_cuenta(),
                entidad.getId_cuenta_pagar(),
                entidad.getDescripcion(),
                entidad.getValor());
    }
}
