package co.edu.unbosque.ElecSys.cotizacion.archivo;

import co.edu.unbosque.ElecSys.cotizacion.dtoCot.CotizacionDTO;
import co.edu.unbosque.ElecSys.cotizacion.detalleCotizacion.dtoDetCot.DetalleCotizacionDTO;
import co.edu.unbosque.ElecSys.lugarTrabajo.dtoLug.LugarTrabajoDTO;
import co.edu.unbosque.ElecSys.usuario.cliente.dtoClie.ClienteDTO;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.DocumentException;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

/**
 * Servicio encargado de coordinar la generación del archivo binario PDF y su almacenamiento local.
 */
@Service
public class Pdf_Cotizacion {

    private ContenidoArchivo contenidoArchivo;

    /**
     * Crea un archivo PDF en memoria utilizando los datos de la cotización, cliente y detalles.
     * * @param cotizacion Datos generales de la cotización.
     * @param cliente Datos del cliente destinatario.
     * @param lugar Datos del lugar de la obra.
     * @param detalles Lista de ítems de la cotización.
     * @return Un arreglo de bytes (byte[]) que representa el archivo PDF.
     * @throws RuntimeException Si ocurre un error de estructura de iText durante la generación.
     */
    public byte[] generarArchivo(CotizacionDTO cotizacion, ClienteDTO cliente, LugarTrabajoDTO lugar , List<DetalleCotizacionDTO> detalles){
        try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 36,36,30,36);
            PdfWriter.getInstance(doc, out);
            doc.open();

            contenidoArchivo = new ContenidoArchivo();
            contenidoArchivo.encabezadoArchivo(doc, cotizacion);

            contenidoArchivo.dirigidoCotizacion(doc, cotizacion, cliente, lugar);

            contenidoArchivo.tablaCotizacion(doc, detalles);

            contenidoArchivo.tablaTotales(doc, cotizacion);

            contenidoArchivo.seccionNotas(doc);

            contenidoArchivo.seccionFirma(doc);

            contenidoArchivo.pieDePagina(doc);

            doc.close();
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Guarda físicamente el PDF generado en la carpeta de descargas del usuario,
     * organizándolo por año y nombre de referencia.
     * * @param cotizacionDTO Datos para nombrar el archivo.
     * @param pdf El contenido binario del documento.
     * @return El nombre del archivo guardado.
     * @throws IOException Si hay errores al crear carpetas o escribir el archivo en disco.
     */
    public String descargarPDF(CotizacionDTO cotizacionDTO, byte[] pdf) throws IOException {

        String nombreArchivo = cotizacionDTO.getId_cotizacion()
                + ".Cotizacion_" + cotizacionDTO.getReferencia().replaceAll("\\s+", "_") + ".pdf";

        String carpetaDescargas = System.getProperty("user.home") + File.separator + "Downloads";

        Path carpetaCotizaciones = Paths.get(carpetaDescargas, "Cotizaciones");

        int añoActual = LocalDate.now().getYear();
        Path carpetaAnual = carpetaCotizaciones.resolve("CT-" + añoActual);

        if (!Files.exists(carpetaCotizaciones)) {
            Files.createDirectory(carpetaCotizaciones);
            System.out.println("Carpeta creada: " + carpetaCotizaciones);
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
