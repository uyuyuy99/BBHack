package me.uyuyuy99.bbhack.types.EBObjects.scripts;

public class EBLabel extends EBScript{
    public String label_name;
    public EBLabel(String name, int position){
        label_name = name;
        object_i = position;
    }
}
