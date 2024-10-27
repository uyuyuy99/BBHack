package me.uyuyuy99.bbhack.CE;

import javax.swing.*;

import me.uyuyuy99.bbhack.MainMenu;
import me.uyuyuy99.bbhack.rom.ROMPalettes;
import me.uyuyuy99.bbhack.tiles.*;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.*;

public class PanelTileSelectCE extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private MainMenu main;
	private ChunkEditor CE;
	
	//Current scroll-bar view coordinates (UNITS: 64x64 tiles)
	int viewX;
	//Dimensions of the current map view (UNITS: 64x64 tiles)
	int viewWidth;
	int viewHeight;
	
	int tileSelected;
	int palette;
	
	public PanelTileSelectCE(MainMenu instance, ChunkEditor CEInstance) {
		main = instance;
		CE = CEInstance;
		
		setBackground(Color.BLACK);
		
		viewX = 0;
		viewWidth = 16;
		viewHeight = 16;
		
		tileSelected = 0;
		palette = 0;
		
		//Selecting chunks
		this.addMouseListener(
			new MouseListener() {
				public void mousePressed(MouseEvent event) {
					if (event.getButton() == MouseEvent.BUTTON1) { //Select appropriate tile if left mouse button is pressed
						int newValue = (((event.getX() / 32) + viewX) * viewHeight) + (event.getY() / 32);
						if (newValue >= 128) return;
						tileSelected = newValue;
						
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
		
		//Change palette from dropdown menu
		CE.panelPaletteSelect.palette.addItemListener(
			new ItemListener() {
				public void itemStateChanged(ItemEvent event) {
					palette = CE.panelPaletteSelect.palette.getSelectedIndex();
					repaint();
				}
			}
		);
		
		//Change alternate tileset options
		CE.panelPaletteSelect.altCheckbox.addItemListener(
			new ItemListener() {
				public void itemStateChanged(ItemEvent event) {
					repaint();
				}
			}
		); CE.panelPaletteSelect.altDropdown.addItemListener(
			new ItemListener() {
				public void itemStateChanged(ItemEvent event) {
					repaint();
				}
			}
		);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		int curX = 0;
		int curY = 0;
		
		viewWidth = getWidth() / 32;
		viewHeight = getHeight() / 32;
		
		for (int i=0; i<(viewWidth*viewHeight); i++) {
			int[] pixels = new int[256 * 3]; //3 values for each color
			
			int tileNum = (viewX * viewHeight) + i;
			if (tileNum < 128) {
				Tile16 curTile;
				if (CE.panelPaletteSelect.altCheckbox.isSelected() &&  CE.useAlternateTileset) {
					curTile = main.gfx.graphics16[(CE.panelPaletteSelect.altDropdown.getSelectedIndex() * 128) + tileNum];
				} else {
					curTile = main.gfx.graphics16[(CE.selectedTileset1 * 128) + tileNum];
				}
				
				for (int j=0; j<pixels.length; j+=3) {
					int paletteNum = (CE.selectedPalette * 4) + palette;
					int colorNum = curTile.getValue(j/3);
					
					pixels[j] = ROMPalettes.colors[(main.palettes.palettes[paletteNum][colorNum] * 3)];
					pixels[j+1] = ROMPalettes.colors[(main.palettes.palettes[paletteNum][colorNum] * 3) + 1];
					pixels[j+2] = ROMPalettes.colors[(main.palettes.palettes[paletteNum][colorNum] * 3) + 2];
				}
				BufferedImage tile = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
				WritableRaster raster = tile.getRaster();
				raster.setPixels(0, 0, 16, 16, pixels);
				g.drawImage(tile.getScaledInstance(32, 32, Image.SCALE_REPLICATE), curX * 32, curY * 32, null);
				
				//Draw grid
				if (tileNum < 128) {
					g.setColor(Color.GRAY);
					g.drawLine((curX * 32) + 31, (curY * 32), (curX * 32) + 31, (curY * 32) + 31);
					g.drawLine((curX * 32), (curY * 32) + 31, (curX * 32) + 31, (curY * 32) + 31);
				}
				
				//Draw transparent square to indicate currently selected tile
				if (tileNum == tileSelected) {
					float alpha = 0.5F;
					g.setColor(new Color(1.0F, 1.0F, 0.0F, alpha));
					g.fillRect(curX * 32, curY * 32, 32, 32);
				}
				
				curY++;
				if (curY >= viewHeight) {
					curY = 0;
					curX++;
				}
			}
		}
	}
	
}
