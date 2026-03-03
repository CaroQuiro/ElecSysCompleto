package co.edu.unbosque.ElecSys.Cotizacion.Analizador;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CotizacionRequest {
    private double total;
    private int materiales;
    private int items;
    private String es_nuevo;
    private String tiene_tramites;

}