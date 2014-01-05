package org.xteam.goldrush.simu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

public class Player {

	private int playerId;
	
	private PlayerConnection playerConnection;
	private String name;
	private BufferedReader reader;
	private Writer writer;
	private Position position;
	private Position startposition;
	private Direction direction = Direction.EAST;
	private int goldInHand = 0;
	private int collectedGold = 0;

	public Player(PlayerConnection playerConnection, int playerId) {
		this.playerConnection = playerConnection;
		this.playerId = playerId;
	}

	public void start(GoldRushMap map) throws IOException {
		playerConnection.start();
		reader = new BufferedReader(playerConnection.getReader());
		writer = playerConnection.getWriter();
		
		// Read Player name
		name = reader.readLine();
		writer.write(map.getWidth() + " " + map.getHeight() + " "
				+ map.getGoldCount() + "\n");
		writer.flush();
	}
	
	public int getPlayerId() {
		return playerId;
	}

	public String getName() {
		return name;
	}
	
	public void setStartPosition(Position pos) {
		this.startposition = pos;
		setPosition(pos);
	}
	
	public Position getStartPosition() {
		return startposition;
	}

	public void setPosition(Position pos) {
		this.position = pos;
	}

	public Position getPosition() {
		return position;
	}
	
	public Direction getDirection() {
		return direction;
	}
	
	public void setDirection(Direction dir) {
		this.direction = dir;
	}
	
	public int getGoldInHand() {
		return goldInHand;
	}
	
	public int getCollectedGold() {
		return collectedGold;
	}

	public void sendEnvironment(PlayerEnvironment playerEnvironment) throws IOException {
		writer.write(position.getX() + " " + position.getY()
				+ " " + playerEnvironment.getPlayerPositions().size() + "\n");
		for (int y = 0; y < playerEnvironment.getHeight(); ++y) {
			StringBuilder builder = new StringBuilder();
			for (int x = 0; x < playerEnvironment.getWidth(); ++x) {
				if (x > 0) {
					builder.append(" ");
				}
				builder.append(playerEnvironment.get(x, y).getCode());
			}
			builder.append("\n");
			writer.write(builder.toString());
		}
		for (Position position : playerEnvironment.getPlayerPositions()) {
			writer.write(position.getX() + " " + position.getY() + "\n");
		}
		writer.flush();
	}

	public String nextCommandCode() throws IOException {
		return reader.readLine();
	}

	public boolean isAtBase() {
		return position.equals(startposition);
	}

	public void dropGoldInBase() {
		this.collectedGold += goldInHand;
		goldInHand = 0;
	}

	public void dropGoldInHand() {
		this.goldInHand = 0;
	}

	public void pick(int quantity) {
		this.goldInHand += quantity;
	}
	
	public void dispose() throws IOException {
		reader.close();
		writer.close();
		
		// on laisse le processus se fermer tout seul 
		// s'il detecte la fermeture des flux.
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
		
		// on force la fermeture pour etre sur.
		this.playerConnection.stop();
	}
}
