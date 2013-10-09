package org.xteam.goldrush.simu;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class TileSet {
	
	private static final String SPRITES_PATH = "/images/sprites.png";

	private static final int CELL_WIDTH = 32;
	private static final int CELL_HEIGHT = 32;
	
	private BufferedImage iconMap;

	public Image getIcon(int index) {
		if (iconMap == null) {
			initialize();
		}
		return iconMap.getSubimage(index * CELL_WIDTH, 0, CELL_WIDTH, CELL_HEIGHT);
	}
	
	private void initialize() {
		try {
			iconMap = ImageIO.read(getClass().getResourceAsStream(SPRITES_PATH));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
