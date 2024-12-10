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

    public String getStatus() {
        return status;
    }

    public String getAction() {
        return action;
    }

    public String getUser() {
        return user;
    }
}
