package model;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Constant {
    public String CONTROL_DB = "control_db";
    public String STAGING_DB = "staging_db";
    public String WAREHOUSE_DB = "warehouse_db";
    public String RAW_DATA = "raw_data";
    public String CLEAN_DATA = "clean_data";
    public String SUCCESS_STATUS = "SUCCESS";
    public String PROCESSING_STATUS = "PROCESSING";
    public String CRAWL_ACTION = "CRAWL";
    public String TO_STAGING_ACTION = "TO_STAGING";
    public String TO_WAREHOUSE_ACTION = "TO_WAREHOUSE";
    public String ADMIN_USER = "username";
    public String ADMIN_PWD = "password";
    public String ADMIN_EMAIL = "email";


    public Constant() {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream("D:\\DataWarehouse\\config\\config.properties");
            prop.load(input);

            CONTROL_DB = prop.getProperty("control_db");
            STAGING_DB = prop.getProperty("staging_db");
            WAREHOUSE_DB = prop.getProperty("warehouse_db");
            RAW_DATA = prop.getProperty("raw_data");
            CLEAN_DATA = prop.getProperty("clean_data");
            CRAWL_ACTION = prop.getProperty("crawl_action");
            TO_STAGING_ACTION = prop.getProperty("staging_action");
            TO_WAREHOUSE_ACTION = prop.getProperty("warehouse_action");
            ADMIN_USER = prop.getProperty("admin_user");
            ADMIN_PWD = prop.getProperty("admin_pwd");
            ADMIN_EMAIL = prop.getProperty("admin_email");
//            System.out.println("doc file config.properties vao Constant");
//            System.out.println("CONTROL_DB: " + CONTROL_DB);
//            System.out.println("STAGING_DB: " + STAGING_DB);
//            System.out.println("dw: " + WAREHOUSE_DB);
//            System.out.println("RAW_DATA: " + RAW_DATA);
//            System.out.println("CLEAN_DATA: " + CLEAN_DATA);


        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void main(String[] args) {
        Constant constant = new Constant();
//        System.out.println(constant.TO_WAREHOUSE_ACTION);
    }
}

