package co.edu.unbosque.ElecSys.Ordenes.OrdenDeVisita.ServicioOrdVis;

import co.edu.unbosque.ElecSys.Ordenes.OrdenDeVisita.DTOOrdVis.OrdenDeVisitaDTO;
import co.edu.unbosque.ElecSys.Ordenes.OrdenDeVisita.EntidadOrdVis.OrdenDeVisitaEntidad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdenDeVisitaService implements OrdenDeVisitaInterface{

    @Autowired
    private OrdenVisitaRepository ordenVisitaRepository;

    /* =====================================================
       AGREGAR ORDEN DE VISITA
       ===================================================== */
    @Override
    public int agregarOrdenVisita(OrdenDeVisitaDTO dto) {
        try {
            OrdenDeVisitaEntidad orden = new OrdenDeVisitaEntidad();
            orden.setIdLugar(dto.getIdLugar());
            orden.setIdCliente(dto.getIdCliente());
            orden.setIdTrabajador(dto.getIdTrabajador());
            orden.setFechaRealizacion(dto.getFechaRealizacion());
            orden.setDescripcion(dto.getDescripcion());
            orden.setEstado(dto.getEstado());

            // Al guardar, el repositorio usa la secuencia y nos devuelve la entidad con el ID
            OrdenDeVisitaEntidad guardada = ordenVisitaRepository.save(orden);

            // Devolvemos el ID generado al controlador
            return guardada.getIdVisita();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear la orden de visita");
        }
    }

    /* =====================================================
       EDITAR ORDEN DE VISITA
       ===================================================== */
    @Override
    public String editarOrdenVisita(int idOrdenAnt, OrdenDeVisitaDTO ordenNueva) {
        try {
            OrdenDeVisitaEntidad orden = ordenVisitaRepository
                    .findById(idOrdenAnt)
                    .orElse(null);

            if (orden == null) {
                return "La orden de visita no existe";
            }

            orden.setIdLugar(ordenNueva.getIdLugar());
            orden.setIdCliente(ordenNueva.getIdCliente());
            orden.setIdTrabajador(ordenNueva.getIdTrabajador());
            orden.setFechaRealizacion(ordenNueva.getFechaRealizacion());
            orden.setDescripcion(ordenNueva.getDescripcion());
            orden.setEstado(ordenNueva.getEstado());

            ordenVisitaRepository.save(orden);

            return "Orden de visita actualizada correctamente";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al actualizar la orden de visita";
        }
    }

    /* =====================================================
       BORRAR ORDEN DE VISITA
       ===================================================== */
    @Override
    public String borrarOrdenVisita(int idOrden) {
        try {
            if (!ordenVisitaRepository.existsById(idOrden)) {
                return "La orden de visita no existe";
            }

            ordenVisitaRepository.deleteById(idOrden);
            return "Orden de visita eliminada correctamente";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al eliminar la orden de visita";
        }
    }

    /* =====================================================
       LISTAR TODAS LAS ÓRDENES DE VISITA
       ===================================================== */
    @Override
    public List<OrdenDeVisitaDTO> listarOrdenVisita() {
        return ordenVisitaRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /* =====================================================
       LISTAR ÓRDENES DE VISITA POR CLIENTE
       ===================================================== */
    @Override
    public List<OrdenDeVisitaDTO> listarOrdenVisitaPorCliente(int idCliente) {
        return ordenVisitaRepository.findByIdCliente(idCliente)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /* =====================================================
       BUSCAR ORDEN DE VISITA POR ID
       ===================================================== */
    @Override
    public OrdenDeVisitaDTO buscarOrdenVisita(int idOrden) {
        return ordenVisitaRepository.findById(idOrden)
                .map(this::mapToDTO)
                .orElse(null);
    }

    /* =====================================================
       MAPPER PRIVADO
       ===================================================== */
    private OrdenDeVisitaDTO mapToDTO(OrdenDeVisitaEntidad entidad) {
        return new OrdenDeVisitaDTO(
                entidad.getIdVisita(),
                entidad.getIdLugar(),
                entidad.getIdCliente(),
                entidad.getIdTrabajador(),
                entidad.getFechaRealizacion(),
                entidad.getDescripcion(),
                entidad.getEstado()
        );
    }

    /* =====================================================
       EXISTE ORDEN DE VISITA
       ===================================================== */
    public boolean existeOrden(int idOrden) {
        return ordenVisitaRepository.existsById(idOrden);
    }
}
