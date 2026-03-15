package co.edu.unbosque.ElecSys.cuentaPorPagar.archivo;

import co.edu.unbosque.ElecSys.cuentaPorPagar.dtoCuen.CuentaPorPagarDTO;
import co.edu.unbosque.ElecSys.cuentaPorPagar.detalle_Cuenta.detalleDTO.Detalle_CuentaDTO;
import co.edu.unbosque.ElecSys.usuario.cliente.dtoClie.ClienteDTO;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.draw.LineSeparator;
import java.net.URL;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.List;

/**
 * Clase encargada de definir la estructura visual y el contenido de los documentos
 * PDF para las Cuentas de Cobro utilizando la librería iText.
 */
public class ContenidoArchivo_Cuentas {

    /**
     * Genera el encabezado del archivo con el nombre de la empresa, número de cuenta y fecha.
     * @param documento Objeto Document donde se escribirá el contenido.
     * @param cuenta DTO con la información de la cuenta por pagar.
     * @throws DocumentException Si ocurre un error al agregar elementos al PDF.
     */
    public void encabezadoArchivo(Document documento, CuentaPorPagarDTO cuenta) throws DocumentException{
        // Tabla principal 2 columnas
        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{3, 1});

        Font empresaFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 12);

        // Celda izquierda (Nombre empresa)
        PdfPCell celdaEmpresa = new PdfPCell();
        celdaEmpresa.setBorder(Rectangle.NO_BORDER);

        Paragraph empresa = new Paragraph("VC ELECTRICOS CONSTRUCCIONES SAS", empresaFont);
        celdaEmpresa.addElement(empresa);

        tabla.addCell(celdaEmpresa);

        // Celda derecha (# numero)
        PdfPCell celdaNumero = new PdfPCell();
        celdaNumero.setBorder(Rectangle.NO_BORDER);
        celdaNumero.setHorizontalAlignment(Element.ALIGN_RIGHT);

        Paragraph numero = new Paragraph("Cuenta de Cobro, # " + cuenta.getId_cuenta_pagar(), normalFont);
        numero.setAlignment(Element.ALIGN_RIGHT);

        celdaNumero.addElement(numero);
        tabla.addCell(celdaNumero);

        documento.add(tabla);

        // Línea divisoria
        LineSeparator line = new LineSeparator();
        documento.add(new Chunk(line));

        documento.add(new Paragraph(" "));

        DateTimeFormatter formatter = DateTimeFormatter.
                                        ofPattern("dd 'de' MMMM 'del' yyyy",
                                                new Locale("es", "CO"));

        String FechaFormateada = cuenta.getFecha_realizacion().format(formatter);
        // Ciudad y fecha
        Paragraph fecha = new Paragraph(
                "Bogotá, D.C. " + FechaFormateada,
                normalFont
        );
        fecha.setAlignment(Element.ALIGN_LEFT);

        documento.add(fecha);
        documento.add(new Paragraph(" "));
    }

    /**
     * Construye el cuerpo principal del documento, detallando quién debe a quién y el monto total.
     * @param document El documento PDF en construcción.
     * @param cuenta Datos de la cuenta (monto, notas).
     * @param cliente Datos del cliente deudor.
     * @throws DocumentException Si hay errores en la estructura del documento.
     */
    public void cuerpoDocumento(Document document, CuentaPorPagarDTO cuenta, ClienteDTO cliente) throws DocumentException{
        Font tituloFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

        Paragraph titulo = new Paragraph("CUENTA DE COBRO", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingBefore(8);
        titulo.setSpacingAfter(12);
        document.add(titulo);

        Font bold = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

        Paragraph senor = new Paragraph("Señor:", bold);
        senor.setAlignment(Element.ALIGN_CENTER);
        senor.setSpacingAfter(10);
        document.add(senor);

        Paragraph client = new Paragraph(cliente.getId_cliente());
        client.setAlignment(Element.ALIGN_CENTER);
        client.setSpacingAfter(4);
        document.add(client);

        Paragraph nombreCliente = new Paragraph(cliente.getNombre(), bold);
        nombreCliente.setAlignment(Element.ALIGN_CENTER);
        document.add(nombreCliente);

        Paragraph direccion = new Paragraph(cliente.getDireccion());
        direccion.setAlignment(Element.ALIGN_CENTER);
        direccion.setSpacingAfter(20);
        document.add(direccion);

        Paragraph debe = new Paragraph("Debe a:", bold);
        debe.setAlignment(Element.ALIGN_CENTER);
        debe.setSpacingAfter(10);
        document.add(debe);

        Paragraph empresa = new Paragraph("VC ELECTRICOS CONSTRUCCIONES S.A.S", bold);
        empresa.setAlignment(Element.ALIGN_CENTER);
        document.add(empresa);

        Paragraph nit = new Paragraph("NIT. 900820830-1");
        nit.setAlignment(Element.ALIGN_CENTER);
        nit.setSpacingAfter(25);
        document.add(nit);

        Paragraph suma = new Paragraph("LA SUMA DE", bold);
        suma.setAlignment(Element.ALIGN_CENTER);
        suma.setSpacingAfter(10);
        document.add(suma);

        NumberFormat formatoColombiano = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));

        String montoFormateado = formatoColombiano.format(cuenta.getMonto());

        Font montoFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);

        Paragraph valorNumero = new Paragraph("(" + montoFormateado + ")", montoFont);
        valorNumero.setAlignment(Element.ALIGN_CENTER);
        document.add(valorNumero);

        CalcularValorLetras calcularValorLetras = new CalcularValorLetras();
        String valorLetrasconMonto = calcularValorLetras.convertir(cuenta.getMonto());

        Paragraph valorLetras = new Paragraph(valorLetrasconMonto, bold);
        valorLetras.setAlignment(Element.ALIGN_CENTER);
        valorLetras.setSpacingAfter(20);
        document.add(valorLetras);

        Paragraph concepto = new Paragraph("POR CONCEPTO DE:", bold);
        concepto.setAlignment(Element.ALIGN_CENTER);
        document.add(concepto);
    }

    /**
     * Genera una tabla con el listado detallado de conceptos o servicios cobrados.
     * @param document El documento PDF.
     * @param detalles Lista de ítems o conceptos asociados a la cuenta.
     * @param cuenta Datos generales de la cuenta para incluir notas adicionales.
     * @throws DocumentException Si falla la creación de la tabla de detalles.
     */
    public void listarDetalles(Document document,
                               List<Detalle_CuentaDTO> detalles,
                               CuentaPorPagarDTO cuenta) throws DocumentException {

        Font normal = new Font(Font.FontFamily.HELVETICA, 12);
        Font bold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(80);
        tabla.setHorizontalAlignment(Element.ALIGN_LEFT);
        tabla.setSpacingBefore(10);

        tabla.setWidths(new float[]{1, 9}); // ancho columnas

        int contador = 1;

        for (Detalle_CuentaDTO detalle : detalles) {

            // Número
            PdfPCell celdaNumero = new PdfPCell(
                    new Phrase(contador + ".", bold)
            );
            celdaNumero.setBorder(Rectangle.NO_BORDER);
            celdaNumero.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celdaNumero.setPaddingRight(3);

            // Descripción
            PdfPCell celdaDescripcion = new PdfPCell(
                    new Phrase(detalle.getDescripcion(), normal)
            );
            celdaDescripcion.setBorder(Rectangle.NO_BORDER);
            celdaDescripcion.setHorizontalAlignment(Element.ALIGN_LEFT);

            tabla.addCell(celdaNumero);
            tabla.addCell(celdaDescripcion);

            contador++;
        }

        document.add(tabla);
        // Notas
        Paragraph notas = new Paragraph(cuenta.getNota(), bold);
        notas.setSpacingBefore(15);
        notas.setAlignment(Element.ALIGN_LEFT);
        document.add(notas);
    }

    /**
     * Inserta la sección de firma del representante legal con imagen digitalizada.
     * @param document El documento PDF.
     * @throws Exception Si no se encuentra el recurso de la imagen o falla la inserción.
     */
    public void firmaDocumento(Document document) throws Exception {

        Font bold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font normal = new Font(Font.FontFamily.HELVETICA, 11);

        Paragraph cordial = new Paragraph("Cordialmente:", bold);
        cordial.setSpacingBefore(28);
        cordial.setSpacingAfter(20);
        document.add(cordial);

        // Tabla contenedora de firma
        PdfPTable tablaFirma = new PdfPTable(1);
        tablaFirma.setWidthPercentage(40); // ancho del bloque
        tablaFirma.setHorizontalAlignment(Element.ALIGN_LEFT);

        // ===== IMAGEN =====
        URL resource = Thread.currentThread()
                .getContextClassLoader()
                .getResource("static/FirmaVictor.png");

        if (resource == null) {
            throw new RuntimeException("No se encontró la imagen");
        }

        Image firma = Image.getInstance(resource);
        firma.scaleToFit(150, 80);

        PdfPCell celdaImagen = new PdfPCell(firma);
        celdaImagen.setBorder(Rectangle.NO_BORDER);
        celdaImagen.setHorizontalAlignment(Element.ALIGN_CENTER);
        tablaFirma.addCell(celdaImagen);

        // ===== LÍNEA =====
        PdfPCell celdaLinea = new PdfPCell();
        celdaLinea.setBorder(Rectangle.TOP);
        celdaLinea.setFixedHeight(10);
        celdaLinea.setBorderWidthTop(1f);
        tablaFirma.addCell(celdaLinea);

        // ===== NOMBRE =====
        PdfPCell celdaNombre = new PdfPCell(
                new Phrase("Víctor Julio Carvajal Rincón", bold));
        celdaNombre.setBorder(Rectangle.NO_BORDER);
        celdaNombre.setHorizontalAlignment(Element.ALIGN_LEFT);
        tablaFirma.addCell(celdaNombre);

        // ===== CARGO =====
        PdfPCell celdaCargo = new PdfPCell(
                new Phrase("Representante legal", normal));
        celdaCargo.setBorder(Rectangle.NO_BORDER);
        celdaCargo.setHorizontalAlignment(Element.ALIGN_LEFT);
        tablaFirma.addCell(celdaCargo);

        document.add(tablaFirma);
    }

    /**
     * Agrega el pie de página con los datos de contacto corporativos de VC Eléctricos.
     * @param documento El documento PDF.
     * @throws DocumentException Si falla la adición del párrafo de cierre.
     */
    public void pieDePagina(Document documento) throws DocumentException {
        Font fontPie = new Font(Font.FontFamily.HELVETICA, 8);

        Paragraph pie = new Paragraph(
                "Dirección. Cll 143 # 149 B – 15\n"
                        + "Suba – Bilbao\n"
                        + "Cel. 311 868 14 05 – 535 73 38\n"
                        + "vcelectricos@hotmail.com\n\n",
                fontPie
        );
        pie.setAlignment(Element.ALIGN_CENTER);
        documento.add(pie);
    }

}
