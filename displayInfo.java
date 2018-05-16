
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.util.LinkedHashSet;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        detailsTable.getColumnModel().getColumn(4).setPreferredWidth(200);
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
        String fopSub = Integer.toString(Summary.countPenSub("1-FOP", passList));
        String optiSub = Integer.toString(Summary.countPenSub("2-OPTI", passList));
        String bstrSub = Integer.toString(Summary.countPenSub("3-BSTR", passList));
        String fmSub = Integer.toString(Summary.countPenSub("4-FM", passList));
        String prefixSub = Integer.toString(Summary.countPenSub("5-PREFIX", passList));

        String fopResub = Integer.toString(Summary.countPenResub("1-FOP", passList));
        String optiResub = Integer.toString(Summary.countPenResub("2-OPTI", passList));
        String bstrResub = Integer.toString(Summary.countPenResub("3-BSTR", passList));
        String fmResub = Integer.toString(Summary.countPenResub("4-FM", passList));
        String prefixResub = Integer.toString(Summary.countPenResub("5-PREFIX", passList));
        
        String data [][] = {
            {"Pending Submission", fopSub, optiSub, bstrSub, fmSub, prefixSub},
            {"Pending Resubmission", fopResub, optiResub, bstrResub, fmResub, prefixResub}
        };
        return data;
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
    
    private void openEmailClient(String email, String body) throws URISyntaxException, IOException {
        Desktop desktop;
        if (Desktop.isDesktopSupported() 
            && (desktop = Desktop.getDesktop()).isSupported(Desktop.Action.MAIL)) {
            URI mailto = null;
            String subject = "BIS Report Submission Status";
            String message = "";
            body = body.replaceAll(" ", "%20");
            body = body.replaceAll("\n", "%0A");
            body = body.replaceAll("\t", "%09");
            body = body.replaceAll(":", "%3A");
            body = body.replaceAll("\\.", "%2E");
            body = body.replaceAll(",", "%2C");
            subject = subject.replaceAll(" ", "%20");
            subject = subject.replaceAll("\n", "%0A");
            subject = subject.replaceAll("\t", "%09");
            subject = subject.replaceAll(":", "%3A");
            subject = subject.replaceAll("\\.", "%2E");
            subject = subject.replaceAll(",", "%2C");
            mailto = new URI("mailto:" + email + "?subject=" + subject + 
                        "&body=" + body);
            desktop.mail(mailto);
            
        } else {
          // TODO fallback to some Runtime.exec(..) voodoo?
          throw new RuntimeException("desktop doesn't support mailto; mail is dead anyway ;)");
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
        outlookBtn = new javax.swing.JButton();
        sendEmailBtn = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        summaryTable = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        detailsTable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        backBtn = new javax.swing.JButton();
        closeBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pendingList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(pendingList);

        jLabel2.setText("Pending Subcontractor:");

        outlookBtn.setText("Open With Outook");
        outlookBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outlookBtnActionPerformed(evt);
            }
        });

        sendEmailBtn.setText("More Functions");
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
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(214, 214, 214)
                                .addComponent(outlookBtn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(sendEmailBtn)))
                        .addGap(0, 197, Short.MAX_VALUE)))
                .addContainerGap())
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
                    .addComponent(outlookBtn)
                    .addComponent(sendEmailBtn))
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
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 885, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 315, Short.MAX_VALUE)
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

        backBtn.setText("Back");
        backBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backBtnActionPerformed(evt);
            }
        });

        closeBtn.setText("Close");
        closeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeBtnActionPerformed(evt);
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
                .addContainerGap(27, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
            .addGroup(layout.createSequentialGroup()
                .addGap(795, 795, 795)
                .addComponent(backBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                    .addComponent(backBtn)
                    .addComponent(closeBtn))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void backBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backBtnActionPerformed
        this.dispose();
        MainPage mp = new MainPage();
        mp.setVisible(true);
    }//GEN-LAST:event_backBtnActionPerformed

    private void outlookBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outlookBtnActionPerformed
        try {
            String subcon = pendingList.getSelectedValue();
            String email = getSubconEmail(subcon);
            if (email == null) {
                email = "";
            }
            String summary = Summary.getSummaryStr(passList);
            openEmailClient(email, summary);
        }
        catch (java.lang.NullPointerException e) {
            JOptionPane.showMessageDialog(this, "Please select a subcontractor.",
                        "Message", JOptionPane.INFORMATION_MESSAGE);
        } catch (URISyntaxException ex) {
            Logger.getLogger(displayInfo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(displayInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_outlookBtnActionPerformed

    private void sendEmailBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendEmailBtnActionPerformed
        chooseSub cs = new chooseSub(this, true, filtered, filteredBisList);
        cs.setVisible(true);
    }//GEN-LAST:event_sendEmailBtnActionPerformed

    private void closeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeBtnActionPerformed
        System.exit(0);
    }//GEN-LAST:event_closeBtnActionPerformed



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backBtn;
    private javax.swing.JButton closeBtn;
    private javax.swing.JTable detailsTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JButton outlookBtn;
    private javax.swing.JList<String> pendingList;
    private javax.swing.JButton sendEmailBtn;
    private javax.swing.JTable summaryTable;
    // End of variables declaration//GEN-END:variables
}
