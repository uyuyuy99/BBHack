package me.uyuyuy99.bbhack.ME;

import javax.swing.*;

import me.uyuyuy99.bbhack.MainMenu;
import me.uyuyuy99.bbhack.rom.ROMPalettes;
import me.uyuyuy99.bbhack.tiles.Tile64;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.*;

public class PanelChunkSelectME extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private MainMenu main;
	private MapEditor ME;
	private PanelMap panelMap;
	
	//Current scroll-bar view coordinates (UNITS: 64x64 tiles)
	int viewY;
	//Dimensions of the current map view (UNITS: 64x64 tiles)
	int viewWidth;
	int viewHeight;
	
	int selectX;
	int selectY;
	
	int chunkSelected;
	
	private boolean mouse1Down;
	
	public PanelChunkSelectME(MainMenu instance, MapEditor MEInstance, PanelMap panelMapInstance) {
		main = instance;
		ME = MEInstance;
		panelMap = panelMapInstance;
		
		selectX = 0;
		selectY = 0;
		
		viewY = 0;
		viewWidth = 4;
		viewHeight = 16;
		
		chunkSelected = -1;
		
		mouse1Down = false;
		
		panelMap.addMouseListener(
			new MouseListener() {
				public void mousePressed(MouseEvent event) {
					if (event.getButton() == MouseEvent.BUTTON3) { //Right click to select tile from map
						selectX = (event.getX() / 64) + panelMap.viewX;
						selectY = (event.getY() / 64) + panelMap.viewY;
						
						int selected = main.map.mapTilesGet(selectX, selectY);
						if (main.map.mapTilesetGet(selectX, selectY)) {
							selected += 64;
						}
						chunkSelected = selected;
						if (!((selected / viewWidth) > viewY) || !((selected / viewWidth) < (viewY + viewHeight))) {
							viewY = (selected / viewWidth);
							while (viewY > (128 / viewWidth) - viewHeight) {
								viewY--;
							} while (viewY < 0) {
								viewY++;
							}
							ME.internalChunkSelect.scroll.setValue(viewY);
						}
						
						repaint();
					} if (event.getButton() == MouseEvent.BUTTON1) { //Left click to place tile (CTRL-click to select sector)
						if (!event.isShiftDown()) {
							mouse1Down = true;
							
							selectX = (event.getX() / 64) + panelMap.viewX;
							selectY = (event.getY() / 64) + panelMap.viewY;
							repaint();
							
							/*
							System.out.println("AREA: " + main.map.sectorAreaGet(selectX / 4, selectY / 4));
							System.out.println("x: " + (selectX * 4));
							System.out.println("y: " + (selectY * 4));
							System.out.println();
							*/
							
							if (!event.isControlDown()) {
								if (chunkSelected != -1) {
									int mapX = (event.getX() / 64) + panelMap.viewX;
									int mapY = (event.getY() / 64) + panelMap.viewY;
									main.map.mapTiles[(mapY * 256) + mapX] = chunkSelected % 64;
									
									if (chunkSelected < 64) main.map.mapTileset[(mapY * 256) + mapX] = false;
									else main.map.mapTileset[(mapY * 256) + mapX] = true;
									
									panelMap.refreshChunk((event.getX() / 64), (event.getY() / 64));
									panelMap.repaint();
								}
							}
						} else { //Shift-click to edit specific chunk
							int mapX = (event.getX() / 64) + panelMap.viewX;
							int mapY = (event.getY() / 64) + panelMap.viewY;
							int sectorX = mapX / 4;
							int sectorY = mapY / 4;
							main.openCEFromMap(main.map.mapTilesGet(mapX, mapY), main.map.mapTilesetGet(mapX, mapY),
									main.map.sectorPaletteGet(sectorX, sectorY), main.map.sectorTileset1Get(sectorX, sectorY), main.map.sectorTileset2Get(sectorX, sectorY));
						}
					}
				}
				
				//Random other unused methods
				public void mouseEntered(MouseEvent event) {
					//Red
				} public void mouseExited(MouseEvent event) {
					mouse1Down = false;
				} public void mouseClicked(MouseEvent event) {
					//Donkeys
				} public void mouseReleased(MouseEvent event) {
					if (event.getButton() == MouseEvent.BUTTON1) mouse1Down = false;
				}
			}
		);
		
		panelMap.addMouseMotionListener(
			new MouseMotionListener() {
				public void mouseDragged(MouseEvent event) {
					if (mouse1Down) {
						selectX = (event.getX() / 64) + panelMap.viewX;
						selectY = (event.getY() / 64) + panelMap.viewY;
						repaint();
						
						if (!event.isControlDown()) {
							if (chunkSelected != -1) {
								int mapX = (event.getX() / 64) + panelMap.viewX;
								int mapY = (event.getY() / 64) + panelMap.viewY;
								main.map.mapTiles[(mapY * 256) + mapX] = chunkSelected % 64;

								if (chunkSelected < 64) main.map.mapTileset[(mapY * 256) + mapX] = false;
								else main.map.mapTileset[(mapY * 256) + mapX] = true;
								
								panelMap.refreshChunk((event.getX() / 64), (event.getY() / 64));
								panelMap.repaint();
							}
						}
					}
				}

				public void mouseMoved(MouseEvent event) {
					//Hallo
				}
			}
		);
		
		//Selecting chunks
		this.addMouseListener(
			new MouseListener() {
				public void mousePressed(MouseEvent event) {
					if (event.getButton() == MouseEvent.BUTTON1) { //Select appropriate chunk if left mouse button is pressed
						int newValue = (((event.getY() / 64) + viewY) * viewWidth) + (event.getX() / 64);
						if (newValue >= 128) return;
						if (chunkSelected != newValue) {
							chunkSelected = newValue;
							panelMap.chunkPreviewAlpha = 1.0F;
							panelMap.chunkPreviewTimer.start();
						} else {
							chunkSelected = -1;
							panelMap.chunkPreviewTimer.stop();
							panelMap.repaint();
						}
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
	
	private int getSelectedTileset1() {
		return main.map.sectorTileset1Get(selectX / 4, selectY / 4);
	}
	
	private int getSelectedTileset2() {
		return main.map.sectorTileset2Get(selectX / 4, selectY / 4);
	}
	
	private int getSelectedPalette() {
		return main.map.sectorPaletteGet(selectX / 4, selectY / 4);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		int curX = 0;
		int curY = 0;
		
		for (int i=0; i<(viewWidth*viewHeight); i++) {
			int[] pixels = new int[4096 * 3]; //3 values for each color
			
			int tileNum = (viewY * viewWidth) + i;
			
			int selectedTileset1 = getSelectedTileset1();
			int selectedTileset2 = getSelectedTileset2();
			int selectedPalette = getSelectedPalette();
			
			Tile64 curTile;
			if (tileNum < 64) {
				curTile = main.gfx.graphics64[(selectedTileset1 * 64) + tileNum];
			} else {
				curTile = main.gfx.graphics64[(selectedTileset2 * 64) + (tileNum % 64)];
			}
			
			for (int j=0; j<pixels.length; j+=3) {
				if (tileNum < 128) {
					int paletteNum = (selectedPalette * 4) + curTile.getPalette((((j/3) % 64) / 16), ((j/3) / 1024));
					int colorNum = curTile.getValue(j/3);
					
					pixels[j] = ROMPalettes.colors[(main.palettes.palettes[paletteNum][colorNum] * 3)];
					pixels[j+1] = ROMPalettes.colors[(main.palettes.palettes[paletteNum][colorNum] * 3) + 1];
					pixels[j+2] = ROMPalettes.colors[(main.palettes.palettes[paletteNum][colorNum] * 3) + 2];
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
			g.setColor(Color.DARK_GRAY);
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
