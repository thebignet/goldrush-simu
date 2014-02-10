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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((code == null) ? 0 : code.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Cell other = (Cell) obj;
        if (code == null) {
            if (other.code != null)
                return false;
        } else if (!code.equals(other.code))
            return false;
        return true;
    }

}
