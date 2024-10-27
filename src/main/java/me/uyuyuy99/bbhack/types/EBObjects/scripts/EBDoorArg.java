package me.uyuyuy99.bbhack.types.EBObjects.scripts;
import me.uyuyuy99.bbhack.types.EBObjects.EBObject.DIRECTION;

public class EBDoorArg extends EBScript{
    public int music,targetX,targetY;
    public DIRECTION targetDir;
    public EBDoorArg(byte[] arguments){
        int word1 = (Byte.toUnsignedInt(arguments[1]) << 8) | Byte.toUnsignedInt(arguments[0]);
        int word2 = (Byte.toUnsignedInt(arguments[3]) << 8) | Byte.toUnsignedInt(arguments[2]);
        music = word1 & 0x3F; //bits 0-5
        targetX = (word1 & 0xFFC0) >> 6; //bits 6-15
        targetDir = DIRECTION.values()[word2 & 0x3F]; //bits 0-5
        targetY = (word2 & 0xFFC0) >> 6; //bits 6-15
    }

}