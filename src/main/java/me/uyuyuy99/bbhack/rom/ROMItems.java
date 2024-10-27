package me.uyuyuy99.bbhack.rom;

import me.uyuyuy99.bbhack.ItemInfo;
import me.uyuyuy99.bbhack.MainMenu;

public class ROMItems {
	
	public static final int MAX_ITEMS = 256;
	
	public static final int INDEX_ITEMS = 0x00;
	public static final int INDEX_TELEPORT = 0x80;
	public static final int INDEX_TRAIN = 0x90;
	public static final int INDEX_PLANE = 0xB8;
	public static final int INDEX_LEVELUP = 0xB8;
	public static final int INDEX_PSI = 0xC0;
	
	MainMenu main;
	
	public ItemInfo[] items = new ItemInfo[MAX_ITEMS];
	
	public ROMItems(MainMenu instance) {
		main = instance;
		
		//1810-200F
		int offset = 0x1810;
		for (int i=0; i<(MAX_ITEMS*8); i+=8) {
			int itemNum = i / 8;
			int curOffs = offset + i;
			ItemInfo item = new ItemInfo();
			int[] bytes = new int[8];
			for (int j=0; j<8; j++) {
				bytes[j] = main.rom.get(curOffs + j);
			}
			
			//Load name (except for level-up table)
			if (!(itemNum >= 0xB8 && itemNum <= 0xBF)) {
				item.namePointer = bytes[0] + ((bytes[1] - 128) * 256) + 16;
			}
			
			//Items
			if (itemNum < 0x80) {
				Boolean[] characters = new Boolean[6];
				for (int j=0; j<6; j++) {
					characters[j] = (bytes[2] / Math.pow(2, j)) % 2 != 0;
				}
				item.characters = characters;
				item.edible = (bytes[2] / 64) % 2 != 0;
				item.droppable = (bytes[2] / 128) % 2 != 0;
				item.power = bytes[3] % 64;
				item.type = bytes[3] / 64;
				item.action = bytes[4];
				item.actionBattle = bytes[5];
				item.price = bytes[6] + (bytes[7] * 256);
			} //Teleport locations
			else if (itemNum < 0x90) {
				item.x = bytes[2] + (bytes[3] * 256);
				item.y = bytes[4] + (bytes[5] * 256);
				item.unknown1 = bytes[6];
				item.unknown2 = bytes[7];
			} //Train destinations
			else if (itemNum < 0x9C) {
				item.music = bytes[2] % 64;
				item.dir = bytes[4] % 64;
				item.x = (bytes[2] / 64) + (bytes[3] * 4);
				item.y = (bytes[4] / 64) + (bytes[5] * 4);
				item.price = bytes[6] + (bytes[7] * 256);
			} //Character level-up data
			else if (itemNum < 0xC0) {
				Integer[] statGrowth = new Integer[5];
				for (int j=0; j<5; j++) {
					statGrowth[j] = bytes[3 + j];
				}
				item.expConstant = bytes[0];
				item.unknown1 = bytes[1];
				item.unknown2 = bytes[2];
				item.statGrowth = statGrowth;
			} //PSI
			else {
				item.levelNinten = bytes[2];
				item.levelAna = bytes[3];
				item.action = bytes[4];
				item.actionBattle = bytes[5];
				item.unknown1 = bytes[6];
				item.ppCost = bytes[7];
			}
			
			items[itemNum] = item;
		}
	}
	
	public String getItemName(int item) {
		return main.getTextFromPointer(items[item].namePointer);
	}

}
