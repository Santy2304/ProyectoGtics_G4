package com.example.proyectogrupo4_gtics.Reportes;

import com.example.proyectogrupo4_gtics.Entity.ReplacementOrder;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class RepoPDF {

    private List<ReplacementOrder> listaRepo;
    private String titulo;

    public RepoPDF(List<ReplacementOrder> listaRepo, String titulo) {
        this.listaRepo = listaRepo;
        this.titulo = titulo;
    }

    private void cabecera(PdfPTable tabla) {
        PdfPCell celda = new PdfPCell();
        celda.setBackgroundColor(Color.orange);
        celda.setPadding(7);
        Font fuente = FontFactory.getFont(FontFactory.HELVETICA);
        fuente.setColor(Color.white);

        celda.setPhrase(new Phrase("ID", fuente));
        tabla.addCell(celda);

        celda.setPhrase(new Phrase("Fecha de emisión", fuente));
        tabla.addCell(celda);

        celda.setPhrase(new Phrase("Estado", fuente));
        tabla.addCell(celda);
    }

    private void escribirDatosDeLaTabla(PdfPTable tabla) {
        for (ReplacementOrder r : listaRepo) {
            tabla.addCell("RP0" + r.getIdReplacementOrder());
            tabla.addCell(r.getReleaseDate().toString());
            tabla.addCell(r.getTrackingState());
        }
    }

    public void exportar(HttpServletResponse response) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fuenteTitulo.setColor(Color.ORANGE);
        fuenteTitulo.setSize(18);

        Paragraph tituloDocumento = new Paragraph(titulo, fuenteTitulo);
        tituloDocumento.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(tituloDocumento);

        PdfPTable tabla = new PdfPTable(3);
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(15);
        tabla.setWidths(new float[]{1f, 2.7f, 1.7f});
        tabla.setWidthPercentage(110);

        cabecera(tabla);
        escribirDatosDeLaTabla(tabla);

        document.add(tabla);
        document.close();
    }
}
