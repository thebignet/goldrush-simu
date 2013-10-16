package org.xteam.goldrush.simu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GoldRushLauncher {

	private static final int MAX_NUMBER_OF_ROUND = 1000;

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("usage: goldrush-simu <mapfile> <player-executable>+");
			System.exit(1);
		}
		
		if (args.length > 5) {
			System.out.println("error: too much players");
			System.exit(1);
		}
		File mapFile = new File(args[0]);
		new GoldRushLauncher().run(mapFile, Arrays.asList(Arrays.copyOfRange(args, 1, args.length)));
	}

	private void run(File mapFile, List<String> playerExecutables) {
		try {
			
			GoldRushMap map = readMap(mapFile);
			
			for (String playerExecutable : playerExecutables) {
				Player player = new Player(new ProcessConnection(playerExecutable), map.getPlayers().size());
				System.out.println("Starting " + playerExecutable);
				player.start(map);
				System.out.println("New player '" + player.getName() + "' " + player.getPlayerId());
				map.addPlayer(player);
			}
			
			GoldRushGUI gui = new GoldRushGUI(map);
			gui.setVisible(true);
			
			for (int i = 0; i < MAX_NUMBER_OF_ROUND; ++i) {
				map.playOneRound();
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
				}
			}
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace(System.out);
		}
		
	}

	private GoldRushMap readMap(File mapFile) throws IOException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(mapFile));
			String[] sizes = reader.readLine().split(" ");
			GoldRushMap map = new GoldRushMap(Integer.parseInt(sizes[0]), Integer.parseInt(sizes[1]));
			for (int y = 0; y < map.getHeight(); ++y) {
				String[] elements = reader.readLine().split(" ");
				for (int x = 0; x < map.getWidth(); ++x) {
					map.setCell(x, y, Cell.getFrom(elements[x]));
				}
			}
			map.initialize();
			return map;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
}
