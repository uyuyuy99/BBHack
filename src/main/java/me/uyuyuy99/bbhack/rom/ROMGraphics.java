package me.uyuyuy99.bbhack.rom;

import java.util.ArrayList;

import me.uyuyuy99.bbhack.MainMenu;
import me.uyuyuy99.bbhack.tiles.Tile16;
import me.uyuyuy99.bbhack.tiles.Tile64;
import me.uyuyuy99.bbhack.tiles.Tile8;

public class ROMGraphics {
	
	private MainMenu main;
	
	public Tile8[] graphics8;
	public Tile16[] graphics16;
	public Tile64[] graphics64;


	int chr_start = 0x40010; //start of chr rom
	int[] banks1664P = { 0x3010, 0x7010, 0xB010, 0xF010, 0x13010, 0x17010, 0x1B010, 0x1F010 };
	int[] banks64 = { 0x2010, 0x6010, 0xA010, 0xE010, 0x12010, 0x16010, 0x1A010, 0x1E010 };

	public ROMGraphics(MainMenu instance) {
		main = instance;
		graphics8 = new Tile8[0x40 * 0x20]; //Tiles per tileset times # of tilesets
		graphics16 = new Tile16[0x200 * 8]; //Bank size times # of banks
		graphics64 = new Tile64[0x100 * 8]; //Bank size times # of banks
		
		/*
		 * The first part of this constructor gets the raw graphics data
		 * from the ROM and translates the bytes into numbers from 0-3
		 * using the NES's graphics compression format
		 */
		
		for (int i=0; i<graphics8.length; i++) {
			graphics8[i] = new Tile8();
		}
		
		for (int tileset=0; tileset<0x20; tileset++) {
			for (int tile=0; tile<0x40; tile++) { //64 8x8 tiles in each tileset
				byte[] bytes1 = main.rom.getT(chr_start + tileset * 0x400 + tile * 0x10, 8);
				byte[] bytes2 = main.rom.getT((chr_start + 8) + tileset * 0x400 + tile * 0x10, 8);
				int[] bits1 = new int[0x40];
				int[] bits2 = new int[0x40];
				
				for (int i=0; i<bytes1.length; i++) {
					int j = 7;
					for (byte m=1; m!=0; m<<=1) {
						bits1[(i*8) + j] = ((Byte.toUnsignedInt(bytes1[i]) & m) != 0)?1:0;
						j--;
					}
				}
				for (int i=0; i<bytes2.length; i++) {
					int j = 7;
					for (byte m=1; m!=0; m<<=1) {
						bits2[(i*8) + j] = ((Byte.toUnsignedInt(bytes2[i]) & m) != 0)?1:0;
						j--;
					}
				}
				
				for (int i=0; i<0x40; i++) {
					graphics8[(tileset*0x40) + tile].setValue(i, (bits1[i]) + (bits2[i] * 2));
				}
			}
		}
		
		/*
		 * See? That didn't take long! :)
		 */
		
		//Lower 6 bits of 3000-37FF
		int curBank = 0;
		for (int offset : banks1664P) {
			for (int i=0; i<0x200; i++) {
				int tileset = (curBank * 4) + (i / 0x80); //4 tilesets for each bank
				int tileOffs = offset + (i * 4); //Offset of current 16x16 tile data
				graphics16[(curBank * 0x200) + i] =
						new Tile16(graphics8[(tileset * 0x40) + (main.rom.get(tileOffs) % 0x40)],
								   graphics8[(tileset * 0x40) + (main.rom.get(tileOffs+1) % 0x40)],
						           graphics8[(tileset * 0x40) + (main.rom.get(tileOffs+2) % 0x40)],
								   graphics8[(tileset * 0x40) + (main.rom.get(tileOffs+3) % 0x40)],
								i % 0x80);
			}
			curBank++;
		}
		
		//Upper 2 bits of 3000-3FFF
		int[][] palettes64 = new int[0x100 * 8][0x10]; //Temporary array for 64x64 tile palettes; used in initialization of Tile64's
		curBank = 0;
		for (int offset : banks1664P) {
			for (int i=0; i<0x100; i++) {
				int paletteOffs = offset + (i * 16); //Offset of current 64x64 palette data
				for (int j=0; j<0x10; j++) {
					palettes64[(curBank * 0x100) + i][j] = main.rom.get(paletteOffs + j) / 0x40;
				}
			}
			curBank++;
		}
		
		//2000-2FFF
		curBank = 0;
		for (int offset : banks64) {
			for (int i=0; i<0x100; i++) {
				int tileset = (curBank * 4) + (i / 0x40); //4 tilesets for each bank
				int tileOffs = offset + (i * 0x10); //Offset of current 64x64 palette data
				Tile16[] curTiles = new Tile16[0x10];
				ArrayList<Integer> altTileset = new ArrayList<Integer>();
				int[] tileNums = new int[0x10];
				
				//Iterate through the 64x64 tile data & decode it
				for (int j=0; j<0x10; j++) {
					int subOffs = tileOffs + j;
					int tileNum = main.rom.get(subOffs) % 0x80;
					curTiles[j] = graphics16[(tileset * 0x80) + tileNum]; //This gets the correct 16x16 tile from the loaded list (ignoring all that alternate tileset crap)
					
					tileNums[j] = tileNum;
					
					if (main.rom.get(subOffs) > 0x7F) { //If last bit is set, use alternate tileset
						altTileset.add(j);
					}
				}
				
				graphics64[(curBank*0x100) + i] = new Tile64(curTiles, palettes64[(curBank*0x100) + i], altTileset, tileNums);
			}
			curBank++;
		}
	}
	
	public void save64() {
		//2000-2FFF
		int curBank = 0;
		for (int offset : banks64) {
			for (int i=0; i<0x100; i++) {
				int tileOffs = offset + (i * 0x10); //Offset of current 64x64 palette data
				Tile64 tile = graphics64[(curBank * 0x100) + i];
				
				//Iterate through the 64x64 tile data & encode it
				for (int j=0; j<0x10; j++) {
					int subOffs = tileOffs + j;
					int curByte = tile.tileNums[j];
					
					if (tile.altTileset.contains(j)) {
						curByte += 0x80;
					}
					
					main.rom.write(subOffs, (byte) curByte);
				}
			}
			curBank++;
		}
		
		//Upper 2 bits of 3000-3FFF
		curBank = 0;
		for (int offset : banks1664P) {
			for (int i=0; i<0x100; i++) {
				int paletteOffs = offset + (i * 0x10); //Offset of current 64x64 palette data
				int[] paletteList = graphics64[(curBank * 0x100) + i].getPalettes();
				for (int j=0; j<0x10; j++) {
					main.rom.write(paletteOffs + j, (byte) ((main.rom.get(paletteOffs + j) % 0x40) + (paletteList[j] * 0x40)));
				}
			}
			curBank++;
		}
		
		main.rom.saveMap();
	}
	
}
