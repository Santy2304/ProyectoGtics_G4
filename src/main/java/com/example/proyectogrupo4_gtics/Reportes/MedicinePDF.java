package com.example.proyectogrupo4_gtics.Reportes;

import com.example.proyectogrupo4_gtics.DTOs.cantidadMedicamentosDTO;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class MedicinePDF {

    private List<cantidadMedicamentosDTO> listaMedicinas;

    public MedicinePDF(List<cantidadMedicamentosDTO> listaMedicinas) {
        super();
        this.listaMedicinas = listaMedicinas;
    }

    private void Cabecera(PdfPTable tabla){
        PdfPCell celda = new PdfPCell();
        celda.setBackgroundColor(Color.orange);
        celda.setPadding(5);
        Font fuente = FontFactory.getFont(FontFactory.HELVETICA);
        fuente.setColor(Color.white);

        celda.setPhrase(new Phrase("ID", fuente));
        tabla.addCell(celda);

        celda.setPhrase(new Phrase("Nombre del Producto", fuente));
        tabla.addCell(celda);

        celda.setPhrase(new Phrase("Categoría", fuente));
        tabla.addCell(celda);

        celda.setPhrase(new Phrase("Cantidad", fuente));
        tabla.addCell(celda);

        celda.setPhrase(new Phrase("Precio (s/.)", fuente));
        tabla.addCell(celda);

    }

    private void escribirDatosDeLaTabla(PdfPTable tabla) {
        for (cantidadMedicamentosDTO medicine : listaMedicinas) {
            tabla.addCell(String.valueOf(medicine.getIdMedicine()));
            tabla.addCell(medicine.getNombreMedicamento());
            tabla.addCell(medicine.getCategoria());
            tabla.addCell(String.valueOf(medicine.getCantidad()));
            tabla.addCell(String.valueOf(medicine.getPrecio()));
        }
    }

    public void exportar(HttpServletResponse response) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font fuente = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fuente.setColor(Color.ORANGE);
        fuente.setSize(18);

        Paragraph titulo = new Paragraph("Lista de Medicamentos",fuente);
        titulo.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(titulo);
        PdfPTable tabla = new PdfPTable(5);
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(15);
        tabla.setWidths(new float[] { 1f, 3f, 2.3f, 2f, 2f});
        tabla.setWidthPercentage(110);

        Cabecera(tabla);
        escribirDatosDeLaTabla(tabla);

        document.add(tabla);
        document.close();

    }
}