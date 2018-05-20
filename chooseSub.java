
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author a80052136
 */
public class chooseSub extends javax.swing.JDialog {
    private ArrayList<String>conList;
    private DefaultListModel subconListModel;
    private DefaultListModel selectedListModel;
    private ArrayList<BIS> filteredBisList;
    private ArrayList<Sender> senderList;

    /**
     * Creates new form chooseSub
     */
    public chooseSub(java.awt.Frame parent, boolean modal, LinkedHashSet<String> conList, 
            ArrayList<BIS> bisList) {
        super(parent, modal);
        initComponents();
        this.conList = new ArrayList<>(conList);
        this.filteredBisList = bisList;
        SenderDA da = new SenderDA();
        senderList = da.getAllSender();
        setupListModel();
        setupListModel2();
        centreWindow(this);
    }
    
    // This function will centre the window
    private void centreWindow(Window frame) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
    }
    private void setupListModel () {
        subconListModel = new DefaultListModel();
        subconList.setModel(subconListModel);
        subconList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        // adding pending subcontractor into the list 
        Collections.sort(conList);
        for (String str: conList) {
            subconListModel.addElement(str);
        }
        subconList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    addSubconToList();
                }
            }
        });
    }
    
    private void setupListModel2 () {
        selectedListModel = new DefaultListModel();
        selectedList.setModel(selectedListModel);
        selectedList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        selectedList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    removeSubconFromList();
                }
            }
        });
    }
    
    private ArrayList<String> getSubconEmail(String subconName) {
        String subconNameTrim = subconName.replaceAll("[^\\p{L}\\p{Nd}]+", "");
        ArrayList<String> emailReturn = new ArrayList<>();
        SubcontractorDA da = new SubcontractorDA();
        ArrayList<Subcontractor> conList = da.getAllSubcontractor();
        for (Subcontractor s: conList) {
            String nameGet = s.getName().replaceAll("[^\\p{L}\\p{Nd}]+", "");
            if (nameGet.equalsIgnoreCase(subconNameTrim)) {
                emailReturn.add(s.getEmail());
            }
        }
        return emailReturn;
    }
    
    /**
     * This function return a list of BIS object of the subcontractor
     * @param subcon the name of the subcontractor
     * @return an array list of BIS of the subcontractor
     */
    private ArrayList<BIS> getSubconBisList(String subcon) {
        ArrayList<BIS> bisList = new ArrayList<>(); 
        for (BIS bis: filteredBisList) {
                if (bis.getSubcontractor().equalsIgnoreCase(subcon)) {
                    if (bis.getStepSubmitted().equalsIgnoreCase("null")) {
                        bisList.add(bis);
                    }
                    else if (!bis.getRejected().equalsIgnoreCase("null") &&
                            bis.getResubmitted().equalsIgnoreCase("null")) {
                        bisList.add(bis);
                    }
                }
            }
        return bisList;
    }
    
    private ArrayList<String> getSelectedSubcon() {
        ArrayList<String> selectedSubcon = new ArrayList<>();
        // Add all element from the list model to an array list
        for(int i = 0; i < selectedListModel.getSize(); i++) {
            selectedSubcon.add((String) selectedListModel.getElementAt(i));
        }
        return selectedSubcon;
    }

    private void addSubconToList() {
        List<String> selectedList = subconList.getSelectedValuesList();
        for (String theSubcon: selectedList) {
            selectedListModel.addElement(theSubcon);
            subconListModel.removeElement(theSubcon);
        }  
    }
    
    private void removeSubconFromList() {
        List<String> removedList = selectedList.getSelectedValuesList();
        for (String theSubcon: removedList) {
            subconListModel.addElement(theSubcon);
            selectedListModel.removeElement(theSubcon);
        }
        int i;
        ArrayList<String> unsortedList = new ArrayList<>();
        for (i = 0; i < subconListModel.size(); i++) {
            unsortedList.add((String) subconListModel.getElementAt(i));
        }
        subconListModel.removeAllElements();
        Collections.sort(unsortedList);
        for (String theSubcon: unsortedList) {
            subconListModel.addElement(theSubcon);
        }
    }
    
    private ArrayList<String> getAllSenderEmail() {
        ArrayList<String> emailList = new ArrayList<>();
        for (Sender s: senderList) {
            emailList.add(s.getEmail());
        }
        return emailList;
    }
    
    private Sender getSender(String email) {
        for (Sender s: senderList) {
            if (s.getEmail().equals(email)) {
                return s;
            }
        }
        return null;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        subconList = new javax.swing.JList<>();
        addBtn = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        selectedList = new javax.swing.JList<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        sendBtn = new javax.swing.JButton();
        removeBtn = new javax.swing.JButton();
        addAllBtn = new javax.swing.JButton();
        removeAllBtn = new javax.swing.JButton();
        exportBtn = new javax.swing.JButton();
        closeBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        subconList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(subconList);

        addBtn.setText("Add");
        addBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBtnActionPerformed(evt);
            }
        });

        selectedList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(selectedList);

        jLabel1.setText("Subcontractor:");

        jLabel2.setText("Selected:");

        sendBtn.setText("Send Email");
        sendBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendBtnActionPerformed(evt);
            }
        });

        removeBtn.setText("Remove");
        removeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeBtnActionPerformed(evt);
            }
        });

        addAllBtn.setText("Add All");
        addAllBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addAllBtnActionPerformed(evt);
            }
        });

        removeAllBtn.setText("Remove All");
        removeAllBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeAllBtnActionPerformed(evt);
            }
        });

        exportBtn.setText("Export to Excel");
        exportBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportBtnActionPerformed(evt);
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 524, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(addBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(addAllBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(removeAllBtn)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(removeBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 490, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(53, 53, 53))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(exportBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sendBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(423, 423, 423))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(126, 126, 126)
                        .addComponent(addBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(addAllBtn)
                        .addGap(39, 39, 39)
                        .addComponent(removeBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(removeAllBtn))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sendBtn)
                    .addComponent(exportBtn)
                    .addComponent(closeBtn))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBtnActionPerformed
        addSubconToList();
    }//GEN-LAST:event_addBtnActionPerformed

    private void removeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeBtnActionPerformed
        removeSubconFromList();
    }//GEN-LAST:event_removeBtnActionPerformed

    private void addAllBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAllBtnActionPerformed
        for (String theSubcon: conList) {
            if (!selectedListModel.contains(theSubcon)) {
                selectedListModel.addElement(theSubcon);
                subconListModel.removeElement(theSubcon);
            }
        }
    }//GEN-LAST:event_addAllBtnActionPerformed

    private void removeAllBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeAllBtnActionPerformed
        selectedListModel.removeAllElements();
        subconListModel.removeAllElements();
        Collections.sort(conList);
        for (String theSubcon: conList) {
            if (!subconListModel.contains(theSubcon)) {
                subconListModel.addElement(theSubcon);
            }
        }
    }//GEN-LAST:event_removeAllBtnActionPerformed

    private void sendBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendBtnActionPerformed
        ArrayList<String> selectedSubcon = getSelectedSubcon();
        ArrayList<String> emailList = getAllSenderEmail();
        // Sending email
        String hasSent;
        String sentSubcon = "";
        ArrayList<String> subconEmail;
        if (selectedSubcon.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No subcontractor is selected.",
                        "Message", JOptionPane.INFORMATION_MESSAGE);
        }
        else {
            String senderEmail = (String) JOptionPane.showInputDialog(this, "Please choose the sender email", "Sender Email", 
                JOptionPane.QUESTION_MESSAGE, null, emailList.toArray(new String[emailList.size()]), "");
            Sender theSender = getSender(senderEmail);
            for (String subcon: selectedSubcon) {
                subconEmail = getSubconEmail(subcon);
                ArrayList<BIS> bisList = getSubconBisList(subcon);
                ExportExcel.export(bisList);    // Export to excel file 
                String summary = Summary.getSummaryStr(bisList);    // Get the summary 
                if (subconEmail != null) {
                    hasSent = SendEmail.sendEmail(summary, subcon, subconEmail, theSender.getEmail(), 
                            theSender.getPassword(), theSender.getUsername());
                    if (hasSent.equalsIgnoreCase("successful")) {
                        sentSubcon += subcon + "\n";
                    }
                    else if (hasSent.equalsIgnoreCase("failed")) {
                        JOptionPane.showMessageDialog(this, "Please check again your username, "
                                + "email or password.",
                            "Message", JOptionPane.INFORMATION_MESSAGE);
                        break;
                    }
                }
                else {
                    JOptionPane.showMessageDialog(this, "No email found for " +
                            subcon + "\nEmail will not be sent to this subcontractor.",
                            "Message", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
        
        if (!sentSubcon.equalsIgnoreCase("")) {
            JOptionPane.showMessageDialog(this, "Email has been sent to the following subcontractor: \n\n" +
                        sentSubcon,
                        "Message", JOptionPane.INFORMATION_MESSAGE);
        }
        
    }//GEN-LAST:event_sendBtnActionPerformed

    private void exportBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportBtnActionPerformed
        boolean hasExported = false;
        ArrayList<String> selectedSubcon = getSelectedSubcon();
        for (String subcon: selectedSubcon) {
            ArrayList<BIS> bisList = getSubconBisList(subcon);
            hasExported = ExportExcel.export(bisList);
        }
        if (hasExported) {
            JOptionPane.showMessageDialog(this, "Exported successfully.",
                        "Message", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_exportBtnActionPerformed

    private void closeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeBtnActionPerformed
        this.dispose();
    }//GEN-LAST:event_closeBtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addAllBtn;
    private javax.swing.JButton addBtn;
    private javax.swing.JButton closeBtn;
    private javax.swing.JButton exportBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton removeAllBtn;
    private javax.swing.JButton removeBtn;
    private javax.swing.JList<String> selectedList;
    private javax.swing.JButton sendBtn;
    private javax.swing.JList<String> subconList;
    // End of variables declaration//GEN-END:variables
}
