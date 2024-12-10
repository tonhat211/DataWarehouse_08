package controller;

import mail.EmailService;
import model.Config;
import model.Constant;
import model.Log;
import service.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ToDatawarehouseController {
    private Connection connection;
    private static Constant constant = new Constant();

    public Config checkStatus(String action, String database) {
        Config re = null;
        try {
            Connection conn = JDBCUtil.getConnection(database);
            String sql = "SELECT c.id, c.description, c.source, c.directory, c.fileName, c.ext, c.createAt, c.createBy, Date(log.time) as date FROM `configs` c join logs log on c.id = log.configID \n" +
                    "where log.action =? and log.status = 'success' and log.id = (select max(id) from logs)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, action);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String description = rs.getString("description");
                String createAt = rs.getString("createAt");
                String createBy = rs.getString("createBy");
                String source = rs.getString("source");
                String dir = rs.getString("directory");
                String fileName = rs.getString("fileName");
                String ext = rs.getString("ext");
                String date = rs.getString("date");

                re = new Config(id, source, description, dir, fileName, ext, createAt, createBy, date);
            }
            pst.close();
            JDBCUtil.closeConnection(conn);
            return re;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //insert log vào bảng log
    public int insertLog(Log log, String database) {
        int re = 0;
        try {
            Connection conn = JDBCUtil.getConnection(database);
            String sql = "insert into logs (configID,action,user,status) values (?,?,?,?);";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, log.getConfigID());
            pst.setString(2, log.getAction());
            pst.setString(3, log.getUser());
            pst.setString(4, log.getStatus());
            re = pst.executeUpdate();

            pst.close();
            JDBCUtil.closeConnection(conn);
            return re;
        } catch (SQLException e) {
//            throw new RuntimeException(e);
            return re = -1;
        }

    }

    /**
     * Runs the entire ETL process
     */
    public static int runToDatawarehouseProcess(String database) throws SQLException {
        int re = 0;
        Connection connection = null;
        try {
            connection = JDBCUtil.getConnection(database);
            populateDateDim(connection);
            populateSourceDim(connection);
            populateGameDim(connection);



            populateGameFact(connection);
            JDBCUtil.closeConnection(connection);
            System.out.println("All ETL steps executed successfully.");
            return re = 1;
        } catch (SQLException e) {
//            System.out.println("smth");
            System.out.println(e.getMessage());
            return re = -1;
        }

    }

    /**
     * Populates the `date_dim` table
     */
    private static void populateDateDim(Connection connection) throws SQLException {
        String sql = """
                    INSERT INTO date_dim (date_value, year, quarter, month, month_name, week, day, day_name, is_weekend)
                    SELECT DISTINCT 
                        cd.updateDate AS date_value,
                        YEAR(cd.updateDate) AS year,
                        QUARTER(cd.updateDate) AS quarter,
                        MONTH(cd.updateDate) AS month,
                        DATE_FORMAT(cd.updateDate, '%%M') AS month_name,
                        WEEKOFYEAR(cd.updateDate) AS week,
                        DAY(cd.updateDate) AS day,
                        DATE_FORMAT(cd.updateDate, '%%W') AS day_name,
                        CASE 
                            WHEN DATE_FORMAT(cd.updateDate, '%%W') IN ('Saturday', 'Sunday') THEN TRUE 
                            ELSE FALSE 
                        END AS is_weekend
                    FROM %s.clean_data cd
                    WHERE NOT EXISTS (
                        SELECT 1 FROM date_dim d WHERE d.date_value = cd.updateDate
                    );
                """.formatted(constant.STAGING_DB);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.executeUpdate();
            System.out.println("date_dim populated done.");
        }
    }

    /**
     * Populates the `source_dim` table
     */
    private static void populateSourceDim(Connection connection) throws SQLException {
        String sql = """
                    INSERT INTO source_dim (source_name)
                    SELECT DISTINCT cd.source
                    FROM %s.clean_data cd
                    WHERE NOT EXISTS (
                        SELECT 1 FROM source_dim s WHERE s.source_name = cd.source
                    );
                """.formatted(constant.STAGING_DB);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.executeUpdate();
            System.out.println("source_dim populated done.");
        }
    }

    /**
     * Populates the `game_dim` table
     */
    private static void populateGameDim(Connection connection) throws SQLException {
        String sql = """
                    INSERT INTO game_dim (game_name, game_genre)
                    SELECT DISTINCT cd.name, cd.genre
                    FROM %s.clean_data cd
                    WHERE NOT EXISTS (
                        SELECT 1 FROM game_dim g WHERE g.game_name = cd.name
                    );
                """.formatted(constant.STAGING_DB);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.executeUpdate();
            System.out.println("game_dim populated done.");
        }
    }

    /**
     * Populates the `game_fact` table
     */
    private static void populateGameFact(Connection connection) throws SQLException {
        String sql = """
                    INSERT INTO game_fact (date_id, source_id, game_id,game_price)
                    SELECT 
                        d.date_id,
                        s.source_id,
                        g.game_id,
                        c.priceInVND
                    FROM %s.clean_data c
                    JOIN date_dim d ON c.updateDate = d.date_value
                    JOIN source_dim s ON c.source = s.source_name
                    JOIN game_dim g ON c.name = g.game_name
                    WHERE NOT EXISTS (
                        SELECT 1 FROM game_fact f 
                        WHERE f.date_id = d.date_id 
                          AND f.source_id = s.source_id 
                          AND f.game_id = g.game_id
                    );
                """.formatted(constant.STAGING_DB);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.executeUpdate();
            System.out.println("game_fact populated done.");
        }
    }
    public static void main(String[] args) {
        Constant constant = new Constant(); // doc du lieu tu file config
        ToDatawarehouseController toDatawarehouseController = new ToDatawarehouseController();
        EmailService emailService = new EmailService();
        String to = constant.ADMIN_EMAIL;
        String subject = "DataWarehouse project";


//
//        Log fakeLog = new Log(1,constant.TO_STAGING_ACTION,"Thuy Thuy", constant.SUCCESS_STATUS);
//        toDatawarehouseController.insertLog(fakeLog,constant.CONTROL_DB);


        //kiem tra status cua server
        Config config = toDatawarehouseController.checkStatus(constant.TO_STAGING_ACTION, constant.CONTROL_DB);

        if (config != null) { // neu da co log hop ve va co the bat dau thuc hien ghi vao staging
            int re;
            //ghi log toDatawarehouse status Processing
            Log log = new Log(config.id, constant.TO_WAREHOUSE_ACTION, config.createBy, constant.PROCESSING_STATUS);
            re = toDatawarehouseController.insertLog(log, constant.CONTROL_DB);
            if (re == 1) {
                System.out.println("Da ghi log toDatawarehouse processing");
            }

            try {
                re = toDatawarehouseController.runToDatawarehouseProcess(constant.WAREHOUSE_DB);
                if (re == 1) {

                    System.out.println("ghi vao " + constant.WAREHOUSE_DB + " thanh cong");
                    //tao va ghi log khi thanh cong
                    log = new Log(config.id, constant.TO_WAREHOUSE_ACTION, config.createBy, constant.SUCCESS_STATUS);
                    re = toDatawarehouseController.insertLog(log, constant.CONTROL_DB);
                    if (re == 1) System.out.println("Da ghi log processing");
                    //mail khi thanh cong
                    String mess = "ghi vao " + constant.WAREHOUSE_DB + " thanh cong";
                    emailService.send(to, subject, mess);
                } else {

                    System.out.println("ghi vao " + constant.WAREHOUSE_DB + " that bai");
                    //mail khi that bai
                    String mess = "ghi vao " + constant.WAREHOUSE_DB + " that bai";
                    emailService.send(to, subject, mess);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);

            }
        } else {
            System.out.println("Chua the thu hien do chu den lich");
        }

    }
}
