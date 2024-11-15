package me.uyuyuy99.bbhack.types.EBObjects.scripts;
import me.uyuyuy99.bbhack.types.EBObjects.EBObject;

public class EBJump extends EBScript {
    public EBLabel label;
    public EBObject object;
    public EBJump(EBLabel labelI, EBObject who){
        label = labelI;
        object = who;
    }
}
