package com.example.proyectogrupo4_gtics.Reportes;

import com.example.proyectogrupo4_gtics.DTOs.CantidadMedicamentosDTO;
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

public class MedicineExcel {

    private XSSFWorkbook libro;
    private XSSFSheet hoja;

    private List<CantidadMedicamentosDTO> listaMedicamentos;

    public MedicineExcel(List<CantidadMedicamentosDTO> listaMedicamentos) {
        this.listaMedicamentos = listaMedicamentos;
        libro = new XSSFWorkbook();
        hoja = libro.createSheet("Medicamentos");
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
        celda.setCellValue("Nombre del producto");
        celda.setCellStyle(estilo);

        celda = fila.createCell(2);
        celda.setCellValue("Categoría");
        celda.setCellStyle(estilo);

        celda = fila.createCell(3);
        celda.setCellValue("Cantidad");
        celda.setCellStyle(estilo);

        celda = fila.createCell(4);
        celda.setCellValue("Precio (s/.)");
        celda.setCellStyle(estilo);

    }

    public void datosTabla() {
        int nueroFilas = 1;

        CellStyle estilo = libro.createCellStyle();
        XSSFFont fuente = libro.createFont();
        fuente.setFontHeight(14);
        estilo.setFont(fuente);

        for (CantidadMedicamentosDTO medicamentosDTO : listaMedicamentos){
            Row fila = hoja.createRow(nueroFilas ++);

            Cell celda = fila.createCell(0);
            celda.setCellValue(medicamentosDTO.getIdMedicine());
            hoja.autoSizeColumn(0);
            celda.setCellStyle(estilo);

            celda = fila.createCell(1);
            celda.setCellValue(medicamentosDTO.getNombreMedicamento());
            hoja.autoSizeColumn(1);
            celda.setCellStyle(estilo);

            celda = fila.createCell(2);
            celda.setCellValue(medicamentosDTO.getCategoria());
            hoja.autoSizeColumn(2);
            celda.setCellStyle(estilo);

            celda = fila.createCell(3);
            celda.setCellValue(medicamentosDTO.getCantidad());
            hoja.autoSizeColumn(3);
            celda.setCellStyle(estilo);

            celda = fila.createCell(4);
            celda.setCellValue(medicamentosDTO.getPrecio());
            hoja.autoSizeColumn(4);
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
