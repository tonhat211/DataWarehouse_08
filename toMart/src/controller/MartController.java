package controller;

import model.Constant;
import model.Log;
import service.JDBCUtil;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import JavaMail.EmailService;
import JavaMail.IJavaMail;

public class MartController {

	private final Constant constant;

	public MartController(Constant constant) {
		this.constant = constant;
	}

	public void executeToMart() {
		// 1. kết nối dữ liệu với warehouse_db, control_db và mart_db
		try (Connection controlConn = JDBCUtil.getConnection(constant.CONTROL_DB);
				Connection warehouseConn = JDBCUtil.getConnection(constant.WAREHOUSE_DB);
				Connection martConn = JDBCUtil.getConnection(constant.MART_DB)) {

			// fake log
//			Log fakeLog = new Log(1, constant.TO_WAREHOUSE_ACTION, "dang", constant.SUCCESS_STATUS);
//			insertLog(controlConn, fakeLog);

			// 2. Kiểm tra log
			Log latestLog = getLatestLog(controlConn);

			if (latestLog == null) {
				System.out.println("rỗng");
				return;
			}
			System.out.println(latestLog.getUser() + "\t" + latestLog.getAction() + "\t" + latestLog.getStatus());
			if ((!latestLog.action.equals(constant.TO_WAREHOUSE_ACTION)
					|| !latestLog.status.equals(constant.SUCCESS_STATUS))) {
				System.out.println("Không có log hợp lệ để thực hiện toMart");
				return;
			}
			System.out.println("có log hợp lệ để thực hiện toMart");
			// 3. Gửi email thông báo bắt đầu
            IJavaMail emailService = new EmailService();
            emailService.send("Bat dau thuc hien toMart", "He thong dang xu ly...");

			// 4. Ghi log trạng thái Processing
			Log processingLog = new Log(latestLog.getConfigID(), constant.TO_MART_ACTION, constant.ADMIN_USER,
					constant.PROCESSING_STATUS);
			insertLog(controlConn, processingLog);

			// 5. kiểm tra Transform dữ liệu từ warehouse_db và chèn vào mart_db có thành công hay không
			boolean check = transformAndInsertData(warehouseConn, martConn);
			
			if(!check) {
				// 6. ghi log với trạng thái fail
				Log failLog = new Log(latestLog.getConfigID(), constant.TO_MART_ACTION, constant.ADMIN_USER,
						constant.FAIL_STATUS);
				insertLog(controlConn, failLog);
				//7. gửi mail báo lỗi
				emailService.send("Loi toMart", "He thong dang loi o Transform du lieu tu warehouse_db va chen vao mart_db ...");
				return;
			}

			// 8. Ghi log trạng thái Success
			Log successLog = new Log(latestLog.getConfigID(), constant.TO_MART_ACTION, constant.ADMIN_USER,
					constant.SUCCESS_STATUS);
			insertLog(controlConn, successLog);

			// 9. Gửi email thông báo hoàn thành
			 //IJavaMail emailService = new EmailService();
			 emailService.send("Hoan thanh toMart", "Qua trinh toMart da thanh cong!");
			// EmailUtil.sendEmail("admin@example.com", "Hoàn thành toMart", "Quá trình
			// toMart đã thành công!");

		} catch (SQLException e) {
			IJavaMail emailService = new EmailService();
            emailService.send("Loi toMart", "He thong dang loi ket noi toi CSDL...");
			System.out.println("kết nối db không thành công");
		}
	}

	private Log getLatestLog(Connection conn) throws SQLException {
		String query = "SELECT * FROM logs ORDER BY id DESC LIMIT 1";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return new Log(rs.getInt("configId"), rs.getString("action"), rs.getString("user"),
						rs.getString("status"));
			}
		}
		return null;
	}

	private void insertLog(Connection conn, Log log) throws SQLException {
		String query = "INSERT INTO logs (configID, action, time, user, status) VALUES (?, ?, NOW(), ?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, log.getConfigID());
			ps.setString(2, log.getAction());
			ps.setString(3, log.getUser());
			ps.setString(4, log.getStatus());
			ps.executeUpdate();
		}
	}

	private boolean transformAndInsertData(Connection warehouseConn, Connection martConn) throws SQLException {
		// 5.1 Transform dữ liệu từ warehouse_db 
		String queryWarehouse = """
			    SELECT d.date_id, d.year, d.quarter, d.month, d.week, d.month_name, d.day, d.day_name, d.is_weekend, g.game_id, g.game_name, g.game_genre,f.fact_id
			    FROM game_fact f
			    JOIN date_dim d ON f.date_id = d.date_id
			    JOIN game_dim g ON f.game_id = g.game_id
			    WHERE f.is_delete = 0
			    GROUP BY d.date_id, d.year, d.quarter, d.month, d.week, d.month_name,d.day, d.day_name, d.is_weekend, g.game_id, g.game_name, g.game_genre, f.fact_id;
			""";
		
		String queryWarehouse2 = """
				    SELECT
				        g.game_id,
				        AVG(f.game_price) AS avg_price,
				        MAX(f.game_price) AS max_price,
				        MIN(f.game_price) AS min_price,
				        COUNT(*) AS total_sales
				    FROM
				        game_fact f
				    JOIN
				        game_dim g ON f.game_id = g.game_id
				    WHERE
				        f.is_delete = 0
				    GROUP BY
				        g.game_id;
				""";

		// 5.2 Insert dữ liệu vào database Mart
		try (Statement stmtWarehouse = warehouseConn.createStatement();
		         ResultSet rs = stmtWarehouse.executeQuery(queryWarehouse);
		         Statement stmtWarehouse2 = warehouseConn.createStatement();
		         ResultSet rs2 = stmtWarehouse2.executeQuery(queryWarehouse2)) {
			

			String insertTimeDim = "INSERT INTO time_dim (time_id, year, quarter, month, week, month_name, day, day_name, is_weekend) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE month_name = VALUES(month_name), day_name = VALUES(day_name), is_weekend = VALUES(is_weekend)";
			String insertGameDim = "INSERT INTO game_dim (game_id, game_name, game_genre) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE game_name = VALUES(game_name), game_genre = VALUES(game_genre)";
			String insertFact = "INSERT INTO game_price_fact (fact_id, time_id, game_id, avg_price, max_price, min_price, total_sales) VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE avg_price = VALUES(avg_price), max_price = VALUES(max_price), min_price = VALUES(min_price), total_sales = VALUES(total_sales)";
			String insertReport = "INSERT INTO game_sales_report (report_id, year, day, week, month, game_name, game_genre, avg_price, max_price, min_price, total_sales) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE avg_price = VALUES(avg_price), max_price = VALUES(max_price), min_price = VALUES(min_price), total_sales = VALUES(total_sales)";

			// Tạo một Map để lưu thông tin thống kê từ queryWarehouse2
			Map<Integer, Map<String, Object>> gameStats = new HashMap<>();
			// Lấy dữ liệu từ rs2 và lưu vào gameStats
			while (rs2.next()) {
			    Map<String, Object> stats = new HashMap<>();
			    stats.put("avg_price", rs2.getDouble("avg_price"));
			    stats.put("max_price", rs2.getDouble("max_price"));
			    stats.put("min_price", rs2.getDouble("min_price"));
			    stats.put("total_sales", rs2.getInt("total_sales"));
			    gameStats.put(rs2.getInt("game_id"), stats);
			}
			
			while (rs.next()) {
				// Insert vào time_dim
				try (PreparedStatement psTime = martConn.prepareStatement(insertTimeDim,
						Statement.RETURN_GENERATED_KEYS)) {
					psTime.setInt(1, rs.getInt("date_id"));
					psTime.setInt(2, rs.getInt("year"));
					psTime.setInt(3, rs.getInt("quarter"));
					psTime.setInt(4, rs.getInt("month"));
					psTime.setInt(5, rs.getInt("week"));
					psTime.setString(6, rs.getString("month_name"));
					psTime.setInt(7, rs.getInt("day"));
					psTime.setString(8, rs.getString("day_name"));
					psTime.setBoolean(9, rs.getBoolean("is_weekend"));
					psTime.executeUpdate();

				}catch (SQLException e) {
	                //e.printStackTrace();
	                return false; // Trả về false nếu có lỗi khi insert vào time_dim
	            }

				// Insert vào game_dim
				try (PreparedStatement psGame = martConn.prepareStatement(insertGameDim)) {
					psGame.setInt(1, rs.getInt("game_id"));
					psGame.setString(2, rs.getString("game_name"));
					psGame.setString(3, rs.getString("game_genre"));
					psGame.executeUpdate();
				}catch (SQLException e) {
	                //e.printStackTrace();
	                return false; // Trả về false nếu có lỗi khi insert vào game_dim
	            }

				

				// Duyệt qua rs từ queryWarehouse để chèn dữ liệu
				int gameId = rs.getInt("game_id");

			    // Lấy thông tin thống kê từ gameStats
			    Map<String, Object> stats = gameStats.get(gameId);
			    if (stats != null) {
			        // Insert vào game_price_fact
			        try (PreparedStatement psFact = martConn.prepareStatement(insertFact)) {
			            psFact.setInt(1, rs.getInt("fact_id"));
			            psFact.setInt(2, rs.getInt("date_id"));
			            psFact.setInt(3, gameId);
			            psFact.setDouble(4, (Double) stats.get("avg_price"));
			            psFact.setDouble(5, (Double) stats.get("max_price"));
			            psFact.setDouble(6, (Double) stats.get("min_price"));
			            psFact.setInt(7, (Integer) stats.get("total_sales"));
			            psFact.executeUpdate();
			        }catch (SQLException e) {
	                    //e.printStackTrace();
	                    return false; // Trả về false nếu có lỗi khi insert vào game_price_fact
	                }
			        
			     // Insert vào game_sales_report
					try (PreparedStatement psReport = martConn.prepareStatement(insertReport,     
							Statement.RETURN_GENERATED_KEYS)) {
						psReport.setInt(1, rs.getInt("fact_id"));
						psReport.setInt(2, rs.getInt("year"));
						psReport.setInt(3, rs.getInt("day"));
						psReport.setInt(4, rs.getInt("week"));
						psReport.setInt(5, rs.getInt("month"));
						psReport.setString(6, rs.getString("game_name"));
						psReport.setString(7, rs.getString("game_genre"));
						psReport.setDouble(8, (Double) stats.get("avg_price"));
			            psReport.setDouble(9, (Double) stats.get("max_price"));
			            psReport.setDouble(10, (Double) stats.get("min_price"));
			            psReport.setInt(11, (Integer) stats.get("total_sales"));
						psReport.executeUpdate();

					}catch (SQLException e) {
	                   // e.printStackTrace();
	                    return false; // Trả về false nếu có lỗi khi insert vào game_sales_report
	                }
			    } else {
			        System.err.println("Không tìm thấy thống kê cho game_id: " + gameId);
			        return false; // Trả về false nếu không tìm thấy thống kê cho game_id
			    }

			}
			return true; // Trả về true nếu không có lỗi và tất cả các thao tác thành công
		}catch (SQLException e) {
	        //e.printStackTrace();
	        return false; // Trả về false khi có lỗi
	    }
	}

	public static void main(String[] args) {
		// Khởi tạo Constant để đọc các cấu hình
		Constant constant = new Constant();

		// Khởi tạo MartController
		MartController martController = new MartController(constant);

		// Thực thi giai đoạn toMart
		System.out.println("Bắt đầu quá trình chuyển dữ liệu từ Warehouse sang Mart...");
		martController.executeToMart();
		System.out.println("Quá trình hoàn tất.");
	}

}
