package model;

public class GameFact {
    private int factId;       // Surrogate Key (Primary Key)
    private int dateId;       // Date ID (Foreign Key)
    private int sourceId;     // Source ID (Foreign Key)
    private int gameId;       // Game ID (Foreign Key)
    private double gamePrice; // Price in VND
    private int isDelete;     // Soft delete flag (default 0)

    // Constructor
    public GameFact(int factId, int dateId, int sourceId, int gameId, double gamePrice, int isDelete) {
        this.factId = factId;
        this.dateId = dateId;
        this.sourceId = sourceId;
        this.gameId = gameId;
        this.gamePrice = gamePrice;
        this.isDelete = isDelete;
    }

    // Default Constructor
    public GameFact() {
    }

    // Getters and Setters
    public int getFactId() {
        return factId;
    }

    public void setFactId(int factId) {
        this.factId = factId;
    }

    public int getDateId() {
        return dateId;
    }

    public void setDateId(int dateId) {
        this.dateId = dateId;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public double getGamePrice() {
        return gamePrice;
    }

    public void setGamePrice(double gamePrice) {
        this.gamePrice = gamePrice;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    @Override
    public String toString() {
        return "GameFact{" +
                "factId=" + factId +
                ", dateId=" + dateId +
                ", sourceId=" + sourceId +
                ", gameId=" + gameId +
                ", gamePrice=" + gamePrice +
                ", isDelete=" + isDelete +
                '}';
    }
}
