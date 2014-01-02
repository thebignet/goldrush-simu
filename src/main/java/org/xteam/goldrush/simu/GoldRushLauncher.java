package org.xteam.goldrush.simu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GoldRushLauncher {

	private void mainEditor(String[] args) {
		String mode = "HELP";
		String path = "";
		int width = 0;
		int height = 0;
		
		if(args.length > 0) {
			if(args[0].equals("-l")) { // load map
				if(args.length == 2) {
					mode = "LOAD";
					path = args[1];
				}
			} else if(args[0].equals("-n"))  { // new map
				if(args.length == 4) {
					mode = "NEW";
					path = args[1];
					width = Integer.valueOf(args[2]);
					height = Integer.valueOf(args[3]);
				}
			}
		}
		
		File mapFile = null;
		GoldRushLauncher simu = new GoldRushLauncher();
		
		switch(mode) {
			case "LOAD":
				mapFile = new File(path);
				break;
			case "NEW":
				mapFile = new File(path);
				try {
					simu.createMap(mapFile, width, height);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
				break;
			default: // HELP
				System.out.println("-d <mapFileToLoad> || -n <mapFileToCreate> <width> <height>");
				return;
		}
		currentFile = path;

		simu.runEditor(mapFile);
	}

	private void runEditor(final File mapFile) {
		try {
			
			final GoldRushMap map = readMap(mapFile);
			GoldRushGUI gui = new GoldRushGUI(map, true);
			gui.setVisible(true);			
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace(System.out);
		}
	}

	public static void main(String[] args) {
		Options options = new Options();
		int optionsIndex = 0;
		while ((args.length - optionsIndex) > 2) {
			if (args[optionsIndex].equals("-t")) {
				options.duration = Integer.parseInt(args[optionsIndex+1]);
				optionsIndex += 2;
			} else if (args[optionsIndex].equals("-r")) {
				options.maxRound = Integer.parseInt(args[optionsIndex+1]);
				optionsIndex += 2;
			} else {
				break;
			}
		}
		if ((args.length - optionsIndex) < 2) {
			System.out.println("usage: goldrush-simu [-t <duration> | -r <maxRounds>] <mapfile> <player-executable>+");
			System.exit(1);
		}
		
		if ((args.length - optionsIndex) > 5) {
			System.out.println("error: too much players");
			System.exit(1);
		}
		File mapFile = new File(args[optionsIndex]);
		new GoldRushLauncher().run(options, mapFile, Arrays.asList(Arrays.copyOfRange(args, optionsIndex+1, args.length)));
	}

	private void run(Options options, File mapFile, List<String> playerExecutables) {
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

	public void createMap(File mapFile, int width, int height) throws IOException {
		GoldRushMap map = new GoldRushMap(width, height);
		writeMap(map, mapFile);
	}
	
	public static void writeMap(GoldRushMap map, File mapFile) throws IOException {
		BufferedWriter writer = null;
		try {
			
			writer = new BufferedWriter(new FileWriter(mapFile));
			writer.write(map.getWidth() + " " + map.getHeight() + "\n");
			
			List<Position> bases = map.getBases();
			
			for (int y = 0; y < map.getHeight(); ++y) {
				for (int x = 0; x < map.getWidth(); ++x) {
					if(x > 0) {
						writer.write(" ");
					}
					
					if(containsBase(bases, x, y)) {
						writer.write(Cell.UNDEFINED.getCode());
					} else {
						writer.write(map.getCell(x, y).getCode());
					}
				}
				writer.write("\n");
			}

		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	private static boolean containsBase(List<Position> bases, int x, int y) {
		for(Position pos : bases) {
			if(pos.getX() == x && pos.getY() == y) {
				return true;
			}
		}
		return false;
	}
}
