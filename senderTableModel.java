
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.table.AbstractTableModel;
/**
 *
 * @author a80052136
 */
public class senderTableModel extends AbstractTableModel {
    private static final String[] colHeader = {
        "Username", "Email"
    };
    
    private ArrayList<Sender> senderList;

    public senderTableModel(ArrayList<Sender> senderList) {
        this.senderList = senderList;
        Collections.sort(senderList);
    }

    public void setConList(ArrayList<Sender> senderList) {
        this.senderList = senderList;
    }
    
    @Override
    public int getRowCount() {
        return senderList.size();
    }

    @Override
    public int getColumnCount() {
        return colHeader.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Sender s = (Sender) senderList.get(rowIndex);
        switch (columnIndex) {
            case 0: return s.getUsername();
            case 1: return s.getEmail();
            default: return "";
        }
    }
    
    public String getColumnName(int column) {
        return colHeader[column];
    }
    
    public Sender getSenderAt(int index) {
        Sender s = (Sender) senderList.get(index);
        return s;
    }
}
