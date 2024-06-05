package Frontend;


import javax.swing.*;
import javax.swing.table.*;

import java.util.ArrayList;
import java.util.Random;

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


//    SQLBackend sq = new SQLBackend();
    Controller controller = new Controller();
    DefaultTableModel tbModel;
    ArrayList<ArrayList<String>> dataList;
    String[][] data;
    Random random;
    String[] cName = new String[]{"Site Name", "Password", "Buttons"};


    public FrontPage() {


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
//        System.out.println(passwordString);
        String passwordConfirmString = passwordConfirm.getText();
        if(passwordString.length()<8){
            JOptionPane.showMessageDialog(null,"Password length must be at least 8 characters");
            return;
        }
        if(passwordString.equals(passwordConfirmString)){
//            JOptionPane.showMessageDialog(null,"Username = "+siteName+"\nPassword = "+passwordString);
            if(controller.addSitePassData(siteName,passwordString)){
                JOptionPane.showMessageDialog(null,"Successfully added site password");
                username.setText("");
                password.setText("");
                passwordConfirm.setText("");
//                tbModel.addRow(new String[]{siteName,passwordString.replaceAll(".","*")});
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
        controller.deleteTable();
        if(tbModel != null){
//            tbModel.setRowCount(0);
//            dataTable.setModel(tbModel);
            tbModel.setRowCount(0);
        }
    }


    void populateTable() {

        tbModel = new DefaultTableModel(cName, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return column == 2;
            }
        };
        dataTable.setModel(tbModel);
        System.out.println("New model set");
        if (fillTableValues()){
            return;
        }
        tbModel.setDataVector(data,cName);
//        tbModel.setDataVector(dataList,dataList.getFirst());
        TableActionCellEditor te = getTableActionCellEditor();
        TableActionCellRender tr = new TableActionCellRender();
        dataTable.getColumnModel().getColumn(2).setCellRenderer(tr);
//        System.out.println("TableActionCellRenderer called");
        dataTable.getColumnModel().getColumn(2).setCellEditor(te);
//        dataTable.setModel(tbModel);
    }

    private boolean fillTableValues() {
        dataList = controller.getData();
//        System.out.println("New datalist acquired inside FrontPage");
//        for(ArrayList<String> row:dataList){
//            System.out.println(row.get(0)+" "+row.get(1));
//        }
        if(dataList.isEmpty()){
            return true;
        }

        // decrease one row - first row has the headings
        // increase one column - give a button to each row to delete that row

        data = new String[dataList.size()][cName.length];
//        cName = dataList.getFirst().toArray(cName);

//        String[] cName = {"Delete Button","Website","Password"};
        for(int i=0; i<dataList.size();i++){
            data[i][0] = dataList.get(i).toArray(new String[0])[0];
            data[i][1] = dataList.get(i).toArray(new String[0])[1].replaceAll(".","*");
            data[i][2] = "";
        }
        return false;
    }

    private TableActionCellEditor getTableActionCellEditor() {
        TableActionEvent event = new TableActionEvent() {
            boolean viewing = false;
            @Override
            public void onEdit(int row) {
                System.out.println("Edit row: " + row);
                String newPassword = JOptionPane.showInputDialog("Enter new password");
                if(newPassword == null || newPassword.length()<8){
                    JOptionPane.showMessageDialog(null,"Password Length must be more than 8");
                }else {
                    controller.editSitePassData(
                            dataList.get(row).get(0),
                            newPassword,
                            dataList.get(row).get(1));
//                    populateTable();
                    tbModel.setValueAt(newPassword.replaceAll(".","*"),row,1);
                    JOptionPane.showMessageDialog(null,"Successfully edited site password");
                }
            }

            @Override
            public void onDelete(int row) {
                System.out.println("Delete row: " + row);
                if(dataTable.isEditing()){
                    dataTable.getCellEditor().stopCellEditing();
                }
                String siteName = dataTable.getModel().getValueAt(row,0).toString();
                String password = dataTable.getModel().getValueAt(row,1).toString();
//                System.out.println("Inside delete passed sitename = "+siteName+" and password = "+password);
                controller.deleteSitePassData(siteName,password);

//                ((DefaultTableModel) dataTable.getModel()).removeRow(row);
                dataList = controller.getData();
                if(dataList.isEmpty()){
//                    tbModel = new DefaultTableModel(cName, 0);
//                    dataTable.setModel(tbModel);
                    tbModel.setRowCount(0);
                }else{
                    System.out.println("Inside else of Delete button of row "+row);
                    // remove (row+1)th row from the data array
//                    String[][] temp = new String[data.length-1][];
//                    for(int i=0,j=0;i<data.length;i++){
//                        if(i!=row){
//                            temp[j++] = data[i];
//                        }
//                    }
//                    data = temp;
                    populateTable();
                }
            }
            @Override
            public void onView(int row) {
                System.out.println("View row: " + row);
                String password;
                if(!viewing){
                    password = dataList.get(row).toArray(new String[0])[1];
                    viewing = true;
                }else{
                    password = dataList.get(row).toArray(new String[0])[1].replaceAll(".","*");
                    viewing = false;
                }
                tbModel.setValueAt(password,row,1);
            }
        };

        return new TableActionCellEditor(event);
    }
}
