package co.edu.unbosque.ElecSys.ordenes.ordenDeTrabajo.Archivo;

import co.edu.unbosque.ElecSys.ConfigArchivos.ConfigOrdenTrabajoFooter;
import co.edu.unbosque.ElecSys.ConfigArchivos.DescargaArchivo;
import co.edu.unbosque.ElecSys.lugarTrabajo.dtoLug.LugarTrabajoDTO;
import co.edu.unbosque.ElecSys.ordenes.ordenDeTrabajo.detalleOrdenTrabajo.dtoDetOrdTra.DetalleOrdenTrabajoDTO;
import co.edu.unbosque.ElecSys.ordenes.ordenDeTrabajo.dtoOrdTra.OrdenDeTrabajoDTO;
import co.edu.unbosque.ElecSys.usuario.cliente.dtoClie.ClienteDTO;
import co.edu.unbosque.ElecSys.usuario.trabajador.dtoTra.TrabajadorDTO;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class PDF_Orden_Trabajo {

    private Contenido_Orden_Trabajo contenidoOrdenTrabajo;

    @Autowired
    private DescargaArchivo descargaArchivo;

    public byte[] generarArchivo(OrdenDeTrabajoDTO ordenDeTrabajoDTO, ClienteDTO cliente,
                                 LugarTrabajoDTO lugarTrabajoDTO, List<DetalleOrdenTrabajoDTO> detallesOrden, TrabajadorDTO trabajador){
        try(ByteArrayOutputStream out = new ByteArrayOutputStream()){
            Document doc = new Document(PageSize.A4, 36,36,30,30);
            PdfWriter writer = PdfWriter.getInstance(doc, out);
            writer.setPageEvent(new ConfigOrdenTrabajoFooter());
            doc.open();

            contenidoOrdenTrabajo = new Contenido_Orden_Trabajo();

            contenidoOrdenTrabajo.encabezadoOrdenTrabajo(doc, ordenDeTrabajoDTO);

            contenidoOrdenTrabajo.SeccionCliente(doc, cliente, lugarTrabajoDTO);

            contenidoOrdenTrabajo.SeccionDetalles(doc, detallesOrden);

            contenidoOrdenTrabajo.SeccionTrabajador(doc, trabajador);

            contenidoOrdenTrabajo.seccionObservaciones(doc, ordenDeTrabajoDTO);

            contenidoOrdenTrabajo.cierreOrdenTrabajo(doc, ordenDeTrabajoDTO);

            contenidoOrdenTrabajo.seccionFirmas(doc, trabajador, cliente);

            doc.close();
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    public String descargarPDFOrden(int Idgenerado, OrdenDeTrabajoDTO ordenDeTrabajoDTO, byte[] pdf) throws IOException {
        String nombreArchivo = "";

        if (Idgenerado == 0){
            nombreArchivo = ordenDeTrabajoDTO.getId_orden() + "." + ordenDeTrabajoDTO.getReferencia_pdf() + ".pdf";
        } else {
            nombreArchivo = Idgenerado + "." + ordenDeTrabajoDTO.getReferencia_pdf() + ".pdf";
        }

        return descargaArchivo.guardarArchivo(pdf, nombreArchivo, "4.Ordenes de Trabajo", "Ordenes");
    }
}
