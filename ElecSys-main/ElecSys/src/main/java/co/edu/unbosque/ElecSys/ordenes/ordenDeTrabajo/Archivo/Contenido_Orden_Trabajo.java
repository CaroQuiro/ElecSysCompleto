package co.edu.unbosque.ElecSys.ordenes.ordenDeTrabajo.Archivo;

import co.edu.unbosque.ElecSys.lugarTrabajo.dtoLug.LugarTrabajoDTO;
import co.edu.unbosque.ElecSys.ordenes.ordenDeTrabajo.detalleOrdenTrabajo.dtoDetOrdTra.DetalleOrdenTrabajoDTO;
import co.edu.unbosque.ElecSys.ordenes.ordenDeTrabajo.dtoOrdTra.OrdenDeTrabajoDTO;
import co.edu.unbosque.ElecSys.usuario.cliente.dtoClie.ClienteDTO;
import co.edu.unbosque.ElecSys.usuario.trabajador.dtoTra.TrabajadorDTO;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import java.text.SimpleDateFormat;
import java.util.List;

public class Contenido_Orden_Trabajo {

    public void encabezadoOrdenTrabajo(Document documento, OrdenDeTrabajoDTO ordenDeTrabajoDTO) throws DocumentException {
        PdfPTable tablaprincipal = new PdfPTable(2);
        tablaprincipal.setWidthPercentage(100);
        tablaprincipal.setWidths(new float[]{6,4});
        tablaprincipal.setSpacingAfter(35f);

        //lado Izquierdo de la tabla principal
        PdfPTable empresaTable = new PdfPTable(2);
        empresaTable.setWidthPercentage(100);
        empresaTable.setWidths(new float[]{2, 4});
        //Seccion del logo
        Image logo = null;

        try {
            logo = Image.getInstance(getClass().getResource("/static/LogoEmpresa.jpeg"));
            logo.scaleToFit(80, 80);
        } catch (Exception e) {
            e.printStackTrace();
        }

        PdfPCell logoCell = new PdfPCell();
        if (logo != null) {
            logoCell.addElement(logo);
        }
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        empresaTable.addCell(logoCell);

        //Info de la empresa
        Font empresaFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
        Font infoFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);

        PdfPCell infoEmpresaCell = new PdfPCell();
        infoEmpresaCell.setBorder(Rectangle.NO_BORDER);
        infoEmpresaCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        infoEmpresaCell.addElement(new Paragraph("VC ELECTRICOS CONSTRUCCIONES S.A.S", empresaFont));
        infoEmpresaCell.addElement(new Paragraph("NIT: 900820830-1", infoFont));
        infoEmpresaCell.addElement(new Paragraph("Email: vcelectricos@hotmail.com", infoFont));

        empresaTable.addCell(infoEmpresaCell);

        PdfPCell leftTop = new PdfPCell(empresaTable);
        leftTop.setBorder(Rectangle.NO_BORDER);
        leftTop.setPadding(5);

        tablaprincipal.addCell(leftTop);

        //lado derecho informacion de id_orden, fecha y estado
        PdfPTable ordenTable = new PdfPTable(2);
        ordenTable.setWidthPercentage(100);
        ordenTable.setWidths(new float[]{1, 1}); // mitad y mitad

        Font whiteFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);

        PdfPCell tituloOrden = new PdfPCell(
                new Phrase("ORDEN DE TRABAJO N° " + ordenDeTrabajoDTO.getId_orden(), whiteFont)
        );

        tituloOrden.setColspan(2);
        tituloOrden.setBackgroundColor(new BaseColor(192, 0, 0));
        tituloOrden.setHorizontalAlignment(Element.ALIGN_CENTER);
        tituloOrden.setPadding(8);
        // bordes suaves
        tituloOrden.setBorderWidth(0.5f);

        ordenTable.addCell(tituloOrden);

        //Fecha
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
        PdfPCell fechaCell = new PdfPCell(
                new Phrase("Fecha: " + ordenDeTrabajoDTO.getFecha_realizacion(), normalFont)
        );

        fechaCell.setPadding(6);
        fechaCell.setBorderWidth(0.5f);

        ordenTable.addCell(fechaCell);

        //Lado derecho
        Font estadoFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

        PdfPCell estadoCell = new PdfPCell(
                new Phrase(ordenDeTrabajoDTO.getEstado(), estadoFont)
        );

        estadoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        estadoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        estadoCell.setPadding(6);
        // color tipo etiqueta
        estadoCell.setBackgroundColor(new BaseColor(255, 230, 150)); // amarillo suave
        estadoCell.setBorderWidth(0.5f);

        ordenTable.addCell(estadoCell);

        PdfPCell rightTop = new PdfPCell(ordenTable);
        rightTop.setBorder(Rectangle.NO_BORDER);
        // 🔥 clave: NO expandir
        rightTop.setVerticalAlignment(Element.ALIGN_TOP);
        rightTop.setPadding(5);

        tablaprincipal.addCell(rightTop);

        documento.add(tablaprincipal);
    }

    public void SeccionCliente (Document documento, ClienteDTO cliente, LugarTrabajoDTO lugar) throws DocumentException {
        Font tituloSeccionFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);

        PdfPTable tituloTabla = new PdfPTable(1);
        tituloTabla.setWidthPercentage(100);

        PdfPCell titulo = new PdfPCell(new Phrase("1. DATOS DEL CLIENTE / PROYECTO", tituloSeccionFont));
        titulo.setBackgroundColor(new BaseColor(192, 0, 0));
        titulo.setPadding(6);
        titulo.setBorder(Rectangle.NO_BORDER);

        tituloTabla.addCell(titulo);
        documento.add(tituloTabla);

        PdfPTable clienteTable = new PdfPTable(4);
        clienteTable.setWidthPercentage(100);
        // proporciones (label pequeño, valor grande)
        clienteTable.setWidths(new float[]{2, 4, 2, 4});
        clienteTable.setSpacingAfter(20f);

        Font labelFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);

        clienteTable.addCell(crearCeldaLabel("Cliente:", labelFont));
        clienteTable.addCell(crearCeldaValor(cliente.getNombre(), valueFont));

        clienteTable.addCell(crearCeldaLabel("Teléfono:", labelFont));
        clienteTable.addCell(crearCeldaValor(cliente.getTelefono(), valueFont));


        clienteTable.addCell(crearCeldaLabel("Contacto:", labelFont));
        clienteTable.addCell(crearCeldaValor(cliente.getCorreo(), valueFont));

        // celda vacía (colspan 2)
        PdfPCell emptyCell = new PdfPCell(new Phrase(""));
        emptyCell.setColspan(2);
        emptyCell.setBorder(Rectangle.NO_BORDER);

        clienteTable.addCell(emptyCell);

        clienteTable.addCell(crearCeldaLabel("Dirección:", labelFont));
        clienteTable.addCell(crearCeldaValor(lugar.getDireccion(), valueFont));

        clienteTable.addCell(crearCeldaLabel("Ciudad:", labelFont));
        clienteTable.addCell(crearCeldaValor("Bogotá", valueFont));

        clienteTable.addCell(crearCeldaLabel("Proyecto:", labelFont));
        PdfPCell proyectoCell = new PdfPCell(new Phrase(lugar.getNombreLugar(), valueFont));
        proyectoCell.setColspan(3);
        proyectoCell.setBorder(Rectangle.NO_BORDER);
        proyectoCell.setPadding(5);

        clienteTable.addCell(proyectoCell);

        documento.add(clienteTable);
    }

    public void SeccionDetalles(Document documento , List<DetalleOrdenTrabajoDTO> detallesOrden) throws DocumentException {
        PdfPTable tituloTabla = new PdfPTable(1);
        tituloTabla.setWidthPercentage(100);

        Font tituloFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);

        PdfPCell titulo = new PdfPCell(new Phrase("2. DETALLE DE LA ORDEN DE TRABAJO", tituloFont));
        titulo.setBackgroundColor(new BaseColor(192, 0, 0));
        titulo.setPadding(6);
        titulo.setBorder(Rectangle.NO_BORDER);

        tituloTabla.addCell(titulo);

        documento.add(tituloTabla);

        //Tabla principal
        PdfPTable detalleTable = new PdfPTable(4);
        detalleTable.setWidthPercentage(100);
        detalleTable.setWidths(new float[]{1, 4, 2, 4});
        detalleTable.setSpacingAfter(10f);

        Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);

        detalleTable.addCell(crearHeader("Ítem", headerFont));
        detalleTable.addCell(crearHeader("Actividad", headerFont));
        detalleTable.addCell(crearHeader("Duración", headerFont));
        detalleTable.addCell(crearHeader("Observaciones", headerFont));

        Font dataFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);

        if (detallesOrden == null || detallesOrden.isEmpty()) {
            PdfPCell empty = new PdfPCell(new Phrase("No hay actividades registradas"));
            empty.setColspan(4);
            empty.setHorizontalAlignment(Element.ALIGN_CENTER);
            empty.setPadding(10);

            detalleTable.addCell(empty);

        } else {
            int i = 1;

            for (DetalleOrdenTrabajoDTO det : detallesOrden) {
                detalleTable.addCell(crearCeldaCentro(String.valueOf(i), dataFont));
                detalleTable.addCell(crearCeldaIzquierda(safe(det.getActividad()), dataFont));
                detalleTable.addCell(crearCeldaCentro(safe(det.getDuracion()), dataFont));
                detalleTable.addCell(crearCeldaIzquierda(safe(det.getObservaciones()), dataFont));
                i++;
            }
        }

        documento.add(detalleTable);
    }

    public void SeccionTrabajador(Document documento, TrabajadorDTO trabajador) throws DocumentException {
        PdfPTable tituloTabla = new PdfPTable(1);
        tituloTabla.setWidthPercentage(100);

        Font tituloFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);

        PdfPCell titulo = new PdfPCell(new Phrase("3. PERSONAL ASIGNADO", tituloFont));
        titulo.setBackgroundColor(new BaseColor(192, 0, 0));
        titulo.setPadding(6);
        titulo.setBorder(Rectangle.NO_BORDER);

        tituloTabla.addCell(titulo);

        documento.add(tituloTabla);

        PdfPTable personalTable = new PdfPTable(2);
        personalTable.setWidthPercentage(100);
        personalTable.setWidths(new float[]{3, 7}); // label pequeño, valor grande
        personalTable.setSpacingAfter(25f);

        Font labelFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);

        personalTable.addCell(crearCeldaLabel("Técnico Responsable:", labelFont));
        personalTable.addCell(crearCeldaValor(trabajador.getNombre(), valueFont));

        personalTable.addCell(crearCeldaLabel("Documento:", labelFont));
        personalTable.addCell(crearCeldaValor(String.valueOf(trabajador.getId_trabajador()), valueFont));

        personalTable.addCell(crearCeldaLabel("Telefono:", labelFont));
        personalTable.addCell(crearCeldaValor(trabajador.getTelefono(), valueFont));

        documento.add(personalTable);
    }

    public void seccionObservaciones(Document documento, OrdenDeTrabajoDTO orden) throws DocumentException {
        PdfPTable tituloObs = new PdfPTable(1);
        tituloObs.setWidthPercentage(100);

        Font tituloFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);

        PdfPCell titulo = new PdfPCell(new Phrase("4. OBSERVACIONES GENERALES", tituloFont));
        titulo.setBackgroundColor(new BaseColor(192, 0, 0));
        titulo.setPadding(6);
        titulo.setBorder(Rectangle.NO_BORDER);

        tituloObs.addCell(titulo);
        documento.add(tituloObs);

        Font obsFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);

        PdfPTable obsTable = new PdfPTable(1);
        obsTable.setWidthPercentage(100);
        obsTable.setSpacingAfter(10f);

        PdfPCell obsCell = new PdfPCell(
                new Phrase(safe(orden.getObservaciones()), obsFont)
        );
        // altura mínima tipo espacio de escritura
        obsCell.setMinimumHeight(60);

        obsCell.setPadding(8);
        obsCell.setBorder(Rectangle.BOX);
        obsCell.setBorderColor(BaseColor.LIGHT_GRAY);

        obsTable.addCell(obsCell);

        documento.add(obsTable);
    }

    public void cierreOrdenTrabajo(Document documento, OrdenDeTrabajoDTO orden) throws DocumentException {
        PdfPTable tituloCierre = new PdfPTable(1);
        tituloCierre.setWidthPercentage(100);

        Font tituloFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);

        PdfPCell titulo = new PdfPCell(new Phrase("5. CIERRE DE LA ORDEN", tituloFont));
        titulo.setBackgroundColor(new BaseColor(192, 0, 0));
        titulo.setPadding(6);
        titulo.setBorder(Rectangle.NO_BORDER);

        tituloCierre.addCell(titulo);
        documento.add(tituloCierre);

        PdfPTable cierreTable = new PdfPTable(2);
        cierreTable.setWidthPercentage(100);
        cierreTable.setWidths(new float[]{3, 7});
        cierreTable.setSpacingAfter(10f);

        Font labelFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);

        cierreTable.addCell(crearCeldaLabel("Estado Final:", labelFont));
        cierreTable.addCell(crearCeldaValor(safe(orden.getEstado()), valueFont));

        String fechaRecibido = "N/A";

        if (orden.getFecha_realizacion() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            fechaRecibido = sdf.format(orden.getFecha_realizacion());
        }

        cierreTable.addCell(crearCeldaLabel("Fecha de Recibido:", labelFont));
        cierreTable.addCell(crearCeldaValor(fechaRecibido, valueFont));

        documento.add(cierreTable);
    }

    public void seccionFirmas(Document documento, TrabajadorDTO trabajador, ClienteDTO cliente) throws DocumentException {

        Font labelFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);

        PdfPTable firmaTable = new PdfPTable(2);
        firmaTable.setWidthPercentage(100);
        firmaTable.setSpacingBefore(20f);
        firmaTable.setWidths(new float[]{5, 5});

        PdfPCell firmaTecnico = new PdfPCell();
        firmaTecnico.setBorder(Rectangle.NO_BORDER);
        firmaTecnico.setHorizontalAlignment(Element.ALIGN_CENTER);

// espacio
        firmaTecnico.addElement(new Paragraph("\n\n\n"));

// línea
        Paragraph lineaTec = new Paragraph("__________________________", valueFont);
        lineaTec.setAlignment(Element.ALIGN_CENTER);
        firmaTecnico.addElement(lineaTec);

// nombre real
        Paragraph nombreTec = new Paragraph(safe(trabajador.getNombre()), labelFont);
        nombreTec.setAlignment(Element.ALIGN_CENTER);
        firmaTecnico.addElement(nombreTec);

// label
        Paragraph labelTec = new Paragraph("Firma Técnico", valueFont);
        labelTec.setAlignment(Element.ALIGN_CENTER);
        firmaTecnico.addElement(labelTec);

        firmaTable.addCell(firmaTecnico);

        PdfPCell firmaCliente = new PdfPCell();
        firmaCliente.setBorder(Rectangle.NO_BORDER);
        firmaCliente.setHorizontalAlignment(Element.ALIGN_CENTER);

        firmaCliente.addElement(new Paragraph("\n\n\n"));

        Paragraph lineaCli = new Paragraph("__________________________", valueFont);
        lineaCli.setAlignment(Element.ALIGN_CENTER);
        firmaCliente.addElement(lineaCli);

        Paragraph nombreCli = new Paragraph(safe(cliente.getNombre()), labelFont);
        nombreCli.setAlignment(Element.ALIGN_CENTER);
        firmaCliente.addElement(nombreCli);

        Paragraph labelCli = new Paragraph("Firma Cliente", valueFont);
        labelCli.setAlignment(Element.ALIGN_CENTER);
        firmaCliente.addElement(labelCli);

        firmaTable.addCell(firmaCliente);
        documento.add(firmaTable);
    }

    private String safe(String value) {
        return value != null ? value : "";
    }

    private PdfPCell crearCeldaLabel(String texto, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(5);
        return cell;
    }

    private PdfPCell crearCeldaValor(String texto, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(5);
        return cell;
    }

    private PdfPCell crearHeader(String texto, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBackgroundColor(new BaseColor(192, 0, 0));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        return cell;
    }

    private PdfPCell crearCeldaCentro(String texto, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        return cell;
    }

    private PdfPCell crearCeldaIzquierda(String texto, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setPadding(5);
        return cell;
    }
}
