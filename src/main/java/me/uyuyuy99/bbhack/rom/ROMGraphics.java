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
	
	public ROMGraphics(MainMenu instance) {
		main = instance;
		graphics8 = new Tile8[64 * 32]; //Tiles per tileset times # of tilesets
		graphics16 = new Tile16[512 * 8]; //Bank size times # of banks
		graphics64 = new Tile64[256 * 8]; //Bank size times # of banks
		
		/*
		 * The first part of this constructor gets the raw graphics data
		 * from the ROM and translates the bytes into numbers from 0-3
		 * using the NES's graphics compression format
		 */
		
		for (int i=0; i<graphics8.length; i++) {
			graphics8[i] = new Tile8();
		}
		
		for (int tileset=0; tileset<32; tileset++) {
			for (int tile=0; tile<64; tile++) { //64 8x8 tiles in each tileset
				short[] bytes1short = main.rom.get(262160 + (tileset*1024) + (tile*16), 8);
				short[] bytes2short = main.rom.get(262168 + (tileset*1024) + (tile*16), 8);
				byte[] bytes1 = new byte[bytes1short.length];
				byte[] bytes2 = new byte[bytes2short.length];
				int[] bits1 = new int[64];
				int[] bits2 = new int[64];
				
				//Convert shorts to bytes
				for (int i=0; i<bytes1short.length; i++) {
					bytes1[i] = (byte) bytes1short[i];
					bytes2[i] = (byte) bytes2short[i];
				}
				
				for (int i=0; i<bytes1.length; i++) {
					int j = 7;
					for (byte m=1; m!=0; m<<=1) {
						bits1[(i*8) + j] = ((bytes1[i] & m) != 0)?1:0;
						j--;
					}
				}
				for (int i=0; i<bytes2.length; i++) {
					int j = 7;
					for (byte m=1; m!=0; m<<=1) {
						bits2[(i*8) + j] = ((bytes2[i] & m) != 0)?1:0;
						j--;
					}
				}
				
				for (int i=0; i<64; i++) {
					graphics8[(tileset*64) + tile].setValue(i, (bits1[i]) + (bits2[i] * 2));
				}
			}
		}
		
		/*
		 * See? That didn't take long! :)
		 */
		
		//Lower 6 bits of 3000-37FF
		int[] banks16 = new int[] { 12304, 28688, 45072, 61456, 77840, 94224, 110608, 126992 };
		int curBank = 0;
		for (int offset : banks16) {
			for (int i=0; i<512; i++) {
				int tileset = (curBank * 4) + (i / 128); //4 tilesets for each bank
				int tileOffs = offset + (i * 4); //Offset of current 16x16 tile data
				graphics16[(curBank * 512) + i] = new Tile16(graphics8[(tileset * 64) + (main.rom.get(tileOffs) % 64)], graphics8[(tileset * 64) + (main.rom.get(tileOffs+1) % 64)],
						graphics8[(tileset * 64) + (main.rom.get(tileOffs+2) % 64)], graphics8[(tileset * 64) + (main.rom.get(tileOffs+3) % 64)], i % 128);
			}
			curBank++;
		}
		
		//Upper 2 bits of 3000-3FFF
		int[][] palettes64 = new int[256 * 8][16]; //Temporary array for 64x64 tile palettes; used in initialization of Tile64's
		int[] banks64Palette = new int[] { 12304, 28688, 45072, 61456, 77840, 94224, 110608, 126992 };
		curBank = 0;
		for (int offset : banks64Palette) {
			for (int i=0; i<256; i++) {
				int paletteOffs = offset + (i * 16); //Offset of current 64x64 palette data
				for (int j=0; j<16; j++) {
					palettes64[(curBank * 256) + i][j] = main.rom.get(paletteOffs + j) / 64;
				}
			}
			curBank++;
		}
		
		//2000-2FFF
		int[] banks64 = new int[] { 8208, 24592, 40976, 57360, 73744, 90128, 106512, 122896 };
		curBank = 0;
		for (int offset : banks64) {
			for (int i=0; i<256; i++) {
				int tileset = (curBank * 4) + (i / 64); //4 tilesets for each bank
				int tileOffs = offset + (i * 16); //Offset of current 64x64 palette data
				Tile16[] curTiles = new Tile16[16];
				ArrayList<Integer> altTileset = new ArrayList<Integer>();
				int[] tileNums = new int[16];
				
				//Iterate through the 64x64 tile data & decode it
				for (int j=0; j<16; j++) {
					int subOffs = tileOffs + j;
					int tileNum = main.rom.get(subOffs) % 128;
					curTiles[j] = graphics16[(tileset * 128) + tileNum]; //This gets the correct 16x16 tile from the loaded list (ignoring all that alternate tileset crap)
					
					tileNums[j] = tileNum;
					
					if (main.rom.get(subOffs) > 127) { //If last bit is set, use alternate tileset
						altTileset.add(j);
					}
				}
				
				graphics64[(curBank*256) + i] = new Tile64(curTiles, palettes64[(curBank*256) + i], altTileset, tileNums);
			}
			curBank++;
		}
	}
	
	public void save64() {
		//2000-2FFF
		int[] banks64 = new int[] { 8208, 24592, 40976, 57360, 73744, 90128, 106512, 122896 };
		int curBank = 0;
		for (int offset : banks64) {
			for (int i=0; i<256; i++) {
				int tileOffs = offset + (i * 16); //Offset of current 64x64 palette data
				Tile64 tile = graphics64[(curBank * 256) + i];
				
				//Iterate through the 64x64 tile data & encode it
				for (int j=0; j<16; j++) {
					int subOffs = tileOffs + j;
					int curByte = tile.tileNums[j];
					
					if (tile.altTileset.contains(j)) {
						curByte += 128;
					}
					
					main.rom.write(subOffs, (short) curByte);
				}
			}
			curBank++;
		}
		
		//Upper 2 bits of 3000-3FFF
		int[][] palettes64 = new int[256 * 8][16]; //Temporary array for 64x64 tile palettes; used in initialization of Tile64's
		int[] banks64Palette = new int[] { 12304, 28688, 45072, 61456, 77840, 94224, 110608, 126992 };
		curBank = 0;
		for (int offset : banks64Palette) {
			for (int i=0; i<256; i++) {
				int paletteOffs = offset + (i * 16); //Offset of current 64x64 palette data
				int[] paletteList = graphics64[(curBank * 256) + i].getPalettes();
				for (int j=0; j<16; j++) {
					main.rom.write(paletteOffs + j, (short) ((main.rom.get(paletteOffs + j) % 64) + (paletteList[j] * 64)));
				}
			}
			curBank++;
		}
		
		main.rom.saveMap();
	}
	
}
