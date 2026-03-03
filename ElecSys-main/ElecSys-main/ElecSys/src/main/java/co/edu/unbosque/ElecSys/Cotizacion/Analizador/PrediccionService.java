package co.edu.unbosque.ElecSys.Cotizacion.Analizador;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.List;

@Service
public class PrediccionService {

    private final String PYTHON_BASE_URL = "http://localhost:8000";

    public Double obtenerProbabilidad(double total, int materiales, int items, String nuevo, String tramites) {
        RestTemplate restTemplate = new RestTemplate();
        String url = PYTHON_BASE_URL + "/predict";

        CotizacionRequest request = new CotizacionRequest(total, materiales, items, nuevo, tramites);

        Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);

        if (response != null && response.containsKey("probabilidad_aceptacion")) {
            Object prob = response.get("probabilidad_aceptacion");
            return Double.valueOf(prob.toString());
        }
        return 0.0;
    }

    public Map<String, Object> entrenarModelo(List<CotizacionReentrenarRequest> datosHistoricos) {
        RestTemplate restTemplate = new RestTemplate();
        String url = PYTHON_BASE_URL + "/train";

        try {
            return restTemplate.postForObject(url, datosHistoricos, Map.class);
        } catch (Exception e) {
            return Map.of("error", "No se pudo conectar con el motor de IA: " + e.getMessage());
        }
    }
}