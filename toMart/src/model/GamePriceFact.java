package model;

public class GamePriceFact {
	public String factID;
	public String timeID;
	public String gameID;
	public double avgPrice;
	public double maxPrice;
	public double minPrice;
	public int totalSales;

	public GamePriceFact(String factID, String timeID, String gameID, double avgPrice, double maxPrice, double minPrice,
			int totalSales) {
		super();
		this.factID = factID;
		this.timeID = timeID;
		this.gameID = gameID;
		this.avgPrice = avgPrice;
		this.maxPrice = maxPrice;
		this.minPrice = minPrice;
		this.totalSales = totalSales;
	}

	public GamePriceFact(String timeID, String gameID, double avgPrice, double maxPrice, double minPrice,
			int totalSales) {
		super();
		this.timeID = timeID;
		this.gameID = gameID;
		this.avgPrice = avgPrice;
		this.maxPrice = maxPrice;
		this.minPrice = minPrice;
		this.totalSales = totalSales;
	}

}
