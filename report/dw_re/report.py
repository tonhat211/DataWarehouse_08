# pip install mysql-connector-python
# pip install pandas
# pip install flask mysql-connector-python pandas
import webbrowser
import threading
from flask import Flask, render_template, request, jsonify
import mysql.connector

app = Flask(__name__)

# Hàm kết nối tới cơ sở dữ liệu
def get_db_connection():
    conn = mysql.connector.connect(
        host='localhost',
        user='root',
        password='',
        database='mart_db'
    )
    return conn

# API: Hiển thị dữ liệu với bộ lọc năm và tháng
@app.route('/api/report', methods=['GET'])
def get_reports():
    # Lấy giá trị năm và tháng từ query parameters
    year = request.args.get('year')
    month = request.args.get('month')

    selected_year = int(year) if year and year.isdigit() else None
    selected_month = int(month) if month and month.isdigit() else None

    connection = get_db_connection()
    cursor = connection.cursor(dictionary=True)

    # Tạo câu truy vấn dựa trên bộ lọc
    query = """
        SELECT year, day, week, month, game_name, game_genre, avg_price, max_price, min_price, total_sales 
        FROM game_sales_report
    """
    conditions = []
    params = []

    if selected_year:
        conditions.append("year = %s")
        params.append(selected_year)

    if selected_month and selected_year:  # Chỉ lọc tháng khi đã chọn năm
        conditions.append("month = %s")
        params.append(selected_month)

    if conditions:
        query += " WHERE " + " AND ".join(conditions)

    cursor.execute(query, tuple(params))
    data = cursor.fetchall()
    connection.close()

    # Trả dữ liệu ra bảng HTML sử dụng template
    return render_template(
        'report.html',
        reports=data,
        selected_year=selected_year,
        selected_month=selected_month
    )


@app.route('/api/chart_data', methods=['GET'])
def get_chart_data():
    # Lấy giá trị năm và tháng từ query parameters
    year = request.args.get('year')
    month = request.args.get('month')

    selected_year = int(year) if year and year.isdigit() else None
    selected_month = int(month) if month and month.isdigit() else None

    connection = get_db_connection()
    cursor = connection.cursor(dictionary=True)

    # Tạo câu truy vấn dựa trên bộ lọc
    query = """
        SELECT year, month, game_name, avg_price, max_price, min_price 
        FROM game_sales_report
    """
    conditions = []
    params = []

    if selected_year:
        conditions.append("year = %s")
        params.append(selected_year)

    if selected_month and selected_year:  # Chỉ lọc tháng khi đã chọn năm
        conditions.append("month = %s")
        params.append(selected_month)

    if conditions:
        query += " WHERE " + " AND ".join(conditions)

    cursor.execute(query, tuple(params))
    data = cursor.fetchall()
    connection.close()

    # Trả dữ liệu dưới dạng JSON
    return jsonify(data)


# Hàm mở trình duyệt tự động
def open_browser():
    webbrowser.open_new("http://localhost:5000/api/report")

# Chạy Flask trong một luồng riêng biệt
def run_app():
    app.run(debug=True, use_reloader=False)

if __name__ == '__main__':
    # Chạy Flask server trong một luồng riêng biệt
    thread = threading.Thread(target=run_app)
    thread.start()

    # Đợi một chút để server Flask khởi động và sau đó mở trình duyệt
    import time
    time.sleep(2)  # Chờ 2 giây để Flask khởi động
    open_browser()

# python app.py
# http://localhost:5000/api/report
# http://localhost:5000/api/report/1