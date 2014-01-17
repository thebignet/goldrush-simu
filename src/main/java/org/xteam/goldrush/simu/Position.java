package org.xteam.goldrush.simu;

public class Position {

	private int y;
	private int x;

	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Position add(int dx, int dy) {
		return new Position(x + dx, y + dy);
	}
	
	@Override
	public boolean equals(Object o) {
		return ((Position)o).x == x && ((Position)o).y == y;
	}
	
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}

}
