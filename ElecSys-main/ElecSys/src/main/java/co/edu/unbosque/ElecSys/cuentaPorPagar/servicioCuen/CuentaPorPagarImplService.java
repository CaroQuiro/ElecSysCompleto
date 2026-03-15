package co.edu.unbosque.ElecSys.cuentaPorPagar.servicioCuen;

import co.edu.unbosque.ElecSys.cuentaPorPagar.dtoCuen.CuentaPorPagarDTO;
import co.edu.unbosque.ElecSys.cuentaPorPagar.entidadCuen.CuentaPorPagarEntidad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de negocio para las Cuentas por Pagar generales.
 */
@Service
public class CuentaPorPagarImplService implements CuentaPorPagarInterface{

    @Autowired
    private CuentasPorPagarRepository cuentasPorPagarRepository;

    /**
     * Crea un registro de cuenta de cobro en la base de datos.
     * @param cuenta DTO con los datos iniciales.
     * @return El objeto guardado con ID asignado.
     */
    @Override
    public CuentaPorPagarDTO agregarCuentaPagar(CuentaPorPagarDTO cuenta) {
        CuentaPorPagarEntidad nuevaCuenta = new CuentaPorPagarEntidad(
          null,
          cuenta.getId_trabajador(),
          cuenta.getId_cliente(),
          cuenta.getNota(),
          cuenta.getFecha_realizacion(),
          cuenta.getMonto(),
          cuenta.getEstado()
        );
        try{
            CuentaPorPagarEntidad cuentaGuardada = cuentasPorPagarRepository.save(nuevaCuenta);
            cuenta.setId_cuenta_pagar(cuentaGuardada.getId_cuenta_pagar());
            return cuenta;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Elimina una cuenta por pagar de la base de datos.
     * @param id ID de la cuenta.
     * @return Mensaje de éxito o error.
     */
    @Override
    public String borrarCuentaPagar(int id) {
        try {
            cuentasPorPagarRepository.deleteById(id);
            return "Cuenta Eliminada Exitosamente";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * Obtiene el listado de todas las cuentas registradas.
     * @return Lista de DTOs de cuentas.
     */
    @Override
    public List<CuentaPorPagarDTO> listarCuentasPagar() {
        List<CuentaPorPagarEntidad> cuenta = cuentasPorPagarRepository.findAll();
        List<CuentaPorPagarDTO> cuentaPorPagarDTOS = new ArrayList<>();

        for (CuentaPorPagarEntidad cuentas : cuenta) {
            cuentaPorPagarDTOS.add(new CuentaPorPagarDTO(
                    cuentas.getId_cuenta_pagar(),
                    cuentas.getId_trabajador(),
                    cuentas.getId_cliente(),
                    cuentas.getNota(),
                    cuentas.getFecha_realizacion(),
                    cuentas.getMonto(),
                    cuentas.getEstado()
            ));
        }

        return cuentaPorPagarDTOS;
    }

    /**
     * Modifica los datos generales de una cuenta de cobro.
     * @param id ID de la cuenta a actualizar.
     * @param cuentaPorPagarDTO Nuevos datos.
     * @return Mensaje de éxito o error.
     */
    @Override
    public String actualizarCuenta(int id, CuentaPorPagarDTO cuentaPorPagarDTO) {

        Optional<CuentaPorPagarEntidad> cuentaExit = cuentasPorPagarRepository.findById(id);
        if (cuentaExit.isEmpty()){
            return "Cuenta No encontrada para actualizar";
        }else {
            CuentaPorPagarEntidad entidad = cuentaExit.get();

            entidad.setId_cliente(cuentaPorPagarDTO.getId_cliente());
            entidad.setId_trabajador(cuentaPorPagarDTO.getId_trabajador());
            entidad.setNota(cuentaPorPagarDTO.getNota());
            entidad.setFecha_realizacion(cuentaPorPagarDTO.getFecha_realizacion());
            entidad.setMonto(cuentaPorPagarDTO.getMonto());
            entidad.setEstado(cuentaPorPagarDTO.getEstado());

            cuentasPorPagarRepository.save(entidad);
            return "Cotizacion Actualizada Correctamente";
        }
    }

    /**
     * Verifica la existencia de una cuenta en el repositorio.
     * @param id ID de la cuenta.
     * @return true si existe, false de lo contrario.
     */
    @Override
    public Boolean existeCuenta(int id) {
        Boolean existe = cuentasPorPagarRepository.existsById(id);
        return existe;
    }

    /**
     * Recupera una cuenta de cobro por su identificador.
     * @param id ID de la cuenta.
     * @return DTO de la cuenta encontrada o null.
     */
    @Override
    public CuentaPorPagarDTO buscarCuenta(int id) {
        Optional<CuentaPorPagarEntidad> cuentaOpt = cuentasPorPagarRepository.findById(id);

        if (cuentaOpt.isEmpty()) {
            return null;
        }

        CuentaPorPagarEntidad c = cuentaOpt.get();

        return new CuentaPorPagarDTO(
                c.getId_cuenta_pagar(),
                c.getId_trabajador(),
                c.getId_cliente(),
                c.getNota(),
                c.getFecha_realizacion(),
                c.getMonto(),
                c.getEstado()
        );
    }

}
