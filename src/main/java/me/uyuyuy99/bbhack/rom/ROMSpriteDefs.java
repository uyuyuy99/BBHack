package me.uyuyuy99.bbhack.rom;

import me.uyuyuy99.bbhack.MainMenu;
import me.uyuyuy99.bbhack.types.SpriteDef;
import me.uyuyuy99.bbhack.types.Sprite;

import java.util.ArrayList;
import java.util.List;

public class ROMSpriteDefs {
	private MainMenu main;
	public List<SpriteDef> Definitions;
	public List<Sprite> Sprites;

	String[] spritedefFiles = {
		"dumped/sprite_defs.bin",
		"dumped/credits_sprite_defs.bin"
  	};
	List<ROMAssetIO> spritedefData = new ArrayList<>();

	String[] spriteFiles = {
		"dumped/sprites.bin",
		"dumped/credits_sprites.bin"
  	};
	List<ROMAssetIO> spriteData = new ArrayList<>();

   	public ROMSpriteDefs(MainMenu instance) {
		main = instance;

		for(String file : spriteFiles){
			spriteData.add(new ROMAssetIO(file));
		}

		for(String file : spritedefFiles){
			spritedefData.add(new ROMAssetIO(file));
		}

		Sprites = new ArrayList<>();
		Definitions = new ArrayList<>();

		for (int b = 0; b < spriteFiles.length; b++){
			int a = 0;
			ROMAssetIO file = spriteData.get(b);
			for(int i = 0; i < file.data.length; i+=4, a+=4){
				byte[] data = file.getT(i, 4);

				int x = Byte.toUnsignedInt(data[0]);
				int y = Byte.toUnsignedInt(data[1]);
				int data2 = Byte.toUnsignedInt(data[2]);
				int palette = data2 & 3; //bits 0,1
				int unk1 = (data2 & 0x1C) >> 2; //bits 2,3,4
				int order = (data2 & 0x20) >> 5; //bit 5
				int flipX = (data2 & 0x40) >> 6; //bit 6
				int flipY = (data2 & 0x80) >> 7; //bit 7
				int index = Byte.toUnsignedInt(data[3]);
				//ramspace pointer. used for spritedefs
				//TODO: get this set up with credits
				int def_size = spritedefData.get(b).data.length;
				short addr = (short) (0x8000+a+def_size);
				Sprites.add(new Sprite(x, y, palette, unk1, order, flipX, flipY, index, addr));
			}

			file = spritedefData.get(b);
			for(int i = 0; i < file.data.length; i+=4){
				byte[] data = file.getT(i, 4);
				//little endian pointer
				short newAddr = (short) ((Byte.toUnsignedInt(data[1]) << 8) | Byte.toUnsignedInt(data[0]));
				//check if sprite at that address exists
				int use = -1;
				for (Sprite test : Sprites) {
					if (test.addr == newAddr) {
						use = Sprites.indexOf(test);
						break;
					}
				}
				int offset = Byte.toUnsignedInt(data[2]);
				int data3 = Byte.toUnsignedInt(data[3]);
				int p1 = data3 & 3; //bits 0,1
				int p2 = (data3 & 0xC) >> 2; //bits 2,3
				int unk1 = (data3 & 0x10) >> 4; //bit 4
				int unk2 = (data3 & 0xE0) >> 5; //bits 5,6,7
				//TODO: get this set up with credits
				short addr = (short) (0x8000+i);
				Definitions.add(new SpriteDef(offset, p1, p2, unk1, unk2, use, addr));
			}
		}

	}

}


