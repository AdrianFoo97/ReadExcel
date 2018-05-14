
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author a80052136
 */
public class detailsTableModel extends AbstractTableModel{
    // set up the column headers
    private static final String[] colHeader = {
        "DU ID Scope", "Acceptance Plan", "Acceptance Step New Category", "Acceptance Step Name", 
        "Subcontractor", "On Air Actual End Date", "BIS Date", "Step Status", 
        "Step Initiated", "Step Submitted", "Step Rejected", "Resubmitted", "Step Accepted"
    };
    private ArrayList<BIS> passList;

    public detailsTableModel(ArrayList<BIS> passList) {
        setPassList(passList);
    }

    public void setPassList(ArrayList<BIS> passList) {
        this.passList = passList;
    }
    

    @Override
    public int getRowCount() {
        return passList.size();
    }

    @Override
    public int getColumnCount() {
        return colHeader.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        BIS theBis = (BIS) passList.get(rowIndex);
        switch (columnIndex) {
            case 0: return theBis.getIdScope();
            case 1: return theBis.getAcceptancePlan();
            case 2: return theBis.getCategory();
            case 3: return theBis.getStepName();
            case 4: return theBis.getSubcontractor();
            case 5: return theBis.getOnADate();
            case 6: return theBis.getBisDate();
            case 7: return theBis.getStepStatus();
            case 8: return theBis.getStepInitiated();
            case 9: return theBis.getStepSubmitted();
            case 10: return theBis.getRejected();
            case 11: return theBis.getResubmitted();
            case 12: return theBis.getStepAccepted();  
            default: return "";
        }
    }
    
    public String getColumnName(int column) {
        return colHeader[column];
    }
    
    public BIS getBisAt(int index) {
        BIS theBis = (BIS) passList.get(index);
        return theBis;
    }
    
}
