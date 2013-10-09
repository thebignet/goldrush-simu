package org.xteam.goldrush.simu;

import java.util.ArrayList;
import java.util.List;

public class PlayerEnvironment {
	
	private Cell[][] env = new Cell[5][5];
	private Player player;
	private List<Position> playerPositions = new ArrayList<Position>();

	public PlayerEnvironment(Player currentPlayer) {
		this.player = currentPlayer;
	}

	public void set(int x, int y, Cell cell) {
		env[y][x] = cell;
	}

	public int getHeight() {
		return env.length;
	}

	public int getWidth() {
		return env[0].length;
	}

	public Cell get(int x, int y) {
		return env[y][x];
	}

	public Position getAbsolute(int x, int y) {
		return player.getPosition().add(x - 2, y - 2);
	}

	private boolean contains(Position position) {
		return Math.abs(position.getX() - player.getPosition().getX()) <= 2
			&& Math.abs(position.getY() - player.getPosition().getY()) <= 2;
	}

	public void addPlayers(List<Player> players) {
		for (Player player : players) {
			if (player != this.player && contains(player.getPosition())) {
				this.playerPositions.add(player.getPosition());
			}
		}
	}

	public List<Position> getPlayerPositions() {
		return playerPositions;
	}
}
