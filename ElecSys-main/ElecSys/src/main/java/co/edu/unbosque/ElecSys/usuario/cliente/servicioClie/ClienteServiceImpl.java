package co.edu.unbosque.ElecSys.usuario.cliente.servicioClie;

import co.edu.unbosque.ElecSys.historialActividad.helperHis.AuditoriaHelper;
import co.edu.unbosque.ElecSys.usuario.cliente.dtoClie.ClienteDTO;
import co.edu.unbosque.ElecSys.usuario.cliente.entidadClie.ClienteEntidad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación de los servicios de negocio para la gestión de clientes.
 * Se encarga de la transformación entre DTOs y Entidades, y la comunicación con el repositorio.
 */
@Service
public class ClienteServiceImpl implements ClienteInterface{

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private AuditoriaHelper auditoria;

    /**
     * Guarda un nuevo cliente en el repositorio.
     * @param cliente DTO con la información del cliente.
     * @return Mensaje de éxito o error en la creación.
     */
    @Override
    public String agregarCliente(ClienteDTO cliente) {
        ClienteEntidad nuevoCliente = new ClienteEntidad(
          cliente.getId_cliente(),
                cliente.getNombre(),
                cliente.getTelefono(),
                cliente.getDireccion(),
          cliente.getCorreo(),
                cliente.getEstado()
        );
        try{
            clienteRepository.save(nuevoCliente);
            auditoria.registrarAccion("CLIENTES", "Creación de Cliente",
                    "ID_CLIENTE", "N/A", String.valueOf(cliente.getId_cliente()));
            return "Cliente creado exitosamente";
        } catch (Exception e) {
            return "Error al crear el cliente";
        }
    }

    /**
     * Recupera un cliente por su identificador primario.
     * @param id ID del cliente.
     * @return DTO del cliente encontrado o null si no existe.
     */
    @Override
    public ClienteDTO buscarCliente(int id) {
        ClienteEntidad cliente = clienteRepository.findById(id).orElse(null);
        if (cliente != null){
            return new ClienteDTO(
                    cliente.getId_cliente(),
                    cliente.getNombre(),
                    cliente.getTelefono(),
                    cliente.getDireccion(),
                    cliente.getCorreo(),
                    cliente.getEstado());
        }
        return null;
    }

    /**
     * Filtra clientes basándose en una cadena de búsqueda.
     * @param query Texto para filtrar (nombre, dirección, etc.).
     * @return Lista de {@link ClienteDTO} mapeados.
     */
    @Override
    public List<ClienteDTO> buscarClienteTexto(String query) {
        List<ClienteEntidad> cliente = clienteRepository.buscarClienteTexto(query);
        if (cliente != null){
            return cliente.stream().map( c -> new ClienteDTO(
                    c.getId_cliente(),
                    c.getNombre(),
                    c.getTelefono(),
                    c.getDireccion(),
                    c.getCorreo(),
                    c.getEstado()
            )).toList();
        }
        return null;
    }

    /**
     * Marca a un cliente como "Deshabilitado" en la base de datos.
     * @param id ID del cliente.
     * @return Mensaje indicando el resultado de la acción.
     */
    @Override
    public String deshabilitarCliente(int id) {
        Optional<ClienteEntidad> clienteExit = clienteRepository.findById(id);
        if (clienteExit.isEmpty()){
            return "Cliente no encontrado para deshabilitar";
        }else {
            ClienteEntidad entidad = clienteExit.get();

            entidad.setEstado("Deshabilitado");

            clienteRepository.save(entidad);

            return "Cliente deshabilitado correctamente";
        }
    }

    /**
     * Recupera todos los clientes almacenados sin filtros.
     * @return Lista de todos los {@link ClienteDTO}.
     */
    @Override
    public List<ClienteDTO> listarClientes() {
        List<ClienteEntidad> cliente = clienteRepository.findAll();
        List<ClienteDTO> clienteDTOS = new ArrayList<>();

        for (ClienteEntidad clientes : cliente){
            clienteDTOS.add(new ClienteDTO(
                    clientes.getId_cliente(),
                    clientes.getNombre(),
                    clientes.getTelefono(),
                    clientes.getDireccion(),
                    clientes.getCorreo(),
                    clientes.getEstado()
            ));
        }

        return clienteDTOS;
    }

    /**
     * Modifica los campos de un cliente existente.
     * @param id ID del cliente a actualizar.
     * @param cliente DTO con la nueva información.
     * @return Mensaje confirmando la actualización.
     */
    @Override
    public String actualizarCliente(int id, ClienteDTO cliente) {

        Optional<ClienteEntidad> clienteExit = clienteRepository.findById(id);
        if (clienteExit.isEmpty()){
            return "Cliente no encontrado para actualizar";
        }else {
            ClienteEntidad entidad = clienteExit.get();

            entidad.setNombre(cliente.getNombre());
            entidad.setTelefono(cliente.getTelefono());
            entidad.setDireccion(cliente.getDireccion());
            entidad.setCorreo(cliente.getCorreo());
            entidad.setEstado(cliente.getEstado());

            clienteRepository.save(entidad);

            auditoria.registrarAccion("CLIENTES", "Actualización de Datos",
                    "ID_CLIENTE", "Existente", String.valueOf(id));
            return "Cliente Actualizado Correctamente";
        }
    }
}
