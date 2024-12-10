import requests
from bs4 import BeautifulSoup
import time
import csv
from datetime import datetime
import configparser
import mysql.connector
import os
import smtplib
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart

def connect(filename):
    #2. Đọc file control_db.ini để lấy cấu hình
    db_control = configparser.ConfigParser()
    db_control.read(filename)
    host = db_control.get('mysql', 'host')
    user = db_control.get('mysql', 'user')
    password = db_control.get('mysql', 'password')
    database = db_control.get('mysql', 'database')
    try:
        #3.kết nối db Control
        cnx = mysql.connector.connect(user=user, password=password, host=host, database=database)
        if cnx.is_connected():
            print('Connected to MySQL database: ' + cnx.database)
            return cnx
    except:
        print('connect fail')

def get_search_results(api_url, headers):
    try:
         #7. gửi request tới source
        response = requests.get(api_url, headers=headers, timeout=30)
        response.raise_for_status()
        data = response.json()
        results_html = data.get('results_html', '')
        return response.status_code,results_html
    except requests.exceptions.RequestException as e:
        print(f"Lỗi khi truy cập API tìm kiếm: {e}")
    except ValueError:
        print("Không thể phân tích JSON từ phản hồi của API tìm kiếm.")
    return 400,''

def parse_search_results(html_content):
    soup = BeautifulSoup(html_content, 'html.parser')
    game_links = soup.find_all('a', class_='search_result_row')

    games = []
    for game in game_links:
        appid = game.get('data-ds-appid', 'Unknown')
        title_tag = game.find('span', class_='title')
        title = title_tag.text.strip() if title_tag else "Unknown"
        price_tag = game.find('div', class_='discount_final_price')
        price = price_tag.text.strip() if price_tag else "Unknown"
        genres = []
        current_time = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        games.append({
            "title": title,
            "price": price,
            "appid": appid,
            "genres": genres,
            "timestamp": current_time
        })
    return games

def parse_price(price):
    if price.lower() in ["free", "Unknown"]:
        return price, ""

    if "₫" in price or "VND" in price.upper():
        amount = price.replace("₫", "").replace("VND", "").strip()
        amount = amount.replace(".", "").strip()  
        return amount, "VNĐ"

    parts = price.split()
    if len(parts) > 1:
        amount = " ".join(parts[:-1])  
        currency = parts[-1]          
        return amount, currency

    return price, ""

def get_game_genres(appid, headers):
    url = f"https://store.steampowered.com/apphover/{appid}?review_score_preference=0&u=1829304735&pagev6=true"
    try:
        response = requests.get(url, headers=headers, timeout=10)
        response.raise_for_status()
        soup = BeautifulSoup(response.text, 'html.parser')
        genres_container = soup.find('div', class_='hover_tag_row')
        if not genres_container:
            return ["Unknown"]
        genre_tags = genres_container.find_all('div', class_='app_tag')
        genres = [tag.text.strip() for tag in genre_tags if tag.text.strip()]
        return  genres if genres else  ["Unknown"]
    except requests.exceptions.RequestException as e:
        print(f"Lỗi khi truy cập App Hover cho AppID {appid}: {e}")
    except Exception as e:
        print(f"Lỗi khi phân tích HTML App Hover cho AppID {appid}: {e}")
    return ["Unknown"]

def writeFileLog(status='', note=''):
    current_date = datetime.now().strftime("%d%m%Y")
    save_directoryEr = r"D:\DataWareHouse\Project"
    file_err = fr"{save_directoryEr}\{current_date}_steamPowered.txt"
    err = "connect unsuccess"
    print(f"Lỗi connect db : {err}")
    with open(file_err, 'a') as file:
        file.write(f'Error: {str(err)}\n')
    print(note)

def writeLog(name, cursor, conn,idConf=1, status=''):
    action = "crawl"
    current_time = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    query = "INSERT INTO logs (configID, action, time, user, status) VALUES (%s, %s, %s, %s, %s)"
    values = (idConf, action, current_time, name,status)
    cursor.execute(query,values)
    conn.commit()

def save_to_csv(games, filename):
    fieldnames = ['ID Game', 'Game name', 'Price', 'Currency', 'Genre', 'timestamp']
    try:
        with open(filename, 'w', newline='', encoding='utf-8-sig') as csvfile:
            writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
            writer.writeheader()
            for game in games:
                amount, currency = parse_price(game['price'])
                writer.writerow({
                    "ID Game": game['appid'],
                    "Game name": game['title'],
                    "Price": amount,
                    "Currency": currency,
                    "Genre": ', '.join(game['genres']),
                    "timestamp": game['timestamp']
                })
        print(f"Đã lưu dữ liệu vào tệp CSV: {filename}")
    except Exception as e:
        print(f"Lỗi khi lưu dữ liệu vào tệp CSV: {e}")

def get_game(source,directory, fileName, ext):
    headers = {
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                      "AppleWebKit/537.36 (KHTML, like Gecko) "
                      "Chrome/115.0.0.0 Safari/537.36",
        "Accept": "application/json",
        "Referer": "https://store.steampowered.com/",
    }
    
    print("Đang lấy dữ liệu từ API tìm kiếm Steam...")
    # 6 .Lấy dữ liệu từ trường source trong bảng config của control db
    statusCode,search_html = get_search_results(source, headers)
    print(statusCode)
    if statusCode != 200:
        print("Không thể tiếp tục do không lấy được dữ liệu tìm kiếm.")
        return statusCode
    print("Đang phân tích dữ liệu tìm kiếm...")
    games = parse_search_results(search_html)
    for index, game in enumerate(games, start=1):
        appid = game['appid']
        if appid == "Unknown":
            game['genres'] = ["Unknown"]
            continue
        genres = get_game_genres(appid, headers)
        #genres= "hihi"
        game['genres'] = genres
        print(f"Đã lấy thể loại cho trò chơi {index}: '{game['title']}' (AppID: {appid}): {', '.join(genres)}")
        time.sleep(1)
    #9. Tạo file csv có dạng steamPowered_dd-mm-yyy.csv và lưu vào đường dẫn theo trường directory trong bảng config của control db
    current_date = datetime.now().strftime("%Y-%m-%d")
    csv_filename = fileName+"_"+ current_date+"."+ext
    save_to_csv(games,directory+'\\'+ csv_filename)
    return statusCode
    
def countdown(t, mess=''):
    print(mess)
    while t:
        minis, secs = divmod(t, 60)
        timer = '{:02d}:{:02d}'.format(minis, secs)
        print("\r", timer, end="")
        time.sleep(1)
        t -= 1
    print("\r", '00:00', end="")
    print()

def sendMail(email_sender,email_receiver,app_password, body, subject):

    message = MIMEMultipart()
    message['From'] = email_sender
    message['To'] = email_receiver
    message['Subject'] = subject

    message.attach(MIMEText(body, 'plain'))

    try:
    # Kết nối đến server SMTP của Gmail
        server = smtplib.SMTP('smtp.gmail.com', 587)
        server.starttls()  # Bảo mật TLS
        server.login(email_sender, app_password)
        server.sendmail(email_sender, email_receiver, message.as_string())
        print("Gửi email thành công!")
    except Exception as e:
        print(f"Đã xảy ra lỗi: {e}")
    finally:
        server.quit()

def main():
    
    count = 0
    current_date = datetime.now().strftime("%d%m%Y")
    current_dir = os.path.dirname(__file__)
    globalConfig = configparser.ConfigParser()
    isContinue = True

     #1. Đọc file config.ini để lấy thông số cấu hình
    globalConfig.read(current_dir + "/config.ini")
    idConfig = int(globalConfig.get('config', 'idConfig'))
    countDownTime = int(globalConfig.get('config', 'countDownTime'))
    loopNum = int(globalConfig.get('config', 'loopNum'))
    logFileDirectory = globalConfig.get('config', 'logFileDirectory')
    file_err = fr"{logFileDirectory}\{current_date}_steam_Powered_Logs.txt"

    email_sender = globalConfig.get('config', 'email_sender')
    app_password = globalConfig.get('config', 'app_password')
    email_receiver = globalConfig.get('config', 'email_receiver')

    while True:
        #3.kết nối db Control
        fileControl =current_dir+ '\\dbconfig\\control_db.ini'
        conn = connect(fileControl)
        #4. Kiểm tra kết nối cơ sở dữ liệu
        if conn is not None:
            try:
                cursor = conn.cursor(dictionary=True)
                #5. Ghi log kết nối thành công và ghi vào 1 dòng với action=crawl và status= PROCESSING ở bảng logs và sendMail đang thực hiện
                selectSourceQuery = 'SELECT source, description, directory, fileName,createBy, ext  FROM configs WHERE id = %s'
                cursor.execute(selectSourceQuery, (idConfig,))
                config = cursor.fetchone()
                writeLog(config['createBy'] , cursor, conn,idConfig,'PROCESSING')
                sendMail(email_sender,email_receiver,app_password,"PROCESSING","Get Data Steam")
                while (True):
                    #6,7
                    statusCode = get_game(config['source'], config['directory'], config['fileName'], config['ext'])
                    #8. kiểm tra kết nối tới source (respone.staus=200)
                    if statusCode == 200:
                        #10. Thêm một dòng log với action=crawl và status= SUCCESS vào log của control_db và sendMail lấy dữ liệu thành công
                        writeLog(config['createBy'], cursor,conn,idConfig, 'SUCCESS')
                        sendMail(email_sender,email_receiver,app_password,"CRAWL_SUCCESS","Get Data Steam")
                        print("Dữ liệu đã được thêm thành công!")
                        break
                    else:
                        #8.1 Ghi log lấy dữ liệu thất bại và sendMail lấy dữ liệu thất bại
                        writeLog(config['createBy'], cursor,conn,idConfig, 'CONNECT_SOURCE_FAIL')
                        sendMail(email_sender,email_receiver,app_password,"CONNECT_SOURCE_FAIL","Get Data Steam")
                        print("Dữ liệu đã được thêm thất bại!")
                        print('Get data again after:')
                        countdown(countDownTime)
                        count += 1
                        #8.2 kiểm tra lần lặp có lớn hơn hoặc =10 không
                        if count >= loopNum:
                            #8.3 thêm 1 log với action=crawl và status=FAIL vào logs của control_db và send mail không lấy dữ liệu thành công
                            writeLog(config['createBy'], cursor,conn,idConfig, 'FAIL')
                            sendMail(email_sender,email_receiver,app_password,"CRAWL_FAIL","Get Data Steam")
                            break 
                        else:
                            continue
                break
            except mysql.connector.Error as err:
                print(f"Lỗi thêm dữ liệu: {err}")
                with open(file_err, 'a') as file:
                    file.write(f'Error: {str(err)}\n')
                continue

            finally:
                # Đóng cursor và kết nối
                cursor.close()
                conn.close()

        else:
            count += 1
            #4.1 kiểm tra lần lặp có lớn hơn hoặc = 10 không?
            if count >= loopNum:
                #4.2 ghi dữ liệu lỗi vào đường dẫn theo trường logFileDirectory trong config.ini
                err = "connect control_db unsuccess"
                print(f"Lỗi connect db : {err}")
                with open(file_err, 'a') as file:
                    file.write(f'Error: {str(err)}\n')
                break
            print('reconnect last:')
            countdown(countDownTime)
            print('start reconnect') 


if __name__ == "__main__":
    main()
 