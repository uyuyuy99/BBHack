package me.uyuyuy99.bbhack.types.EBObjects;

import me.uyuyuy99.bbhack.types.EBObjects.scripts.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EBProgrammable extends EBObject{

    public enum SCRIPT {
        NULL(-1),
        STOP(0),
        J_JUMP(1),
        OJ_SUBROUTINE(2),
        RETURN(3),
        T_DELAY(4),
        F_DISAPPEAR(5),
        F_APPEAR(6),
        DIALOGUE(8),
        J_YESNO(9),
        J_TALK(0xA),
        J_CHECK(0xB),
        PJ_USE(0xC),
        IJ_USE(0xD),
        RESET(0xF),
        F_SETFLAG(0x10),
        F_CLEARFLAG(0x11),
        FJ_JUMP(0x12),
        C_DECCOUNTER(0x13),
        C_INCCOUNTER(0x14),
        C_RESETCOUNTER(0x15),
        CNJ_COMPCOUNTER(0x16),
        J_CHOOSECHAR(0x18), //choose character, jump if b pressed
        C_SELECT(0x19), //select character
        CJ_CHARSELECTED(0x1A), //jump to j if chararacter c not selected
        J_NONEWMONEY(0x1B), //jump to j if no money has been gained since last call
        N_LOADNUMBER(0x1D), //load number ????
        SHOWMONEY(0x1F), //yeah
        J_CHOOSEITEM(0x20), //jump to j if declined
        J_CHOOSEITEMCLOSET(0x21), //jump to j if declined
        IIIIJ_LIST(0x22), //jump if b pressed
        I_PICKITEM(0x25), //load i into selected item
        IJ_SELECTEDITEM(0x26), //jump to j if i isnt selected
        IJ_HASITEM(0x27), //jump to j if i not in inventory
        J_GIVEMONEY(0x28), //jump to j if cant hold money
        J_TAKEMONEY(0x29), //jump to j if not enough money
        J_UNSELLABLE(0x2C), // jump to j if item cannot be sold
        J_GIVEITEM(0x2D), //jump to j if inventory full. else give selected item
        J_REMOVEITEM(0x2E), //remove item, jump if doesn't have
        J_ADDITEMCLOSET(0x2F), //add item to closet, jump to j if closet full
        J_TAKEITEMCLOSET(0x30), //remove item to closet, jump to j if not available
        IJ_PICKCHARITEM(0x31), //pick character's I'th item, jump if empty (0)
        N_MULNUMBER(0x32), //multiply number by n / 100
        CJ_PRESENT(0x33), //jump to j if character c is not in party
        J_TOUCH(0x35), //jump to j if not touching
        J_UNK(0x36), //jump to j if ??????
        JJ_CUSTOMMENU(0x37), //display menu pointer, jump to j1 if option 2, jump to j2 if b pressed
        J_NOITEMS(0x38), //jump to j if no items
        J_NOITEMSCLOSET(0x39), //jump to j if no items in closet
        CJ_SELECTPARTY(0x3A), //select character c in party, jump to j if not present
        T_CHANGETYPE(0x3B), //change object type to t
        DA_TELEPORT(0x3D), //teleport player to doorArgDef (basically, runs a door command)
        M_MOVE(0x3E), //move using m pointer (word)
        O_SIGNAL(0x3F), //signal object o (index)
        J_SIGNALED(0x40), //jump to j if not signaled
        CJ_ADDCHAR(0x42), //add char c from party, jump to j if full
        CJ_REMOVECHAR(0x43), //remove char c from party, jump to j if not in
        B_BATTLE(0x44), //start battle b in battles list
        CHARMULT(0x45), //multiply by number of characters
        D_ROCKET(0x46), //spawn rocket in direction (?)
        D_AIRPLANE(0x47), //spawn airplane in direction (?)
        D_TANK(0x48), //spawn tank in direction (?)
        D_NOVEC(0x4C), //spawn players in direction (?)
        PLANEEND(0x4D), //ending of plane paths
        J_UNK2(0x4F), //jump to j if ?????
        J_NOTMAX(0x50), //jump to j if < max hp
        N_HEAL(0x51), //heal hp n
        SJ_PRESENT(0x52), //jump to j if character has status s
        S_REMOVEBUT(0x53), //remove all statuses but s
        NJ_BELOWLEVEL(0x54), //jump to j if character < n
        SLEEP(0x55), //sleep
        SAVE(0x56), //self-explanatory
        GETCHARNEXTEXP(0x57), //get selected characters' needed exp
        GETCASH(0x58), //get money
        S_GIVESTATUS(0x59), //inflict s status
        M_MUSIC(0x5A), //play m song
        S_PLAYSOUND2(0x5B), //play s
        S_PLAYSOUND(0x5C), //play s
        J_NOTMAXPP(0x60), //jump to j if < max pp
        N_HEALPP(0x61), //heal pp n
        J_REMOVEWEAPON(0x62), //jump to j if no weapon, else take
        J_CONFISC(0x63), //get confiscated weapon, jump to j if none
        LIVESHOW(0x64), //in ellay
        J_MELODIES(0x65), //jump to j if not all melodies learnt
        REGNAME(0x66), //register name
        LANDMINE(0x68); //in yucca desert
        private int value;
        private SCRIPT(int value) {
            this.value = value;
        }
        public int getValue() {
            return this.value;
        }
        //func to get a value from a non-orderly enum
        public static SCRIPT getEnum(int i){
            for (SCRIPT value1 : values()) {
                if (value1.value == i) {
                    return value1;
                }
            }
            return SCRIPT.NULL;
        }
    }


    public List<EBLabel> labels = new ArrayList<>();
    public List<EBLabel> move_labels = new ArrayList<>();
    public List<EBLabel> subroutines = new ArrayList<>();

    public void init_main(EBObject original){
        type = original.type;
        x = original.x;
        dir = original.dir;
        y = original.y;
        script = original.script;
        scriptbytes = original.scriptbytes;
    }
    public void init_main(EBObject original, short myaddr){
        init_main(original);
        start_addr = myaddr;
    }

    public EBProgrammable(EBObject original){
        super();
        init_main(original);
        ParseScript(scriptbytes, (short) 4);
    }
    public EBProgrammable(EBObject original, short myaddr){
        super();
        init_main(original, myaddr);
        ParseScript(scriptbytes, (short) 4);
    }
    public EBProgrammable(EBObject original, boolean npc){
        super();
        init_main(original);
    }
    public EBProgrammable(EBObject original, short myaddr, boolean npc){
        super();
        init_main(original, myaddr);
    }

    public void ParseScript(byte[] scriptbyte, short offset){
        short currentAddr = (short) (start_addr+offset);
        for(int i = 0; i < scriptbyte.length;){
            int inc = 1;
            boolean mover_found = false;
            for (EBLabel label : move_labels){
                if(start_addr == -27234){
                    int sss = 0;
                }
                int newbnert = Short.toUnsignedInt(currentAddr);
                if (label.object_i == newbnert){
                    mover_found = true;
                    break;
                }
            }
            if (mover_found){
                break;
            }


            int storeI = -1;
            int stype = Byte.toUnsignedInt(scriptbyte[i]);
            SCRIPT result = SCRIPT.getEnum(stype);
            if (result != SCRIPT.NULL){
                //1 == generic jump
                //2 == generic single var
                //3 == generic no var
                int genericType = 0;
                List<Integer> intVars = new ArrayList<>();
                boolean found = false;
                switch (result){
                    case J_YESNO:
                    case J_JUMP:
                    case J_TALK:
                    case J_CHECK:
                    case J_TOUCH:
                    case J_SIGNALED:
                    case J_GIVEITEM:
                    case J_UNK:
                    case J_UNK2:
                    case J_NOTMAX:
                    case J_NOITEMS:
                    case J_NOITEMSCLOSET:
                    case J_NONEWMONEY:
                    case J_REMOVEITEM:
                    case J_ADDITEMCLOSET:
                    case J_TAKEITEMCLOSET:
                    case J_REMOVEWEAPON:
                    case J_CONFISC:
                    case J_TAKEMONEY:
                    case J_GIVEMONEY:
                    case J_CHOOSEITEM:
                    case J_CHOOSEITEMCLOSET:
                    case J_CHOOSECHAR:
                    case J_UNSELLABLE:
                    case J_MELODIES:
                    case J_NOTMAXPP:
                    case M_MOVE:
                        genericType = 1;
                        break;

                    case O_SIGNAL:
                    case F_DISAPPEAR:
                    case F_APPEAR:
                    case B_BATTLE:
                    case F_SETFLAG:
                    case F_CLEARFLAG:
                    case I_PICKITEM:
                    case C_RESETCOUNTER:
                    case C_DECCOUNTER:
                    case C_INCCOUNTER:
                    case S_PLAYSOUND:
                    case S_PLAYSOUND2:
                    case M_MUSIC:
                    case C_SELECT:
                    case T_DELAY:
                    case N_HEAL:
                    case S_GIVESTATUS:
                    case S_REMOVEBUT:
                    case N_MULNUMBER:
                    case N_HEALPP:
                    case D_ROCKET:
                    case D_AIRPLANE:
                    case D_TANK:
                    case D_NOVEC:
                        genericType = 2;
                        break;

                    case SAVE:
                    case GETCHARNEXTEXP:
                    case GETCASH:
                    case LANDMINE:
                    case RETURN:
                    case SLEEP:
                    case LIVESHOW:
                    case SHOWMONEY:
                    case CHARMULT:
                    case PLANEEND:
                    case RESET:
                    case REGNAME:
                    case STOP:
                        genericType = 3;
                        break;

                    //ACTUAL parsers.
                    case IIIIJ_LIST:
                        intVars.add(Byte.toUnsignedInt(scriptbyte[i+1]));
                        intVars.add(Byte.toUnsignedInt(scriptbyte[i+2]));
                        storeI = i;
                        i += 2;
                        currentAddr += 2;
                    case CNJ_COMPCOUNTER:
                        intVars.add(Byte.toUnsignedInt(scriptbyte[i+1]));
                        if(storeI == -1){
                            storeI = i;
                        }
                        i += 1;
                        currentAddr += 1;
                    case NJ_BELOWLEVEL:
                    case SJ_PRESENT:
                    case CJ_REMOVECHAR:
                    case CJ_PRESENT:
                    case CJ_ADDCHAR:
                    case CJ_CHARSELECTED:
                    case CJ_SELECTPARTY:
                    case PJ_USE:
                    case IJ_USE:
                    case IJ_SELECTEDITEM:
                    case IJ_PICKCHARITEM:
                    case IJ_HASITEM:
                    case FJ_JUMP:
                        String usename = result.name()+"_"+labels.size();
                        intVars.add(Byte.toUnsignedInt(scriptbyte[i+1]));
                        int label_position = Byte.toUnsignedInt(scriptbyte[i+2])-Short.toUnsignedInt(offset);
                        EBLabel newLabel = new EBLabel(usename, label_position);
                        //search if this label has already been created
                        for(EBLabel label : labels){
                            if (label.object_i == label_position){
                                newLabel = label;
                                found = true;
                                break;
                            }
                        }
                        if(!found){
                            labels.add(newLabel);
                        }
                        EBVarJump meJ = new EBVarJump(newLabel, this, intVars);
                        meJ.id = result.getValue();
                        if (storeI == -1) {
                            meJ.object_i = i;
                        }else{
                            meJ.object_i = storeI;
                        }
                        script.add(meJ);

                        inc = 3;
                        break;
                    case DIALOGUE:
                    case N_LOADNUMBER:
                        short pointer = (short) ((scriptbyte[i+2] << 8) | (scriptbyte[i+1] & 0xFF));
                        intVars.add(Short.toUnsignedInt(pointer));

                        EBVar meV2 = new EBVar(intVars);
                        meV2.id = result.getValue();
                        meV2.object_i = i;
                        script.add(meV2);
                        inc = 3;
                        break;
                    case DA_TELEPORT:
                        byte[] slice = Arrays.copyOfRange(scriptbyte, i+1, i+5+1);
                        EBDoorArg meDA = new EBDoorArg(slice);
                        meDA.id = result.getValue();
                        meDA.object_i = i;
                        script.add(meDA);
                        inc = 4;
                        break;
                    case OJ_SUBROUTINE:
                        short pointerOJ = (short) ((scriptbyte[i+2] << 8) | (scriptbyte[i+1] & 0xFF));
                        intVars.add(Short.toUnsignedInt(pointerOJ));
                        int label_positionOJ = Byte.toUnsignedInt(scriptbyte[i+3])-Short.toUnsignedInt(offset);
                        EBLabel newLabelOJ = new EBLabel("", label_positionOJ);
                        subroutines.add(newLabelOJ);
                        EBVarJump meOJ = new EBVarJump(newLabelOJ, null, intVars);
                        meOJ.id = result.getValue();
                        meOJ.object_i = i;
                        script.add(meOJ);

                        inc = 3;
                        break;
                    case JJ_CUSTOMMENU:
                        short pointerJJ = (short) ((scriptbyte[i+2] << 8) | (scriptbyte[i+1] & 0xFF));
                        intVars.add(Short.toUnsignedInt(pointerJJ));

                        String usenameJJ = result.name()+"_"+labels.size();
                        int label_positionJJ = Byte.toUnsignedInt(scriptbyte[i+3])-Short.toUnsignedInt(offset);
                        EBLabel newLabelJJ = new EBLabel(usenameJJ, label_positionJJ);
                        //search if this label has already been created
                        for(EBLabel label : labels){
                            if (label.object_i == label_positionJJ){
                                newLabelJJ = label;
                                found = true;
                                break;
                            }
                        }
                        if(!found) {
                            labels.add(newLabelJJ);
                        }
                        usenameJJ = result.name()+"_"+labels.size();
                        label_positionJJ = Byte.toUnsignedInt(scriptbyte[i+4])-Short.toUnsignedInt(offset);
                        EBLabel newLabelJJ2 = new EBLabel(usenameJJ, label_positionJJ);
                        //search if this label has already been created
                        found = false;
                        for(EBLabel label : labels){
                            if (label.object_i == label_positionJJ){
                                newLabelJJ = label;
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            labels.add(newLabelJJ);
                        }
                        EBVarJump meJJ = new EBVarJump(newLabelJJ, newLabelJJ2, this, intVars);
                        meJJ.id = result.getValue();
                        meJJ.object_i = i;
                        script.add(meJJ);

                        inc = 4;
                        break;
                }
                switch (genericType){
                    //generic jump
                    case 1:
                        EBLabel newLabel;
                        inc = 2;
                        if(result != SCRIPT.M_MOVE) {
                            String usename = result.name() + "_" + labels.size();
                            int label_position = Byte.toUnsignedInt(scriptbyte[i + 1])-Short.toUnsignedInt(offset);
                            newLabel = new EBLabel(usename, label_position);
                            //search if this label has already been created
                            found = false;
                            for (EBLabel label : labels) {
                                if (label.object_i == label_position) {
                                    newLabel = label;
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                labels.add(newLabel);
                            }
                        }else{
                            String usename = result.name() + "_" + move_labels.size();
                            short pointer = (short) ((scriptbyte[i+2] << 8) | (scriptbyte[i+1] & 0xFF));
                            int label_position = Short.toUnsignedInt(pointer);
                            newLabel = new EBLabel(usename, label_position);
                            //search if this label has already been created
                            found = false;
                            for (EBLabel label : move_labels) {
                                if (label.object_i == label_position) {
                                    newLabel = label;
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                move_labels.add(newLabel);
                            }
                            inc += 1;
                        }
                        EBJump meJ = new EBJump(newLabel, this);
                        meJ.id = result.getValue();
                        meJ.object_i = i;
                        script.add(meJ);

                        break;
                    case 2:
                        intVars.add(Byte.toUnsignedInt(scriptbyte[i+1]));
                        EBScript meV = new EBVar(intVars);
                        meV.id = result.getValue();
                        meV.object_i = i;
                        script.add(meV);
                        inc = 2;
                        break;
                    case 3:
                        EBScript meS = new EBScript();
                        meS.id = result.getValue();
                        meS.object_i = i;
                        script.add(meS);
                        break;
                }
            }else{
                System.out.println("err!");
            }
            i+=inc;
            currentAddr+=(short) inc;
        }

    }
}




