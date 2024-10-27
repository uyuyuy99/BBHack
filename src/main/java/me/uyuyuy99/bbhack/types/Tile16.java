package me.uyuyuy99.bbhack.types;


public class Tile16 {
	
	private Tile8[] tiles;
	private int[] values;
	public int tileNum;
	
	public Tile16(Tile8[] tilesGiven, int tileNumGiven) {
		tiles = tilesGiven;
		values = new int[256];
		tileNum = tileNumGiven;
		
		for (int i=0; i<256; i++) {
			values[i] = tiles[(i/128)*2 + (i%16)/8].getValue(i % 8, (i/16) % 8);
		}
	}
	
	public Tile16(Tile8 t1, Tile8 t2, Tile8 t3, Tile8 t4, int tileNumGiven) {
		tiles = new Tile8[] {t1, t2, t3, t4};
		values = new int[256];
		tileNum = tileNumGiven;
		
		for (int i=0; i<256; i++) {
			values[i] = tiles[(i/128)*2 + (i%16)/8].getValue(i % 8, (i/16) % 8);
		}
	}
	
	public int[] getValues() {
		return values;
	}
	
	public int getValue(int i) {
		return values[i];
	}
	
	public int getValue(int x, int y) {
		int i = (y * 16) + x;
		return values[i];
	}
	
	public Tile8[] getTiles() {
		return tiles;
	}
	
	public Tile8 getTile(int i) {
		return tiles[i];
	}
	
	public Tile8 getTile(int x, int y) {
		int i = (y * 2) + x;
		return tiles[i];
	}
	
}
