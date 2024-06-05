package Backend;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;
import java.util.ArrayList;

public class SQLBackend {
    Connection conn;
    String tableName = "SitePassData";
    String dbName = "db_arya";
    String dbUser = "root";
    String dbPass = "";
    ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
    ArrayList<String> temp = new ArrayList<String>();

    public ArrayList<ArrayList<String>> getData(){
//        System.out.println("inside sql backend");
//        for(ArrayList<String> arr : data){
//            for(String s : arr){
//                System.out.println(s);
//            }
//        }
        spitOutAllTableRows();
        return data;
    }
    public ArrayList<String> getColumnName(){
//        System.out.println("inside sql backend ");
//        for(String s:data.getFirst()){
//            System.out.println(s);
//        }
        spitOutAllTableRows();
        return data.getFirst();
    }

    public SQLBackend(){
        try{
            conn = getConnection();
            PreparedStatement ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS "+tableName+" (SITE VARCHAR(8000),PASSWORD VARCHAR(8000))");
//        ps.setString(1,tableName);
            ps.executeUpdate();
            spitOutAllTableRows();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void spitOutAllTableRows() {
        try {
            data.clear();
//            System.out.println("spitOutAllTableRows called");
//            System.out.println("spit out all tables called");
//            System.out.println("current " + tableName + " is:");
            PreparedStatement selectStmt = conn.prepareStatement("SELECT * from "+tableName, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
//            selectStmt.setString(1,tableName);
            ResultSet rs = selectStmt.executeQuery();
            if (!rs.isBeforeFirst()) {
              System.out.println("no rows found");
            }
            else {
//              System.out.println("types:");
//                temp = new ArrayList<>();
//                for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
////                  System.out.print(rs.getMetaData().getColumnName(i + 1) + ":" + rs.getMetaData().getColumnTypeName(i + 1) + " ");
//                    temp.add(rs.getMetaData().getColumnName(i + 1));
//                }
//                data.add(new ArrayList<>(temp));
//              System.out.println();
                while (rs.next()) {
                    temp = new ArrayList<>();
                    for (int i = 1; i < rs.getMetaData().getColumnCount() + 1; i++) {
//                           System.out.print(" " + rs.getMetaData().getColumnName(i) + "=" + rs.getObject(i));
                        temp.add(rs.getObject(i).toString());
                    }
                    data.add(new ArrayList<>(temp));
//                  System.out.println("");
                }
                }
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean addSitePassData(String siteName, String password){
        int ret = 0;
        try{
            PreparedStatement ps = conn.prepareStatement("INSERT INTO "+tableName+" VALUES (?,?)");
//            ps.setString(1,tableName);
            ps.setString(1,siteName);
            ps.setString(2,password);
            ret = ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ret > 0;
    }
    public void deleteSitePassData(String siteName, String password){
        try{
            PreparedStatement ps = conn.prepareStatement("DELETE FROM "+tableName+" WHERE SITE = ? AND PASSWORD = ?");
//            ps.setString(1,tableName);
            ps.setString(1,siteName);
            ps.setString(2,password);
            System.out.println("Output for deleteSitePassData was "+ps.executeUpdate());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Connection getConnection() throws ClassNotFoundException, SQLException {
        Connection conn;
        String dbUrl = "jdbc:mariadb://localhost:3306/";
        String dbDriver = "org.mariadb.jdbc.Driver";
        Class.forName(dbDriver);
        conn = DriverManager.getConnection(dbUrl + dbName, dbUser, dbPass);
        return conn;
    }
    public void deleteTable(){
        try{
            PreparedStatement ps = conn.prepareStatement("TRUNCATE TABLE "+tableName);
//            ps.setString(1,tableName);
            ps.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    public void editSitePassData(String siteName, String newPass, String oldPass) {
        try{
            PreparedStatement ps = conn.prepareStatement("UPDATE "+tableName+" SET PASSWORD = ? WHERE SITE = ? AND PASSWORD = ?");
//            ps.setString(1,tableName);
            ps.setString(1,newPass);
            ps.setString(2,siteName);
            ps.setString(3,oldPass);
            System.out.println(ps);
            System.out.println("editSitePassData returned "+ps.executeUpdate());
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }
}
