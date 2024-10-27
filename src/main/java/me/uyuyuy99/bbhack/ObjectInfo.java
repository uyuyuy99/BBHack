package me.uyuyuy99.bbhack;

import java.util.LinkedList;

public class ObjectInfo {
	
	public static final String[] objectTypes = {
		"[00] (empty)",
		"[01] Door (normal)",
		"[02] (empty)",
		"[03] Door (stairs)",
		"[04] Door (hole)",
		"[05] (empty)",
		"[06] (empty)",
		"[07] (empty)",
		"[08] (empty)",
		"[09] (empty)",
		"[10] (empty)",
		"[11] (empty)",
		"[12] (empty)",
		"[13] (empty)",
		"[14] (empty)",
		"[15] (empty)",
		"[16] Still NPC",
		"[17] Slow walking NPC",
		"[18] Fast walking NPC",
		"[19] Walking in place NPC",
		"[20] Still NPC (shown when flag set)",
		"[21] Slow walking NPC (shown when flag set)",
		"[22] (empty)",
		"[23] (empty)",
		"[24] Invisible (solid)",
		"[25] Graphic (solid)",
		"[26] Elevator",
		"[27] Invisible (not solid)",
		"[28] Enemy encounter",
		"[29] Graphic (shown when flag set)",
		"[30] Vehicle (plane/tank)",
		"[31] Invisible (disabled when flag set)",
		"[32] Present",
		"[33] Shaking graphic (solid)",
		"[34] ?",
		"[35] Graveyard post?",
		"[36] Walk behind tile?",
		"[37] Live House sign?",
		"[38] Running away",
		"[39] Moving doll/lamp",
		"[40] Boat?",
		"[41] Top of Ninten's house + on trees + random areas?",
		"[42] (empty)",
		"[43] Wrecked EVE?",
		"[44] At lower Mt. Itoi? (might trigger tank scene)",
		"[45] Island Rocket?"
	};
	
	public static final int[] jump1 = { 0x1, 0x9, 0xA, 0xB, 0x18, 0x1B, 0x1C, 0x20, 0x21, 0x28, 0x29, 0x2A, 0x2B, 0x2C, 0x2D, 0x2E, 0x2F, 0x30, 0x35, 0x36, 0x38, 0x39, 0x40, 0x4F, 0x50, 0x60, 0x62, 0x63, 0x65 };
	public static final int[] jump2 = { 0xC, 0xD, 0x12, 0x1A, 0x23, 0x24, 0x26, 0x27, 0x31, 0x33, 0x3A, 0x42, 0x43, 0x52, 0x54 };
	public static final int[] jump3 = { 0x2, 0x16, 0x1E, 0x37 };
	public static final int[] jump4 = { 0x37 };
	public static final int[] jump5 = { 0x22 };
	
	public static final boolean hasJump(int code) {
		for (int i : jump1) {
			if (i == code) return true;
		} for (int i : jump2) {
			if (i == code) return true;
		} for (int i : jump3) {
			if (i == code) return true;
		} for (int i : jump4) {
			if (i == code) return true;
		} for (int i : jump5) {
			if (i == code) return true;
		}
		return false;
	}
	
	private static final int[] script0 = { 0x0, 0x3, 0x7, 0xE, 0xF, 0x1F, 0x41, 0x45, 0x4A, 0x55, 0x56, 0x57, 0x58, 0x5E, 0x5F, 0x64, 0x66, 0x67, 0x68, 0x69, 0x6A };
	private static final int[] script1 = { 0x0, 0x1, 0x4, 0x5, 0x6, 0x9, 0xA, 0xB, 0x10, 0x11, 0x13, 0x14, 0x15, 0x18, 0x19, 0x1B, 0x1C, 0x20, 0x21, 0x25, 0x28, 0x29, 0x2A, 0x2B, 0x2C, 0x2D, 0x2E, 0x2F, 0x30, 0x32, 0x35, 0x36, 0x38, 0x39, 0x3B, 0x3C, 0x3F, 0x40, 0x44, 0x46, 0x47, 0x48, 0x49, 0x4B, 0x4C, 0x4F, 0x50, 0x51, 0x53, 0x59, 0x5A, 0x5B, 0x5C, 0x5D, 0x60, 0x61, 0x62, 0x63, 0x65 };
	private static final int[] script2 = { 0x8, 0xC, 0xD, 0x12, 0x17, 0x1A, 0x1D, 0x23, 0x24, 0x26, 0x27, 0x31, 0x33, 0x3A, 0x3E, 0x42, 0x43, 0x52, 0x54 };
	private static final int[] script3 = { 0x2, 0x16, 0x1E };
	private static final int[] script4 = { 0x37, 0x3D };
	private static final int[] script5 = { 0x22 };
	
	public static final int getCodeArgumentSize(int code) {
		for (int i : script0) {
			if (i == code) return 0;
		} for (int i : script1) {
			if (i == code) return 1;
		} for (int i : script2) {
			if (i == code) return 2;
		} for (int i : script3) {
			if (i == code) return 3;
		} for (int i : script4) {
			if (i == code) return 4;
		} for (int i : script5) {
			if (i == code) return 5;
		}
		return -1;
	}
	
	private static final int[] headers4 = { 24, 27, 28, 31 };
	private static final int[] headers6 = { 16, 17, 18, 19, 20, 21, 25, 26, 29, 30, 33, 34, 35, 36, 37, 39, 40, 41, 43, 44, 45 };
	private static final int[] headers8 = { 1, 3, 4, 32 };
	
	public static final int getHeaderSize(int type) {
		for (int i : headers4) {
			if (i == type) return 4;
		} for (int i : headers6) {
			if (i == type) return 6;
		} for (int i : headers8) {
			if (i == type) return 8;
		}
		return -1;
	}
	
	//Required
	private int type;
	private int dir;
	private int x;
	private int y;
	
	//Not required
	private Integer sprite;
	private Integer presentItem;
	private Integer presentId;
	private Integer targetMusic;
	private Integer targetDir;
	private Integer targetX;
	private Integer targetY;
	
	//Scripts, yay!
	public LinkedList<Integer> script;
	
	//Whether the script can be edited or not (based on whether the script contains unknown control codes or not)
	public boolean editable;
	
	//Position of object data in ROM
	public int pointer;
	
	//Index of movement data in script
	public Integer movementIndex;
	
	@SuppressWarnings("unchecked")
	public ObjectInfo(int type, int dir, int x, int y, LinkedList<Integer> script, int pointer) {
		this.type = type;
		this.dir = dir;
		this.x = x;
		this.y = y;
		
		this.script = (LinkedList<Integer>) script.clone();
		editable = true;
		this.pointer = pointer;
	}
	
	public int size() {
		return getHeaderSize(type) + script.size();
	}
	
	public int getType() {
		return type;
	} public int getDir() {
		return dir;
	} public int getX() {
		return x;
	} public int getY() {
		return y;
	}
	
	public void setX(int n) {
		x = n;
	} public void setY(int n) {
		y = n;
	}
	
	public int getSprite() {
		return sprite;
	} public int getPresentItem() {
		return presentItem;
	} public int getPresentId() {
		return presentId;
	} public int getTargetMusic() {
		return targetMusic;
	} public int getTargetDir() {
		return targetDir;
	} public int getTargetX() {
		return targetX;
	} public int getTargetY() {
		return targetY;
	}
	
	public void setSprite(int value) {
		sprite = value;
	} public void setPresentItem(int value) {
		presentItem = value;
	} public void setPresentId(int value) {
		presentId = value;
	} public void setTargetMusic(int value) {
		targetMusic = value;
	} public void setTargetDir(int value) {
		targetDir = value;
	} public void setTargetX(int value) {
		targetX = value;
	} public void setTargetY(int value) {
		targetY = value;
	}
	
	public ObjectInfo clone() {
		ObjectInfo obj = new ObjectInfo(type, dir, x, y, script, pointer);
		
		obj.editable = editable;
		obj.movementIndex = movementIndex;
		obj.sprite = sprite;
		obj.presentItem = presentItem;
		obj.presentId = presentId;
		obj.targetMusic = targetMusic;
		obj.targetDir = targetDir;
		obj.targetX = targetX;
		obj.targetY = targetY;
		
		return obj;
	}
	
}
