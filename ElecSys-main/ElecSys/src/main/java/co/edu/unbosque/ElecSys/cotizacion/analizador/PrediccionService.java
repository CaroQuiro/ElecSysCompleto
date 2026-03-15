package co.edu.unbosque.ElecSys.cotizacion.analizador;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Servicio encargado de la comunicación con el motor de analisis de datos (Python).
 * Permite obtener probabilidades de aceptación de cotizaciones y gestionar el re-entrenamiento del modelo.
 */
@Service
public class PrediccionService {

    private final String PYTHON_BASE_URL = "http://localhost:8000";

    /**
     * Envía los datos de una cotización al servicio de analisis de datos para calcular la probabilidad de que el cliente la acepte.
     * * @param total Valor total de la cotización.
     * @param materiales Sumatoria de la cantidad de materiales/ítems.
     * @param items Cantidad de líneas o ítems diferentes en la cotización.
     * @param nuevo Indica si el cliente es nuevo ("si") o antiguo ("no").
     * @param tramites Indica si la cotización incluye trámites legales o con Codensa ("si"/"no").
     * @return La probabilidad de aceptación como un valor Double (0.0 a 1.0).
     */
    public Double obtenerProbabilidad(double total, int materiales, int items, String nuevo, String tramites) {
        RestTemplate restTemplate = new RestTemplate();
        String url = PYTHON_BASE_URL + "/predict";

        CotizacionAnalisisRequest request = new CotizacionAnalisisRequest(total, materiales, items, nuevo, tramites);

        Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);

        if (response != null && response.containsKey("probabilidad_aceptacion")) {
            Object prob = response.get("probabilidad_aceptacion");
            return Double.valueOf(prob.toString());
        }
        return 0.0;
    }

    /**
     * Envía un conjunto de datos históricos al servicio de analisis de datos para actualizar y re-entrenar el modelo predictivo.
     * * @param datosHistoricos Lista de objetos con datos de cotizaciones pasadas y su estado final (Aceptado/Rechazado).
     * @return Un mapa con la respuesta del servidor de analisis de datos o un mensaje de error en caso de fallo de conexión.
     */
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