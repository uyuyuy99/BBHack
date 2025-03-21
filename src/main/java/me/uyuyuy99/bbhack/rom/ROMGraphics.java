package me.uyuyuy99.bbhack.rom;

import java.util.ArrayList;
import me.uyuyuy99.bbhack.MainMenu;
import me.uyuyuy99.bbhack.types.Tile16;
import me.uyuyuy99.bbhack.types.Tile64;
import me.uyuyuy99.bbhack.types.Tile8;
import java.util.List;

public class ROMGraphics {

	private MainMenu main;

	public List<Tile8> graphics8;
	String[] tileset_files = {
		"dumped/chr/tileset1.bin",
		"dumped/chr/tileset2.bin",
		"dumped/chr/tileset3.bin",
		"dumped/chr/tileset4.bin",
		"dumped/chr/tileset5.bin",
		"dumped/chr/tileset6.bin",
		"dumped/chr/tileset7.bin",
		"dumped/chr/tileset8.bin",
		"dumped/chr/tileset9.bin",
		"dumped/chr/tileset10.bin",
		"dumped/chr/tileset11.bin",
		"dumped/chr/tileset12.bin",
		"dumped/chr/tileset13.bin",
		"dumped/chr/tileset14.bin",
		"dumped/chr/tileset15.bin",
		"dumped/chr/tileset16.bin",
		"dumped/chr/tileset17.bin",
		"dumped/chr/tileset18.bin",
		"dumped/chr/tileset19.bin",
		"dumped/chr/tileset20.bin",
		"dumped/chr/tileset21.bin",
		"dumped/chr/tileset22.bin",
		"dumped/chr/tileset23.bin",
		"dumped/chr/tileset24.bin",
		"dumped/chr/tileset25.bin",
		"dumped/chr/tileset26.bin",
		"dumped/chr/tileset27.bin",
		"dumped/chr/tileset28.bin",
		"dumped/chr/tileset29.bin",
		"dumped/chr/tileset30.bin",
		"dumped/chr/tileset31.bin",
		"dumped/chr/tileset32.bin",
	};
	List<ROMAssetIO> tileset_data = new ArrayList<>();

	public List<Tile16> graphics16;
	public List<Tile64> graphics64;
	int[] banks1664P = { 0x1000, 0x5000, 0x9000, 0xD000, 0x11000, 0x15000, 0x19000, 0x1D000 };
	int[] banks64    = {      0, 0x4000, 0x8000, 0xC000, 0x10000, 0x14000, 0x18000, 0x1C000 };

	String mapfile = "dumped/map.bin";
	ROMAssetIO mapData;

	String area_character_lut = "dumped/area_character_lut.bin";
	public byte[] areaTable;

	//the tiles needed to reconstruct character/object sprites
	public List<Tile8> characters;
	String[] character_files = {
		"dumped/chr/characters1.bin",
		"dumped/chr/characters2.bin",
		"dumped/chr/characters3.bin",
		"dumped/chr/characters4.bin",

		"dumped/chr/characters5.bin",
		"dumped/chr/characters6.bin",
		"dumped/chr/characters7.bin",
		"dumped/chr/characters8.bin",

		"dumped/chr/characters9.bin",
		"dumped/chr/characters10.bin",
		"dumped/chr/characters11.bin",
		"dumped/chr/characters12.bin",
	};
	List<ROMAssetIO> character_data = new ArrayList<>();


	public ROMGraphics(MainMenu instance) {
		main = instance;
		graphics8 = new ArrayList<>(); //Tiles per tileset times # of tilesets
		graphics16 = new ArrayList<>(); //Bank size times # of banks
		graphics64 = new ArrayList<>(); //Bank size times # of banks
		characters = new ArrayList<>();
		mapData = new ROMAssetIO(mapfile);

		//load files
		for(String file : character_files){
			character_data.add(new ROMAssetIO(file));
		}

		//start of character/object chr banks
		for (ROMAssetIO file : character_data){
			for(int a = 0; a < file.data.length; a+=0x10){
				byte[] bytes = file.getT(a, 0x10);
				int[] palette = new int[8*8];
				for (int n = 0; n < bytes.length / 2; n++) {
					byte bpl1 = bytes[n];
					byte bpl2 = bytes[n+8];
					int i = 7;
					for (byte bit = 1; bit != 0; bit = (byte) (bit << 1)){
						//get each pixel
						int bpl1Result = ((bpl1 & bit) != 0) ? 1 : 0;
						int bpl2Result = ((bpl2 & bit) != 0) ? 2 : 0; //lsh 1
						palette[(n*8)+i] = bpl1Result | bpl2Result;
						i--;
					}
				}
				characters.add(new Tile8(palette));
			}
		}

		//area bank lookup table
		areaTable = ROMAssetIO.open(area_character_lut);

		/*
		 * The first part of this constructor gets the raw graphics data
		 * from the ROM and translates the bytes into numbers from 0-3
		 * using the NES's graphics compression format
		 */

		//load files
		for(String file : tileset_files){
			tileset_data.add(new ROMAssetIO(file));
		}

		for (ROMAssetIO file : tileset_data){
			for (int a=0; a<0x400; a+=0x10) { //64 8x8 tiles in each tileset
				int[] bytes1 = file.get(a, 8);
				int[] bytes2 = file.get(a+8, 8);
				int[] bits1 = new int[0x40];
				int[] bits2 = new int[0x40];

				for (int i=0; i<8; i++) {
					int j = 7;
					for (byte m=1; m!=0; m<<=1) {
						bits1[(i*8) + j] = ((bytes1[i] & m) != 0)?1:0;
						bits2[(i*8) + j] = ((bytes2[i] & m) != 0)?1:0;
						j--;
					}
				}



				for (int i=0; i<0x40; i++) {
					bits1[i] |= bits2[i] * 2;
				}
				graphics8.add(new Tile8(bits1));
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
				graphics16.add(new Tile16(
				graphics8.get((tileset * 0x40) + (mapData.get(tileOffs) % 0x40)),
				graphics8.get((tileset * 0x40) + (mapData.get(tileOffs+1) % 0x40)),
				graphics8.get((tileset * 0x40) + (mapData.get(tileOffs+2) % 0x40)),
				graphics8.get((tileset * 0x40) + (mapData.get(tileOffs+3) % 0x40)),
				i % 0x80));
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
					palettes64[(curBank * 0x100) + i][j] = mapData.get(paletteOffs + j) / 0x40;
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
					int tileNum = mapData.get(subOffs) % 0x80;
					curTiles[j] = graphics16.get((tileset * 0x80) + tileNum); //This gets the correct 16x16 tile from the loaded list (ignoring all that alternate tileset crap)

					tileNums[j] = tileNum;

					if (mapData.get(subOffs) > 0x7F) { //If last bit is set, use alternate tileset
						altTileset.add(j);
					}
				}

				graphics64.add(new Tile64(curTiles, palettes64[(curBank*0x100) + i], altTileset, tileNums));
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
				Tile64 tile = graphics64.get((curBank * 0x100) + i);

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
				int[] paletteList = graphics64.get((curBank * 0x100) + i).getPalettes();
				for (int j=0; j<0x10; j++) {
					main.rom.write(paletteOffs + j, (byte) ((main.rom.get(paletteOffs + j) % 0x40) + (paletteList[j] * 0x40)));
				}
			}
			curBank++;
		}

		//main.rom.saveMap();
	}

}
