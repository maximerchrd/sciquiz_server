package tools;

import javax.swing.*;

/**
 * Created by maximerichard on 03.01.18.
 */
public class ListEntry {
    private String value;
    private ImageIcon icon;

    public ListEntry(String value, ImageIcon icon) {
        this.value = value;
        this.icon = icon;
    }

    public String getValue() {
        return value;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public String toString() {
        return value;
    }
}
