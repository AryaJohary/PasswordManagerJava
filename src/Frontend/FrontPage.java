package Frontend;

import Backend.SQLBackend;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;
import java.util.stream.Stream;



public class FrontPage extends JFrame{
    private JButton saveButton;
    private JPanel panelMain;
    private JTextField username;
    private JPasswordField password;
    private JLabel head;
    private JTextField passwordConfirm;
    private JTable dataTable;
    private JButton refreshButton;
    private JScrollPane scrollPane;
    private JButton deleteTableButton;
    private JButton generateButton;


    SQLBackend sq = new SQLBackend();
    DefaultTableModel tbModel;
    ArrayList<ArrayList<String>> dataList;
    String[] cName;
    String[][] data;
    Random random;

    public FrontPage() throws SQLException, ClassNotFoundException {


//        String siteName = "";
//        String passwordString = "";
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setTitle("Front Page");
        setSize(750,400);
        setContentPane(panelMain);
        dataTable.setRowHeight(35);
        setResizable(false);
        populateTable();


        saveButton.addActionListener(e -> saveData());
//        refreshButton.addActionListener(e -> populateTable());
        deleteTableButton.addActionListener(e -> clearTable());
        refreshButton.addActionListener(e -> populateTable());
        generateButton.addActionListener(e -> {
            random = new Random();
            String generatedPassword = generatePassword(random.nextInt(10,12));
            password.setText(generatedPassword);
            passwordConfirm.setText(generatedPassword);
        });
    }

    String generatePassword(int size){
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#$%^&*()_+-/.,<>?;':\"[]{}\\|`~";
        StringBuilder password = new StringBuilder();
        // creating object of Random class
        random = new Random();
        // looping to generate password
        while (password.length() < size) {
            // get a random number between 0 and length of chars
            int index = (int) (random.nextFloat() * chars.length());
            // add character at index to password
            password.append(chars.charAt(index));
        }
        return password.toString();
    }

    void saveData(){
        String siteName = username.getText();
        String passwordString
                = Stream.of(password.getPassword())
                .map(String::new)
                .collect(Collectors.joining());
        System.out.println(passwordString);
        String passwordConfirmString = passwordConfirm.getText();
        if(passwordString.length()<8){
            JOptionPane.showMessageDialog(null,"Password length must be at least 8 characters");
            return;
        }
        if(passwordString.equals(passwordConfirmString)){
//            JOptionPane.showMessageDialog(null,"Username = "+siteName+"\nPassword = "+passwordString);
            if(sq.addSitePassData(siteName,passwordString)){
                JOptionPane.showMessageDialog(null,"Successfully added site password");
                username.setText("");
                password.setText("");
                passwordConfirm.setText("");
                populateTable();
            }else {
                JOptionPane.showMessageDialog(null,"Error while adding site password");
            }

        }else{
            JOptionPane.showMessageDialog(null,"Password not matching");
        }
    }

    void clearTable() {

//        for(int i=1; i<dataList.size(); i++){
//            sq.deleteSitePassData(dataList.get(i).get(0),dataList.get(i).get(1));
//        }
        sq.deleteTable();
        if(tbModel != null){
            tbModel.setRowCount(0);
            dataTable.setModel(tbModel);
        }
    }


    void populateTable() {
        dataList = sq.getData();
        cName = new String[]{"Site Name", "Password", "Buttons"};
        if(dataList.isEmpty()){
            tbModel = new DefaultTableModel(cName, 0);
            dataTable.setModel(tbModel);
            return;
        }

        // decrease one row - first row has the headings
        // increase one column - give a button to each row to delete that row

        data = new String[dataList.size()-1][cName.length];
//        cName = dataList.getFirst().toArray(cName);

//        String[] cName = {"Delete Button","Website","Password"};
        for(int i=1; i<dataList.size();i++){
            data[i-1][0] = dataList.get(i).toArray(new String[0])[0];
            data[i-1][1] = dataList.get(i).toArray(new String[0])[1].replaceAll(".","*");
            data[i-1][2] = null;
        }

        tbModel = new DefaultTableModel(data ,cName){
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
        };
        dataTable.setModel(tbModel);

        TableActionCellEditor te = getTableActionCellEditor();
        TableActionCellRender tr = new TableActionCellRender();
        dataTable.getColumnModel().getColumn(2).setCellRenderer(tr);
        dataTable.getColumnModel().getColumn(2).setCellEditor(te);
    }

    private TableActionCellEditor getTableActionCellEditor() {

        TableActionEvent event = new TableActionEvent() {
            boolean viewing = false;
            @Override
            public void onEdit(int row) {
                System.out.println("Edit row: " + row);
            }

            @Override
            public void onDelete(int row) {
                System.out.println("Delete row: " + row);
                if(dataTable.isEditing()){
                    dataTable.getCellEditor().stopCellEditing();
                }
                String siteName = dataTable.getModel().getValueAt(row,0).toString();
                String password = dataTable.getModel().getValueAt(row,1).toString();
                sq.deleteSitePassData(siteName,password);

                ((DefaultTableModel) dataTable.getModel()).removeRow(row);
                dataList = sq.getData();
                if(dataList.isEmpty()){
                    tbModel = new DefaultTableModel(cName, 0);
                    dataTable.setModel(tbModel);
                }else{
                    System.out.println("Inside else of Delete button of row "+row);
                }
            }
            @Override
            public void onView(int row) {
                System.out.println("View row: " + row);
                String password;
                if(!viewing){
                    password = dataList.get(row+1).toArray(new String[0])[1];
                    viewing = true;
                }else{
                    password = dataList.get(row+1).toArray(new String[0])[1].replaceAll(".","*");
                    viewing = false;
                }
                dataTable.getModel().setValueAt(password,row,1);
            }
        };

        return new TableActionCellEditor(event);
    }
}
