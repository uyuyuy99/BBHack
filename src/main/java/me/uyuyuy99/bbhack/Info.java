package me.uyuyuy99.bbhack;

import java.awt.Color;

public interface Info {
	
	public static final int CHAR_OFFSET = 0xC1 - (int) 'A';
	
	public static final Color COLOR_GRID1 = Color.GRAY;
	public static final Color COLOR_GRID2 = Color.DARK_GRAY;
	public static final Color COLOR_FLASH = Color.BLUE;
	public static final Color COLOR_OBJECT = new Color(255, 0, 255, 128);
	public static final Color COLOR_BORDER = new Color(255, 0, 0, 128);
	public static final Color COLOR_FADED = new Color(0, 0, 0, 224);
	
	public static final String[] tilesetNames = {
		"[0] Suburbs 1",
		"[1] Suburbs 2",
		"[2] Outdoors 1",
		"[3] City 1",
		"[4] City 2",
		"[5] City 3",
		"[6] Zoo",
		"[7] Graveyard",
		"[8] Suburbs 3",
		"[9] Outdoors 2",
		"[10] Mt. Itoi 1",
		"[11] Coast",
		"[12] Building Top",
		"[13] Desert",
		"[14] Monkey Caves",
		"[15] Magicant 1",
		"[16] Magicant 2",
		"[17] Magicant 3",
		"[18] Factory 1",
		"[19] Factory 2",
		"[20] Church",
		"[21] Caves",
		"[22] Fading Magicant",
		"[23] Basement",
		"[24] Indoors 1",
		"[25] Indoors 2",
		"[26] Indoors 3",
		"[27] Store",
		"[28] Railroad",
		"[29] Mt. Itoi 2",
		"[30] Trashed Lab",
		"[31] Test Tubes"
	};
	
	public static final String[] statusNames = {
		"Cold",
		"Poison",
		"Puzzled",
		"Confused",
		"Asleep",
		"Paralyzed",
		"Stone",
		"Fainted"
	};
	
	public static final String[] characterNames = {
		"Ninten",
		"Ana",
		"Loid",
		"Teddy",
		"Pippi",
		"Flying Man",
		"EVE"
	};
	
	public static final String[] musicNames = {
		"[00] (don't change)",
		"[01] 8 Melodies (ocarina)",
		"[02] Battle w/ a Flippant Foe",
		"[03] Battle w/ a Dangerous Foe",
		"[04] Battle w/ a Hippe",
		"[05] You Won!",
		"[06] Bein' Friends",
		"[07] Bein' Friends",
		"[08] Yucca Desert",
		"[09] Magicant",
		"[10] Snowman",
		"[11] Mt. Itoi",
		"[12] Factory",
		"[13] A Ghastly Site",
		"[14] Twinkle Elementary School",
		"[15] Drugstore",
		"[16] Poltergeist",
		"[17] Underground",
		"[18] House",
		"[19] Approaching Mt. Itoi",
		"[20] The Paradise Line",
		"[21] Fallin' Love",
		"[22] Introduction",
		"[23] Roving Tank",
		"[24] Monkey Cave",
		"[25] 8 Melodies",
		"[26] Queen Mary's Castle",
		"[27] Tombstone",
		"[28] Game Over",
		"[29] Surprise!",
		"[30] Airplane Ride",
		"[31] Level Up",
		"[32] Hotel Sleep",
		"[33] Comforting Sleep",
		"[34] Live House",
		"[35] All That I Needed Was You",
		"[36] Melody 1",
		"[37] Melody 2",
		"[38] Melody 3",
		"[39] Melody 4",
		"[40] Melody 5",
		"[41] Melody 6",
		"[42] Melody 7",
		"[43] Melody 8",
		"[44] Versus Giegue",
		"[45] The End",
		"[46] Alien Investigateion",
		"[47] Phone",
		"[48] Youngtown",
		"[49] Cave of the Tail",
		"[50] Right Before Giegue"
	};
	
	public static final String[] soundEffectNames1 = {
		"[00] Nothing",
		"[01] Player attack",
		"[02] Explosion",
		"[03] PK Thunder",
		"[04] PK Fire",
		"[05] Smash attack ready",
		"[06] Enemy death",
		"[07] ?",
		"[08] Stairs",
		"[09] Rocket",
		"[10] Rocket crash?",
		"","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",""
	};
	
	public static final String[] soundEffectNames2 = {
		"[00] Nothing",
		"[01] Enemy attack ready",
		"[02] PK Beam",
		"[03] Stat up",
		"[04] Enemy attack",
		"[05] Menu select",
		"[06] Get item",
		"[07] Heal",
		"[08] Laura chirping",
		"[09] Learn new PSI",
		"[10] Player attack ready",
		"[11] ?",
		"","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",""
	};
	
	public static final String[] soundEffectNames3 = {
		"[00] Nothing",
		"[01] PK Freeze",
		"[02] ?",
		"[03] Faint",
		"[04] ?",
		"","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",""
	};
	
	public static final String[] directionNames = {
		"[00] Up",
		"[01] Up-right",
		"[02] Right",
		"[03] Down-right",
		"[04] Down",
		"[05] Down-left",
		"[06] Left",
		"[07] Up-left"
	};
	
	public static final String[] codeNames = {
		"END",
		"Jump to line",
		"Run another object's script",
		"Return to original object's script",
		"Delay",
		"If flag set, make object disappear",
		"If flag set, make object appear",
		"(unused, freezes game)",
		"Display text",
		"Ask yes/no, jump to line if 'no' selected",
		"Jump unless TALKing to object",
		"Jump unless CHECKing object",
		"Jump unless using a PSI power on object",
		"Jump unless using an item on object",
		"(unused, freezes game)",
		"Reset NES",
		"Set flag",
		"Clear flag",
		"Jump unless flag set",
		"Decrease counter",
		"Increase counter",
		"Set counter to 0",
		"Jump if counter less than value",
		"Change map variable",
		"Choose character, jump if B pressed",
		"Load specific character",
		"Jump unless character selected",
		"Jump if no money added to bank acct since last call",
		"Input a number, jump if B pressed",
		"Load a number",
		"Jump if loaded number less than value",
		"Show money",
		"Choose item from inventory, jump if B pressed",
		"Choose item from closet, jump if B pressed",
		"Choose item from list, jump if B pressed",
		"Jump unless item in loaded character's inventory",
		"Jump unless item in closet",
		"Select specific item",
		"Jump unless item selected",
		"Jump unless item in any character's inventory",
		"Give money, jump if can't hold any more",
		"Take money, jump if not enough",
		"Add loaded # to bank account, jump if can't hold any more",
		"Take from bank account, jump if not enough",
		"Jump if loaded item unsellable",
		"Add loaded item to inventory, jump if full",
		"Remove loaded item from inventory, jump if not present",
		"Add loaded item to closet, jump if full",
		"Remove loaded item from closet, jump if not present",
		"Load loaded character's #'th item, jump if empty slot",
		"Multiply loaded number by #/256",
		"Jump if character not in party",
		"",
		"Jump unless touching object",
		"",
		"Show 2-option menu, jump if 2nd selected, jump if B pressed",
		"Jump if no items in inventory",
		"Jump if no items in closet",
		"Load #'th character in party, jump if not present",
		"Change object type",
		"",
		"Teleport player",
		"Move object (using pointer to movement data)",
		"Signal another object",
		"Jump unless signalled by another object",
		"Teleport to saved game location",
		"Add character to party, jump if party full",
		"Remove character from party, jump if absent",
		"Start battle",
		"Multiply loaded # by number of characters in party",
		"Rocket",
		"Airplane",
		"Tank",
		"Boat",
		"Train",
		"Elevator",
		"No vehicle",
		"",
		"",
		"",
		"Jump if loaded character at less than max HP",
		"Heal loaded character's HP",
		"Jump if loaded character has status(es)",
		"Remove status(es) from loaded character",
		"Jump if character below level",
		"Sleep",
		"Save game",
		"Load loaded character's exp needed for next level",
		"Load money",
		"Inflict status(es) on loaded character",
		"Change BG music",
		"Play sound effect (bank 1)",
		"Play sound effect (bank 2)",
		"Play sound effect (bank 3)",
		"(unused, freezes game)",
		"Teach characters 1 and 2 to teleport",
		"Jump if loaded character at less than max PP",
		"Heal loaded character's PP",
		"Confiscate weapon, jump if none",
		"Load confiscated weapon, jump if none",
		"Live show routine",
		"Jump unless all 8 melodies learned",
		"Register your name",
		"Darken palette (Magicant end)",
		"Land mine routine",
		"Horizontal shake (EVE?)",
		"XX-Stone routine"
	};
	
	public static final String[] codeGroupNames = {
		"Interaction",
		"Simple",
		"Items",
		"Characters",
		"Jumps/Objects",
		"Numbers/Money",
		"Flags/Counters",
		"Vehicles",
		"Misc."
	};
	
	public static final int[][] codeGroups = {
		{0x5, 0x6, 0xA, 0xB, 0xC, 0xD, 0x35},
		{0x0, 0x4, 0x8, 0xF, 0x3D, 0x44, 0x55, 0x56, 0x5A, 0x5B, 0x5C, 0x5D},
		{0x20, 0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x2C, 0x2D, 0x2E, 0x2F, 0x30, 0x31, 0x38, 0x39, 0x62, 0x63},
		{0x18, 0x19, 0x1A, 0x33, 0x3A, 0x42, 0x43, 0x45, 0x50, 0x51, 0x52, 0x53, 0x54, 0x57, 0x59},
		{0x1, 0x2, 0x3, 0x9, 0x37, 0x3F, 0x40, 0x41},
		{0x1B, 0x1C, 0x1D, 0x1E, 0x1F, 0x28, 0x29, 0x2A, 0x2B, 0x32, 0x58},
		{0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16},
		{0x46, 0x47, 0x48, 0x49, 0x4A, 0x4B, 0x4C},
		{0x17, 0x3B, 0x3E, 0x5F, 0x60, 0x61, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x6A},
	};
	
	public static final String forumThread = "http://bit.ly/BBHackThread";
	
	public static final String version = "2.0";
	
	public static final String aboutMessage =
			"<html><body style='font-size:10px; font-family:Verdana;'><center><b>BB Hack v" + version + "<br/>" +
			"All-in-one Earthbound Zero hacking tool<br/>" +
			"Coded by uyuyuy99</b><br/>" +
			"<br/>" +
			"<br/>" +
			"If you find any bugs, want to request features, or want to help me<br/>" +
			"uncover some data from the EB0 ROM, just contact me:<br/>" +
			"PM on starmen.net (uyuyuy99)<br/>" +
			"Making a post on the <a href='http://google.com/'>forum thread</a><br/>" +
			"Email (uyuyuy99@gmail.com)</center><br/>" +
			"<br/>" +
			"<h2>Changelog</h2>" +
			"<u>v1.0</u> (September 6, 2012)" +
			"<ul><li>Initial release! Includes map editor and chunk editor.</li></u>" + 
			"</u></body></html>";
	
}
