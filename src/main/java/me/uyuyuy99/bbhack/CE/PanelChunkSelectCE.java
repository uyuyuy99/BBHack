package me.uyuyuy99.bbhack.CE;

import javax.swing.*;

import me.uyuyuy99.bbhack.MainMenu;
import me.uyuyuy99.bbhack.rom.ROMPalettes;
import me.uyuyuy99.bbhack.types.Tile64;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.*;

public class PanelChunkSelectCE extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private MainMenu main;
	private ChunkEditor CE;
	
	//Current scroll-bar view coordinates (UNITS: 64x64 tiles)
	int viewY;
	//Dimensions of the current map view (UNITS: 64x64 tiles)
	int viewWidth;
	int viewHeight;
	
	int chunkSelected;
	
	public PanelChunkSelectCE(MainMenu instance, ChunkEditor CEInstance) {
		main = instance;
		CE = CEInstance;
		
		viewY = 0;
		viewWidth = 2;
		viewHeight = 8;
		
		chunkSelected = 0;
		
		//Selecting chunks
		this.addMouseListener(
			new MouseListener() {
				public void mousePressed(MouseEvent event) {
					if (event.getButton() == MouseEvent.BUTTON1) { //Select appropriate chunk if left mouse button is pressed
						if (CE.useAlternateTileset) {
							CE.useAlternateTileset = false;
							CE.panelChunkView.repaint();
							CE.panelTileSelect.repaint();
						}
						
						int newValue = (((event.getY() / 64) + viewY) * viewWidth) + (event.getX() / 64);
						if (newValue >= 64) return;
						chunkSelected = newValue;
						
						CE.panelChunkView.repaint();
						repaint();
					}
				}
				
				//Random other unused methods
				public void mouseEntered(MouseEvent event) {
					//Vegetarian
				} public void mouseExited(MouseEvent event) {
					//Elephants
				} public void mouseClicked(MouseEvent event) {
					//Eat
				} public void mouseReleased(MouseEvent event) {
					//Cheese
				}
			}
		);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		int curX = 0;
		int curY = 0;
		
		if (main.gfx.graphics64[(CE.selectedTileset1 * 64) + chunkSelected].altTileset.size() > 0 || CE.useAlternateTileset) {
			CE.panelPaletteSelect.altCheckbox.setEnabled(true);
			CE.panelPaletteSelect.altDropdown.setEnabled(true);
		} else {
			CE.panelPaletteSelect.altCheckbox.setEnabled(false);
			CE.panelPaletteSelect.altDropdown.setEnabled(false);
		}
		
		for (int i=0; i<(viewWidth*viewHeight); i++) {
			int[] pixels = new int[4096 * 3]; //3 values for each color
			
			int tileNum = (viewY * viewWidth) + i;
			
			Tile64 curTile = main.gfx.graphics64[(CE.selectedTileset1 * 64) + tileNum].getCopy();
			
			for (int j=0; j<pixels.length; j+=3) {
				if (tileNum < 64) {
					if (curTile.altTileset.contains((((j/3) / 1024) * 4) + (((j/3) % 64) / 16))) {
						pixels[j] = 255;
						pixels[j+1] = 0;
						pixels[j+2] = 0;
					} else {
						int paletteNum = (CE.selectedPalette * 4) + curTile.getPalette((((j/3) % 64) / 16), ((j/3) / 1024));
						int colorNum = curTile.getValue(j/3);
						
						pixels[j] = ROMPalettes.colors[(main.palettes.palettes[paletteNum][colorNum] * 3)];
						pixels[j+1] = ROMPalettes.colors[(main.palettes.palettes[paletteNum][colorNum] * 3) + 1];
						pixels[j+2] = ROMPalettes.colors[(main.palettes.palettes[paletteNum][colorNum] * 3) + 2];
					}
				} else {
					pixels[j] = 0;
					pixels[j+1] = 0;
					pixels[j+2] = 0;
				}
			}
			BufferedImage tile = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
			WritableRaster raster = tile.getRaster();
			raster.setPixels(0, 0, 64, 64, pixels);
			g.drawImage(tile, curX * 64, curY * 64, null);
			
			//Draw grid
			g.setColor(Color.GRAY);
			g.drawLine((curX * 64) + 63, (curY * 64), (curX * 64) + 63, (curY * 64) + 63);
			g.drawLine((curX * 64), (curY * 64) + 63, (curX * 64) + 63, (curY * 64) + 63);
			
			//Draw transparent square to indicate currently selected tile
			if (tileNum == chunkSelected) {
				float alpha = 0.5F;
				g.setColor(new Color(1.0F, 1.0F, 0.0F, alpha));
				g.fillRect(curX * 64, curY * 64, 64, 64);
			}
			
			curX++;
			if (curX >= viewWidth) {
				curX = 0;
				curY++;
			}
		}
	}
	
}
