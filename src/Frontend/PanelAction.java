package Frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class PanelAction extends JPanel {
    private JButton deleteButton;
    private JButton editButton;
    private JButton viewButton;


    public PanelAction(){

        deleteButton = new JButton("Delete");
        editButton = new JButton("Edit");
        viewButton = new JButton("View");
        setLayout(new FlowLayout());
        add(deleteButton);
        add(editButton);
        add(viewButton);
    }
    public void initEvent(TableActionEvent event, int row){
        editButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                event.onEdit(row);
            }
        });
        deleteButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                event.onDelete(row);
            }
        });
        viewButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                event.onView(row);
            }
        });
    }
}
