package me.uyuyuy99.bbhack.types.EBObjects;
import me.uyuyuy99.bbhack.types.EBObjects.scripts.EBFlagSetArg;

public class EBFlagSet extends EBObject{
    
    public EBFlagSet(EBObject original){
        super();
        type = original.type;
        x = original.x;
        dir = original.dir;
        y = original.y;
        script = original.script;
        scriptbytes = original.scriptbytes;
        
        byte[] myarg = {original.scriptbytes[0]};
        EBFlagSetArg fsa = new EBFlagSetArg(myarg);
        script.add(fsa);
        //usually ends in a zero. just in case
        for(int i = 1; i < original.scriptbytes.length; i++){
            script.add(original.scriptbytes[i]);
        }
    }
}




