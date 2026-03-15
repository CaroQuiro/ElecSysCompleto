package co.edu.unbosque.ElecSys.contrato.archivoContrato;

import co.edu.unbosque.ElecSys.contrato.dtoCon.ContratoDTO;
import co.edu.unbosque.ElecSys.contrato.dtoCon.ContratoRequest;
import co.edu.unbosque.ElecSys.usuario.trabajador.dtoTra.TrabajadorDTO;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

/**
 * Servicio encargado de la orquestación y generación de documentos PDF.
 * Utiliza plantillas HTML y las transforma en archivos binarios listos para descarga.
 */
@Service
public class Pdf_Contrato {

    private MetodoCalculoFecha metodos = new MetodoCalculoFecha();

    /**
     * Coordina la creación del PDF inyectando datos en la plantilla y procesando el renderizado.
     * @param contratoDTO Datos del contrato guardado.
     * @param trabajador Información del empleado.
     * @param encargado Información de quien firma por la empresa.
     * @param datos Información adicional (edad, estado civil, etc.).
     * @return Arreglo de bytes del PDF generado.
     */
    public byte[] generarContrato(ContratoDTO contratoDTO, TrabajadorDTO trabajador, TrabajadorDTO encargado, ContratoRequest datos){
        try(ByteArrayOutputStream out = new ByteArrayOutputStream()){

            String html = construirContratoHTML(contratoDTO, trabajador, datos);

            PdfRendererBuilder builder = new PdfRendererBuilder();

            String baseUrls = getClass().getResource("/templates/PlantillaContrato/").toExternalForm();

            builder.withHtmlContent(html, baseUrls);
            builder.toStream(out);
            builder.run();

            System.out.println("BASE URI: " + baseUrls);

            return out.toByteArray();

        } catch (IOException e) {
            throw  new RuntimeException("Error al generar el pdf de contrato", e);
        }
    }

    /**
     * Realiza el mapeo de etiquetas (${etiqueta}) en el HTML con los datos reales del trabajador y contrato.
     * @param contratoDTO Datos laborales.
     * @param trabajador Datos personales del empleado.
     * @param datos Metadatos adicionales de la solicitud.
     * @return Cadena HTML con la información reemplazada.
     */
    public String construirContratoHTML(ContratoDTO contratoDTO,
                                        TrabajadorDTO trabajador,
                                        ContratoRequest datos){

        try{
            String html = cargarPlantilla();

            // Valores de trabajador
            html = html.replace("${trabajadorNombre}", trabajador.getNombre());
            html = html.replace("${trabajadorCedula}", String.valueOf(trabajador.getId_trabajador()));
            html = html.replace("${trabajadorDireccion}", trabajador.getDireccion());
            html = html.replace("${trabajadorNacimiento}", String.valueOf(datos.getFecha_nacimiento()));
            html = html.replace("${lugarNacimiento}", datos.getLugar_nacimiento());
            html = html.replace("${trabajadorEdad}", String.valueOf(datos.getEdad()));
            html = html.replace("${trabajadorEstadoCivil}", datos.getEstadoCivil());
            html = html.replace("${trabajadorTelefono}", trabajador.getTelefono());

            //Valores sobre el cargo
            String sueldoFormateado = metodos.formatearMoneda(contratoDTO.getSueldo());
            String salarioLetras = metodos.salarioEnLetras(contratoDTO.getSueldo());

            html = html.replace("${cargo}", contratoDTO.getCargo());
            html = html.replace("${salario}", sueldoFormateado);
            html = html.replace("${fechaInicio}", String.valueOf(contratoDTO.getFecha_iniciacion()));


            html = html.replace("${valor_letras}", salarioLetras);


            int numDias = contratoDTO.getFecha_expedicion().getDayOfMonth();
            int numAno = contratoDTO.getFecha_expedicion().getYear();

            String mesTextoFechaRealizacion = metodos.mesEnLetras(contratoDTO.getFecha_expedicion());

            String textoDiasFechaRealizacion = metodos.numeroALetras(numDias);
            String texto_anoFechaRealizacion = metodos.numeroALetras(numAno);

            // Valores de fechas
            html = html.replace("${text_dias}", textoDiasFechaRealizacion);
            html = html.replace("${num_dias}", String.valueOf(numDias));
            html = html.replace("${mes}", mesTextoFechaRealizacion);
            html = html.replace("${text_año}", texto_anoFechaRealizacion);
            html = html.replace("${num_año}", String.valueOf(numAno));


            int numDIasIniciacion = contratoDTO.getFecha_iniciacion().getDayOfMonth();
            int numAnoIniciacion = contratoDTO.getFecha_iniciacion().getYear();
            String mesTextoIniciacion = metodos.mesEnLetras(contratoDTO.getFecha_iniciacion());
            String textoDiasIniciacion = metodos.numeroALetras(numDIasIniciacion);

            html = html.replace("${text_dias_iniciacion}", textoDiasIniciacion);
            html = html.replace("${num_dias_iniciacion}", String.valueOf(numDIasIniciacion));
            html = html.replace("${mes_iniciacion}", mesTextoIniciacion);
            html = html.replace("${num_año_iniciacion}", String.valueOf(numAnoIniciacion));
            return html;
        } catch (IOException e){
            return e.getMessage();
        }
    }

    /**
     * Lee el archivo base HTML desde los recursos del sistema.
     * @return Contenido de la plantilla en texto plano.
     */
    private String cargarPlantilla() throws IOException {
        InputStream is = getClass()
                .getResourceAsStream("/templates/PlantillaContrato/plantilla_contrato_base.html");

        if (is == null) {
            throw new RuntimeException("No se encontró la plantilla HTML");
        }

        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }

    /**
     * Guarda físicamente el PDF generado en la carpeta de Descargas del usuario,
     * organizándolo por carpetas anuales.
     * @param contratoDTO Datos para el nombre del archivo.
     * @param trabajador Nombre del trabajador para el archivo.
     * @param pdf Binario del documento.
     * @return Nombre final del archivo guardado.
     */
    public String descargarPDF(ContratoDTO contratoDTO, TrabajadorDTO trabajador, byte[] pdf) throws IOException {
        String nombreArchivo = contratoDTO.getId_contrato()
                + ".Contrato_" + trabajador.getNombre() + ".pdf";

        String carpetaDescargas = System.getProperty("user.home") + File.separator + "Downloads";

        Path carpetaContratos = Paths.get(carpetaDescargas, "Contratos");

        int añoActual = LocalDate.now().getYear();
        Path carpetaAnual = carpetaContratos.resolve("CONTRATOS " + añoActual);

        if (!Files.exists(carpetaContratos)) {
            Files.createDirectory(carpetaContratos);
            System.out.println("Carpeta creada: " + carpetaContratos);
        }
        if (!Files.exists(carpetaAnual)) {
            Files.createDirectory(carpetaAnual);
            System.out.println("Subcarpeta anual creada: " + carpetaAnual);
        }

        Path rutaArchivo = carpetaAnual.resolve(nombreArchivo);

        if (Files.exists(rutaArchivo)) {
            System.out.println("Ya existe un archivo con el mismo nombre: " + rutaArchivo);
        }

        Files.write(rutaArchivo, pdf);
        System.out.println("PDF generado y guardado en: " + rutaArchivo.toAbsolutePath());
        return rutaArchivo.getFileName().toString();
    }
}
