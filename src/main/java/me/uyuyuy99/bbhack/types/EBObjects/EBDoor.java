package me.uyuyuy99.bbhack.types.EBObjects;
import me.uyuyuy99.bbhack.types.EBObjects.scripts.EBDoorArg;

public class EBDoor extends EBObject{
    
    public EBDoor(EBObject original){
        super();
        type = original.type;
        x = original.x;
        dir = original.dir;
        y = original.y;
        script = original.script;
        scriptbytes = original.scriptbytes;
        
        EBDoorArg myarg = new EBDoorArg(original.scriptbytes);
        script.add(myarg);
    }
}




