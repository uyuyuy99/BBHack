package me.uyuyuy99.bbhack;

public class ItemInfo {
	
	public static final int WEAPON = 0;
	public static final int COIN = 1;
	public static final int RING = 2;
	public static final int PENDANT = 3;
	
	public Integer namePointer;
	public Boolean[] characters = new Boolean[6];
	public Boolean edible;
	public Boolean droppable;
	public Integer power;
	public Integer type;
	public Integer action;
	public Integer actionBattle;
	public Integer price;
	
	public Integer x;
	public Integer y;
	public Integer unknown1;
	public Integer unknown2;
	
	public Integer music;
	public Integer dir;
	
	public Integer expConstant;
	public Integer[] statGrowth = new Integer[5];
	
	public Integer levelNinten;
	public Integer levelAna;
	public Integer ppCost;
	
}
