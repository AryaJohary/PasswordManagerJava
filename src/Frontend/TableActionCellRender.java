package Frontend;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class TableActionCellRender extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
        Component com =  super.getTableCellRendererComponent(table, value, false, false, 0, 0);
        PanelAction action = new PanelAction();
        return action;
    }
}
