package org.xteam.goldrush.simu.test;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.xteam.goldrush.simu.Cell;
import org.xteam.goldrush.simu.GoldCell;
import org.xteam.goldrush.simu.GoldRushMap;
import org.xteam.goldrush.simu.Player;
import org.xteam.goldrush.simu.Position;

public class GoldRushMapTest {

	@Test
	public void testCreation() {
		GoldRushMap map = new GoldRushMap(10, 5);
		
		Assert.assertEquals(10, map.getWidth());
		Assert.assertEquals(5, map.getHeight());
		for (int y = 0; y < map.getHeight(); ++y) {
			for (int x = 0; x < map.getWidth(); ++x) {
				Assert.assertEquals(Cell.EMPTY, map.getCell(x, y));
			}
		}
		Assert.assertEquals(0, map.getGoldCount());
		Assert.assertEquals(0, map.getPlayers().size());
	}
	
	@Test
	public void testInitialize() {
		GoldRushMap map = new GoldRushMap(5, 5);
		for (int y = 0; y < map.getHeight(); ++y) {
			for (int x = 0; x < map.getWidth(); ++x) {
				map.setCell(x, y, x == 1 && y == 1 ? Cell.UNDEFINED : Cell.MUD);
			}
		}
		map.setCell(1, 2, new GoldCell(3));
		map.setCell(3, 3, new GoldCell(4));
		
		map.initialize();
		
		Assert.assertEquals(7, map.getGoldCount());
	}
	
	@Test
	public void testAddPlayer() {
		GoldRushMap map = new GoldRushMap(5, 5);
		for (int y = 0; y < map.getHeight(); ++y) {
			for (int x = 0; x < map.getWidth(); ++x) {
				map.setCell(x, y, x == 1 && y == 1 ? Cell.UNDEFINED : Cell.MUD);
			}
		}
		map.setCell(1, 2, new GoldCell(3));
		map.setCell(3, 3, new GoldCell(4));
		
		map.initialize();
		
		Player player1 = new Player(null, 1);
		map.addPlayer(player1);
		
		Assert.assertEquals(new Position(1, 1), player1.getStartPosition());
	}
	
	@Test
	public void testMoveOK() throws IOException {
		GoldRushMap map = new GoldRushMap(5, 5);
		for (int y = 0; y < map.getHeight(); ++y) {
			for (int x = 0; x < map.getWidth(); ++x) {
				map.setCell(x, y, x == 1 && y == 1 ? Cell.UNDEFINED : Cell.MUD);
			}
		}
		map.setCell(1, 2, new GoldCell(3));
		map.setCell(3, 3, new GoldCell(4));
		
		map.initialize();
		
		PlayerConnectionMock mock = new PlayerConnectionMock("TestPlayer\nSOUTH");
		Player player1 = new Player(mock, 1);
		player1.start(map);
		map.addPlayer(player1);
		
		map.playOneRound();
		
		Assert.assertEquals(new Position(1, 1), player1.getStartPosition());
		Assert.assertEquals(new Position(1, 2), player1.getPosition());
	}
	
	@Test
	public void testMoveInFixedStone() throws IOException {
		GoldRushMap map = new GoldRushMap(5, 5);
		initMap(map,
				  "S S S S S\n"
				+ "S - M M M\n"
				+ "S M M M M\n"
				+ "S M M M M\n"
				+ "S M M M M\n");
		for (int y = 0; y < map.getHeight(); ++y) {
			for (int x = 0; x < map.getWidth(); ++x) {
				Cell cell = Cell.MUD;
				if (x == 1 && y == 1) {
					cell = Cell.UNDEFINED;
				} else if (x == 0 || y == 0){
					cell = Cell.STONE;
				}
				map.setCell(x, y, cell);
			}
		}
		map.setCell(1, 2, new GoldCell(3));
		map.setCell(3, 3, new GoldCell(4));
		
		map.initialize();
		
		PlayerConnectionMock mock = new PlayerConnectionMock("TestPlayer\nNORTH");
		Player player1 = new Player(mock, 1);
		player1.start(map);
		map.addPlayer(player1);
		
		map.playOneRound();
		
		Assert.assertEquals(new Position(1, 1), player1.getStartPosition());
		Assert.assertEquals(new Position(1, 1), player1.getPosition());
	}

	private void initMap(GoldRushMap map, String spec) {
		int y = 0;
		for (String line : spec.split("\n")) {
			int x = 0;
			for (String element : line.split(" ")) {
				map.setCell(x, y, Cell.getFrom(element));
			}
		}
	}
}
