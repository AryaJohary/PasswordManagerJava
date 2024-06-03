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

    public SQLBackend() throws SQLException, ClassNotFoundException {
        conn = getConnection();
        PreparedStatement ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS "+tableName+" (SITE VARCHAR(8000),PASSWORD VARCHAR(8000)) ");
        ps.executeUpdate();
        spitOutAllTableRows();
    }

    public void spitOutAllTableRows() {
        try {
            data.clear();
//            System.out.println("spitOutAllTableRows called");
//            System.out.println("spit out all tables called");
//            System.out.println("current " + tableName + " is:");

            try (PreparedStatement selectStmt = conn.prepareStatement(
                    "SELECT * from " + tableName, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                 ResultSet rs = selectStmt.executeQuery()) {
                if (!rs.isBeforeFirst()) {
//                    System.out.println("no rows found");
                }
                else {
//                    System.out.println("types:");
                    temp = new ArrayList<>();
                    for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
//                        System.out.print(rs.getMetaData().getColumnName(i + 1) + ":" + rs.getMetaData().getColumnTypeName(i + 1) + " ");
                        temp.add(rs.getMetaData().getColumnName(i + 1));
                    }
                    data.add(new ArrayList<>(temp));
//                    System.out.println();
                    while (rs.next()) {
                        temp = new ArrayList<>();
                        for (int i = 1; i < rs.getMetaData().getColumnCount() + 1; i++) {
//                            System.out.print(" " + rs.getMetaData().getColumnName(i) + "=" + rs.getObject(i));
                            temp.add(rs.getObject(i).toString());
                        }
                        data.add(new ArrayList<>(temp));
//                        System.out.println("");
                    }
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean addSitePassData(String siteName, String password){
        int ret = 0;
        try{
            PreparedStatement ps = conn.prepareStatement("INSERT INTO "+tableName+" VALUES (?,?)");
            ps.setString(1,siteName);
            ps.setString(2,password);
            ret = ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ret > 0;
    }
    public boolean deleteSitePassData(String siteName, String password){
        int ret = 0;
        try{
            PreparedStatement ps = conn.prepareStatement("DELETE FROM "+tableName+" WHERE SITE = ? AND PASSWORD = ?");
            ps.setString(1,siteName);
            ps.setString(2,password);
            ret = ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ret > 0;
    }
    public Connection getConnection() throws ClassNotFoundException, SQLException {
        Connection conn;
        String dbUrl = "jdbc:mysql://localhost:3306/";
        String dbDriver = "com.mysql.cj.jdbc.Driver";
        Class.forName(dbDriver);
        conn = DriverManager.getConnection(dbUrl + dbName, dbUser, dbPass);
        return conn;
    }
}
