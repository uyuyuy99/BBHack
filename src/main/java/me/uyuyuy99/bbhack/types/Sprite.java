package me.uyuyuy99.bbhack.types;

//tile data for each sprite (modified oam entries)
 public class Sprite{
     public int x,y,palette,unk1,order,flipX,flipY,index;
     public short addr;
     
   public Sprite(int useX, int useY, int usePalette, int useUnk1,
           int useOrder, int useFlipX, int useFlipY, int useIndex,
           short useAddr){
       x = useX;
       y = useY;
       palette = usePalette;
       unk1 = useUnk1;
       order = useOrder;
       flipX = useFlipX;
       flipY = useFlipY;
       index = useIndex;
       addr = useAddr;
   }
   public Sprite(){}
   
 }