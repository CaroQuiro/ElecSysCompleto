package co.edu.unbosque.ElecSys.Usuario.Cliente.ServicioClie;

import co.edu.unbosque.ElecSys.Cotizacion.DTOCot.CotizacionDTO;
import co.edu.unbosque.ElecSys.Usuario.Cliente.DTOClie.ClienteDTO;

import java.util.List;

public interface ClienteInterface {
    public String agregarCliente(ClienteDTO cliente);
    public ClienteDTO buscarCliente(int id);
    public List<ClienteDTO> buscarClienteTexto(String query);
    public String deshabilitarCliente(int id);
    public List<ClienteDTO> listarClientes();
    public String actualizarCliente(int id, ClienteDTO cliente);
}
