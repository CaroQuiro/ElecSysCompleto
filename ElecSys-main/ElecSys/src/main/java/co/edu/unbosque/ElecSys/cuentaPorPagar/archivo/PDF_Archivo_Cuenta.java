package co.edu.unbosque.ElecSys.cuentaPorPagar.archivo;

import co.edu.unbosque.ElecSys.ConfigArchivos.ConfigOrdenTrabajoFooter;
import co.edu.unbosque.ElecSys.ConfigArchivos.DescargaArchivo;
import co.edu.unbosque.ElecSys.ConfigArchivos.FootersDocumento;
import co.edu.unbosque.ElecSys.cuentaPorPagar.dtoCuen.CuentaPorPagarDTO;
import co.edu.unbosque.ElecSys.cuentaPorPagar.detalle_Cuenta.detalleDTO.Detalle_CuentaDTO;
import co.edu.unbosque.ElecSys.usuario.cliente.dtoClie.ClienteDTO;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Servicio encargado de coordinar la creación del flujo de bytes del PDF
 * y su almacenamiento en el sistema de archivos local.
 */
@Service
public class PDF_Archivo_Cuenta {

    private ContenidoArchivo_Cuentas contenidoArchivoCuentas;

    @Autowired
    private DescargaArchivo descargaArchivo;

    /**
     * Genera el archivo PDF en memoria y devuelve su contenido en un arreglo de bytes.
     * @param cuenta Datos maestros de la cuenta de cobro.
     * @param cliente Datos del cliente asociado.
     * @param detallesCuentas Lista de detalles/conceptos del cobro.
     * @return Arreglo de bytes del PDF generado.
     * @throws RuntimeException Si ocurre un error durante la generación del documento.
     */
    public byte[] generarArchivoCuenta(CuentaPorPagarDTO cuenta, ClienteDTO cliente, List<Detalle_CuentaDTO> detallesCuentas) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 45, 36, 30, 36);
            PdfWriter writer = PdfWriter.getInstance(doc, out);
            writer.setPageEvent(new ConfigOrdenTrabajoFooter());
            doc.open();

            contenidoArchivoCuentas = new ContenidoArchivo_Cuentas();

            contenidoArchivoCuentas.encabezadoArchivo(doc, cuenta);

            contenidoArchivoCuentas.cuerpoDocumento(doc, cuenta, cliente);

            contenidoArchivoCuentas.listarDetalles(doc, detallesCuentas, cuenta);

            contenidoArchivoCuentas.firmaDocumento(doc);

            doc.close();
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Guarda el PDF generado en la carpeta de descargas del usuario, organizada por año.
     * @param cuenta Datos para identificar el archivo.
     * @param pdf Binario del documento.
     * @return El nombre del archivo guardado.
     * @throws IOException Si hay errores de escritura en el disco o creación de carpetas.
     */
    public String descargarCuentaPDF(CuentaPorPagarDTO cuenta, byte[] pdf) throws IOException{
        String nombreArchivo = cuenta.getId_cuenta_pagar() + "." + cuenta.getReferencia_pdf() +".pdf";
        return descargaArchivo.guardarArchivo(pdf, nombreArchivo, "3.Cuenta de Cobro", "Cuentas");
    }
}
