package me.uyuyuy99.bbhack.types;

public class Tile8 {
	
	private int[] values;
	
	public Tile8(int[] valuesGiven) {
		values = valuesGiven;
	}
	
	public Tile8() {
		values = new int[64];
	}
	
	public int[] getValues() {
		return values;
	}
	
	public int getValue(int i) {
		return values[i];
	}
	
	public int getValue(int x, int y) {
		int i = (y * 8) + x;
		return values[i];
	}
	
	public void setValue(int i, int value) {
		values[i] = value;
	}
	
}
