package co.edu.unbosque.ElecSys.Cotizacion.Archivo;

import co.edu.unbosque.ElecSys.Cotizacion.DTOCot.CotizacionDTO;
import co.edu.unbosque.ElecSys.Cotizacion.DetalleCotizacion.DTODetCot.DetalleCotizacionDTO;
import co.edu.unbosque.ElecSys.LugarTrabajo.DTOLug.LugarTrabajoDTO;
import co.edu.unbosque.ElecSys.Usuario.Cliente.DTOClie.ClienteDTO;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class ContenidoArchivo {

    public void encabezadoArchivo(Document documento, CotizacionDTO cotizacionDTO) throws DocumentException {
        Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font fontNormal = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

        // Crear una tabla con dos columnas
        PdfPTable encabezado = new PdfPTable(2);
        encabezado.setWidthPercentage(100); // ancho total
        encabezado.setWidths(new float[]{70, 30}); // proporciones

        PdfPCell celdaEmpresa = new PdfPCell(new Phrase("VC ELECTRICOS CONSTRUCCIONES S.A.S.", fontTitulo));
        celdaEmpresa.setBorder(Rectangle.NO_BORDER);
        celdaEmpresa.setHorizontalAlignment(Element.ALIGN_LEFT);
        encabezado.addCell(celdaEmpresa);

        // Celda derecha: Número de cotización
        PdfPCell celdaNumero = new PdfPCell(new Phrase("Cotización No. " + cotizacionDTO.getId_cotizacion(), fontNormal));
        celdaNumero.setBorder(Rectangle.NO_BORDER);
        celdaNumero.setHorizontalAlignment(Element.ALIGN_RIGHT);
        encabezado.addCell(celdaNumero);

        // Agregar la tabla al documento
        documento.add(encabezado);

        // Línea divisoria
        LineSeparator linea = new LineSeparator();
        linea.setOffset(-2);
        documento.add(new Chunk(linea));
    }

    public void dirigidoCotizacion(Document documento, CotizacionDTO cotizacionDTO, ClienteDTO clienteDTO, LugarTrabajoDTO lugar) throws DocumentException {
        Font fontNormal = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
        Font fontBold = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);

        // para el encabezado de la fecha
        Paragraph fecha = new Paragraph("Bogotá D.C., " + cotizacionDTO.getFecha_realizacion(), new Font(Font.FontFamily.HELVETICA, 10));
        fecha.setSpacingBefore(7);
        fecha.setSpacingAfter(10);
        documento.add(fecha);

        // --- Información del cliente con sangría
        Paragraph clienteInfo = new Paragraph("Señores:\n", fontNormal);

        clienteInfo.setIndentationLeft(40f); // sangría a la derecha (como tab)
        clienteInfo.add(new Phrase(clienteDTO.getNombre().toUpperCase() + "\n", fontBold));
        clienteInfo.add(new Phrase(lugar.getNombreLugar().toUpperCase(), fontBold));
        clienteInfo.setSpacingAfter(15);
        documento.add(clienteInfo);

        Paragraph ciudad = new Paragraph("Ciudad", fontNormal);
        ciudad.setSpacingAfter(5);
        documento.add(ciudad);

        // --- Referencia centrada
        Paragraph ref = new Paragraph("Ref. " + cotizacionDTO.getReferencia(), fontBold);
        ref.setAlignment(Element.ALIGN_CENTER); // centrado
        ref.setSpacingAfter(11);
        documento.add(ref);

        Paragraph saludo = new Paragraph(
                "Cordial saludo.\nA continuación, someto a su consideración la siguiente cotización:",
                fontNormal
        );
        saludo.setSpacingBefore(10);
        documento.add(saludo);
    }

    public void tablaCotizacion(Document documento, java.util.List<DetalleCotizacionDTO> detalles) throws DocumentException {
        // Definir fuentes
        Font fontHeader = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        Font fontBody = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

        // Crear tabla con 5 columnas
        PdfPTable tabla = new PdfPTable(5);
        tabla.setWidthPercentage(100); // ocupa todo el ancho de la página
        tabla.setWidths(new float[]{10, 45, 15, 15, 15}); // proporciones entre columnas
        tabla.setSpacingBefore(20f); // espacio antes de la tabla
        tabla.setSpacingAfter(10f);

// 🎨 COLOR DE ENCABEZADO PERSONALIZADO (gris)
        BaseColor headerColor = new BaseColor(210, 210, 210);

        String[] headers = {"Ítem", "Descripción", "Cantidad", "Valor Unitario", "Subtotal"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, fontHeader));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(headerColor);
            cell.setPadding(6);
            tabla.addCell(cell);
        }

        int item = 1;
        for (DetalleCotizacionDTO detalle : detalles) {
            // Ítem
            PdfPCell itemCell = new PdfPCell(new Phrase(String.valueOf(item++), fontBody));
            itemCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            itemCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tabla.addCell(itemCell);

            // Descripción (alineada a la izquierda)
            PdfPCell descCell = new PdfPCell(new Phrase(detalle.getDescripcion(), fontBody));
            descCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            descCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            descCell.setPadding(5);
            tabla.addCell(descCell);

            // Cantidad
            PdfPCell cantCell = new PdfPCell(new Phrase(String.valueOf(detalle.getCantidad()), fontBody));
            cantCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cantCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tabla.addCell(cantCell);

            // Valor unitario
            PdfPCell valUniCell = new PdfPCell(new Phrase(formatoMoneda(detalle.getValor_unitario()), fontBody));
            valUniCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            valUniCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tabla.addCell(valUniCell);

            // Subtotal
            PdfPCell subCell = new PdfPCell(new Phrase(formatoMoneda(detalle.getSubtotal()), fontBody));
            subCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            subCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tabla.addCell(subCell);
        }

        documento.add(tabla);
    }

    public void tablaTotales(Document documento, CotizacionDTO cot) throws DocumentException {

        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(32); // más compacto
        tabla.setHorizontalAlignment(Element.ALIGN_RIGHT);

        Font fBold = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
        Font fGray = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, new BaseColor(60,60,60));

        BaseColor grisSuave = new BaseColor(235,235,235);

        addRow(tabla, "SUBTOTAL", cot.getValor_total(), fBold, fGray, false);

        if(cot.getAdministracion()!=null)
            addRow(tabla, "ADMINISTRACIÓN 7%", cot.getAdministracion(), fBold, fGray, false);

        if(cot.getImprevistos()!=null)
            addRow(tabla, "IMPREVISTOS 5%", cot.getImprevistos(), fBold, fGray, false);

        if(cot.getUtilidad()!=null)
            addRow(tabla, "UTILIDADES 4%", cot.getUtilidad(), fBold, fGray, false);

        if(cot.getIva()!=null && cot.getIva().compareTo(BigDecimal.ZERO)>0)
            addRow(tabla, "IVA 19%", cot.getIva(), fBold, fGray, false);

        // TOTAL destacado
        addRow(tabla, "TOTAL", cot.getTotal_pagar(), fBold, fGray, true);

        documento.add(tabla);
    }


    private void addRow(PdfPTable tabla, String label, BigDecimal value, Font fBold, Font fGray, boolean esTotal) {
        PdfPCell c1 = new PdfPCell(new Phrase(label, fBold));
        PdfPCell c2 = new PdfPCell(new Phrase(formatoMoneda(value), fBold));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c2.setHorizontalAlignment(Element.ALIGN_RIGHT);

        if(esTotal){
            c1.setBorderWidth(1.3f);
            c2.setBorderWidth(1.3f);
        }

        tabla.addCell(c1);
        tabla.addCell(c2);
    }

    private String formatoMoneda(BigDecimal valor) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');

        DecimalFormat formato = new DecimalFormat("#,###.00", symbols);
        return "$ " + formato.format(valor);
    }

    public void seccionNotas(Document documento) throws DocumentException {
        Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        Font fontTexto = new Font(Font.FontFamily.HELVETICA, 9);

        Paragraph titulo = new Paragraph("NOTA.", fontTitulo);
        titulo.setSpacingBefore(15);
        titulo.setSpacingAfter(5);
        documento.add(titulo);

        com.itextpdf.text.List lista = new com.itextpdf.text.List(com.itextpdf.text.List.ORDERED);
        lista.setSymbolIndent(12);

        lista.add(new ListItem("Si se desea realizar opciones de cámaras, datos y aumento de carga, se añadirá al subtotal.", fontTexto));
        lista.add(new ListItem("Las reformas se cobrarán como adicional dependiendo del ítem correspondiente.", fontTexto));
        lista.add(new ListItem("Todo el presupuesto está cotizado con las especificaciones de cada ítem.", fontTexto));
        lista.add(new ListItem("El material suministrado cuenta con certificación RETIE, RETLAP y CIDET.", fontTexto));

        ListItem formaPago = new ListItem("Forma de pago:", fontTexto);
        com.itextpdf.text.List sublista = new com.itextpdf.text.List(com.itextpdf.text.List.ORDERED);
        sublista.setSymbolIndent(15);
        sublista.add(new ListItem("50% de anticipo.", fontTexto));
        sublista.add(new ListItem("Restante a cortes de obra.", fontTexto));
        formaPago.add(sublista);

        lista.add(formaPago);
        lista.add(new ListItem("Vigencia de la cotización: 30 días.", fontTexto));

        documento.add(lista);
    }

    public void seccionFirma(Document documento) throws DocumentException, IOException {
        Font fBold = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
        Font fNormal = new Font(Font.FontFamily.HELVETICA, 8);

        Paragraph firma = new Paragraph();
        firma.setSpacingBefore(10); // <-- reducido
        firma.setSpacingAfter(2);

        firma.add(new Phrase("Atentamente.\n\n", fNormal));
        firma.add(new Phrase("___________________________________\n", fNormal));
        firma.add(new Phrase("VC ELECTRICOS CONSTRUCCIONES S.A.S.\n", fBold));
        firma.add(new Phrase("VICTOR JULIO CARVAJAL RINCON\n", fBold));
        firma.add(new Phrase("Representante legal\n", fNormal));

        documento.add(firma);
    }

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
