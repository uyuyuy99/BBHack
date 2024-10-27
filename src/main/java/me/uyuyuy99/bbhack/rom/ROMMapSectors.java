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
	
	public ROMMapSectors(MainMenu instance) {
		main = instance;
		
		//Map height * width in terms of 64x64 chunks
		mapTiles = new int[256 * 256];
		mapTileset = new boolean[256 * 256];
		mapEvent = new boolean[256 * 256];
		
		//Same as above, divided by 4
		sectorPalette = new int[64 * 64];
		sectorArea = new int[64 * 64];
		sectorTileset1 = new int[64 * 64];
		sectorTileset2 = new int[64 * 64];
		
		//0000-1FFF (skip 1st bank)
		int[] banksMap = new int[] { 16400, 32784, 49168, 65552, 81936, 98320, 114704 };
		int curBank = 0;
		for (int offset : banksMap) {
			for (int i=0; i<8192; i++) {
				int tileOffs = offset + i; //Offset of current 64x64 map data
				int arrayOffs = (curBank * 8192) + i;
				int currentByte = main.rom.get(tileOffs);
				
				mapTiles[arrayOffs] = currentByte % 64; //Get lower 6 bits only of map data, store in array
				
				int upper2 = currentByte / 64;
				if (upper2 % 2 == 1) mapTileset[arrayOffs] = true; //If first bit is set
				else mapTileset[arrayOffs] = false;
				if (upper2 > 1) mapEvent[arrayOffs] = true; //If second bit is set
				else mapEvent[arrayOffs] = false;
			}
			curBank++;
		}
		
		//Lower 6 bits of 3800-3FFF (skip 1st bank)
		int[] banksSector = new int[] { 30736, 47120, 63504, 79888, 96272, 112656, 129040 };
		curBank = 0;
		for (int offset : banksSector) {
			for (int i=0; i<512; i++) {
				int tileOffs = offset + (i * 4); //Offset of current 256x256 sector data
				int arrayOffs = (curBank * 512) + i;
				
				//First 6 bits of each byte
				sectorPalette[arrayOffs] = main.rom.get(tileOffs) % 64;
				sectorArea[arrayOffs] = main.rom.get(tileOffs + 1) % 64;
				sectorTileset1[arrayOffs] = main.rom.get(tileOffs + 2) % 64;
				sectorTileset2[arrayOffs] = main.rom.get(tileOffs + 3) % 64;
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
		int[] banksSector = new int[] { 30736, 47120, 63504, 79888, 96272, 112656, 129040 };
		int curBank = 0;
		for (int offset : banksSector) {
			for (int i=0; i<512; i++) {
				int tileOffs = offset + (i * 4); //Offset of current 256x256 sector data
				int arrayOffs = (curBank * 512) + i;
				
				main.rom.write(tileOffs, (short) (((main.rom.get(tileOffs) / 64) * 64) + sectorPalette[arrayOffs]));
				main.rom.write(tileOffs + 1, (short) (((main.rom.get(tileOffs + 1) / 64) * 64) + sectorArea[arrayOffs]));
				main.rom.write(tileOffs + 2, (short) (((main.rom.get(tileOffs + 2) / 64) * 64) + sectorTileset1[arrayOffs]));
				main.rom.write(tileOffs + 3, (short) (((main.rom.get(tileOffs + 3) / 64) * 64) + sectorTileset2[arrayOffs]));
			}
			curBank++;
		}
		
		//0000-1FFF (skip 1st bank)
		int[] banksMap = new int[] { 16400, 32784, 49168, 65552, 81936, 98320, 114704 };
		curBank = 0;
		for (int offset : banksMap) {
			for (int i=0; i<8192; i++) {
				int tileOffs = offset + i; //Offset of current 64x64 map data
				int arrayOffs = (curBank * 8192) + i;
				int curByte = mapTiles[arrayOffs];
				
				boolean curTileset = mapTileset[arrayOffs];
				boolean curEvent = mapEvent[arrayOffs];
				
				//Upper 2 bits
				if (curTileset) {
					curByte += 64;
				} if (curEvent) {
					curByte += 128;
				}
				
				main.rom.write(tileOffs, (short) curByte);
			}
			curBank++;
		}
		
		main.rom.saveMap();
	}
	
	public int mapTilesGet(int x, int y) {
		return mapTiles[(y * 256) + x];
	}
	
	public boolean mapTilesetGet(int x, int y) {
		return mapTileset[(y * 256) + x];
	}
	
	public boolean mapEventGet(int x, int y) {
		return mapEvent[(y * 256) + x];
	}
	
	public int sectorPaletteGet(int x, int y) {
		return sectorPalette[(y * 64) + x];
	}
	
	public int sectorAreaGet(int x, int y) {
		return sectorArea[(y * 64) + x];
	}
	
	public int sectorTileset1Get(int x, int y) {
		return sectorTileset1[(y * 64) + x];
	}
	
	public int sectorTileset2Get(int x, int y) {
		return sectorTileset2[(y * 64) + x];
	}
	
}
