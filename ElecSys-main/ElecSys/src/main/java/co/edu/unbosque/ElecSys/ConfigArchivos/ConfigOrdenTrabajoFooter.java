package co.edu.unbosque.ElecSys.ConfigArchivos;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class ConfigOrdenTrabajoFooter extends PdfPageEventHelper {

    private Font fontpie = new Font(Font.FontFamily.HELVETICA, 8);

    @Override
    public void onEndPage(PdfWriter writer, Document document){
        agregarWatermark(writer, document);
        agregarFooter(writer, document);
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
