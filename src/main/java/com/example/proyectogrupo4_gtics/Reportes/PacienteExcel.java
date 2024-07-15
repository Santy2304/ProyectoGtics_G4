package com.example.proyectogrupo4_gtics.Reportes;

import com.example.proyectogrupo4_gtics.Entity.Administrator;
import com.example.proyectogrupo4_gtics.Entity.Patient;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class PacienteExcel {
    private XSSFWorkbook libro;
    private XSSFSheet hoja;
    private List<Patient> listaPatients;
    private String titulo;

    public PacienteExcel(List<Patient> listaPatients, String titulo) {
        this.titulo = titulo;
        this.listaPatients = listaPatients;
        libro = new XSSFWorkbook();
        hoja = libro.createSheet("Pacientes");
    }
    private void crearTitulo() {
        Row filaTitulo = hoja.createRow(0);
        Cell celdaTitulo = filaTitulo.createCell(0);
        celdaTitulo.setCellValue(titulo);

        CellStyle estiloTitulo = libro.createCellStyle();
        XSSFFont fuenteTitulo = libro.createFont();
        fuenteTitulo.setBold(true);
        fuenteTitulo.setFontHeight(16);
        estiloTitulo.setFont(fuenteTitulo);
        estiloTitulo.setAlignment(HorizontalAlignment.CENTER);
        estiloTitulo.setVerticalAlignment(VerticalAlignment.CENTER);

        celdaTitulo.setCellStyle(estiloTitulo);

        // Combinar celdas para el título
        hoja.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
    }

    private void cabeceraTabla(){
        Row fila = hoja.createRow(1);
        CellStyle estilo = libro.createCellStyle();
        XSSFFont fuente = libro.createFont();
        fuente.setBold(true);
        fuente.setFontHeight(11);
        estilo.setFont(fuente);
        estilo.setAlignment(HorizontalAlignment.CENTER);
        estilo.setVerticalAlignment(VerticalAlignment.CENTER);

        estilo.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        estilo.setBorderBottom(BorderStyle.THIN);
        estilo.setBorderTop(BorderStyle.THIN);
        estilo.setBorderLeft(BorderStyle.THIN);
        estilo.setBorderRight(BorderStyle.THIN);

        String[] cabeceras = {"Nombre", "Apellido", "Seguro", "Correo", "Fecha de Registro", "Estado"};
        for (int i = 0; i < cabeceras.length; i++) {
            Cell celda = fila.createCell(i);
            celda.setCellValue(cabeceras[i]);
            celda.setCellStyle(estilo);
            hoja.autoSizeColumn(i);
        }

    }

    public void datosTabla() {
        int numeroFilas = 2;

        CellStyle estilo = libro.createCellStyle();
        XSSFFont fuente = libro.createFont();
        fuente.setFontHeight(11);
        estilo.setFont(fuente);

        estilo.setAlignment(HorizontalAlignment.CENTER);
        estilo.setVerticalAlignment(VerticalAlignment.CENTER);

        // Aplicar bordes
        estilo.setBorderBottom(BorderStyle.THIN);
        estilo.setBorderTop(BorderStyle.THIN);
        estilo.setBorderLeft(BorderStyle.THIN);
        estilo.setBorderRight(BorderStyle.THIN);

        for (Patient patient : listaPatients){
            Row fila = hoja.createRow(numeroFilas ++);

            Cell celda = fila.createCell(0);
            celda.setCellValue(patient.getName());
            celda.setCellStyle(estilo);

            celda = fila.createCell(1);
            celda.setCellValue(patient.getLastName());
            celda.setCellStyle(estilo);

            celda = fila.createCell(2);
            celda.setCellValue(patient.getInsurance());
            celda.setCellStyle(estilo);

            celda = fila.createCell(3);
            celda.setCellValue(patient.getEmail());
            celda.setCellStyle(estilo);

            celda = fila.createCell(4);
            celda.setCellValue(patient.getDateCreationAccount().toString());
            celda.setCellStyle(estilo);

            celda = fila.createCell(5);
            celda.setCellValue(patient.getState());
            celda.setCellStyle(estilo);

        }
        // Ajustar el tamaño de las columnas al contenido
        for (int i = 0; i < 6; i++) {
            hoja.autoSizeColumn(i);
        }

        // Añadir fecha actual
        LocalDate fechaActual = LocalDate.now();
        Row filaFecha = hoja.createRow(numeroFilas + 1);
        Cell celdaFecha = filaFecha.createCell(0);
        celdaFecha.setCellValue("Fecha de exportación: " + fechaActual.toString());
        // Aplicar estilo a la celda de fecha
        CellStyle estiloFecha = libro.createCellStyle();
        XSSFFont fuenteFecha = libro.createFont();
        fuenteFecha.setFontHeight(11);
        estiloFecha.setFont(fuenteFecha);
        celdaFecha.setCellStyle(estiloFecha);
        hoja.addMergedRegion(new CellRangeAddress(numeroFilas + 1, numeroFilas + 1, 0, 5));
    }
    public void exportar(HttpServletResponse response) throws IOException {
        crearTitulo();
        cabeceraTabla();
        datosTabla();

        ServletOutputStream outPutStream = response.getOutputStream();
        libro.write(outPutStream);

        libro.close();
        outPutStream.close();
    }
}
