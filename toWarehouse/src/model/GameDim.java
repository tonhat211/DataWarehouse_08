package model;

public class GameDim {
    private int gameId;          // Surrogate Key (Primary Key)
    private String gameName;     // Name of the game
    private String gameGenre;    // Genre of the game

    // Constructor
    public GameDim(int gameId, String gameName, String gameGenre) {
        this.gameId = gameId;
        this.gameName = gameName;
        this.gameGenre = gameGenre;
    }

    // Default Constructor
    public GameDim() {
    }

    // Getters and Setters
    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getGameGenre() {
        return gameGenre;
    }

    public void setGameGenre(String gameGenre) {
        this.gameGenre = gameGenre;
    }

    @Override
    public String toString() {
        return "GameDim{" +
                "gameId=" + gameId +
                ", gameName='" + gameName + '\'' +
                ", gameGenre='" + gameGenre + '\'' +
                '}';
    }
}
