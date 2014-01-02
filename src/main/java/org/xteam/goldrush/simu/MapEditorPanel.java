package org.xteam.goldrush.simu;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapEditorPanel extends MapPanel {
	private static final long serialVersionUID = 1575062462583141256L;
	
	private Cell currentcell = Cell.MUD;
	private List<Cell> headerCells;
	private int mirrorWidth = 0;
	private boolean isMirrored = false;
	
	public MapEditorPanel(GoldRushMap map) {
		super(map);
				
		headerCells = new ArrayList<Cell>();
		headerCells.add(Cell.EMPTY);
		headerCells.add(Cell.MUD);
		headerCells.add(Cell.STONE);
		headerCells.add(Cell.START);
		headerCells.add(new GoldCell(+1));
		headerCells.add(new GoldCell(-1));
		
		AddMouseModifier(map);
	}
	
	private void handleCellClick(int cellX, int cellY) {
		Cell old = map.getCell(cellX, cellY);
		
		List<Position> bases = map.getBases();
		int indexOldBase = -1;
		for(int i = 0; i < bases.size() && indexOldBase < 0; i++) {
			if(bases.get(i).getX() == cellX && bases.get(i).getY() == cellY) {
				indexOldBase = i;
			}
		}
		
		if(!currentcell.isGold()) {
			if(indexOldBase >= 0 && currentcell.getCode().equals(Cell.START.getCode())) {
				return;
			}
			else if(currentcell.getCode().equals(Cell.START.getCode())) {
				bases.add(new Position(cellX, cellY));
				map.setBases(bases);
			} 
			else {
				if(currentcell.getCode().equals(old.getCode()))
					return;
				
				map.setCell(cellX, cellY, currentcell);
			}
		} else {
			int newQuantity = 0;
			if(old.isGold()) {
				newQuantity = ((GoldCell)old).getQuantity() + ((GoldCell)currentcell).getQuantity();
			} else {
				newQuantity = ((GoldCell)currentcell).getQuantity() > 0 ? ((GoldCell)currentcell).getQuantity() : 0;
			}
			
			if(newQuantity >= 0) {
				map.setCell(cellX, cellY, new GoldCell(newQuantity));
			} else {
				map.setCell(cellX, cellY, Cell.MUD);
			}
		}
		
		if(!currentcell.getCode().equals(Cell.START.getCode())) {
			if(indexOldBase >= 0) {
				bases.remove(indexOldBase);
				map.setBases(bases);
			}
		}
	}
	
	private void handleClick(int mouseX, int mouseY) {
		if(mouseY <= HEADER_HEIGHT) {
			int index = (mouseX / (CELL_WIDTH + GAP * 2));
			if(index < 0 || index >= headerCells.size()) {
				if(index >= headerCells.size()) {
					int offset = mouseX - (CELL_WIDTH + GAP * 2) * (headerCells.size() - 1);
					index = (offset / mirrorWidth);					
					
					if(index > 0) {
						try {
							GoldRushLauncher.writeMap(map, new File(GoldRushLauncher.currentFile));
							System.out.println("Map "+GoldRushLauncher.currentFile+" saved.");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					else {
						isMirrored = !isMirrored;
					}
				}
			} else {
				currentcell = headerCells.get(index);
			}
			
		} else {
			int cellX = mouseX / CELL_WIDTH;
			int cellY = (mouseY - HEADER_HEIGHT) / CELL_HEIGHT;

			handleCellClick(cellX, cellY);
			
			if(isMirrored) {
				handleCellClick(map.getWidth() - cellX - 1, cellY);
				handleCellClick(cellX, map.getHeight() - cellY - 1);
				handleCellClick(map.getWidth() - cellX - 1, map.getHeight() - cellY - 1);
			}
		}
		
		super.refresh(map);
	}
	
	private void AddMouseModifier(final GoldRushMap map) {
		this.addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent arg0) {
			}
			
			@Override
			public void mouseDragged(MouseEvent arg) {
				handleClick(arg.getX(), arg.getY());
			}
		});
		
		this.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseClicked(MouseEvent arg) {
				handleClick(arg.getX(), arg.getY());
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
		});
	}
	
	protected void drawOverlay(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.WHITE);
		g2d.setStroke(new BasicStroke(2.0f));
		g2d.setFont(new Font("Arial", Font.BOLD, 20));
		
		// Affiche le nombre de diamants dans un tas
		for (int y = 0; y < map.getHeight(); ++y) {
			for (int x = 0; x < map.getWidth(); ++x) {
				Cell cell = map.getCell(x, y);
				if(cell.isGold()) {
					String str = String.valueOf(((GoldCell)cell).getQuantity());
					g2d.drawString(str, (x + 1)*CELL_WIDTH - g2d.getFontMetrics().stringWidth(str), HEADER_HEIGHT + (y + 1)*CELL_HEIGHT);
				}
			}
		}
		
		// Affiche les bases
		for (Position pos : map.getBases()) {
			g.drawImage(tileSet.getIcon(Cell.START.getIconIndex()), pos.getX()*CELL_WIDTH, HEADER_HEIGHT + pos.getY()*CELL_HEIGHT, this);
		}
	}

	protected void drawHeader(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setFont(new Font("Arial", Font.BOLD, 20));
		
		int x = 16;
		for(Cell cell : headerCells) {
			x += drawHeaderCell(g2d, cell, x);
		}
		
		x += drawMirrorButton(g2d, x);

		x += drawSaveButton(g2d, x);
	}
	
	private int drawHeaderCell(Graphics2D g2d, Cell cell, int x) {
		
		if(!currentcell.isGold() && !cell.isGold() && currentcell.getCode().equals(cell.getCode())) {
			g2d.setColor(Color.GREEN);
		}
		else if(currentcell.isGold() && cell.isGold() && ((GoldCell)currentcell).getQuantity() == ((GoldCell)cell).getQuantity()) {
			g2d.setColor(Color.GREEN);
		}
		else {
			g2d.setColor(Color.GRAY);
		}
		g2d.setStroke(new BasicStroke(2.0f));
		
		g2d.drawRoundRect(x-4, 16-4, 32+4*2, 32+4*2, 8, 8);
		g2d.drawImage(tileSet.getIcon(cell.getIconIndex()), x, 16, this);
		
		if(cell.isGold()) {
			g2d.setColor(Color.WHITE);
			String str = ((GoldCell)cell).getQuantity() > 0 ? "+" : "-";
			g2d.drawString(str, x + CELL_WIDTH - g2d.getFontMetrics().stringWidth(str), 16 + CELL_HEIGHT);
		}
		
		return CELL_WIDTH + GAP*2;
	}
	
	private int drawMirrorButton(Graphics2D g2d, int x) {
		String str = "MIRROR OFF";

		if(isMirrored) {
			g2d.setColor(Color.GREEN);
			str = "MIRROR ON";
		} else {
			g2d.setColor(Color.ORANGE);
		}

		g2d.drawString(str, x, 16 + CELL_HEIGHT);
		g2d.drawRoundRect(x-4, 16-4, g2d.getFontMetrics().stringWidth(str)+4*2, 32+4*2, 8, 8);
		mirrorWidth = g2d.getFontMetrics().stringWidth(str)+4*2 + GAP*2;
		return mirrorWidth;
	}

	private int drawSaveButton(Graphics2D g2d, int x) {
		g2d.setColor(Color.RED);
		String str = "SAVE";
		g2d.drawString(str, x, 16 + CELL_HEIGHT);
		int width = g2d.getFontMetrics().stringWidth(str)+4*2;
		g2d.drawRoundRect(x-4, 16-4, width, 32+4*2, 8, 8);
		return width + 2*GAP;
		
	}
}
