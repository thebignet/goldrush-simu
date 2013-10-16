package org.xteam.goldrush.simu.test;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.xteam.goldrush.simu.Cell;
import org.xteam.goldrush.simu.Player;
import org.xteam.goldrush.simu.PlayerEnvironment;
import org.xteam.goldrush.simu.Position;

public class PlayerEnvironmentTest {

	@Test
	public void testCreation() {
		Player player = new Player(null, 1);
		PlayerEnvironment env = new PlayerEnvironment(player);
		
		Assert.assertEquals(5, env.getWidth());
		Assert.assertEquals(5, env.getHeight());
	}
	
	@Test
	public void testPosition() {
		Player player = new Player(null, 1);
		player.setStartPosition(new Position(9, 10));
		PlayerEnvironment env = new PlayerEnvironment(player);
		
		Assert.assertEquals(new Position(8, 9), env.getAbsolute(1, 1));
	}
	
	@Test
	public void testUpdate() {
		Player player = new Player(null, 1);
		player.setStartPosition(new Position(9, 10));
		PlayerEnvironment env = new PlayerEnvironment(player);
		
		env.set(1, 1, Cell.EMPTY);
		Assert.assertEquals(Cell.EMPTY, env.get(1, 1));
		
		env.set(1, 1, Cell.MUD);
		Assert.assertEquals(Cell.MUD, env.get(1, 1));
	}
	
	@Test
	public void testPlayerPositions() {
		Player player1 = new Player(null, 1);
		Player player2 = new Player(null, 2);
		Player player3 = new Player(null, 3);
		List<Player> allPlayers = Arrays.asList(player1, player2, player3);
		player1.setStartPosition(new Position(9, 10));
		player2.setStartPosition(new Position(30, 30));
		player3.setStartPosition(new Position(10, 11));
		PlayerEnvironment env = new PlayerEnvironment(player1);
		
		env.addPlayers(allPlayers);
		List<Position> visiblePlayers = env.getPlayerPositions();
		
		Assert.assertEquals(1, visiblePlayers.size());
		Assert.assertEquals(new Position(10, 11), visiblePlayers.get(0));
	}
}
