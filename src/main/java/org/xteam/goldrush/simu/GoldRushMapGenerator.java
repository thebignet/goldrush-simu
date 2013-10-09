package org.xteam.goldrush.simu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;

public class GoldRushMapGenerator {
	public static void main(String[] args) throws FileNotFoundException {
		int width = 40;
		int height = 25;
		double stoneProb = 0.35;
		double goldProb = 0.05;
		
		GoldRushMap map = new GoldRushMap(width, height);
		Random random = new Random();
		
		for (int y = 0; y < map.getHeight(); ++y) {
			for (int x = 0; x < map.getWidth(); ++x) {
				double prob = random.nextDouble();
				if (prob < stoneProb) {
					map.setCell(x, y, Cell.STONE);
				} else if (prob < (stoneProb + goldProb)) {
					map.setCell(x, y, new GoldCell(random.nextInt(10) + 1));
				} else {
					map.setCell(x, y, Cell.MUD);
				}
			}
		}
		for (int y = 0; y < map.getHeight(); ++y) {
			if (y == 0 || y == map.getHeight()-1) {
				for (int x = 0; x < map.getWidth(); ++x) {
					map.setCell(x, y, Cell.STONE);
				}
			} else {
				map.setCell(0, y, Cell.STONE);
				map.setCell(map.getWidth()-1, y, Cell.STONE);
			}
		}
		
		writeMap(map, new File("gen.map"));
	}

	private static void writeMap(GoldRushMap map, File file) throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(file);
		writer.println(map.getWidth() + " " + map.getHeight());
		for (int y = 0; y < map.getHeight(); ++y) {
			for (int x = 0; x < map.getWidth(); ++x) {
				if (x > 0) {
					writer.append(" ");
				}
				Cell cell = map.getCell(x, y);
				writer.append(cell.getCode());
			}
			writer.println();
		}
		writer.close();
	}
}
