package com.example.proyectogrupo4_gtics.Reportes;

import com.example.proyectogrupo4_gtics.Entity.Administrator;
import com.example.proyectogrupo4_gtics.Entity.Patient;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class PacientePDF {
    private List<Patient> listaPatients;

    public PacientePDF(List<Patient> listaPatients) {
        super();
        this.listaPatients = listaPatients;
    }

    private void Cabecera(PdfPTable tabla){
        PdfPCell celda = new PdfPCell();
        celda.setBackgroundColor(Color.orange);
        celda.setPadding(5);
        com.lowagie.text.Font fuente = FontFactory.getFont(FontFactory.HELVETICA);
        fuente.setColor(Color.white);

        celda.setPhrase(new Phrase("ID", fuente));
        tabla.addCell(celda);

        celda.setPhrase(new Phrase("Nombre", fuente));
        tabla.addCell(celda);

        celda.setPhrase(new Phrase("Apellido", fuente));
        tabla.addCell(celda);

        celda.setPhrase(new Phrase("Seguro", fuente));
        tabla.addCell(celda);

        celda.setPhrase(new Phrase("Correo", fuente));
        tabla.addCell(celda);

        celda.setPhrase(new Phrase("Fecha de Registro", fuente));
        tabla.addCell(celda);

        celda.setPhrase(new Phrase("Estado", fuente));
        tabla.addCell(celda);

    }
    private void escribirDatosDeLaTabla(PdfPTable tabla) {
        for (Patient patient : listaPatients) {
            tabla.addCell(String.valueOf(patient.getIdPatient()));
            tabla.addCell(patient.getName());
            tabla.addCell(patient.getLastName());
            tabla.addCell(patient.getInsurance());
            tabla.addCell(patient.getEmail());
            tabla.addCell(patient.getDateCreationAccount().toString());
            tabla.addCell(patient.getState());
        }
    }

    public void exportar(HttpServletResponse response) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font fuente = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fuente.setColor(Color.ORANGE);
        fuente.setSize(18);

        Paragraph titulo = new Paragraph("Lista de Pacientes",fuente);
        titulo.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(titulo);
        PdfPTable tabla = new PdfPTable(7);
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(15);
        tabla.setWidths(new float[] { 1f, 3f, 2.3f, 2f, 3f,3f,1.2f});
        tabla.setWidthPercentage(110);

        Cabecera(tabla);
        escribirDatosDeLaTabla(tabla);

        document.add(tabla);
        document.close();

    }
}
