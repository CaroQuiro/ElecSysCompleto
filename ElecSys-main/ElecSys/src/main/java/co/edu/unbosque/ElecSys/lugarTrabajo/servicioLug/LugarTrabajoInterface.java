package co.edu.unbosque.ElecSys.lugarTrabajo.servicioLug;

import co.edu.unbosque.ElecSys.lugarTrabajo.dtoLug.LugarTrabajoDTO;

import java.util.List;

public interface LugarTrabajoInterface {

    public String crearLugar(LugarTrabajoDTO lugar);
    public String editarLugar(int idAnterior, LugarTrabajoDTO lugar);
    public List<LugarTrabajoDTO> listarLugar();
    public LugarTrabajoDTO buscarLugar(int idLugar);
    public List<LugarTrabajoDTO> buscarLugarTexto(String query);

}
