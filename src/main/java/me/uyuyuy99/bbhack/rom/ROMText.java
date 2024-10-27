package me.uyuyuy99.bbhack.rom;

import me.uyuyuy99.bbhack.MainMenu;

public class ROMText {
	
	public static final int MAX_POINTERS = 0x1770 / 3;
	
	private MainMenu main;
	
	public int[] textPointers = new int[MAX_POINTERS];
	
	public ROMText(MainMenu instance) {
		main = instance;
		
		//30010-3177F
		int offset = 0x30010;
		for (int i=0; i<0x1770; i+=3) {
			int index = i / 3;
			int curOffs = offset + i;
			
			textPointers[index] = main.rom.get(curOffs) + (main.rom.get(curOffs+1) * 256) + (main.rom.get(curOffs+2) * 256 * 256) + 0x60010;
		}
	}
	
	public String getText(int index) {
		return main.getTextFromPointer(textPointers[index]);
	}
	
}
