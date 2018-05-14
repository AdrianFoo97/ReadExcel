
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author a80052136
 */
public class displayInfo extends javax.swing.JFrame {
    private ArrayList<BIS> bisList = new ArrayList<>();
    private ArrayList<BIS> filteredBisList = new ArrayList<>();
    private DefaultListModel listModel;
    private LinkedHashSet<String> filtered;
    private ArrayList<BIS> passList = new ArrayList<>();
    private detailsTableModel dtm;
    private ArrayList<Subcontractor> conList;
    private ArrayList<String> summaryList;
    /**
     * Creates new form displayInfo
     */
    public displayInfo(ArrayList<BIS> bisList) {
        initComponents();
        this.bisList = bisList;
        this.conList = conList;
        
        setupTableModel();
        getPenSubcon();
        setupListModel();
        centreWindow(this);
    }
    
    // This function will centre the window
    private void centreWindow(Window frame) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
    }
    
    // setting the table model
    public void setupTableModel() {
        dtm = new detailsTableModel(passList);
        detailsTable.setModel(dtm);
    }
    
    // adding the pending subcontractor to the list model
    public void setupListModel () {
        listModel = new DefaultListModel();
        pendingList.setModel(listModel);
        pendingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // adding pending subcontractor into the list 
        for (String str: filtered) {
            listModel.addElement(str);
        }
        // adding listener to pending list
        pendingList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if (!lse.getValueIsAdjusting()) {
                    int index = pendingList.getSelectedIndex();
                    passList = new ArrayList<>();
                    for (BIS bis: filteredBisList) {
                        if (bis.getSubcontractor().equalsIgnoreCase(
                                (String)listModel.getElementAt(index))) {
                            passList.add(bis);
                        }
                    }
                    setupTableModel();
                    setSummaryLbl();
                }
            }
            
        });
    }

    // filter the arraylist to get the pending subcontractor
    public void getPenSubcon() {
        filtered = new LinkedHashSet();
        filteredBisList = new ArrayList<>();
        for (BIS bis: bisList) {
            if (bis.getStepSubmitted() == "null") {
                filtered.add(bis.getSubcontractor());
                filteredBisList.add(bis);
            }
            else if (!bis.getRejected().equalsIgnoreCase("null")) {
                if (bis.getResubmitted().equalsIgnoreCase("null")) {
                    filtered.add(bis.getSubcontractor());
                    filteredBisList.add(bis);
                }
            }
        }
    }
    
    public void setSummaryLbl() {
        String forSub = Integer.toString(countPenSub("1-FOP"));
        String optiSub = Integer.toString(countPenSub("2-OPTI"));
        String bstrSub = Integer.toString(countPenSub("3-BSTR"));
        String fmSub = Integer.toString(countPenSub("4-FM"));
        String prefixSub = Integer.toString(countPenSub("5-PREFIX"));
        
        fopSubLbl.setText(forSub);
        optiSubLbl.setText(optiSub);
        bstrSubLbl.setText(bstrSub);
        fmSubLbl.setText(fmSub);
        prefixSubLbl.setText(prefixSub);
        
        String fopResub = Integer.toString(countPenResub("1-FOP"));
        String optiResub = Integer.toString(countPenResub("2-OPTI"));
        String bstrResub = Integer.toString(countPenResub("3-BSTR"));
        String fmResub = Integer.toString(countPenResub("4-FM"));
        String prefixResub = Integer.toString(countPenResub("5-PREFIX"));
        
        fopResubLbl.setText(fopResub);
        optiResubLbl.setText(optiResub);
        bstrResubLbl.setText(bstrResub);
        fmResubLbl.setText(fmResub);
        prefixResubLbl.setText(prefixResub);
        
        summaryList = new ArrayList<>();
        summaryList.add(forSub);
        summaryList.add(fopResub);
        summaryList.add(optiSub);
        summaryList.add(optiResub);
        summaryList.add(bstrSub);
        summaryList.add(bstrResub);
        summaryList.add(fmSub);
        summaryList.add(fmResub);
        summaryList.add(prefixSub);
        summaryList.add(prefixResub);
         
    }
    

    public int countPenSub(String category) {
        int total = 0;
        for (BIS bis: passList) {
            if (bis.stepSubmitted.equalsIgnoreCase("null") &&
                    bis.getCategory().equalsIgnoreCase(category)) {
                total += 1;
            }
        }
        return total;
    }
    
    public int countPenResub(String category) {
        int total = 0;
        for (BIS bis: passList) {
            if (!bis.getRejected().equalsIgnoreCase("null") &&
                    bis.getCategory().equalsIgnoreCase(category) &&
                    bis.getResubmitted().equalsIgnoreCase("null")) {
                total += 1;
            }
        }
        return total;
    }
    
    public void exportExcel(ArrayList<BIS> inList) {
        String[] columns = {"DU ID Scope", "Acceptance Plan", "Acceptance Step New Category",
            "Acceptance Step Name", "Subcontractor", "On Air Actual End Date", "BIS Date", "Step Status", 
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
            row.createCell(5).setCellValue(bis.getOnADate());
            row.createCell(6).setCellValue(bis.getBisDate());
            row.createCell(7).setCellValue(bis.getStepStatus());
            row.createCell(8).setCellValue(bis.getStepInitiated());
            row.createCell(9).setCellValue(bis.getStepSubmitted());
            row.createCell(10).setCellValue(bis.getRejected());
            row.createCell(11).setCellValue(bis.getResubmitted());
            row.createCell(12).setCellValue(bis.getStepAccepted());
            
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
        } catch (IOException ex) {
            Logger.getLogger(displayInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        pendingList = new javax.swing.JList<>();
        jLabel2 = new javax.swing.JLabel();
        sendEmailBtn = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        fopSubLbl = new javax.swing.JLabel();
        fopResubLbl = new javax.swing.JLabel();
        optiSubLbl = new javax.swing.JLabel();
        optiResubLbl = new javax.swing.JLabel();
        bstrSubLbl = new javax.swing.JLabel();
        bstrResubLbl = new javax.swing.JLabel();
        fmSubLbl = new javax.swing.JLabel();
        fmResubLbl = new javax.swing.JLabel();
        prefixSubLbl = new javax.swing.JLabel();
        prefixResubLbl = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        detailsTable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        exportBtn = new javax.swing.JButton();
        exportAllBtn = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pendingList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(pendingList);

        jLabel2.setText("Pending Subcontractor:");

        sendEmailBtn.setText("Send Email");
        sendEmailBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendEmailBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 605, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 331, Short.MAX_VALUE)
                .addComponent(sendEmailBtn)
                .addGap(209, 209, 209))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(23, 23, 23)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sendEmailBtn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel3.setText("Summary:  ");

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jLabel9.setText("Pending Submission");

        jLabel10.setText("Pending Resubmission");

        fopSubLbl.setText("0");

        fopResubLbl.setText("0");

        optiSubLbl.setText("0");

        optiResubLbl.setText("0");

        bstrSubLbl.setText("0");

        bstrResubLbl.setText("0");

        fmSubLbl.setText("0");

        fmResubLbl.setText("0");

        prefixSubLbl.setText("0");

        prefixResubLbl.setText("0");

        jLabel4.setText("1-FOP");

        jLabel5.setText("2-OPIT");

        jLabel6.setText("3-BSTR");

        jLabel7.setText("4-FM");

        jLabel8.setText("5-PREFIX");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(jLabel9))
                .addGap(38, 38, 38)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fopSubLbl)
                    .addComponent(fopResubLbl)
                    .addComponent(jLabel4))
                .addGap(69, 69, 69)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(optiSubLbl)
                    .addComponent(optiResubLbl)
                    .addComponent(jLabel5))
                .addGap(76, 76, 76)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bstrSubLbl)
                    .addComponent(bstrResubLbl)
                    .addComponent(jLabel6))
                .addGap(55, 55, 55)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(fmSubLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
                            .addComponent(fmResubLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(66, 66, 66)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(prefixSubLbl)
                            .addComponent(prefixResubLbl)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(78, 78, 78)
                        .addComponent(jLabel8)))
                .addContainerGap(45, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addGap(19, 19, 19)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(optiSubLbl)
                    .addComponent(fopSubLbl)
                    .addComponent(jLabel9)
                    .addComponent(bstrSubLbl)
                    .addComponent(fmSubLbl)
                    .addComponent(prefixSubLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(optiResubLbl)
                    .addComponent(fopResubLbl)
                    .addComponent(jLabel10)
                    .addComponent(bstrResubLbl)
                    .addComponent(fmResubLbl)
                    .addComponent(prefixResubLbl))
                .addContainerGap(33, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 70, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        detailsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        detailsTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(detailsTable);

        jLabel1.setText("Details:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1518, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 489, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        exportBtn.setText("Export");
        exportBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportBtnActionPerformed(evt);
            }
        });

        exportAllBtn.setText("Export All");
        exportAllBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportAllBtnActionPerformed(evt);
            }
        });

        jButton1.setText("Close");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
            .addGroup(layout.createSequentialGroup()
                .addGap(671, 671, 671)
                .addComponent(exportBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(exportAllBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(exportAllBtn)
                    .addComponent(jButton1)
                    .addComponent(exportBtn))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void sendEmailBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendEmailBtnActionPerformed
        if (pendingList.getSelectedValue() != null) {
            sendEmail se = new sendEmail(conList, pendingList.getSelectedValue(), 
            summaryList);
            se.setVisible(true);
        } 
    }//GEN-LAST:event_sendEmailBtnActionPerformed

    private void exportBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportBtnActionPerformed
        exportExcel(passList);
        JOptionPane.showMessageDialog(this, "Exported successfully.",
                        "Message", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_exportBtnActionPerformed

    private void exportAllBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportAllBtnActionPerformed
        for (String subcon: filtered) {
            ArrayList<BIS> exportList = new ArrayList<>();
            for (BIS bis: filteredBisList) {
                if (bis.getSubcontractor().equalsIgnoreCase(subcon)) {
                    exportList.add(bis);
                }
            }
            exportExcel(exportList);
        }
        JOptionPane.showMessageDialog(this, "Exported successfully.",
                        "Message", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_exportAllBtnActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.dispose();
        MainPage mp = new MainPage();
        mp.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel bstrResubLbl;
    private javax.swing.JLabel bstrSubLbl;
    private javax.swing.JTable detailsTable;
    private javax.swing.JButton exportAllBtn;
    private javax.swing.JButton exportBtn;
    private javax.swing.JLabel fmResubLbl;
    private javax.swing.JLabel fmSubLbl;
    private javax.swing.JLabel fopResubLbl;
    private javax.swing.JLabel fopSubLbl;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel optiResubLbl;
    private javax.swing.JLabel optiSubLbl;
    private javax.swing.JList<String> pendingList;
    private javax.swing.JLabel prefixResubLbl;
    private javax.swing.JLabel prefixSubLbl;
    private javax.swing.JButton sendEmailBtn;
    // End of variables declaration//GEN-END:variables
}
