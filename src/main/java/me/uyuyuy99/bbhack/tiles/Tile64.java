package me.uyuyuy99.bbhack.tiles;

import java.util.ArrayList;


public class Tile64 implements Cloneable {
	
	private Tile16[] tiles;
	private int[] values;
	private int[] palettes;
	public ArrayList<Integer> altTileset = new ArrayList<Integer>(); //List of indexes of 16x16 tiles that use the secondary tileset
	public int[] tileNums;
	
	public Tile64(Tile16[] tilesGiven, int[] palettesGiven, ArrayList<Integer> altTilesetGiven, int[] tileNumsGiven) {
		tiles = tilesGiven;
		values = new int[4096];
		palettes = palettesGiven;
		altTileset = altTilesetGiven;
		tileNums = tileNumsGiven;
		
		for (int i=0; i<4096; i++) {
			values[i] = tiles[(i/1024) * 4 + (i%64) / 16].getValue(i % 16, (i/64) % 16);
		}
	}
	
	public int[] getValues() {
		return values;
	}
	
	public int getValue(int i) {
		return values[i];
	}
	
	public int getValue(int x, int y) {
		int i = (y * 64) + x;
		return values[i];
	}
	
	public Tile16[] getTiles() {
		return tiles;
	}
	
	public Tile16 getTile(int i) {
		return tiles[i];
	}
	
	public Tile16 getTile(int x, int y) {
		int i = (y * 4) + x;
		return tiles[i];
	}
	
	public int[] getPalettes() {
		return palettes;
	}
	
	public int getPalette(int i) {
		return palettes[i];
	}
	
	public int getPalette(int x, int y) {
		int i = (y * 4) + x;
		return palettes[i];
	}
	
	public void setTile(int index, Tile16 tile) {
		tiles[index] = tile;
		tileNums[index] = tile.tileNum;
		
		for (int i=0; i<4096; i++) {
			values[i] = tiles[(i/1024) * 4 + (i%64) / 16].getValue(i % 16, (i/64) % 16);
		}
	}
	
	public void setPalette(int index, int value) {
		palettes[index] = value;
	}
	
	@SuppressWarnings("unchecked")
	public Tile64 getCopy() {
		return new Tile64(tiles.clone(), palettes.clone(), (ArrayList<Integer>) altTileset.clone(), tileNums.clone());
	}
	
}
