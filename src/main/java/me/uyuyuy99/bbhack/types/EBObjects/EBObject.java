package me.uyuyuy99.bbhack.types.EBObjects;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class EBObject {
    public enum DIRECTION {
        UP,
        UP_RIGHT,
        RIGHT,
        DOWN_RIGHT,
        DOWN,
        DOWN_LEFT,
        LEFT,
        UP_LEFT,
        IN_PLACE;
    }

    public enum OBJTYPE {
        NULL(0),
        DOOR(1),
        DOOR_UNK(2),
        STAIRS(3),
        HOLE(4),
        STATIONARY_NPC2(0x10),
        WANDERING_NPC2(0x11),
        WANDERINGFAST_NPC(0x12),
        SPINNING_NPC(0x13),
        WANDERING_NPC(0x15),
        TRIGGER(0x1B),
        FLAGSET_SEE(0x29); //sets a flag in a byte when on screen. uses teleportFlagDef
        private int value;
        private OBJTYPE(int value) {
            this.value = value;
        }
        public int getValue() {
            return this.value;
        }
        //func to get a value from a non-orderly enum
        public static OBJTYPE getEnum(int i){
            for (OBJTYPE value1 : values()) {
                if (value1.value == i) {
                    return value1;
                }
            }
            return values()[0];
        }
    }
    public int type_int;
    public OBJTYPE type;
    public DIRECTION dir;
    public int x,y;
    public byte[] scriptbytes;
    
    //specifically type object so normal ints and bytes can be used
    public List<Object> script = new ArrayList<>();
    
    public EBObject(byte[] myData){
        byte[] objectDef = Arrays.copyOf(myData, 4);

        int word1 = (Byte.toUnsignedInt(objectDef[1]) << 8) | Byte.toUnsignedInt(objectDef[0]);
        int word2 = (Byte.toUnsignedInt(objectDef[3]) << 8) | Byte.toUnsignedInt(objectDef[2]);
        type_int = word1 & 0x3F;
        type = OBJTYPE.getEnum(type_int); //bits 0-5
        x = (word1 & 0xFFC0) >> 6; //bits 6-15
        dir = DIRECTION.values()[word2 & 0x3F]; //bits 0-5
        y = (word2 & 0xFFC0) >> 6; //bits 6-15
        scriptbytes = Arrays.copyOfRange(myData, 4, myData.length);
    }
    
    public EBObject(){
    }
}



