package me.uyuyuy99.bbhack.rom;

import me.uyuyuy99.bbhack.MainMenu;
import me.uyuyuy99.bbhack.Info;

import java.io.IOException;

public class ROMPalettes {


	//the raw rgb
	public static int[] NES_PALETTE;
	public static int[] bb_palette;
	public static int[] colors = bb_palette;

	private MainMenu main;

	public int[][] palettes;

	public int[][] sprite_palettes;

	//TODO: invalidate these
	int mapPaletteAddr = 0x29010;
	//fairly certain this is mixed with asm???
	int spritePaletteAddr = 0x3Cf9c+0x10;

	public ROMPalettes(MainMenu instance) {
		main = instance;
		palettes = new int[0x20 * 4][4]; //[palette][colors]
		sprite_palettes = new int[4][4];

		try {
			byte[] nes = Info.class.getResourceAsStream("/palettes/nes.pal").readAllBytes();
			NES_PALETTE = new int[nes.length];
			for (int i = 0; i < nes.length; i++){
				NES_PALETTE[i] = Byte.toUnsignedInt(nes[i]);
			}
			byte[] bb = Info.class.getResourceAsStream("/palettes/bb.pal").readAllBytes();
			bb_palette = new int[bb.length];
			for (int i = 0; i < bb.length; i++){
				bb_palette[i] = Byte.toUnsignedInt(bb[i]);
			}

			colors = bb_palette;

		} catch (IOException e) {
			e.printStackTrace();
		}

		for (int i=0; i<0x80; i++) { //128 pallette entries
			for (int j=0; j<4; j++) { //4 colors in each palette
				palettes[i][j] = main.rom.get(mapPaletteAddr + (i*4) + j) % 0x40;
			}
		}

		//Hardcoded palette values
		for (int i=3; i<palettes.length; i+=4) {
			palettes[i][0] = 15;
			palettes[i][2] = 48;
		}

		for (int i=0; i<4; i++) { //4 pallette entries
			for (int j=0; j<4; j++) { //4 colors in each palette
				sprite_palettes[i][j] = main.rom.get(spritePaletteAddr + (i*4) + j);
			}
		}
	}

}
