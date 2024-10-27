package me.uyuyuy99.bbhack.types.EBObjects.scripts;

public class EBFlagSetArg extends EBScript{
    public int flag,ibyte;
    public EBFlagSetArg(byte[] arguments){
        flag = Byte.toUnsignedInt(arguments[0]) & 7; //bits 0-2
        ibyte = (Byte.toUnsignedInt(arguments[0]) & 0xF8) >> 3; //bits 3-7
    }

}