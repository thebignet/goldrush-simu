package org.xteam.goldrush.simu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.FilteredImageSource;

import javax.swing.JPanel;

public abstract class MapPanel extends JPanel implements MapListener {
	private static final long serialVersionUID = 2266552496444689887L;

	protected static final int CELL_WIDTH = 32;
	protected static final int CELL_HEIGHT = 32;
	protected static final int HEADER_HEIGHT = 64;
	protected static final int GAP = 20;
	
	protected GoldRushMap map;
	protected TileSet tileSet = new TileSet();
	
	protected MapPanel(GoldRushMap map) {
		this.map = map;
		setOpaque(true);
		setBackground(Color.BLACK);
		map.addListener(this);
	}
	
	protected Cell getCellFordirection(Direction direction) {
		return direction == Direction.WEST ? Cell.LEFT : Cell.RIGHT;
	}
	
	protected Image createImage(Cell cell, int playerId) {
		Image playerImage = tileSet.getIcon(cell.getIconIndex());
		return createImage(new FilteredImageSource(playerImage.getSource(), new PlayerColorFilter(playerId)));
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
		
		drawCells(g);
		
		drawOverlay(g);
	}
	
	private void drawCells(Graphics g) {
		for (int y = 0; y < map.getHeight(); ++y) {
			for (int x = 0; x < map.getWidth(); ++x) {
				Cell cell = map.getCell(x, y);
				g.drawImage(tileSet.getIcon(cell.getIconIndex()), x*CELL_WIDTH, HEADER_HEIGHT + y*CELL_HEIGHT, this);
			}
		}
	}
	
	protected abstract void drawHeader(Graphics g);
	
	protected abstract void drawOverlay(Graphics g);
	
	@Override
	public void refresh(GoldRushMap goldRushMap) {
		this.repaint();
	}
}
