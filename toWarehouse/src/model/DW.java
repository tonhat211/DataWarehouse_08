package model;

public class DW {
    public int date_id;
    public int source_id;
    public int gameID;
    public double priceInVND;
    public String genre;
    public String source;

    public DW(int date_id, int source_id, int gameID, double priceInVND, String genre, String source) {
        this.date_id = date_id;
        this.source_id = source_id;
        this.gameID = gameID;
        this.priceInVND = priceInVND;
        this.genre = genre;
        this.source = source;
    }
}
