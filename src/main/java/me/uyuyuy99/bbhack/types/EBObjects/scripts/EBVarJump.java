package me.uyuyuy99.bbhack.types.EBObjects.scripts;
import me.uyuyuy99.bbhack.types.EBObjects.EBObject;

import java.util.List;

public class EBVarJump extends EBJump {

    public List<Integer> vars;
    public EBLabel label2;
    public EBVarJump(EBLabel labelI, EBObject who, List<Integer> varsi) {
        super(labelI, who);
        vars = varsi;
    }
    public EBVarJump(EBLabel labelI, EBLabel label2I, EBObject who, List<Integer> varsi){
        super(labelI, who);
        label2 = label2I;
        vars = varsi;
    }
}
