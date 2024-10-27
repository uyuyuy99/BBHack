package me.uyuyuy99.bbhack.rom;

import me.uyuyuy99.bbhack.MainMenu;

public class ROMMapSectors {
	
	private MainMenu main;
	
	public int[] mapTiles;
	public boolean[] mapTileset;
	public boolean[] mapEvent;
	
	public int[] sectorPalette;
	public int[] sectorArea;
	public int[] sectorTileset1;
	public int[] sectorTileset2;

	int[] banksMap = { 0x4010, 0x8010, 0xC010, 0x10010, 0x14010, 0x18010, 0x1C010 };
	int[] banksSector = { 0x7810, 0xB810, 0xF810, 0x13810, 0x17810, 0x1B810, 0x1F810 };
	
	public ROMMapSectors(MainMenu instance) {
		main = instance;
		
		//Map height * width in terms of 64x64 chunks
		mapTiles = new int[0x100 * 0x100];
		mapTileset = new boolean[0x100 * 0x100];
		mapEvent = new boolean[0x100 * 0x100];
		
		//Same as above, divided by 4
		sectorPalette = new int[0x40 * 0x40];
		sectorArea = new int[0x40 * 0x40];
		sectorTileset1 = new int[0x40 * 0x40];
		sectorTileset2 = new int[0x40 * 0x40];
		
		//0000-1FFF (skip 1st bank)
		int curBank = 0;
		for (int offset : banksMap) {
			for (int i=0; i<0x2000; i++) {
				int tileOffs = offset + i; //Offset of current 64x64 map data
				int arrayOffs = (curBank * 0x2000) + i;
				int currentByte = main.rom.get(tileOffs);
				
				mapTiles[arrayOffs] = currentByte % 0x40; //Get lower 6 bits only of map data, store in array
				
				int upper2 = currentByte / 0x40;
				if (upper2 % 2 == 1) mapTileset[arrayOffs] = true; //If first bit is set
				else mapTileset[arrayOffs] = false;
				if (upper2 > 1) mapEvent[arrayOffs] = true; //If second bit is set
				else mapEvent[arrayOffs] = false;
			}
			curBank++;
		}
		
		//Lower 6 bits of 3800-3FFF (skip 1st bank)
		curBank = 0;
		for (int offset : banksSector) {
			for (int i=0; i<0x200; i++) {
				int tileOffs = offset + (i * 4); //Offset of current 256x256 sector data
				int arrayOffs = (curBank * 0x200) + i;
				
				//First 6 bits of each byte
				sectorPalette[arrayOffs] = main.rom.get(tileOffs) % 0x40;
				sectorArea[arrayOffs] = main.rom.get(tileOffs + 1) % 0x40;
				sectorTileset1[arrayOffs] = main.rom.get(tileOffs + 2) % 0x40;
				sectorTileset2[arrayOffs] = main.rom.get(tileOffs + 3) % 0x40;
			}
			curBank++;
		}
		
		//Print list of areas in game (testing purposes)
//		List<Integer> sectorAreaList = Arrays.stream(sectorArea).boxed().distinct().sorted().toList();
//		System.out.println("sector areas:");
//		for (Integer i : sectorAreaList) {
//			System.out.println(i);
//		}
	}
	
	public void save() {
		//Lower 6 bits of 3800-3FFF (skip 1st bank)
		int curBank = 0;
		for (int offset : banksSector) {
			for (int i=0; i<0x200; i++) {
				int tileOffs = offset + (i * 4); //Offset of current 256x256 sector data
				int arrayOffs = (curBank * 0x200) + i;
				
				main.rom.write(tileOffs, (byte) (((main.rom.get(tileOffs) / 0x40) * 0x40) + sectorPalette[arrayOffs]));
				main.rom.write(tileOffs + 1, (byte) (((main.rom.get(tileOffs + 1) / 0x40) * 0x40) + sectorArea[arrayOffs]));
				main.rom.write(tileOffs + 2, (byte) (((main.rom.get(tileOffs + 2) / 0x40) * 0x40) + sectorTileset1[arrayOffs]));
				main.rom.write(tileOffs + 3, (byte) (((main.rom.get(tileOffs + 3) / 0x40) * 0x40) + sectorTileset2[arrayOffs]));
			}
			curBank++;
		}
		
		//0000-1FFF (skip 1st bank)
		curBank = 0;
		for (int offset : banksMap) {
			for (int i=0; i<0x2000; i++) {
				int tileOffs = offset + i; //Offset of current 64x64 map data
				int arrayOffs = (curBank * 0x2000) + i;
				int curByte = mapTiles[arrayOffs];
				
				boolean curTileset = mapTileset[arrayOffs];
				boolean curEvent = mapEvent[arrayOffs];
				
				//Upper 2 bits
				if (curTileset) {
					curByte += 0x40;
				} if (curEvent) {
					curByte += 0x80;
				}
				
				main.rom.write(tileOffs, (byte) curByte);
			}
			curBank++;
		}
		
		main.rom.saveMap();
	}
	
	public int mapTilesGet(int x, int y) {
		return mapTiles[(y * 0x100) + x];
	}
	
	public boolean mapTilesetGet(int x, int y) {
		return mapTileset[(y * 0x100) + x];
	}
	
	public boolean mapEventGet(int x, int y) {
		return mapEvent[(y * 0x100) + x];
	}
	
	public int sectorPaletteGet(int x, int y) {
		return sectorPalette[(y * 0x40) + x];
	}
	
	public int sectorAreaGet(int x, int y) {
		return sectorArea[(y * 0x40) + x];
	}
	
	public int sectorTileset1Get(int x, int y) {
		return sectorTileset1[(y * 0x40) + x];
	}
	
	public int sectorTileset2Get(int x, int y) {
		return sectorTileset2[(y * 0x40) + x];
	}
	
}
