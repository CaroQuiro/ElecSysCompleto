package co.edu.unbosque.ElecSys.ConfigArchivos;

import co.edu.unbosque.ElecSys.cotizacion.dtoCot.CotizacionDTO;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;

public class ConfigCotizacion extends PdfPageEventHelper {

    private Font fontpie = new Font(Font.FontFamily.HELVETICA, 8);

    private CotizacionDTO cotizacionDTO; // 🔥 guardar dato

    public ConfigCotizacion(CotizacionDTO cotizacionDTO) {
        this.cotizacionDTO = cotizacionDTO;
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document){
        try {
            agregarHeader(writer, document, cotizacionDTO);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        agregarWatermark(writer, document);
        agregarFooter(writer, document);
    }

    public void agregarHeader(PdfWriter writer, Document documento, CotizacionDTO cotizacionDTO) throws DocumentException {
        Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font fontNormal = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

        PdfPTable encabezado = new PdfPTable(2);
        encabezado.setTotalWidth(520);
        encabezado.setLockedWidth(true);
        encabezado.setWidths(new float[]{70, 30});

        PdfPCell celdaEmpresa = new PdfPCell(
                new Phrase("VC ELECTRICOS CONSTRUCCIONES S.A.S.", fontTitulo));
        celdaEmpresa.setBorder(Rectangle.NO_BORDER);
        celdaEmpresa.setHorizontalAlignment(Element.ALIGN_LEFT);

        PdfPCell celdaNumero = new PdfPCell(
                new Phrase("Cotización No. " + cotizacionDTO.getId_cotizacion(), fontNormal));
        celdaNumero.setBorder(Rectangle.NO_BORDER);
        celdaNumero.setHorizontalAlignment(Element.ALIGN_RIGHT);

        encabezado.addCell(celdaEmpresa);
        encabezado.addCell(celdaNumero);

        PdfContentByte canvas = writer.getDirectContent();

        canvas.setLineWidth(0.8f);
        float y = documento.getPageSize().getHeight() - 40;

        canvas.moveTo(documento.left(), y);
        canvas.lineTo(documento.right(), y);

        canvas.stroke();

        encabezado.writeSelectedRows(
                0, -1,
                documento.left(),
                documento.getPageSize().getHeight() - 20,
                writer.getDirectContent()
        );
    }


    public void agregarFooter(PdfWriter writer, Document document){
        PdfPTable footer = new PdfPTable(1);
        footer.setTotalWidth(500);
        footer.setLockedWidth(true);

        PdfPCell cell = new PdfPCell(new Phrase(
                "Dirección. Cll 143 # 149 B – 15\n" +
                        "Suba – Bilbao\n" +
                        "Cel. 311 868 14 05 – 535 73 38\n" +
                        "vcelectricos@hotmail.com",
                fontpie
        ));

        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

        footer.addCell(cell);

        footer.writeSelectedRows(
                0, -1,
                (document.right() + document.left()) / 2 - 250,
                document.bottom() + 30,
                writer.getDirectContent()
        );
    }

    private void agregarWatermark(PdfWriter writer, Document document) {

        try {
            PdfContentByte canvas = writer.getDirectContentUnder();

            Image logo = Image.getInstance(
                    Thread.currentThread()
                            .getContextClassLoader()
                            .getResource("static/LogoEmpresa.jpeg"));

            logo.scaleToFit(300, 300);

            logo.setAbsolutePosition(
                    (document.getPageSize().getWidth() - logo.getScaledWidth()) / 2,
                    (document.getPageSize().getHeight() - logo.getScaledHeight()) / 2
            );

            PdfGState estado = new PdfGState();
            estado.setFillOpacity(0.08f);
            canvas.setGState(estado);

            canvas.addImage(logo);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
