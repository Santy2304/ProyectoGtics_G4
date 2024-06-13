package com.example.proyectogrupo4_gtics.Reportes;

import com.example.proyectogrupo4_gtics.DTOs.CantidadMedicamentosDTO;
import com.example.proyectogrupo4_gtics.Entity.Administrator;
import com.example.proyectogrupo4_gtics.Entity.Pharmacist;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.List;

public class FarmacistaExcel {
    private XSSFWorkbook libro;
    private XSSFSheet hoja;

    private List<Pharmacist> listaFarmacistas;

    public FarmacistaExcel(List<Pharmacist> listaFarmacistas) {
        this.listaFarmacistas = listaFarmacistas;
        libro = new XSSFWorkbook();
        hoja = libro.createSheet("Farmacistas");
    }

    private void cabeceraTabla(){
        Row fila = hoja.createRow(0);
        CellStyle estilo = libro.createCellStyle();
        XSSFFont fuente = libro.createFont();
        fuente.setBold(true);
        fuente.setFontHeight(16);
        estilo.setFont(fuente);

        Cell celda = fila.createCell(0);
        celda.setCellValue("ID");
        celda.setCellStyle(estilo);

        celda = fila.createCell(1);
        celda.setCellValue("Nombre");
        celda.setCellStyle(estilo);

        celda = fila.createCell(2);
        celda.setCellValue("Apellido");
        celda.setCellStyle(estilo);

        celda = fila.createCell(3);
        celda.setCellValue("Sede");
        celda.setCellStyle(estilo);

        celda = fila.createCell(4);
        celda.setCellValue("Correo");
        celda.setCellStyle(estilo);

        celda = fila.createCell(5);
        celda.setCellValue("Fecha de Creación");
        celda.setCellStyle(estilo);

        celda = fila.createCell(6);
        celda.setCellValue("Estado");
        celda.setCellStyle(estilo);

    }

    public void datosTabla() {
        int nueroFilas = 1;

        CellStyle estilo = libro.createCellStyle();
        XSSFFont fuente = libro.createFont();
        fuente.setFontHeight(14);
        estilo.setFont(fuente);

        for (Pharmacist pharmacist : listaFarmacistas){
            Row fila = hoja.createRow(nueroFilas ++);

            Cell celda = fila.createCell(0);
            celda.setCellValue(pharmacist.getIdFarmacista());
            hoja.autoSizeColumn(0);
            celda.setCellStyle(estilo);

            celda = fila.createCell(1);
            celda.setCellValue(pharmacist.getName());
            hoja.autoSizeColumn(1);
            celda.setCellStyle(estilo);

            celda = fila.createCell(2);
            celda.setCellValue(pharmacist.getLastName());
            hoja.autoSizeColumn(2);
            celda.setCellStyle(estilo);

            celda = fila.createCell(3);
            celda.setCellValue(pharmacist.getSite());
            hoja.autoSizeColumn(3);
            celda.setCellStyle(estilo);

            celda = fila.createCell(4);
            celda.setCellValue(pharmacist.getEmail());
            hoja.autoSizeColumn(4);
            celda.setCellStyle(estilo);

            celda = fila.createCell(5);
            celda.setCellValue(pharmacist.getCreationDate().toString());
            hoja.autoSizeColumn(5);
            celda.setCellStyle(estilo);

            celda = fila.createCell(6);
            celda.setCellValue(pharmacist.getState());
            hoja.autoSizeColumn(6);
            celda.setCellStyle(estilo);

        }
    }
    public void exportar(HttpServletResponse response) throws IOException {
        cabeceraTabla();
        datosTabla();

        ServletOutputStream outPutStream = response.getOutputStream();
        libro.write(outPutStream);

        libro.close();
        outPutStream.close();
    }
}
