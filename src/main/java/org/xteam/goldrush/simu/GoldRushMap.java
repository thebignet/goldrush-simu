package org.xteam.goldrush.simu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GoldRushMap {

	private static final int MAX_GOLD_IN_HAND = 3;

	private int roundNumber = 0;
	private Cell[][] cells;
	private List<Player> players = new ArrayList<Player>();
	private List<MapListener> listeners = new ArrayList<MapListener>();
	private int totalGoldCount;
	private List<Position> bases = new ArrayList<Position>();

	public GoldRushMap(int width, int height) {
		this.cells = new Cell[height][width];
		for (int y = 0; y < getHeight(); ++y) {
			for (int x = 0; x < getWidth(); ++x) {
				setCell(x, y, Cell.EMPTY);
			}
		}
	}

	public int getWidth() {
		return cells[0].length;
	}

	public int getHeight() {
		return cells.length;
	}

	public Cell getCell(int x, int y) {
		return cells[y][x];
	}

	private Cell getCell(Position position) {
		return getCell(position.getX(), position.getY());
	}

	private void setCell(Position position, Cell cell) {
		setCell(position.getX(), position.getY(), cell);
	}

	public void setCell(int x, int y, Cell cell) {
		this.cells[y][x] = cell;
	}

	public void addListener(MapListener listener) {
		this.listeners.add(listener);
	}

	public List<Player> getPlayers() {
		return players;
	}

	public int getGoldCount() {
		return totalGoldCount;
	}

	public void addPlayer(Player player) {
		players.add(player);
		Position pos = findFreePosition();
		player.setStartPosition(pos);
		setCell(pos.getX(), pos.getY(), Cell.START);
	}

	private Position findFreePosition() {
		return bases.remove(new Random().nextInt(bases.size()));
	}

	public List<Position> getBases() {
		return bases;
	}

	public void setBases(List<Position> newbases) {
		bases = newbases;
	}

	public void playOneRound() throws IOException {
		++roundNumber;
		System.out.println("Round " + roundNumber);
		List<Player> randomPlayerOrder = new ArrayList<Player>(players);
		Collections.shuffle(randomPlayerOrder);
		for (Player player : randomPlayerOrder) {
			playOneRound(player);
		}
		fireRefresh();
	}

	private void fireRefresh() {
		for (MapListener listener : listeners) {
			listener.refresh(this);
		}
	}

	private void playOneRound(Player player) throws IOException {
		player.sendEnvironment(prepareEnvironment(player));
		String code = player.nextCommandCode();
		Command command = Command.getFrom(code);
		if (command != null) {
			System.out.println("execute command " + code + " from " + player.getName());
			command.execute(this, player);
		} else {
			System.out.println("bad command " + code + " from " + player.getName());
		}
	}

	private PlayerEnvironment prepareEnvironment(Player currentPlayer) {
		PlayerEnvironment env = new PlayerEnvironment(currentPlayer);
		for (int y = 0; y < env.getHeight(); ++y) {
			for (int x = 0; x < env.getWidth(); ++x) {
				Position position = env.getAbsolute(x, y);
				if (isInGame(position)) {
					env.set(x, y, getCell(position.getX(), position.getY()));
				} else {
					env.set(x, y, Cell.STONE);
				}
			}
		}
		env.addPlayers(players);
		return env;
	}

	private boolean isInGame(Position pos) {
		return pos.getX() >= 0 && pos.getX() < getWidth() && pos.getY() >= 0 && pos.getY() < getHeight();
	}

	public void move(Player player, Direction direction) {
		Position pos = player.getPosition().add(direction.getDx(), direction.getDy());
		if (canGoTo(player, pos, direction)) {
			player.setPosition(pos);
			Cell cell = getCell(pos.getX(), pos.getY());
			if (cell == Cell.MUD) {
				setCell(pos.getX(), pos.getY(), Cell.EMPTY);
			} else if (cell == Cell.STONE) {
				Position nexwStonePos = pos.add(direction.getDx(), direction.getDy());
				setCell(pos.getX(), pos.getY(), Cell.EMPTY);
				setCell(nexwStonePos.getX(), nexwStonePos.getY(), Cell.STONE);
			}
		}
		player.setDirection(direction);
	}

	private boolean canGoTo(Player currentPlayer, Position pos, Direction dir) {
		for (Player player : players) {
			if (player != currentPlayer && player.getPosition().equals(pos)) {
				return false;
			}
		}
		Cell cell = getCell(pos.getX(), pos.getY());
		if (cell == Cell.EMPTY || cell == Cell.MUD || cell.isGold()) {
			return true;
		}
		if (cell == Cell.START && pos.equals(currentPlayer.getStartPosition())) {
			return true;
		}
		if (cell == Cell.STONE) {
			Position newStonePosition = pos.add(dir.getDx(), dir.getDy());
			return isInGame(newStonePosition)
					&& getCell(newStonePosition.getX(), newStonePosition.getY()) == Cell.EMPTY;
		}
		return false;
	}

	public void pickGold(Player player) {
		Cell cell = getCell(player.getPosition());
		if (cell.isGold()) {
			GoldCell goldCell = (GoldCell) cell;
			int quantity = Math.min(MAX_GOLD_IN_HAND - player.getGoldInHand(), goldCell.getQuantity());
			player.pick(quantity);
			if (goldCell.getQuantity() == quantity) {
				setCell(player.getPosition(), Cell.EMPTY);
			} else {
				setCell(player.getPosition(), new GoldCell(goldCell.getQuantity() - quantity));
			}
		}
	}

	public void dropGold(Player player) {
		if (player.isAtBase()) {
			player.dropGoldInBase();
		} else {
			if (player.getGoldInHand() != 0) {
				Cell cell = getCell(player.getPosition());
				int inCell = player.getGoldInHand();
				if (cell.isGold()) {
					inCell += ((GoldCell) cell).getQuantity();
				}
				setCell(player.getPosition(), new GoldCell(inCell));
				player.dropGoldInHand();
			}
		}
	}

	public void shoot(Player currentPlayer) {
		Position pos = currentPlayer.getPosition();
		while (true) {
			pos = pos.add(currentPlayer.getDirection().getDx(), currentPlayer.getDirection().getDy());
			if (!isInGame(pos)) {
				return;
			}
			for (Player player : players) {
				if (player.getPosition().equals(pos)) {
					shootTo(currentPlayer, player);
					return;
				}
			}
			Cell cell = getCell(pos.getX(), pos.getY());
			if (cell != Cell.EMPTY) {
				break;
			}
		}
	}

	private void shootTo(Player currentPlayer, Player player) {
		dropGold(player);
		player.setPosition(player.getStartPosition());
		player.setDirection(Direction.EAST);
	}

	public void initialize() {
		this.totalGoldCount = 0;
		this.bases.clear();
		for (int y = 0; y < getHeight(); ++y) {
			for (int x = 0; x < getWidth(); ++x) {
				Cell cell = getCell(x, y);
				if (cell.isGold()) {
					GoldCell goldCell = (GoldCell) cell;
					totalGoldCount += goldCell.getQuantity();
				} else if (cell == Cell.UNDEFINED) {
					bases.add(new Position(x, y));
					setCell(x, y, Cell.MUD);
				}
			}
		}
	}

}
