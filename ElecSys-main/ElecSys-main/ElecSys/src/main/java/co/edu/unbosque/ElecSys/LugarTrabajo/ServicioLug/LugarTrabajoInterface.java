package co.edu.unbosque.ElecSys.LugarTrabajo.ServicioLug;

import co.edu.unbosque.ElecSys.LugarTrabajo.DTOLug.LugarTrabajoDTO;
import co.edu.unbosque.ElecSys.Usuario.Cliente.DTOClie.ClienteDTO;

import java.util.List;

public interface LugarTrabajoInterface {

    public String crearLugar(LugarTrabajoDTO lugar);
    public String editarLugar(int idAnterior, LugarTrabajoDTO lugar);
    public List<LugarTrabajoDTO> listarLugar();
    public LugarTrabajoDTO buscarLugar(int idLugar);
    public List<LugarTrabajoDTO> buscarLugarTexto(String query);

}
