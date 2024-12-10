package model;

import java.io.*;
import java.util.Properties;

public class Constant {
    public String CONTROL_DB = "control_db";
    public String WAREHOUSE_DB = "warehouse_db";
    public String MART_DB = "mart_db";
    public String TIME_DIM = "time_dim";
    public String GAME_DIM = "game_dim";
    public String GAME_PRICE_FACT = "game_price_fact";
    public String SUCCESS_STATUS = "SUCCESS";
    public String PROCESSING_STATUS = "PROCESSING";
    public String FAIL_STATUS = "FAIL";
    public String TO_MART_ACTION = "TO_MART";
    public String TO_WAREHOUSE_ACTION = "TO_WAREHOUSE";
    public String ADMIN_EMAIL = "21130463@st.hcmuaf.edu.vn";
    public String ADMIN_USER = "admin";
    public String ADMIN_PWD = "admin";

    public Constant() {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream("D:\\DataWarehouse\\config\\config.properties");
            prop.load(input);

            CONTROL_DB = prop.getProperty("control_db");
            WAREHOUSE_DB = prop.getProperty("warehouse_db");
            MART_DB = prop.getProperty("mart_db");
            TIME_DIM = prop.getProperty("time_dim");
            GAME_DIM = prop.getProperty("game_dim");
            GAME_PRICE_FACT = prop.getProperty("game_price_fact");
            TO_MART_ACTION= prop.getProperty("mart_action");
            TO_WAREHOUSE_ACTION= prop.getProperty("warehouse_action");
            ADMIN_EMAIL= prop.getProperty("admin_email");
            ADMIN_USER= prop.getProperty("admin_user");
            ADMIN_PWD= prop.getProperty("admin_pwd");

            System.out.println("doc file config.properties vao Constant");
            System.out.println("CONTROL_DB: " + CONTROL_DB);
            System.out.println("WAREHOUSE_DB: " + WAREHOUSE_DB);
            System.out.println("MART_DB: " + MART_DB);
            System.out.println("TIME_DIM: " + TIME_DIM);
            System.out.println("GAME_DIM: " + GAME_DIM);
            System.out.println("GAME_PRICE_FACT: " + GAME_PRICE_FACT);
            System.out.println("TO_MART_ACTION: " + TO_MART_ACTION);
            System.out.println("TO_WAREHOUSE_ACTION: " + TO_WAREHOUSE_ACTION);


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

}

