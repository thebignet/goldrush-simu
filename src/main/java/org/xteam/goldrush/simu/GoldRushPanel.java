package org.xteam.goldrush.simu;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.FilteredImageSource;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

public class GoldRushPanel extends JPanel implements MapListener {

	private static final long serialVersionUID = -9115259929363532953L;
	
	private static final int CELL_WIDTH = 32;
	private static final int CELL_HEIGHT = 32;
	private static final int HEADER_HEIGHT = 64;

	private GoldRushMap map;
	private TileSet tileSet = new TileSet();
	private Map<Integer, Image[]> playerImageCache = new HashMap<Integer, Image[]>();
	
	public GoldRushPanel(GoldRushMap map) {
		this.map = map;
		setOpaque(true);
		setBackground(Color.BLACK);
		map.addListener(this);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(
				CELL_WIDTH * map.getWidth(),
				HEADER_HEIGHT + CELL_HEIGHT * map.getHeight());
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		drawHeader(g);
		for (int y = 0; y < map.getHeight(); ++y) {
			for (int x = 0; x < map.getWidth(); ++x) {
				Cell cell = map.getCell(x, y);
				g.drawImage(tileSet.getIcon(cell.getIconIndex()), x*CELL_WIDTH, HEADER_HEIGHT + y*CELL_HEIGHT, this);
			}
		}
		for (Player player : map.getPlayers()) {
			Position pos = player.getPosition();
			g.drawImage(getCachedPlayerImage(player.getPlayerId(), getCellFordirection(player.getDirection())),
					pos.getX()*CELL_WIDTH, HEADER_HEIGHT + pos.getY()*CELL_HEIGHT, this);
		}
	}

	private Cell getCellFordirection(Direction direction) {
		return direction == Direction.WEST ? Cell.LEFT : Cell.RIGHT;
	}

	private Image getCachedPlayerImage(int playerId, Cell cell) {
		Image[] cachedImages = playerImageCache.get(playerId);
		if (cachedImages == null) {
			cachedImages = new Image[2];
		    cachedImages[0] = createImage(Cell.LEFT, playerId);
		    cachedImages[1] = createImage(Cell.RIGHT, playerId);
		    playerImageCache .put(playerId, cachedImages);
		}
		return cachedImages[cell.getIconIndex() - Cell.LEFT.getIconIndex()];
	}

	private Image createImage(Cell cell, int playerId) {
		Image playerImage = tileSet.getIcon(cell.getIconIndex());
		return createImage(new FilteredImageSource(playerImage.getSource(), new PlayerColorFilter(playerId)));
	}

	private void drawHeader(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.GRAY);
		g2d.setStroke(new BasicStroke(2.0f));
		g2d.setFont(new Font("Arial", Font.BOLD, 20));
		
		final int gap = 20;
		int x = 16;
		for (Player player : map.getPlayers()) {
			g2d.drawRoundRect(x-4, 16-4, 32+4*2, 32+4*2, 8, 8);
			g2d.drawImage(getCachedPlayerImage(player.getPlayerId(), Cell.RIGHT), x, 16, this);
			String str = player.getName() + "(" + player.getCollectedGold() + ")";
			g2d.drawString(str, x + CELL_WIDTH + gap, 16 + CELL_HEIGHT - 6);
			x += CELL_WIDTH + gap*2 + g2d.getFontMetrics().stringWidth(str);
		}
	}

	@Override
	public void refresh(GoldRushMap goldRushMap) {
		this.repaint();
	}
}
