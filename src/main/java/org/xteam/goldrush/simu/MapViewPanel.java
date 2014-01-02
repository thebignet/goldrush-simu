package org.xteam.goldrush.simu;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

public class MapViewPanel extends MapPanel {
	private static final long serialVersionUID = 7160775394226880053L;
	
	private Map<Integer, Image[]> playerImageCache = new HashMap<Integer, Image[]>();
	
	public MapViewPanel(GoldRushMap map) {
		super(map);
	}
	
	@Override
	protected void drawHeader(Graphics g) {
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
	protected void drawOverlay(Graphics g) {
		for (Player player : map.getPlayers()) {
			Position pos = player.getPosition();
			g.drawImage(getCachedPlayerImage(player.getPlayerId(), getCellFordirection(player.getDirection())),
					pos.getX()*CELL_WIDTH, HEADER_HEIGHT + pos.getY()*CELL_HEIGHT, this);
		}
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
}
