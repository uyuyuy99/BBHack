package me.uyuyuy99.bbhack.CE;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import javax.swing.*;

import me.uyuyuy99.bbhack.MainMenu;
import me.uyuyuy99.bbhack.rom.ROMPalettes;
import me.uyuyuy99.bbhack.types.Tile64;

public class PanelChunkView extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private MainMenu main;
	private ChunkEditor CE;
	
	private boolean mouse1Down;
	
	public PanelChunkView(MainMenu instance, ChunkEditor CEInstance) {
		main = instance;
		CE = CEInstance;
		
		mouse1Down = false;
		
		//Selecting chunks
		this.addMouseListener(
			new MouseListener() {
				public void mousePressed(MouseEvent event) {
					if (event.getButton() == MouseEvent.BUTTON3) {
						int index64 = (CE.selectedTileset1 * 64) + CE.panelChunkSelect.chunkSelected;
						int i = ((event.getY() / 32) * 4) + (event.getX() / 32);
						
						CE.panelTileSelect.tileSelected = main.gfx.graphics64[index64].tileNums[i];
						CE.panelTileSelect.palette = main.gfx.graphics64[index64].getPalette(i);
						CE.panelPaletteSelect.palette.setSelectedIndex(CE.panelTileSelect.palette);
						CE.panelTileSelect.repaint();
					} if (event.getButton() == MouseEvent.BUTTON1) {
						mouse1Down = true;
						int index64 = (CE.selectedTileset1 * 64) + CE.panelChunkSelect.chunkSelected;
						int i = ((event.getY() / 32) * 4) + (event.getX() / 32);
						
						//Set tile to one currently selected
						main.gfx.graphics64[index64].setTile
							(i, main.gfx.graphics16[(CE.selectedTileset1 * 128) + CE.panelTileSelect.tileSelected]);
						
						//Same with palette
						main.gfx.graphics64[index64].setPalette(i, CE.panelTileSelect.palette);
						
						//If INSERT was presse, use alternate tileset
						if (CE.useAlternateTileset) {
							if (!main.gfx.graphics64[index64].altTileset.contains(i)) {
								main.gfx.graphics64[index64].altTileset.add(i);
							}
							CE.useAlternateTileset = false;
							CE.panelTileSelect.repaint();
						} else {
							if (main.gfx.graphics64[index64].altTileset.contains(i)) {
								main.gfx.graphics64[index64].altTileset.remove(new Integer(i));
							}
						}
						
						//Repaint stuff
						CE.panelChunkSelect.repaint();
						repaint();
					}
				}
				
				//Random other unused methods
				public void mouseEntered(MouseEvent event) {
					//Eighteen Thousand
				} public void mouseExited(MouseEvent event) {
					mouse1Down = false;
				} public void mouseClicked(MouseEvent event) {
					//2-pound Gorillas
				} public void mouseReleased(MouseEvent event) {
					if (event.getButton() == MouseEvent.BUTTON1) mouse1Down = false;
				}
			}
		);
		
		this.addMouseMotionListener(
			new MouseMotionListener() {
				public void mouseDragged(MouseEvent event) {
					if (mouse1Down) {
						int index64 = (CE.selectedTileset1 * 64) + CE.panelChunkSelect.chunkSelected;
						int i = ((event.getY() / 32) * 4) + (event.getX() / 32);
						if (i > 15) return; //If mouse is offscreen, don't touch anything!
						
						//Set tile to one currently selected
						main.gfx.graphics64[index64].setTile
							(i, main.gfx.graphics16[(CE.selectedTileset1 * 128) + CE.panelTileSelect.tileSelected]);
						
						//Same with palette
						main.gfx.graphics64[index64].setPalette(i, CE.panelTileSelect.palette);
						
						//Repaint stuff
						CE.panelChunkSelect.repaint();
						repaint();
					}
				}
	
				public void mouseMoved(MouseEvent event) {
					//Wassssuppp maaaaan
				}
			}
		);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		int[] pixels = new int[4096 * 3]; //3 values for each color
		
		int tileNum = CE.panelChunkSelect.chunkSelected;
		
		Tile64 curTile = main.gfx.graphics64[(CE.selectedTileset1 * 64) + tileNum].getCopy();
		
		for (int index : curTile.altTileset) {
			if (CE.panelPaletteSelect.altCheckbox.isSelected()) {
				CE.selectedTileset2 = CE.panelPaletteSelect.altDropdown.getSelectedIndex();
				
				curTile.setTile(index, main.gfx.graphics16[(CE.selectedTileset2 * 128) + curTile.tileNums[index]]);
			}
		}
		
		for (int j=0; j<pixels.length; j+=3) {
			if (tileNum < 64) {
				if (curTile.altTileset.contains((((j/3) / 1024) * 4) + (((j/3) % 64) / 16)) && !CE.panelPaletteSelect.altCheckbox.isSelected()) {
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
		g.drawImage(tile.getScaledInstance(128, 128, Image.SCALE_REPLICATE), 0, 0, null);
		
		//Draw grid
		g.setColor(Color.GRAY);
		g.drawLine(31, 0, 31, 127);
		g.drawLine(63, 0, 63, 127);
		g.drawLine(95, 0, 95, 127);
		g.drawLine(0, 31, 127, 31);
		g.drawLine(0, 63, 127, 63);
		g.drawLine(0, 95, 127, 95);
	}
	
}
