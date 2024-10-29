package com.example.proyectogrupo4_gtics.Reportes;

import com.example.proyectogrupo4_gtics.DTOs.CantidadMedicamentosDTO;
import com.example.proyectogrupo4_gtics.DTOs.MedicamentosPorSedeDTO;
import com.example.proyectogrupo4_gtics.Entity.Patient;
import com.lowagie.text.*;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class MedicinePDF {

    private List<CantidadMedicamentosDTO> listaMedicinas;
    private String titulo;

    public MedicinePDF(List<CantidadMedicamentosDTO> listaMedicinas,String titulo) {
        super();
        this.listaMedicinas = listaMedicinas;
        this.titulo = titulo;

    }

    private void cabecera(PdfPTable tabla) {
        PdfPCell celda = new PdfPCell();
        celda.setBackgroundColor(Color.orange);
        celda.setPadding(5);
        Font fuente = FontFactory.getFont(FontFactory.HELVETICA);
        fuente.setColor(Color.white);
        fuente.setSize(12); // Ajustar el tamaño de la fuente

        String[] cabeceras = {"Nombre del producto", "Categoría", "Cantidad", "Precio(s/.)"};
        for (String cabecera : cabeceras) {
            celda.setPhrase(new Phrase(cabecera, fuente));
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(celda);
        }
    }


    private void escribirDatosDeLaTabla(PdfPTable tabla) {
        Font fuente = FontFactory.getFont(FontFactory.HELVETICA);
        fuente.setSize(12); // Ajustar el tamaño de la fuente
        for (CantidadMedicamentosDTO d : listaMedicinas) {
            PdfPCell celda = new PdfPCell(new Phrase(d.getNombreMedicamento(), fuente));
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(celda);

            celda = new PdfPCell(new Phrase(d.getCategoria(), fuente));
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(celda);

            celda = new PdfPCell(new Phrase(String.valueOf(d.getCantidad()), fuente));
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(celda);

            celda = new PdfPCell(new Phrase(String.valueOf(d.getPrecio()), fuente));
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(celda);

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

        PdfPTable tabla = new PdfPTable(4);
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(15);
        tabla.setWidths(new float[]{3f, 2.3f, 2f, 2f});
        tabla.setWidthPercentage(110);

        cabecera(tabla);
        escribirDatosDeLaTabla(tabla);

        // Agregar la fecha al final de la tabla
        LocalDate fechaActual = LocalDate.now();
        PdfPCell celdaFecha = new PdfPCell(new Phrase("Fecha de exportación: " + fechaActual.toString()));
        celdaFecha.setColspan(4);
        celdaFecha.setHorizontalAlignment(Element.ALIGN_RIGHT);
        celdaFecha.setBorder(Rectangle.NO_BORDER);
        tabla.addCell(celdaFecha);

        document.add(tabla);
        document.close();
    }
}
