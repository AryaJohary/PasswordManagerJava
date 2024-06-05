package Frontend;
import Backend.*;

import java.util.ArrayList;

// I have created this class so that i can encrypt and decrypt the data from sql before passing it to other party

public class Controller {
    SQLBackend sq = new SQLBackend();
    public boolean addSitePassData(String siteName, String passwordString) {
        // first convert the password to encrypted key and then store the password
        String encryptedPassword = Encryptor.encrypt(passwordString);
        return sq.addSitePassData(siteName,encryptedPassword);
    }

    public void deleteTable() {
        sq.deleteTable();
    }

    public ArrayList<ArrayList<String>> getData() {
        // when I get all the passwords,
        ArrayList<ArrayList<String>> data = sq.getData();
//        System.out.println("New datalist acquired");
//        for(ArrayList<String> row:data){
//            System.out.println(row.get(0)+" "+row.get(1));
//        }
//        data.removeFirst();
        ArrayList<String> tempRowStorage = new ArrayList<>();
        String tempSiteStorage = "";
        String tempPasswordStorage = "";
        for(int i=0; i<data.size(); i++) {
            if(!tempRowStorage.isEmpty()){
                tempRowStorage.clear();
            }
            tempSiteStorage = data.get(i).get(0);
            tempPasswordStorage = data.get(i).get(1);
//            System.out.println("temp password Storage length = "+tempPasswordStorage.length());
            tempRowStorage.add(tempSiteStorage);
            // decrypt them before passing it to front page
            tempRowStorage.add(Encryptor.decrypt(tempPasswordStorage));
            data.set(i,new ArrayList<>(tempRowStorage));
        }
        return data;
    }

    public void editSitePassData(String siteName, String newPassword, String oldPassword) {
        // first encrypt the existing old password
        String encryptedPassword = Encryptor.encrypt(oldPassword);
        // then encrypt the new password
        String encryptedNewPassword = Encryptor.encrypt(newPassword);
        // then pass on the data
        sq.editSitePassData(siteName,encryptedNewPassword,encryptedPassword);
    }

    public void deleteSitePassData(String siteName, String password) {
        // first encrypt the password then pass the data
        String encryptedPassword = Encryptor.encrypt(password);
        sq.deleteSitePassData(siteName,encryptedPassword);
    }
}
