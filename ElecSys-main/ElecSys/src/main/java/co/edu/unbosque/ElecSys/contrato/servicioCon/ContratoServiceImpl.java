package co.edu.unbosque.ElecSys.contrato.servicioCon;

import co.edu.unbosque.ElecSys.contrato.dtoCon.ContratoDTO;
import co.edu.unbosque.ElecSys.contrato.entidadCon.ContratoEntidad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación de la lógica de negocio para la persistencia de contratos.
 */
@Service
public class ContratoServiceImpl implements ContratoInterface{

    @Autowired
    private ContratoRepository contratoRepository;

    /**
     * Transforma el DTO en entidad y lo persiste en la base de datos PostgreSQL.
     * @param contrato DTO con los datos de entrada.
     * @return DTO actualizado con el ID generado.
     */
    @Override
    public ContratoDTO agregarContrato(ContratoDTO contrato) {
        ContratoEntidad nuevocontrato = new ContratoEntidad(
                null,
                contrato.getId_trabajador(),
                contrato.getSueldo(),
                contrato.getFecha_expedicion(),
                contrato.getFecha_iniciacion(),
                contrato.getId_trabajador_encargado(),
                contrato.getCargo(),
                contrato.getTipo_contrato(),
                contrato.getEstado()
        );
        try {
            ContratoEntidad contratoGuardado = contratoRepository.save(nuevocontrato);
            contrato.setId_contrato(contratoGuardado.getId_contrato());
            System.out.println("Contrato Guardado exitosamente");
            return contrato;

        }catch (Exception e){
            System.out.println("Error al crear el contrato");
            return null;
        }
    }

    /**
     * Obtiene todos los registros de la tabla contrato y los convierte a DTOs.
     * @return Lista de contratos para la vista administrativa.
     */
    @Override
    public List<ContratoDTO> listarcontratos() {
        List<ContratoEntidad> contrato = contratoRepository.findAll();
        List<ContratoDTO> contratoDTOS = new ArrayList<>();

        for (ContratoEntidad contratos : contrato){
            contratoDTOS.add(new ContratoDTO(
                    contratos.getId_contrato(),
                    contratos.getId_trabajador(),
                    contratos.getSueldo(),
                    contratos.getFecha_expedicion(),
                    contratos.getFecha_iniciacion(),
                    contratos.getId_trabajador_encargado(),
                    contratos.getCargo(),
                    contratos.getTipo_contrato(),
                    contratos.getEstado()
            ));
        }
        return contratoDTOS;
    }

    /**
     * Busca un contrato específico por su identificador único.
     * @param id Identificador del contrato.
     * @return DTO del contrato o null si no existe.
     */
    @Override
    public ContratoDTO buscarContrato(int id) {
        Optional<ContratoEntidad> contratoopt = contratoRepository.findById(id);

        if (contratoopt.isEmpty()){
            return null;
        }
        ContratoEntidad c = contratoopt.get();

        return new ContratoDTO(c.getId_contrato(),
                c.getId_trabajador(),
                c.getSueldo(),
                c.getFecha_expedicion(),
                c.getFecha_iniciacion(),
                c.getId_trabajador_encargado(),
                c.getCargo(),
                c.getTipo_contrato(),
                c.getEstado());
    }

}
