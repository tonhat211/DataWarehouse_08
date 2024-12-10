package model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GameDim {
    public String gameID;
    public String name;
    public String genre;

    public GameDim(String gameID, String name, String genre) {
        this.gameID = gameID;
        this.name = name;
        this.genre = genre;
    }

	public GameDim(String name, String genre) {
		super();
		this.name = name;
		this.genre = genre;
	}

    
}
