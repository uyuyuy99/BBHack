package me.uyuyuy99.bbhack.types.EBObjects;

public class EBProgrammable extends EBObject{
    
    public void init_main(EBObject original){
        type = original.type;
        x = original.x;
        dir = original.dir;
        y = original.y;
        script = original.script;
        scriptbytes = original.scriptbytes;
    }
    
    public EBProgrammable(EBObject original){
        super();
        init_main(original);
        ParseScript(scriptbytes);
    }
    public EBProgrammable(EBObject original, boolean npc){
        super();
        init_main(original);
    }
    
    public void ParseScript(byte[] scriptbyte){
        
        
    }
}




