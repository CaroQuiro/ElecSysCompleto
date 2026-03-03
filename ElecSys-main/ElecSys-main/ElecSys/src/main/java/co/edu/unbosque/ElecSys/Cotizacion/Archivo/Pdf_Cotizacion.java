package co.edu.unbosque.ElecSys.Cotizacion.Archivo;

import co.edu.unbosque.ElecSys.Cotizacion.DTOCot.CotizacionDTO;
import co.edu.unbosque.ElecSys.Cotizacion.DetalleCotizacion.DTODetCot.DetalleCotizacionDTO;
import co.edu.unbosque.ElecSys.LugarTrabajo.DTOLug.LugarTrabajoDTO;
import co.edu.unbosque.ElecSys.Usuario.Cliente.DTOClie.ClienteDTO;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class Pdf_Cotizacion {

    private ContenidoArchivo contenidoArchivo;

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
//            String timestamp = LocalDateTime.now()
//                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
//            String nuevoNombre = cotizacionDTO.getId_cotizacion()
//                    + ".Cotizacion_" + cotizacionDTO.getReferencia().replaceAll("\\s+", "_")
//                    + "_" + timestamp + ".pdf";
//            rutaArchivo = carpetaAnual.resolve(nuevoNombre);
//            System.out.println(" Se generó con un nombre alternativo: " + nuevoNombre);
        }

        Files.write(rutaArchivo, pdf);
        System.out.println("PDF generado y guardado en: " + rutaArchivo.toAbsolutePath());

        return rutaArchivo.getFileName().toString();
    }
}
