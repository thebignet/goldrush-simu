package org.xteam.goldrush.simu.test;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.xteam.goldrush.simu.Cell;
import org.xteam.goldrush.simu.Direction;
import org.xteam.goldrush.simu.GoldRushMap;
import org.xteam.goldrush.simu.Player;
import org.xteam.goldrush.simu.PlayerEnvironment;
import org.xteam.goldrush.simu.Position;

public class PlayerTest {
	
	@Test
	public void testCreation() {
		PlayerConnectionMock mock = new PlayerConnectionMock("");
		Player player = new Player(mock, 1);
		
		Assert.assertEquals(1, player.getPlayerId());
		Assert.assertEquals(0, player.getGoldInHand());
		Assert.assertEquals(0, player.getCollectedGold());
		Assert.assertEquals(Direction.EAST, player.getDirection());
	}
	
	@Test
	public void testChangeStartPosition() {
		PlayerConnectionMock mock = new PlayerConnectionMock("");
		Player player = new Player(mock, 1);
		player.setStartPosition(new Position(9, 10));
		
		Assert.assertEquals(new Position(9, 10), player.getStartPosition());
		Assert.assertEquals(new Position(9, 10), player.getPosition());
		Assert.assertTrue(player.isAtBase());
	}
	
	@Test
	public void testChangeDirection() {
		PlayerConnectionMock mock = new PlayerConnectionMock("");
		Player player = new Player(mock, 1);
		player.setDirection(Direction.NORTH);
		
		Assert.assertEquals(Direction.NORTH, player.getDirection());
	}
	
	@Test
	public void testStart() throws IOException {
		PlayerConnectionMock mock = new PlayerConnectionMock("TestPlayer");
		GoldRushMap map = new GoldRushMap(10, 10);
		Player player = new Player(mock, 1);
		player.start(map);
		
		Assert.assertEquals("TestPlayer", player.getName());
		Assert.assertEquals("10 10 0\n", mock.getWritten());
	}
	
	@Test
	public void testGetCommand() throws IOException {
		PlayerConnectionMock mock = new PlayerConnectionMock("TestPlayer\nNORTH");
		GoldRushMap map = new GoldRushMap(10, 10);
		Player player = new Player(mock, 1);
		player.start(map);
		String command = player.nextCommandCode();
		
		Assert.assertEquals("TestPlayer", player.getName());
		Assert.assertEquals("10 10 0\n", mock.getWritten());
		Assert.assertEquals("NORTH", command);
	}
	
	@Test
	public void testPick() throws IOException {
		PlayerConnectionMock mock = new PlayerConnectionMock("");
		Player player = new Player(mock, 1);
		player.pick(22);
		Assert.assertEquals(22, player.getGoldInHand());
		Assert.assertEquals(0, player.getCollectedGold());
	}
	
	@Test
	public void testDropGoldInHand() throws IOException {
		PlayerConnectionMock mock = new PlayerConnectionMock("");
		Player player = new Player(mock, 1);
		player.pick(22);
		player.dropGoldInHand();
		Assert.assertEquals(0, player.getGoldInHand());
		Assert.assertEquals(0, player.getCollectedGold());
	}
	
	@Test
	public void testDropGoldInBase() throws IOException {
		PlayerConnectionMock mock = new PlayerConnectionMock("");
		Player player = new Player(mock, 1);
		player.pick(22);
		player.dropGoldInBase();
		Assert.assertEquals(0, player.getGoldInHand());
		Assert.assertEquals(22, player.getCollectedGold());
	}
	
	@Test
	public void testSendEnvironment() throws IOException {
		PlayerConnectionMock mock = new PlayerConnectionMock("TestPlayer\nNORTH");
		GoldRushMap map = new GoldRushMap(10, 10);
		
		Player player1 = new Player(mock, 1);
		player1.setStartPosition(new Position(3, 4));
		
		Player player2 = new Player(mock, 2);
		player2.setStartPosition(new Position(1, 2));
		
		PlayerEnvironment playerEnvironment = new PlayerEnvironment(player1);
		
		for (int y = 0; y < playerEnvironment.getHeight(); ++y) {
			for (int x = 0; x < playerEnvironment.getWidth(); ++x) {
				playerEnvironment.set(x, y, x == 0 ? Cell.STONE : Cell.MUD);
			}
		}
		playerEnvironment.addPlayers(Arrays.asList(player1, player2));
		
		player1.start(map);
		player1.sendEnvironment(playerEnvironment);
		
		Assert.assertEquals("TestPlayer", player1.getName());
		Assert.assertEquals("10 10 0\n3 4 1\nS M M M M\nS M M M M\nS M M M M\nS M M M M\nS M M M M\n1 2\n", mock.getWritten());
	}
	
}
