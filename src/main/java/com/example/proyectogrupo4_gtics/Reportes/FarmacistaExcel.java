package com.example.proyectogrupo4_gtics.Reportes;

import com.example.proyectogrupo4_gtics.Entity.Pharmacist;
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

public class FarmacistaExcel {
    private XSSFWorkbook libro;
    private XSSFSheet hoja;

    private List<Pharmacist> listaFarmacistas;
    private String titulo;

    public FarmacistaExcel(List<Pharmacist> listaFarmacistas, String titulo) {
        this.listaFarmacistas = listaFarmacistas;
        this.titulo = titulo;
        libro = new XSSFWorkbook();
        hoja = libro.createSheet("Farmacistas");
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

        // Color de fondo naranja claro
        estilo.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Aplicar bordes
        estilo.setBorderBottom(BorderStyle.THIN);
        estilo.setBorderTop(BorderStyle.THIN);
        estilo.setBorderLeft(BorderStyle.THIN);
        estilo.setBorderRight(BorderStyle.THIN);


        String[] cabeceras = {"Nombre", "Apellido", "Sede", "Correo", "Fecha de Creación", "Estado"};

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
        fuente.setFontHeight(14);
        estilo.setFont(fuente);

        for (Pharmacist pharmacist : listaFarmacistas){
            Row fila = hoja.createRow(numeroFilas ++);

            Cell celda = fila.createCell(0);
            celda.setCellValue(pharmacist.getName());
            celda.setCellStyle(estilo);

            celda = fila.createCell(1);
            celda.setCellValue(pharmacist.getLastName());
            celda.setCellStyle(estilo);

            celda = fila.createCell(2);
            celda.setCellValue(pharmacist.getSite());
            celda.setCellStyle(estilo);

            celda = fila.createCell(3);
            celda.setCellValue(pharmacist.getEmail());
            celda.setCellStyle(estilo);

            celda = fila.createCell(4);
            celda.setCellValue(pharmacist.getCreationDate().toString());
            celda.setCellStyle(estilo);

            celda = fila.createCell(5);
            celda.setCellValue(pharmacist.getState());
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

        // Combinar celdas para la fecha
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
