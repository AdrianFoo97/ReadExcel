
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
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author a80052136
 */
public class displayInfo extends javax.swing.JFrame {
    // Full list of the bis report
    private ArrayList<BIS> bisList = new ArrayList<>();
    // Full list of the pending bis report
    private ArrayList<BIS> filteredBisList = new ArrayList<>();
    private DefaultListModel listModel;
    // Full list of pending subcontractor without duplicate data
    private LinkedHashSet<String> filtered;
    // Full list of pending bis report of a subcontracor
    private ArrayList<BIS> passList = new ArrayList<>();
    private detailsTableModel dtm;
    /**
     * Creates new form displayInfo
     */
    public displayInfo(ArrayList<BIS> bisList) {
        initComponents();
        this.bisList = bisList;
        setResizable(false);
        
        setupTableModel();
        getPenSubcon();
        setupListModel();
        centreWindow(this);
        setupSummaryTable(passList);
    }
    // Setting the summary table 
    private void setupSummaryTable(ArrayList<BIS> passList) {
        String column[] = {"", "1-FOP", "2-OPTI", "3-BSTR", "4-FM", "5-Prefix"};
        String data[][] = getSummaryTable(passList);
        DefaultTableModel model = new DefaultTableModel(data, column);
        summaryTable.setModel(model);
        summaryTable.setRowHeight(90);
        summaryTable.setFillsViewportHeight(true);
        summaryTable.setRowSelectionAllowed(false);
        summaryTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        summaryTable.getTableHeader().setResizingAllowed(false);
    }
    
    // This function will centre the window
    private void centreWindow(Window frame) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
    }
    
    // setting the table model
    private void setupTableModel() {
        dtm = new detailsTableModel(passList);
        detailsTable.setModel(dtm);
    }
    
    // adding the pending subcontractor to the list model
    private void setupListModel () {
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
                    setupSummaryTable(passList);
                }
            }
            
        });
    }

    
    // filter the arraylist to get the pending subcontractor and bis object
    private void getPenSubcon() {
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
    
    /**
     * Get all the summary for all the bis
     * @return the summary for all bis status
     */
    private String[][] getSummaryTable(ArrayList<BIS> passList) {
        String fopSub = Integer.toString(countPenSub("1-FOP", passList));
        String optiSub = Integer.toString(countPenSub("2-OPTI", passList));
        String bstrSub = Integer.toString(countPenSub("3-BSTR", passList));
        String fmSub = Integer.toString(countPenSub("4-FM", passList));
        String prefixSub = Integer.toString(countPenSub("5-PREFIX", passList));

        String fopResub = Integer.toString(countPenResub("1-FOP", passList));
        String optiResub = Integer.toString(countPenResub("2-OPTI", passList));
        String bstrResub = Integer.toString(countPenResub("3-BSTR", passList));
        String fmResub = Integer.toString(countPenResub("4-FM", passList));
        String prefixResub = Integer.toString(countPenResub("5-PREFIX", passList));
        
        String data [][] = {
            {"Pending Submission", fopSub, optiSub, bstrSub, fmSub, prefixSub},
            {"Pending Resubmission", fopResub, optiResub, bstrResub, fmResub, prefixResub}
        };
        return data;
    }
    
    private String getSummaryStr(ArrayList<BIS> passList) {
        String fopSub = Integer.toString(countPenSub("1-FOP", passList));
        String optiSub = Integer.toString(countPenSub("2-OPTI", passList));
        String bstrSub = Integer.toString(countPenSub("3-BSTR", passList));
        String fmSub = Integer.toString(countPenSub("4-FM", passList));
        String prefixSub = Integer.toString(countPenSub("5-PREFIX", passList));

        String fopResub = Integer.toString(countPenResub("1-FOP", passList));
        String optiResub = Integer.toString(countPenResub("2-OPTI", passList));
        String bstrResub = Integer.toString(countPenResub("3-BSTR", passList));
        String fmResub = Integer.toString(countPenResub("4-FM", passList));
        String prefixResub = Integer.toString(countPenResub("5-PREFIX", passList));
        
        String strReturn = "Below are the latest BIS Report submission status:\n\n"
                + "1-FOP\t\tPending\t\t" + fopSub + "\t\tSubmission\t\t" + 
                fopResub + "\t\tResubmission\n" +
                "2-OPTI\t\tPending\t\t" + optiSub + "\t\tSubmission\t\t" + 
                optiResub + "\t\tResubmission\n" +
                "3-BSTR\t\tPending\t\t" + bstrSub + "\t\tSubmission\t\t" + 
                bstrResub + "\t\tResubmission\n" +
                "4-FM\t\tPending\t\t" + fmSub + "\t\tSubmission\t\t" + 
                fmResub + "\t\tResubmission\n" +
                "5-PREFIX\t\tPending\t\t" + prefixSub + "\t\tSubmission\t\t" + 
                prefixResub + "\t\tResubmission\n\n" +
                "Attachment can find the details acceptance plan information.\n\n" + 
                "Thank you.";
        
        return strReturn;
    }
    
    // Count the total pending submission for a category
    private int countPenSub(String category, ArrayList<BIS> passList) {
        int total = 0;
        for (BIS bis: passList) {
            if (bis.stepSubmitted.equalsIgnoreCase("null") &&
                    bis.getCategory().equalsIgnoreCase(category)) {
                total += 1;
            }
        }
        return total;
    }
    
    // Count the total pending resubmission for a category
    private int countPenResub(String category, ArrayList<BIS> passList) {
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
    
    // Export the bis table to excel file
    private void exportExcel(ArrayList<BIS> inList) {
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
    
    private boolean sendEmail(String summaryStr, String subcon, String email) {
        try{
            String host ="smtpscn.huawei.com" ;
            String user = "a80052136";
            String pass = "HelpB1600693!";
            // Recipient's email ID
            String to = "adrianf.wei@huawei.com";
            // Sender's email ID
            String from = "adrianf.wei@huawei.com";
            String subject = "BIS Report Submission Status (" + email + ")";
            String messageText = summaryStr;
            boolean sessionDebug = false;

            Properties props = System.getProperties(); // to set different type of properties

            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.smtp.ssl.trust", "smtpscn.huawei.com");

            java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            Session mailSession = Session.getDefaultInstance(props, null);
            mailSession.setDebug(sessionDebug);
            
            // Create a default MimeMessage oject
            Message msg = new MimeMessage(mailSession);
            
            // Set From: heder field of the header
            msg.setFrom(new InternetAddress(from));
            
            // Set To: header filed of the header
            InternetAddress[] address = {new InternetAddress(to)}; // address of sender
            msg.setRecipients(Message.RecipientType.TO, address); // receiver email
            
            // Set Subject: header field
            msg.setSubject(subject); 
            msg.setSentDate(new Date()); // message send date
            
            // Create the message part
            BodyPart msgBodyPart = new MimeBodyPart();
            
            // Now set the actual message
            msgBodyPart.setText(messageText);
            
            // Create a multipar message
            Multipart multipart = new MimeMultipart();
            
            // Set text message part
            multipart.addBodyPart(msgBodyPart);
            
            // Part two is attachment
            msgBodyPart = new MimeBodyPart();
            String filename = "C:\\Users\\a80052136\\Documents\\NetBeansProjects\\sendMessage\\" + 
                    subcon + ".xlsx";
            DataSource source = new FileDataSource(filename);
            msgBodyPart.setDataHandler(new DataHandler(source));
            msgBodyPart.setFileName(source.getName());
            //msgBodyPart.setFileName(filename);
            multipart.addBodyPart(msgBodyPart);

            // Send the complete message parts
            msg.setContent(multipart);
                       
            //msg.setText(messageText); // actual message

           Transport transport=mailSession.getTransport("smtp"); //server through which we are going to send msg
           transport.connect(host, user, pass); // we need because we have to authenticate sender email and password
           transport.sendMessage(msg, msg.getAllRecipients());
           transport.close();
           System.out.println("message send successfully");
           return true;
        }catch(Exception ex)
        {
            System.out.println(ex); // if any error occur then error message will print
        }
        return false;
    }
    
    private String getSubconEmail(String subconName) {
        String subconNameTrim = subconName.replaceAll("[^\\p{L}\\p{Nd}]+", "");
        String emailReturn = null;
        SubcontractorDA da = new SubcontractorDA();
        ArrayList<Subcontractor> conList = da.getAllSubcontractor();
        for (Subcontractor s: conList) {
            String nameGet = s.getName().replaceAll("[^\\p{L}\\p{Nd}]+", "");
            if (nameGet.equalsIgnoreCase(subconNameTrim)) {
                emailReturn = s.getEmail();
            }
        }
        return emailReturn;
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
        sendAllBtn = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        summaryTable = new javax.swing.JTable();
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

        sendAllBtn.setText("Send Email To All");
        sendAllBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendAllBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 605, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(228, 228, 228)
                        .addComponent(sendEmailBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sendAllBtn)))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sendEmailBtn)
                    .addComponent(sendAllBtn))
                .addGap(36, 36, 36))
        );

        jLabel3.setText("Summary:  ");

        summaryTable.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        summaryTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "1-FOP", "2-OPTI", "3-BSTR", "4-FM", "5-PREFIX"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        summaryTable.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        summaryTable.setFillsViewportHeight(true);
        summaryTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(summaryTable);
        if (summaryTable.getColumnModel().getColumnCount() > 0) {
            summaryTable.getColumnModel().getColumn(0).setResizable(false);
            summaryTable.getColumnModel().getColumn(1).setResizable(false);
            summaryTable.getColumnModel().getColumn(2).setResizable(false);
            summaryTable.getColumnModel().getColumn(3).setResizable(false);
            summaryTable.getColumnModel().getColumn(4).setResizable(false);
        }

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 867, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 55, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(71, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
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
                    .addComponent(jScrollPane1))
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
                .addGap(704, 704, 704)
                .addComponent(exportBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(exportBtn)
                    .addComponent(exportAllBtn)
                    .addComponent(jButton1))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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

    private void sendEmailBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendEmailBtnActionPerformed
        if (pendingList.getSelectedValue() != null) {
            sendEmail se = new sendEmail(pendingList.getSelectedValue());
            se.setVisible(true);
        }
    }//GEN-LAST:event_sendEmailBtnActionPerformed

    private void sendAllBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendAllBtnActionPerformed
        ArrayList<BIS> bisList;
        boolean hasSent = false;
        String sentSubcon = "";
        String email;
        for (String subcon: filtered) {
            bisList = new ArrayList<>();
            email = getSubconEmail(subcon);
            for (BIS bis: filteredBisList) {
                if (bis.getSubcontractor().equalsIgnoreCase(subcon)) {
                    bisList.add(bis);
                }
            }
            exportExcel(bisList);
            String summary = getSummaryStr(bisList);
            if (email != null) {
                hasSent = sendEmail(summary, subcon, email);
                if (hasSent) {
                    sentSubcon += subcon + "\n";
                }
            }
            else {
                JOptionPane.showMessageDialog(this, "No email found for the contractor - " +
                        subcon + "\nEmail will not be sent to this contractor.",
                        "Message", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        JOptionPane.showMessageDialog(this, "Email has been sent to the following subcontractor: \n" +
                        sentSubcon,
                        "Message", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_sendAllBtnActionPerformed



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable detailsTable;
    private javax.swing.JButton exportAllBtn;
    private javax.swing.JButton exportBtn;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JList<String> pendingList;
    private javax.swing.JButton sendAllBtn;
    private javax.swing.JButton sendEmailBtn;
    private javax.swing.JTable summaryTable;
    // End of variables declaration//GEN-END:variables
}
