package com.example.proyectogrupo4_gtics.Reportes;
import com.example.proyectogrupo4_gtics.Entity.Doctor;
import com.example.proyectogrupo4_gtics.Entity.ReplacementOrder;
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
public class RepoExcel {
    private XSSFWorkbook libro;
    private XSSFSheet hoja;
    private List<ReplacementOrder> listaReporte;
    private String titulo;
    public RepoExcel(List<ReplacementOrder> listaReporte, String titulo) {
        this.titulo = titulo;
        this.listaReporte = listaReporte;
        libro = new XSSFWorkbook();
        hoja = libro.createSheet("Ordenes_de_Reposición");
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
        hoja.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));
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

        String[] cabeceras = {"ID", "Fecha de Emisión", "Estado"};

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

        for (ReplacementOrder r : listaReporte) {
            Row fila = hoja.createRow(numeroFilas++);

            Cell celda = fila.createCell(0);
            celda.setCellValue("RP0" + r.getIdReplacementOrder());
            celda.setCellStyle(estilo);

            celda = fila.createCell(1);
            celda.setCellValue(r.getReleaseDate().toString());
            celda.setCellStyle(estilo);

            celda = fila.createCell(2);
            celda.setCellValue(r.getTrackingState());
            celda.setCellStyle(estilo);


        }
        // Ajustar el tamaño de las columnas al contenido
        for (int i = 0; i < 3; i++) {
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
        hoja.addMergedRegion(new CellRangeAddress(numeroFilas + 1, numeroFilas + 1, 0, 3));

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
