package Frontend;


import javax.swing.*;
import javax.swing.table.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;
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
    private JTextField siteName;


    //    SQLBackend sq = new SQLBackend();
    Controller controller = new Controller();
    DefaultTableModel tbModel;
    ArrayList<ArrayList<String>> dataList;
    String[][] data;
    Random random;
    String[] cName = new String[]{"Site Name", "Username", "Password", "Buttons"};
    ArrayList<String> temp = new ArrayList<>();

    public FrontPage() {


//        String siteName = "";
//        String passwordString = "";
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setTitle("Front Page");
        setSize(950,600);
        setContentPane(panelMain);
        dataTable.setRowHeight(35);
        setResizable(false);
        head.setFont(new Font("Serif", Font.BOLD,40));
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
        String siteNameText = siteName.getText();
        String userNameText = username.getText();
        String passwordString
                = Stream.of(password.getPassword())
                .map(String::new)
                .collect(Collectors.joining());
//        System.out.println(passwordString);
        String passwordConfirmString = passwordConfirm.getText();
        if(siteNameText.isEmpty()){
            JOptionPane.showMessageDialog(null,"Please enter a Site Name");
        }else if(userNameText.isEmpty()){
            JOptionPane.showMessageDialog(null,"Please enter a User Name");
        }else if(passwordString.length()<8){
            JOptionPane.showMessageDialog(null,"Password length must be at least 8 characters");
            return;
        }else if(passwordString.equals(passwordConfirmString)){
//            JOptionPane.showMessageDialog(null,"Username = "+siteName+"\nPassword = "+passwordString);
            if(controller.addSitePassData(siteNameText, userNameText, passwordString)){
                JOptionPane.showMessageDialog(null,"Successfully added site password");
                siteName.setText("");
                username.setText("");
                password.setText("");
                passwordConfirm.setText("");
                tbModel.addRow(new String[]{siteNameText,userNameText,passwordString.replaceAll(".","*")});
//                populateTable();
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
                return column == cName.length-1;
            }
        };
        dataTable.setModel(tbModel);
//        System.out.println("New model set");
        if (fillTableValues()){
            return;
        }
        tbModel.setDataVector(data,cName);
//        tbModel.setDataVector(dataList,dataList.getFirst());
        TableActionCellEditor te = getTableActionCellEditor();
        TableActionCellRender tr = new TableActionCellRender();
        dataTable.getColumnModel().getColumn(cName.length-1).setCellRenderer(tr);
//        System.out.println("TableActionCellRenderer called");
        dataTable.getColumnModel().getColumn(cName.length-1).setCellEditor(te);
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
            data[i][1] = dataList.get(i).toArray(new String[0])[1];
            data[i][2] = dataList.get(i).toArray(new String[0])[2].replaceAll(".","*");
            data[i][3] = "";
        }
        return false;
    }

    private TableActionCellEditor getTableActionCellEditor() {
        TableActionEvent event = new TableActionEvent() {
            boolean viewing = false;
            @Override
            public void onEdit(int row) {
//                System.out.println("Edit row: " + row);
                String newPassword = JOptionPane.showInputDialog("Enter new password");
                if(newPassword == null || newPassword.length()<8){
                    JOptionPane.showMessageDialog(null,"Password Length must be more than 8");
                }else {
                    controller.editSitePassData(
                            dataList.get(row).get(0),
                            dataList.get(row).get(1),
                            newPassword);
//                    populateTable();
                    if(!temp.isEmpty()){
                        temp.clear();
                    }
                    temp.add(dataList.get(row).get(0));
                    temp.add(dataList.get(row).get(1));
                    temp.add(newPassword);
                    dataList.set(row,temp);
                    tbModel.setValueAt(newPassword.replaceAll(".","*"),row,cName.length-2);
                    JOptionPane.showMessageDialog(null,"Successfully edited site password");
                }
            }

            @Override
            public void onDelete(int row) {
//                System.out.println("Delete row: " + row);
                if(dataTable.isEditing()){
                    dataTable.getCellEditor().stopCellEditing();
                }
                String siteName = dataTable.getModel().getValueAt(row,0).toString();
                String userName = dataTable.getModel().getValueAt(row,1).toString();
//                System.out.println("Inside delete passed sitename = "+siteName+" and password = "+password);
                controller.deleteSitePassData(siteName,userName);

//                ((DefaultTableModel) dataTable.getModel()).removeRow(row);
                dataList.remove(row);
                tbModel.removeRow(row);
            }
            @Override
            public void onView(int row) {
//                System.out.println("View row: " + row);
                String password;
                if(!viewing){
                    password = dataList.get(row).toArray(new String[0])[cName.length-2];
                    viewing = true;
                }else{
                    password = dataList.get(row).toArray(new String[0])[cName.length-2].replaceAll(".","*");
                    viewing = false;
                }
                tbModel.setValueAt(password,row,cName.length-2);
            }
        };

        return new TableActionCellEditor(event);
    }
}
