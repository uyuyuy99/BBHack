package me.uyuyuy99.bbhack.ME;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.*;

import javax.swing.*;

import me.uyuyuy99.bbhack.CoordList;
import me.uyuyuy99.bbhack.Info;
import me.uyuyuy99.bbhack.MainMenu;
import me.uyuyuy99.bbhack.ObjectInfo;
import me.uyuyuy99.bbhack.rom.ROMObjects;
import me.uyuyuy99.bbhack.rom.ROMPalettes;
import me.uyuyuy99.bbhack.types.Tile64;

import me.uyuyuy99.bbhack.types.SpriteDef;
import me.uyuyuy99.bbhack.types.Sprite;
import me.uyuyuy99.bbhack.types.EBObjects.*;

import java.util.ArrayList;
import java.util.List;

public class PanelMap extends JPanel implements Info {

	private MainMenu main;
	private PanelChunkSelectME panelChunkSelect;
	
	private static final int[] sectorColors = new int[] {
		0xE3, 0x26, 0x36,
		0xC4, 0x62, 0x10,
		0xFF, 0xBF, 0x00,
		0xFF, 0x7E, 0x00,
		0x99, 0x66, 0xCC,
		0xA4, 0xC6, 0x39,
		0x66, 0x5D, 0x1E,
		0xFB, 0xCE, 0xB1,
		0x00, 0xFF, 0xFF,
		0xFD, 0xEE, 0x00,
		0x00, 0x7F, 0xFF,
		0xFF, 0x00, 0x7F,
		0x96, 0x4B, 0x00,
		0x66, 0xFF, 0x00,
		0xFF, 0x08, 0x00,
		0xB8, 0x86, 0x0B,
		0xCD, 0x5B, 0x45,
		0x17, 0x72, 0x45,
		0x00, 0x00, 0x7F,
		0xC7, 0x2C, 0x48,
		0xE4, 0x9B, 0x0F,
		0x00, 0xA8, 0x77,
		0xAD, 0xFF, 0x2F,
		0x66, 0x38, 0x54,
		0xB9, 0x00, 0x16,
		0xF4, 0x00, 0xA1,
		0xFC, 0xF7, 0x5E,
		0xC0, 0x36, 0x2C,
		0x71, 0xA6, 0xD2,
		0x90, 0xEE, 0x90,
		0x00, 0xA6, 0x93,
		0x69, 0x69, 0x69
	};
	
	private static final long serialVersionUID = 1L;
	private BufferedImage[][] mapGraphics;
	private List<BufferedImage> objectGraphics;
	
	//Current scroll-bar view coordinates (UNITS: 64x64 tiles)
	int viewX = 0;
	int viewY = 0;
	//Dimensions of the current map view (UNITS: 64x64 tiles)
	int viewWidth = 32;
	int viewHeight = 32;
	//The last positions of the scroll bars
	public int scrollHLast = 0;
	public int scrollVLast = 0;
	
	//Used for the flashing red effect on similar chunks when chunk is selected
	public float chunkPreviewAlpha;
	public Timer chunkPreviewTimer;
	
	//Object you are currently moving
	private ObjectInfo objectSelected;
	private int areaSelected;
	private int objectNumSelected;
	
	//View flags
	boolean viewGridChunk = true;
	boolean viewGridSector = false;
	boolean viewTilesetWarnings = true;
	boolean viewTilesetColors = false;
	boolean viewObjects = true;
	
	//Tutorial viewed flags
	private boolean tutorialTilesetColors = false;
	
	//Already drawn sprites
	private CoordList objectCoords;
	
	public PanelMap(MainMenu instance) {
		main = instance;
		
		setLayout(new BorderLayout());
		
		//Allocate enough space in graphics caching array for the entire screen
		mapGraphics = new BufferedImage[48][48];
		
		chunkPreviewAlpha = -0.1F;
		chunkPreviewTimer = new Timer(40,
			new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					repaint();
					if (chunkPreviewAlpha < 0)
						chunkPreviewTimer.stop();
				}
			}
		);
		
		objectCoords = new CoordList();
		
		this.addMouseListener(
			new MouseListener() {
				public void mousePressed(MouseEvent event) {
					for (int i=0; i<objectCoords.size(); i++) {
						int x = (objectCoords.getX(i)*16) - (viewX*64);
						int y = (objectCoords.getY(i)*16) - (viewY*64) - 8;
						
						if (((event.getX() > x) && (event.getX() < x+16)) && ((event.getY() > y) && (event.getY() < y+16))) {
							int sectorX = ((event.getX() / 64) + viewX) / 4;
							int sectorY = ((event.getY() / 64) + viewY) / 4;
							int area = main.map.sectorAreaGet(sectorX, sectorY);
							
							for (int j=0; j<ROMObjects.MAX_OBJECTS; j++) {
								if (main.objects.objects[area][j] != null) {
									if (main.objects.objects[area][j].getX() == objectCoords.getX(i) && main.objects.objects[area][j].getY() == objectCoords.getY(i)) {
										objectSelected = main.objects.objects[area][j];
										
										areaSelected = area;
										objectNumSelected = j;
										
										/*
										System.out.println("Area: " + area);
										System.out.println("ObjectNum: " + j);
										System.out.println("Type: " + objectSelected.getType());
										System.out.println("Dir: " + objectSelected.getDir());
										System.out.println("Location: " + objectSelected.getX() + ", " + objectSelected.getY());
										System.out.println();
										*/
										
										break;
									}
								}
							}
						}
					}
				}
				
				public void mouseReleased(MouseEvent event) {
					objectSelected = null;
					repaint();
				}
				
				public void mouseEntered(MouseEvent event) {
					//Mice
				}
				public void mouseExited(MouseEvent event) {
					//Eat
				}
				public void mouseClicked(MouseEvent event) {
					//Burritos
				}
			}
		);
		
		this.addMouseMotionListener(
			new MouseMotionListener() {
				public void mouseDragged(MouseEvent event) {
					if (objectSelected != null) {
						int x1 = objectSelected.getX();
						int y1 = objectSelected.getY();
						int x2 = (viewX * 4) + (event.getX() / 16);
						int y2 = (viewY * 4) + ((event.getY() + 8) / 16);
						
						int area = main.map.sectorAreaGet(x2 / 16, y2 / 16);
						
						// --- Check for area change + change area if needed --- //
						
						if (x1 != x2 || y1 != y2) {
							if (area != areaSelected) {
								if (main.objects.addObject(objectSelected, area)) {
									main.objects.removeObject(areaSelected, objectNumSelected);
									objectSelected.setX(x2);
									objectSelected.setY(y2);
								} else {
									//User can't move to filled area
								}
							} else {
								objectSelected.setX(x2);
								objectSelected.setY(y2);
							}
							
							repaint();
						}
					}
				}
				
				public void mouseMoved(MouseEvent event) {
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					
					for (int i=0; i<objectCoords.size(); i++) {
						int x = (objectCoords.getX(i)*16) - (viewX*64);
						int y = (objectCoords.getY(i)*16) - (viewY*64) - 8;
						
						if (((event.getX() > x) && (event.getX() < x+16)) && ((event.getY() > y) && (event.getY() < y+16))) {
							setCursor(new Cursor(Cursor.MOVE_CURSOR));
						}
					}
				}
			}
		);
	}

	public void setPanelChunkSelect(PanelChunkSelectME panel) {
		panelChunkSelect = panel;
	}
	
	public void scroll(int scrollH, int scrollV) {
		if (scrollH != scrollHLast || scrollV != scrollVLast) {
			//Graphics caching thingymaboobers
			if (scrollH > scrollHLast) {
				int diff = scrollH - scrollHLast;
				for (int x=0; x<48; x++) {
					for (int y=0; y<48; y++) {
						int newIndex = x - diff;
						if (newIndex < 0) continue; //Don't go past array bounds!
						if (x >= viewWidth || y >= viewHeight) mapGraphics[x][y] = null;
						
						mapGraphics[newIndex][y] = mapGraphics[x][y];
					}
				}
			} if (scrollH < scrollHLast) {
				int diff = scrollHLast - scrollH;
				for (int x=diff; x>=0; x--) { //Nullify the things outside the view
					for (int y=viewHeight; y>=0; y--) {
						if (x >= 48 || y >= 48) continue; //Don't go past array bounds!
						mapGraphics[x][y] = null;
					}
				}
				for (int x=47; x>=0; x--) {
					for (int y=47; y>=0; y--) {
						int newIndex = x + diff;
						if (newIndex >= 48) continue; //Don't go past array bounds!
						if (x >= viewWidth || y >= viewHeight) mapGraphics[x][y] = null;
						
						mapGraphics[newIndex][y] = mapGraphics[x][y];
					}
				}
			} if (scrollV > scrollVLast) {
				int diff = scrollV - scrollVLast;
				for (int x=0; x<48; x++) {
					for (int y=0; y<48; y++) {
						int newIndex = y - diff;
						if (newIndex < 0) continue; //Don't go past array bounds!
						if (x >= viewWidth || y >= viewHeight) mapGraphics[x][y] = null;
						
						mapGraphics[x][newIndex] = mapGraphics[x][y];
					}
				}
			} if (scrollV < scrollVLast) {
				int diff = scrollVLast - scrollV;
				for (int x=viewWidth; x>=0; x--) { //Nullify the things outside the view
					for (int y=diff; y>=0; y--) {
						if (x >= 48 || y >= 48) continue; //Don't go past array bounds!
						mapGraphics[x][y] = null;
					}
				}
				for (int x=47; x>=0; x--) {
					for (int y=47; y>=0; y--) {
						int newIndex = y + diff;
						if (newIndex >= 48) continue; //Don't go past array bounds!
						if (x >= viewWidth || y >= viewHeight) mapGraphics[x][y] = null;
						
						mapGraphics[x][newIndex] = mapGraphics[x][y];
					}
				}
			}
			
			//Reset last scroll variables, deal with scrolling offscreen
			if (scrollH > (256 - viewWidth)) scrollH = (256 - viewWidth);
			if (scrollV > (224 - viewHeight)) scrollV = (224 - viewHeight);
			
			scrollHLast = scrollH;
			scrollVLast = scrollV;
			
			//Change view variables accordingly, redraw everything
			viewX = scrollH;
			viewY = scrollV;
			
			repaint();
		}
	}
	
	public void clearGraphicsCache() {
		for (int i=0; i<48; i++) {
			for (int j=0; j<48; j++) {
				mapGraphics[i][j] = null;
			}
		}
	}
	
	public void refreshChunk(int x, int y) {
		mapGraphics[x][y] = null;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		BufferedImage[][] tempGraphics = new BufferedImage[48][48];
		
		viewWidth = (getWidth() / 64) + 1;
		viewHeight = (getHeight() / 64) + 1;
		
		int curX = 0;
		int curY = 0;
		
		objectCoords.clear();
		
		CoordList tilesetBorders = new CoordList();
		
		//long before = System.currentTimeMillis();
		for (int i=0; i<(viewWidth*viewHeight); i++) {
			BufferedImage tile;
			
			final int mapX = viewX + curX;
			final int mapY = viewY + curY;
			final int sectorX = (viewX + curX) / 4;
			final int sectorY = (viewY + curY) / 4;
			final int area = main.map.sectorAreaGet(sectorX, sectorY);
			
			if (mapGraphics[curX][curY] == null) {
				int[] pixels = new int[4096 * 3]; //3 values for each color
				
				boolean secondTileset = main.map.mapTilesetGet(mapX, mapY);
				
				Tile64 curTile;
				if (!secondTileset) {
					curTile = main.gfx.graphics64[(main.map.sectorTileset1Get(sectorX, sectorY) * 64) + main.map.mapTilesGet(mapX, mapY)].getCopy();
					for (int index : curTile.altTileset) {
						curTile.setTile(index, main.gfx.graphics16[(main.map.sectorTileset2Get(sectorX, sectorY) * 128) + curTile.tileNums[index]]);
					}
				} else {
					curTile = main.gfx.graphics64[(main.map.sectorTileset2Get(sectorX, sectorY) * 64) + main.map.mapTilesGet(mapX, mapY)].getCopy();
					for (int index : curTile.altTileset) {
						curTile.setTile(index, main.gfx.graphics16[(main.map.sectorTileset1Get(sectorX, sectorY) * 128) + curTile.tileNums[index]]);
					}
				}
				
				for (int j=0; j<pixels.length; j+=3) {
					int paletteNum = (main.map.sectorPaletteGet(sectorX, sectorY) * 4) + curTile.getPalette((((j/3) % 64) / 16), ((j/3) / 1024));
					int colorNum = curTile.getValue(j/3);
					
					pixels[j] = ROMPalettes.colors[(main.palettes.palettes[paletteNum][colorNum] * 3)];
					pixels[j+1] = ROMPalettes.colors[(main.palettes.palettes[paletteNum][colorNum] * 3) + 1];
					pixels[j+2] = ROMPalettes.colors[(main.palettes.palettes[paletteNum][colorNum] * 3) + 2];
				}
				
				//Image creation
				tile = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
				WritableRaster raster = tile.getRaster();
				raster.setPixels(0, 0, 64, 64, pixels);
			}
			else {
				tile = mapGraphics[curX][curY];
			}
			
			g.drawImage(tile, curX * 64, curY * 64, null); //Draw dat image yo
			tempGraphics[curX][curY] = tile; //Cache images already drawn
			
			//Load object positions
			for (int j=0; j<ROMObjects.MAX_OBJECTS; j++) {
				if (main.objects.objects[area][j] != null) {
					int type = main.objects.objects[area][j].getType();
					int dir = main.objects.objects[area][j].getDir();
					int x = main.objects.objects[area][j].getX();
					int y = main.objects.objects[area][j].getY();
					
					if (((x > viewX*4) && (x < (viewX*4) + (viewWidth*4))) && ((y > viewY*4) && (y < (viewY*4) + (viewHeight*4)))) {
						if (!objectCoords.contains(x, y)) {
							objectCoords.add(x, y);
						}
					}
				}
			}
			
			//Draw grid(s)
			g.setColor(COLOR_GRID1);
			if (viewGridChunk) {
				if (!((mapX+1) % 4 == 0) || !viewGridSector)
					g.drawLine((curX * 64) + 63, (curY * 64), (curX * 64) + 63, (curY * 64) + 63);
				if (!((mapY+1) % 4 == 0) || !viewGridSector)
					g.drawLine((curX * 64), (curY * 64) + 63, (curX * 64) + 63, (curY * 64) + 63);
			}
			if (viewGridSector) {
				g.setColor(COLOR_GRID2);
				if ((mapX+1) % 4 == 0) {
					g.drawLine((curX * 64) + 63, (curY * 64), (curX * 64) + 63, (curY * 64) + 63);
				} if ((mapY+1) % 4 == 0) {
					g.drawLine((curX * 64), (curY * 64) + 63, (curX * 64) + 63, (curY * 64) + 63);
				}
			}
			
			//Fade out chunks which user cannot move object to
			if (objectSelected != null) {
				if (area != areaSelected && !main.objects.isRoom(objectSelected, area)) {
					g.setColor(COLOR_FADED);
					g.fillRect(curX * 64, curY * 64, 64, 64);
				}
			}
			
			//Flashing red effect on similar chunks when chunk is selected
			if (chunkPreviewAlpha >= 0) {
				int selected = panelChunkSelect.chunkSelected;
				if (selected != -1) {
					if (selected < 64) {
						if (!main.map.mapTilesetGet(mapX, mapY)) {
							if (selected == main.map.mapTilesGet(mapX, mapY)) {
								g.setColor(transparent(COLOR_FLASH, (int) (chunkPreviewAlpha * 255)));
								g.fillRect(curX * 64, curY * 64, 64, 64);
							}
						}
					} else {
						if (main.map.mapTilesetGet(mapX, mapY)) {
							if (selected - 64 == main.map.mapTilesGet(mapX, mapY)) {
								g.setColor(transparent(COLOR_FLASH, (int) (chunkPreviewAlpha * 255)));
								g.fillRect(curX * 64, curY * 64, 64, 64);
							}
						}
					}
				}
			}
			
			// --- SECTOR COLORS TESTING --- //
			//float alpha = 0.6F;
			//int colorIndex = main.map.sectorTileset2Get(sectorX, sectorY);
			//g.setColor(new Color(sectorColors[colorIndex*3], sectorColors[colorIndex*3 + 1], sectorColors[colorIndex*3 + 2], alpha));
			//g.fillRect((curX * 64), (curY * 64), 64, 64);
			
			curX++;
			if ((curX+1) > viewWidth) {
				curX = 0;
				curY++;
			}
		}
		
		//Draw tileset triangle colors + load bad tileset border coords
		for (curX=-4; curX<viewWidth; curX++) {
			for (curY=-4; curY<viewHeight; curY++) {
				//Map coords
				final int mapX = viewX + curX;
				final int mapY = viewY + curY;
				final int sectorX = (viewX + curX) / 4;
				final int sectorY = (viewY + curY) / 4;
				
				if (offMap(mapX, mapY)) continue;
				
				//Tileset conflict borders
				if (mapX % 4 == 0 && mapY % 4 == 0) {
					final int t1 = main.map.sectorTileset1Get(sectorX, sectorY);
					final int t2 = main.map.sectorTileset2Get(sectorX, sectorY);
					
					if (viewTilesetColors) {
						//Triangle for tileset 1
						int[] xPoints = new int[] { (curX * 64) + 32, (curX * 64) + 224, (curX * 64) + 32 };
						int[] yPoints = new int[] { (curY * 64) + 32, (curY * 64) + 32, (curY * 64) + 224 };
						g.setColor(new Color(sectorColors[t1*3], sectorColors[t1*3 + 1], sectorColors[t1*3 + 2], 224));
						g.fillPolygon(xPoints, yPoints, 3);
						
						//Triangle for tileset 2
						xPoints = new int[] { (curX * 64) + 224, (curX * 64) + 224, (curX * 64) + 32 };
						yPoints = new int[] { (curY * 64) + 224, (curY * 64) + 32, (curY * 64) + 224 };
						g.setColor(new Color(sectorColors[t2*3], sectorColors[t2*3 + 1], sectorColors[t2*3 + 2], 224));
						g.fillPolygon(xPoints, yPoints, 3);
					}
					
					for (int checkX=-1; checkX<=1; checkX++) {
						for (int checkY=-1; checkY<=1; checkY++) {
							if (offMap((sectorX+checkX) * 4, (sectorY+checkY) * 4)) continue;
							if (main.map.sectorPaletteGet(sectorX+checkX, sectorY+checkY) != main.map.sectorPaletteGet(sectorX, sectorY)) continue;
							
							final int xa = mapX + (checkX * 4);
							final int ya = mapY + (checkY * 4);
							final boolean diff1 = (main.map.sectorTileset1Get(sectorX+checkX, sectorY+checkY) != t1);
							final boolean diff2 = (main.map.sectorTileset2Get(sectorX+checkX, sectorY+checkY) != t2);
							
							if (diff1) {
								for (int x=0; x<4; x++) {
									for (int y=0; y<4; y++) {
										if (outOfRange(x + checkX, 0, 3) || outOfRange(y + checkY, 0, 3)) continue;
										if (!main.map.mapTilesetGet(xa + x, ya + y)) {
											tilesetBorders.add(curX + (checkX * 4) + x, curY + (checkY * 4) + y);
										}
									}
								}
							} if (diff2) {
								for (int x=0; x<4; x++) {
									for (int y=0; y<4; y++) {
										if (outOfRange(x + checkX, 0, 3) || outOfRange(y + checkY, 0, 3)) continue;
										if (main.map.mapTilesetGet(xa + x, ya + y)) {
											tilesetBorders.add(curX + (checkX * 4) + x, curY + (checkY * 4) + y);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		if (viewObjects) {
			g.setColor(transparent(COLOR_OBJECT));
			for (int i=0; i<objectCoords.size(); i++) {
				int x = objectCoords.getX(i);
				int y = objectCoords.getY(i);
				g.fillRect((x*16) - (viewX*64), (y*16) - (viewY*64) - 8, 16, 16);
			}
		}
		
		if (viewTilesetWarnings) {
			g.setColor(transparent(COLOR_BORDER, 160));
			for (int i=0; i<tilesetBorders.size(); i++) {
				int x = tilesetBorders.getX(i);
				int y = tilesetBorders.getY(i);
				g.fillRect(x * 64, y * 64, 64, 64);
			}
		}
		
		if (chunkPreviewAlpha >= 0)
			chunkPreviewAlpha -= 0.1; //Flash effect animation
		
		mapGraphics = tempGraphics; //After done drawing, reset cache
		
		//System.out.println("LAG: " + (System.currentTimeMillis() - before) + "ms");





		objectGraphics = new ArrayList<>();
		if(true){
			for (List<List<EBObject>> bank : main.objects_eb.Banks) {
				for (List<EBObject> area_bank : bank) {
					for (EBObject object : area_bank){
						int fixObjX = object.x*2;
						int fixObjY = (object.y-0x81)*2;
						int offsetX = (fixObjX * 8);
						int offsetY = (fixObjY * 8);
						int viewXFix = viewX * 64;
						int viewYFix = viewY * 64;
						int viewWidthFix = viewWidth * 64;
						int viewHeightFix = viewHeight * 64;
						if(offsetX < viewXFix || offsetX > viewXFix+viewWidthFix){continue;}
						if(offsetY < viewYFix || offsetY > viewYFix+viewHeightFix){continue;}
						offsetX -= viewXFix;
						offsetY -= viewYFix-8;

						if(object instanceof EBNPC){
							SpriteDef def = ((EBNPC) object).mysprite;
							if(def != null){
								int calcId = def.offset;
								int sectorX = object.x / 16;
								int sectorY = (object.y - 0x80) / 16;
								int myarea = main.map.sectorAreaGet(sectorX, sectorY);
								if(calcId >= 0x80){
									calcId -= 0x80;
								}
								else {
									myarea=0;
								}
								drawSpriteDef(g, def, calcId, myarea, fixObjX, fixObjY, 2, 2);
							}
						}else if(object instanceof EBDoor){
							Image image = new ImageIcon(Info.class.getResource("/tiles/door.png")).getImage();
							Graphics2D g2 = (Graphics2D) g;
							g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,.75f));
							g.drawImage(image, offsetX, offsetY, 16, 16, null);
							g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
						}else if(object instanceof EBFlagSet){
							Image image = new ImageIcon(Info.class.getResource("/tiles/setflag.png")).getImage();
							Graphics2D g2 = (Graphics2D) g;
							g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,.75f));
							g.drawImage(image, offsetX, offsetY, 16, 16, null);
							g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
						}else{
							Image image = new ImageIcon(Info.class.getResource("/tiles/unk.png")).getImage();
							g.drawImage(image, offsetX, offsetY, 16, 16, null);
						}
					}
				}
			}

		}
		//int fucker = 0;
		//drawSpriteDef(g, fucker, main.sprites.Definitions[fucker].offset, 0, 0, 0, 2, 2);
	}

	//width + height is kinda tacky. pls find some other way to calc sprites
	public void drawSpriteDef(Graphics g, int i, int offset, int area, int x, int y, int width, int height){
		if(main.sprites.Definitions[i].spriteStart == -1) return;
		for (int z = 0; z < width * height; z++){
			Sprite hi = main.sprites.Sprites[main.sprites.Definitions[i].spriteStart+z];
			drawCharTile(g, hi.index+offset, area, x-width, (y-height)-height/2, hi.x, hi.y, hi.flipX == 1, hi.flipY == 1);
		}
	}
	public void drawSpriteDef(Graphics g, SpriteDef Definition, int offset, int area, int x, int y, int width, int height){
		if(Definition.spriteStart == -1) return;
		for (int z = 0; z < width * height; z++){
			Sprite hi = main.sprites.Sprites[Definition.spriteStart+z];
			drawCharTile(g, hi.index+offset, area, x-width, (y-height)-height/2, hi.x, hi.y, hi.flipX == 1, hi.flipY == 1);
		}
	}

	//dummy palette
	int[] fakeColors = {
			0,0,0,0,
			0,0,0,255,
			181,50,32,255,
			247,217,166,255
	};
	//function to draw  a character tile from the spritedefs
	public void drawCharTile(Graphics g, int id, int area, int x, int y, int subX, int subY, boolean flipX, boolean flipY){

		//draw char stuff

		//use the area lookup table to get the general chr id
		//if (area == 0){area = 1;}
		byte bank = main.gfx.areaTable[area];
		if(area == 0 || bank == 0){
			bank = 0x60;
		}
		//offset from start of chr
		int addr = (Byte.toUnsignedInt(bank) * 0x400);
		//ofset from start of the character chr
		int chroff = (addr - 0x18000)/0x10;
		int calcId = id + chroff;
		if (calcId < 0){
			calcId = calcId;
		}

		//x*y*rgb
		int[] pixels = new int[8*8*4];
		for (int j = 0; j < pixels.length; j+=4) {
			//int paletteNum = 7;
			int colorNum = main.gfx.characters[calcId].getValue(j/4);


			//pixels[j] = ROMPalettes.colors[main.palettes.palettes[paletteNum][colorNum] * 3];
			//pixels[j + 1] = ROMPalettes.colors[main.palettes.palettes[paletteNum][colorNum] * 3 + 1];
			//pixels[j + 2] = ROMPalettes.colors[main.palettes.palettes[paletteNum][colorNum] * 3 + 2];
			pixels[j] = fakeColors[colorNum * 4]; //r
			pixels[j + 1] = fakeColors[colorNum * 4 + 1]; //g
			pixels[j + 2] = fakeColors[colorNum * 4 + 2]; //b
			pixels[j + 3] = fakeColors[colorNum * 4 + 3]; //a
		}

		BufferedImage charTile = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
		WritableRaster raster = charTile.getRaster();
		raster.setPixels(0, 0, 8, 8, pixels);

		int offsetX = (x * 8) - (viewX*64);
		int offsetY = (y * 8) - (viewY*64);
		int useWidth = 8;
		int useHeight = 8;
		if(flipX) {
			offsetX += useWidth;
			useWidth *= -1;
		}
		if(flipY) {
			offsetY += useHeight;
			useHeight *= -1;
		}

		g.drawImage(charTile, offsetX+subX, offsetY+subY, useWidth, useHeight, null);
		objectGraphics.add(charTile);
	}



	private final boolean offMap(int x, int y) {
		if (x < 0) return true;
		if (y < 0) return true;
		if (x + viewWidth > 256) return true;
		if (y + viewHeight > 224) return true;

		return false;
	}

	public static final Color transparent(Color c) {
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), 128);
	} public static final Color transparent(Color c, int alpha) {
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
	}

	private static final boolean outOfRange(int n, int r1, int r2) {
		return (n < r1 || n > r2);
	}

}
