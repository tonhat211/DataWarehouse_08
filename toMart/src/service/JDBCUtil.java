package service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

import model.Constant;

public class JDBCUtil {
//    String database = "staging_db";
    public static Connection getConnection(String database) {
        Connection c = null;
        Constant constant = new Constant();

        try {
//            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());

            String url = "jdbc:mySQL://localhost:3306/"+database;
            String user = constant.ADMIN_USER;
            String pwd =constant.ADMIN_PWD;

            c = DriverManager.getConnection(url,user,pwd);

        } catch (SQLException e){
            e.printStackTrace();
        }
        return c;
    }

    public static void closeConnection(Connection c){
        try {
            if(c!=null){
                c.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void printInfo(Connection c){
        try {

            if (c != null) {
                DatabaseMetaData mtdt = c.getMetaData();
                System.out.println(mtdt.getDatabaseProductName());
                System.out.println(mtdt.getDatabaseProductVersion());
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
