package me.uyuyuy99.bbhack.types;

 //specifically the listing, not the actual data
 public class SpriteDef{
     public int spriteStart = -1;
     public int offset,p1,p2,unk1,unk2;
     public short addr;
     public SpriteDef(int useOffset, int useP1, int useP2, int useUnk1, int useUnk2, int useRef, short useAddr){
         offset = useOffset; //specifically the offset into the memory table. so usually the second half of the sprite table
         p1 = useP1;
         p2 = useP2;
         unk1 = useUnk1;
         unk2 = useUnk2;
         spriteStart = useRef;
         addr = useAddr;
     }
   public SpriteDef(){}
   
 }