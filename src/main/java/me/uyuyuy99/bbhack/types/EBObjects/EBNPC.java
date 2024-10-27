package me.uyuyuy99.bbhack.types.EBObjects;
import me.uyuyuy99.bbhack.types.SpriteDef;
import java.util.Arrays;

public class EBNPC extends EBProgrammable{
    public SpriteDef mainSprite;
    public int mainI;
    public SpriteDef mysprite;
    public short myspritePointer;
    public EBNPC(EBObject original){
        super(original, true);
        myspritePointer = (short) ((Byte.toUnsignedInt(original.scriptbytes[1]) << 8) | Byte.toUnsignedInt(original.scriptbytes[0]));
        //shave off the first two since its a sprite def
        scriptbytes = Arrays.copyOfRange(original.scriptbytes, 2, original.scriptbytes.length);
        ParseScript(scriptbytes);
    }
    
    public void DoSpriteStuff(SpriteDef[] Defs){
        for(int i = 0; i < Defs.length; i++){
            SpriteDef Definition = Defs[i];
            if(Definition.addr == myspritePointer){
                mainSprite = Definition;
                mainI = i;
                //get the direction sprite (mostly for npcs)
                getDirectionFromMain(Defs);
            }
            if(mysprite != null){break;}
        }
    }
    
    public void getDirectionFromMain(SpriteDef[] Defs){
        mysprite = Defs[mainI+dir.ordinal()];
    }
    
}




