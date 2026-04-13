package co.edu.unbosque.ElecSys.ConfigArchivos;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class FootersDocumento extends PdfPageEventHelper {

    private Font fontpie = new Font(Font.FontFamily.HELVETICA, 8);

    @Override
    public void onEndPage(PdfWriter writer, Document document) {

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
}
