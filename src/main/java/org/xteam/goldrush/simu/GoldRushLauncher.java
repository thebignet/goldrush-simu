package org.xteam.goldrush.simu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GoldRushLauncher {

	public static void main(String[] args) {
		Options options = new Options();
		int optionsIndex = 0;
		while (optionsIndex < args.length) {
			if (args[optionsIndex].equals("-t")) {
				options.duration = Integer.parseInt(args[optionsIndex+1]);
				optionsIndex += 2;
			} else if (args[optionsIndex].equals("-r")) {
				options.maxRound = Integer.parseInt(args[optionsIndex+1]);
				optionsIndex += 2;
			} else if (args[optionsIndex].equals("-l")) {
				options.startMode = StartMode.Load;
				optionsIndex += 1;
			} else if (args[optionsIndex].equals("-n")) {
				options.startMode = StartMode.New;
				options.width = Integer.parseInt(args[optionsIndex+1]);
				options.height = Integer.parseInt(args[optionsIndex+2]);
				optionsIndex += 3;
			} else {
				break;
			}
		}
		if (options.startMode == StartMode.Run && (args.length - optionsIndex) < 2
				|| (options.startMode == StartMode.Load || options.startMode == StartMode.New) && (args.length - optionsIndex) < 1) {
			System.out.println("usage: goldrush-simu [-t <duration> | -r <maxRounds>] <mapfile> <player-executable>+");
			System.out.println("usage: goldrush-simu [-l | -n width height] <mapfile>");
			System.exit(1);
		}
		
		if (options.startMode == StartMode.Run && (args.length - optionsIndex) > 5) {
			System.out.println("error: too much players");
			System.exit(1);
		}
		File mapFile = new File(args[optionsIndex]);
		new GoldRushLauncher().run(options, mapFile, Arrays.asList(Arrays.copyOfRange(args, optionsIndex+1, args.length)));
	}
	
	private void run(Options options, File mapFile, List<String> playerExecutables) {
		if (options.startMode == StartMode.Run) {
			runSimu(options, mapFile, playerExecutables);
		} else {
			runEditor(options, mapFile);
		}
	}

	private void runSimu(Options options, File mapFile, List<String> playerExecutables) {
		try {
			
			GoldRushMap map = readMap(mapFile);
			
			for (String playerExecutable : playerExecutables) {
				Player player = new Player(new ProcessConnection(playerExecutable), map.getPlayers().size());
				System.out.println("Starting " + playerExecutable);
				player.start(map);
				System.out.println("New player '" + player.getName() + "' " + player.getPlayerId());
				map.addPlayer(player);
			}
			
			GoldRushGUI gui = new GoldRushGUI(map, false);
			gui.setVisible(true);
			
			for (int i = 0; i < options.maxRound; ++i) {
				map.playOneRound();
				try {
					Thread.sleep(options.duration);
				} catch (InterruptedException e) {
				}
			}
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace(System.out);
		}
		
	}
	
	private void runEditor(Options options, final File mapFile) {
		try {
			GoldRushMap map = new GoldRushMap(mapFile, options.width, options.height);
			if (options.startMode == StartMode.Load) {
				map = readMap(mapFile);
			}
			GoldRushGUI gui = new GoldRushGUI(map, true);
			gui.setVisible(true);			
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
			GoldRushMap map = new GoldRushMap(mapFile, Integer.parseInt(sizes[0]), Integer.parseInt(sizes[1]));
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
