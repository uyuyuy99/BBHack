package me.uyuyuy99.bbhack.types.EBObjects;
import me.uyuyuy99.bbhack.types.SpriteDef;
import java.util.Arrays;
import java.util.List;

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
        ParseScript(scriptbytes, (short) 6);
    }
    public EBNPC(EBObject original, short myaddr){
        super(original, myaddr, true);
        myspritePointer = (short) ((Byte.toUnsignedInt(original.scriptbytes[1]) << 8) | Byte.toUnsignedInt(original.scriptbytes[0]));
        //shave off the first two since its a sprite def
        scriptbytes = Arrays.copyOfRange(original.scriptbytes, 2, original.scriptbytes.length);
        ParseScript(scriptbytes, (short) 6);
    }

    public void DoSpriteStuff(List<SpriteDef> Defs){
        for(SpriteDef Definition : Defs){
            if(Definition.addr == myspritePointer){
                mainSprite = Definition;
                mainI = Defs.indexOf(Definition);
                //get the direction sprite (mostly for npcs)
                getDirectionFromMain(Defs);
            }
            if(mysprite != null){break;}
        }
    }

    public void getDirectionFromMain(List<SpriteDef> Defs){
        mysprite = Defs.get(mainI+dir.ordinal());
    }

}




