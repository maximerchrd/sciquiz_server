package tools;

import javax.swing.*;
import java.awt.*;

/**
 * Created by maximerichard on 03.01.18.
 */
public class ListEntryCellRenderer
        extends JLabel implements ListCellRenderer {
    private JLabel label;

    public Component getListCellRendererComponent(JList list, Object value,
                                                  int index, boolean isSelected,
                                                  boolean cellHasFocus) {
        ListEntry entry = (ListEntry) value;

        setText(value.toString());
        setIcon(entry.getIcon());

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        setEnabled(list.isEnabled());
        setFont(list.getFont());
        setOpaque(true);

        return this;
    }
}
