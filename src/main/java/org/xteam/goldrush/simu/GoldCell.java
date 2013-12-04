package org.xteam.goldrush.simu;

public class GoldCell extends Cell {

	private int quantity;

	public GoldCell(int quantity) {
		super(4, "G");
		this.quantity = quantity;
	}
	
	public boolean isGold() {
		return true;
	}
	
	@Override
	public String getCode() {
		return String.valueOf(quantity);
	}

	public int getQuantity() {
		return quantity;
	}

}
