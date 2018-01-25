package tools;

import javax.swing.table.DefaultTableModel;
import java.util.Vector;

/**
 * Created by maximerichard on 25.01.18.
 */
public class CustomTableModel extends DefaultTableModel {
    public void removeColumn(int column) {
        // for each row, remove the column
        Vector rows = dataVector;
        for (Object row : rows) {
            ((Vector) row).remove(column);
        }

        // remove the header
        columnIdentifiers.remove(column);

        // notify
        fireTableStructureChanged();
    }
}
