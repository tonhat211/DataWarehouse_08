package model;

import java.time.LocalDateTime;

public class Log {
    public int configID;
    public String action;
    public LocalDateTime time;
    public String user;
    public String status;

    public Log(int configID, String action, String user, String status) {
        this.configID = configID;
        this.action = action;
        this.user = user;
        this.status = status;
    }

	public int getConfigID() {
		return configID;
	}

	public void setConfigID(int configID) {
		this.configID = configID;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

    
}
