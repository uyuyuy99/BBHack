 package me.uyuyuy99.bbhack.rom;
 
 import me.uyuyuy99.bbhack.MainMenu;
 import me.uyuyuy99.bbhack.types.SpriteDef;
 import me.uyuyuy99.bbhack.types.Sprite;
 import me.uyuyuy99.bbhack.types.Tile8;

 
 public class ROMSpriteDefs
 {
   private MainMenu main;
   //the tiles needed to reconstruct character/object sprites
   public Tile8[] characters;
   int sprdef_start = 0x2a010; //start of spritedef data (us)
   int spr_start = sprdef_start+0xB5C; //start of sprite data (us)
   int data_end = 0x2bd0c; //end of the data
   public SpriteDef[] Definitions = new SpriteDef[(spr_start-sprdef_start)/4];
   public Sprite[] Sprites = new Sprite[(data_end-spr_start)/4];
   
   public ROMSpriteDefs(MainMenu instance) {
     main = instance;
     
     for(int i = 0; i < data_end-spr_start; i+=4){
        byte[] data = main.rom.getT(spr_start + i, 4);
         
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
        short addr = (short) ((spr_start-sprdef_start)+0x8000+i);
        Sprites[i/4] = new Sprite(x, y, palette, unk1, order, flipX, flipY, index, addr);
     }
     for(int i = 0; i < spr_start-sprdef_start; i+=4){
         byte[] data = main.rom.getT(sprdef_start + i, 4);
         //little endian pointer
         short newAddr = (short) ((Byte.toUnsignedInt(data[1]) << 8) | Byte.toUnsignedInt(data[0]));
         //check if sprite at that address exists
         int use = -1;
         for (int x = 0; x < Sprites.length; x++) {
             if (Sprites[x].addr == newAddr) {
                 use = x;
                 break;
             }
         }
         int offset = Byte.toUnsignedInt(data[2]);
         int data3 = Byte.toUnsignedInt(data[3]);
         int p1 = data3 & 3; //bits 0,1
         int p2 = (data3 & 0xC) >> 2; //bits 2,3
         int unk1 = (data3 & 0x10) >> 4; //bit 4
         int unk2 = (data3 & 0xE0) >> 5; //bits 5,6,7
         short addr = (short) (0x8000+i);
         Definitions[i/4] = new SpriteDef(offset, p1, p2, unk1, unk2, use, addr);
     }
     
   }
   
 }


