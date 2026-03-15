package co.edu.unbosque.ElecSys.historialActividad.helperHis;

import co.edu.unbosque.ElecSys.historialActividad.detalleActividad.dtoDetalleActividad.DetalleActividadDTO;
import co.edu.unbosque.ElecSys.historialActividad.detalleActividad.servicioDetalleActividad.DetalleActividadService;
import co.edu.unbosque.ElecSys.historialActividad.dtoHis.HistorialActividadDTO;
import co.edu.unbosque.ElecSys.historialActividad.servicioHis.HistorialActividadService;
import co.edu.unbosque.ElecSys.usuario.trabajador.servicioTra.TrabajadorRepository;
import co.edu.unbosque.ElecSys.usuario.trabajador.entidadTra.TrabajadorEntidad; // Asegura el import de la entidad
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
@Component
public class AuditoriaHelper {

    @Autowired
    private HistorialActividadService historialService;
    @Autowired
    private DetalleActividadService detalleService;
    @Autowired
    private TrabajadorRepository trabajadorRepository;

    public void registrarAccion(String modulo, String accion, String campo, String anterior, String nuevo) {
        try {
            // 1. Validar autenticación
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null) return;

            String correo = (String) auth.getPrincipal();

            int idTrabajador = trabajadorRepository.findByCorreo(correo)
                    .map(TrabajadorEntidad::getId_trabajador)
                    .orElse(0);

            // 2. Crear cabecera de historial
            HistorialActividadDTO h = new HistorialActividadDTO();
            h.setIdTrabajador(idTrabajador);
            h.setModuloSistema(modulo);
            h.setAccionRealizada(accion);
            h.setFechaRealizacion(LocalDate.now());
            h.setHora(LocalTime.now());

            String respuestaService = historialService.agregarHistorialActividad(h);

            // 3. Convertir ID con seguridad
            int idGenerado = 0;
            try {
                idGenerado = Integer.parseInt(respuestaService);
            } catch (NumberFormatException e) {
                System.err.println("Error: El servicio de historial no devolvió un ID válido");
            }

            // 4. Registrar detalle si hay un ID válido y un campo afectado
            if (idGenerado > 0 && campo != null) {
                DetalleActividadDTO d = new DetalleActividadDTO();
                d.setIdHistorial(idGenerado);
                d.setCampoAfectado(campo);
                d.setValorAnterior(anterior);
                d.setValorNuevo(nuevo);
                detalleService.agregarDetalleActividad(d);
            }
        } catch (Exception e) {
            // Evitamos que un error en auditoría tumbe la operación principal
            System.err.println("Error no controlado en AuditoriaHelper: " + e.getMessage());
        }
    }
}