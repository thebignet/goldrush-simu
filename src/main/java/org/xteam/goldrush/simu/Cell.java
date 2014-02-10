package org.xteam.goldrush.simu;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Cell {
	
	public static final Cell EMPTY = new Cell(0, "E");
	public static final Cell MUD = new Cell(1, "M");
	public static final Cell STONE = new Cell(2, "S");
	public static final Cell START = new Cell(3, "X");
	public static final Cell LEFT = new Cell(5, "L");
	public static final Cell RIGHT = new Cell(6, "R");
	public static final Cell UP = new Cell(7, "U");
	public static final Cell DOWN = new Cell(8, "D");
	public static final Cell UNDEFINED = new Cell(-1, "-");
	
	private static final Set<Cell> values = new HashSet<Cell>(
			Arrays.asList(EMPTY, MUD, STONE, UNDEFINED));
	
	private int iconIndex;
	private String code;

	public Cell(int iconIndex, String code) {
		this.iconIndex = iconIndex;
		this.code = code;
	}

	public int getIconIndex() {
		return iconIndex;
	}
	
	public String getCode() {
		return code;
	}

	public static Cell getFrom(String code) {
		for (Cell cell : values) {
			if (cell.code.equals(code)) {
				return cell;
			}
		}
		int quantity = Integer.parseInt(code);
		return new GoldCell(quantity);
	}

	public boolean isGold() {
		return false;
	}

}
