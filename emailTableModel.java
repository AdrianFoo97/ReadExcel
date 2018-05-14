
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
public class emailTableModel extends AbstractTableModel{
    private static final String[] colHeader = {
        "Subcontractor", "Email"
    };
    
    private ArrayList<Subcontractor> conList;

    public emailTableModel(ArrayList<Subcontractor> conList) {
        this.conList = conList;
    }

    public void setConList(ArrayList<Subcontractor> conList) {
        this.conList = conList;
    }
    
    @Override
    public int getRowCount() {
        return conList.size();
    }

    @Override
    public int getColumnCount() {
        return colHeader.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Subcontractor subcon = (Subcontractor) conList.get(rowIndex);
        switch (columnIndex) {
            case 0: return subcon.getName();
            case 1: return subcon.getEmail();
            default: return "";
        }
    }
    
    public String getColumnName(int column) {
        return colHeader[column];
    }
    
    public Subcontractor getSubconAt(int index) {
        Subcontractor theSubcon = (Subcontractor) conList.get(index);
        return theSubcon;
    }
}
