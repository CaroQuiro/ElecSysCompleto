package co.edu.unbosque.ElecSys.cotizacion.archivo;

import co.edu.unbosque.ElecSys.ConfigArchivos.ConfigCotizacion;
import co.edu.unbosque.ElecSys.ConfigArchivos.DescargaArchivo;
import co.edu.unbosque.ElecSys.ConfigArchivos.FootersDocumento;
import co.edu.unbosque.ElecSys.cotizacion.dtoCot.CotizacionDTO;
import co.edu.unbosque.ElecSys.cotizacion.detalleCotizacion.dtoDetCot.DetalleCotizacionDTO;
import co.edu.unbosque.ElecSys.lugarTrabajo.dtoLug.LugarTrabajoDTO;
import co.edu.unbosque.ElecSys.usuario.cliente.dtoClie.ClienteDTO;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Servicio encargado de coordinar la generación del archivo binario PDF y su almacenamiento local.
 */
@Service
public class Pdf_Cotizacion {

    private ContenidoArchivo contenidoArchivo;

    @Autowired
    private DescargaArchivo descargaArchivo;

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
            Document doc = new Document(PageSize.A4, 36,36,50,36);
            PdfWriter writer = PdfWriter.getInstance(doc, out);
            writer.setPageEvent(new ConfigCotizacion(cotizacion));
            doc.open();

            contenidoArchivo = new ContenidoArchivo();
            contenidoArchivo.dirigidoCotizacion(doc, cotizacion, cliente, lugar);

            contenidoArchivo.tablaCotizacion(doc, detalles);

            contenidoArchivo.tablaTotales(doc, cotizacion);

            if(detalles.size() > 1){
                doc.newPage();
            }

            contenidoArchivo.seccionNotas(doc);

            contenidoArchivo.seccionFirma(doc);


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
                + "." + cotizacionDTO.getReferencia().replaceAll("\\s+", "_") + ".pdf";

        return descargaArchivo.guardarArchivo(pdf, nombreArchivo, "1.Cotizaciones", "CT");
    }
}
