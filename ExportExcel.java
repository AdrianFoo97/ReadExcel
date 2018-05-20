
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author a80052136
 */
public class ExportExcel {
    // Export the bis table to excel file
    public static boolean export(ArrayList<BIS> inList) {
        String[] columns = {"DU ID Scope", "Acceptance Plan", "Acceptance Step New Category",
            "Acceptance Step Name", "Subcontractor", "Sites Label","On Air Actual End Date", "BIS Date", "Step Status", 
            "Step Initiated", "Step Submitted", "Step Rejected", "Resubmitted", 
            "Step Accepted"};
        
        // Create a Workbook
        Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file

        /* CreationHelper helps us create instances for various things like DataFormat, 
           Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way */
        CreationHelper createHelper = workbook.getCreationHelper();

        // Create a Sheet
        Sheet sheet = workbook.createSheet("Sheet1");

        // Create a Font for styling header cells
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 11);
        headerFont.setColor(IndexedColors.BLACK.getIndex());

        // Create a CellStyle with the font
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        // Create a Row
        Row headerRow = sheet.createRow(0);

        // Creating cells
        for(int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }

        // Create Cell Style for formatting Date
        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));

        // Create Other rows and cells with employees data
        int rowNum = 1;
        String subcon = "";
        for (BIS bis: inList) {
            Row row = sheet.createRow(rowNum++);
            
            row.createCell(0).setCellValue(bis.getIdScope());
            row.createCell(1).setCellValue(bis.getAcceptancePlan());
            row.createCell(2).setCellValue(bis.getCategory());
            row.createCell(3).setCellValue(bis.getStepName());
            row.createCell(4).setCellValue(bis.getSubcontractor());
            row.createCell(5).setCellValue(bis.getSite());
            row.createCell(6).setCellValue(bis.getOnADate());
            row.createCell(7).setCellValue(bis.getBisDate());
            row.createCell(8).setCellValue(bis.getStepStatus());
            row.createCell(9).setCellValue(bis.getStepInitiated());
            row.createCell(10).setCellValue(bis.getStepSubmitted());
            row.createCell(11).setCellValue(bis.getRejected());
            row.createCell(12).setCellValue(bis.getResubmitted());
            row.createCell(13).setCellValue(bis.getStepAccepted());
            
            subcon = bis.getSubcontractor();
        }
        
        // Resize all columns to fit the content size
        for(int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write the output to a file
        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(subcon + ".xlsx");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(displayInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            workbook.write(fileOut);
        } catch (IOException ex) {
            Logger.getLogger(displayInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            fileOut.close();
        } catch (IOException ex) {
            Logger.getLogger(displayInfo.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            // Closing the workbook
            workbook.close();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(displayInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
