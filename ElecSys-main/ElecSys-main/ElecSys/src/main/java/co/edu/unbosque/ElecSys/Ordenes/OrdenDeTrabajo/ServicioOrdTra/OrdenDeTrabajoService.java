package co.edu.unbosque.ElecSys.Ordenes.OrdenDeTrabajo.ServicioOrdTra;

import co.edu.unbosque.ElecSys.Ordenes.OrdenDeTrabajo.DTOOrdTra.OrdenDeTrabajoDTO;
import co.edu.unbosque.ElecSys.Ordenes.OrdenDeTrabajo.EntidadOrdTra.OrdenDeTrabajoEntidad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdenDeTrabajoService implements OrdenDeTrabajoInterface{

    @Autowired
    private OrdenDeTrabajoRepository ordenTrabajoRepository;

    /* =====================================================
   AGREGAR ORDEN DE TRABAJO (CORREGIDO)
   ===================================================== */
    @Override
    public Integer agregarOrdenTrabajo(OrdenDeTrabajoDTO dto) {
        try {
            OrdenDeTrabajoEntidad orden = new OrdenDeTrabajoEntidad();

            // NO asignamos el ID manualmente, dejamos que sea NULL para activar la secuencia
            orden.setIdOrdenVisita(dto.getId_orden_visita());
            orden.setIdLugar(dto.getId_lugar());
            orden.setIdCliente(dto.getId_cliente());
            orden.setIdTrabajador(dto.getId_trabajador());
            orden.setFechaRealizacion(dto.getFecha_realizacion());
            orden.setEstado(dto.getEstado());

            // Capturamos la entidad guardada para obtener el ID real
            OrdenDeTrabajoEntidad guardada = ordenTrabajoRepository.save(orden);

            return guardada.getIdOrden(); // Retornamos el ID (1, 2, 3...)
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear la orden de trabajo en el servidor");
        }
    }

    /* =====================================================
       EDITAR ORDEN DE TRABAJO
       ===================================================== */
    @Override
    public String editarOrdenTrabajo(int idOrdenAnt, OrdenDeTrabajoDTO ordenNueva) {
        try {
            OrdenDeTrabajoEntidad orden = ordenTrabajoRepository
                    .findById(idOrdenAnt)
                    .orElse(null);

            if (orden == null) {
                return "La orden de trabajo no existe";
            }

            orden.setIdOrdenVisita(ordenNueva.getId_orden_visita());
            orden.setIdLugar(ordenNueva.getId_lugar());
            orden.setIdCliente(ordenNueva.getId_cliente());
            orden.setIdTrabajador(ordenNueva.getId_trabajador());
            orden.setFechaRealizacion(ordenNueva.getFecha_realizacion());
            orden.setEstado(ordenNueva.getEstado());

            ordenTrabajoRepository.save(orden);

            return "Orden de trabajo actualizada correctamente";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al actualizar la orden de trabajo";
        }
    }

    /* =====================================================
       BORRAR ORDEN DE TRABAJO
       ===================================================== */
    @Override
    public String borrarOrdenTrabajo(int idOrden) {
        try {
            if (!ordenTrabajoRepository.existsById(idOrden)) {
                return "La orden de trabajo no existe";
            }

            ordenTrabajoRepository.deleteById(idOrden);
            return "Orden de trabajo eliminada correctamente";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al eliminar la orden de trabajo";
        }
    }

    /* =====================================================
       LISTAR TODAS LAS ÓRDENES
       ===================================================== */
    @Override
    public List<OrdenDeTrabajoDTO> listarOrdenTrabajo() {
        return ordenTrabajoRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /* =====================================================
       LISTAR ÓRDENES POR CLIENTE
       ===================================================== */
    @Override
    public List<OrdenDeTrabajoDTO> listarOrdenTrabajoPorCliente(int idCliente) {
        return ordenTrabajoRepository.findByIdCliente(idCliente)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /* =====================================================
       BUSCAR ORDEN POR ID
       ===================================================== */
    @Override
    public OrdenDeTrabajoDTO buscarOrdenTrabajo(int idOrden) {
        return ordenTrabajoRepository.findById(idOrden)
                .map(this::mapToDTO)
                .orElse(null);
    }

    /* =====================================================
       MAPPER PRIVADO
       ===================================================== */
    private OrdenDeTrabajoDTO mapToDTO(OrdenDeTrabajoEntidad entidad) {
        return new OrdenDeTrabajoDTO(
                entidad.getIdOrden(),
                entidad.getIdOrdenVisita(),
                entidad.getIdLugar(),
                entidad.getIdCliente(),
                entidad.getIdTrabajador(),
                entidad.getFechaRealizacion(),
                entidad.getEstado()
        );
    }

    /* =====================================================
       EXISTE ORDEN
       ===================================================== */
    public boolean existeOrden(int idOrden) {
        return ordenTrabajoRepository.existsById(idOrden);
    }
}
