package me.uyuyuy99.bbhack.rom;

import me.uyuyuy99.bbhack.MainMenu;
import me.uyuyuy99.bbhack.types.EBObjects.*;
import me.uyuyuy99.bbhack.types.SpriteDef;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;


public class ROMObjects_EB
{
	private MainMenu main;
	int[] main_banks_start = {0x20010, 0x22010};
	int[] main_banks_end = {0x20010+0x1FE3, 0x22010+0x1EAB};
	//wow! thats a lot of lists
	public List<List<List<EBObject>>> Banks = new ArrayList<>();

	public ROMObjects_EB(MainMenu instance) {
		main = instance;


		for(int b = 0; b < main_banks_start.length; b++){
			int i = 0; //i just acts like a tracker for the address. just in case
			List<Short> mainPointers = new ArrayList<>();
			while(true){
				byte[] data = main.rom.getT(main_banks_start[b] + i, 2);
				short word1 = (short) ((Byte.toUnsignedInt(data[1]) << 8) | Byte.toUnsignedInt(data[0]));
				short whereamI = (short)(0x8000 + i);
				if(!mainPointers.isEmpty()){
					if(whereamI == mainPointers.get(0)){
						break;
					}
				}
				i += 2;
				mainPointers.add(word1);
			}
			for (int mp = 0; mp < mainPointers.size(); mp++){
				short whereamI = (short)(0x8000 + i);
				if(whereamI != mainPointers.get(mp)){
					System.out.println("Address mismatch! Something went wrong!");
				}
				List<List<EBObject>> irrelevant = new ArrayList<>();
				Banks.add(irrelevant);
				List<Short> myPointers = new ArrayList<>();
				while(true){
					byte[] data = main.rom.getT(main_banks_start[b] + i, 2);
					short word1 = (short) ((Byte.toUnsignedInt(data[1]) << 8) | Byte.toUnsignedInt(data[0]));
					i += 2;
					if(b == 0 && mp == 0){
						if(!myPointers.isEmpty() && word1 == 0){
							break;
						}
					} else if(word1 == 0){
						break;
					}
					myPointers.add(word1);
				}
				List<byte[]> objectData = new ArrayList<>();
				for(int x = 0; x < myPointers.size(); x++){
					if(myPointers.get(x) == 0){
						break;
					}

					int start,end;
					start = main_banks_start[b] + Short.toUnsignedInt(myPointers.get(x)) - 0x8000;
					if(x < myPointers.size() - 1){
						end = main_banks_start[b] + Short.toUnsignedInt(myPointers.get(x+1)) - 0x8000;
					} else if (mp < mainPointers.size() - 1){
						end = main_banks_start[b] + Short.toUnsignedInt(mainPointers.get(mp+1)) - 0x8000;
					} else {
						end = main_banks_end[b];
					}
					objectData.add(main.rom.getT(start, end-start));
				}
				List<EBObject> parsedData = new ArrayList<>();
				for(int o = 0; o < objectData.size(); o++){
					byte[] myData = objectData.get(o);
					EBObject newObject = new EBObject(myData);
					switch(newObject.type){
						case DOOR:
							parsedData.add(new EBDoor(newObject));
							break;
						case FLAGSET_SEE:
							parsedData.add(new EBFlagSet(newObject));
							break;
						case STATIONARY_NPC2:
						case WANDERING_NPC2:
						case WANDERINGFAST_NPC:
						case SPINNING_NPC:
						case WANDERING_NPC:
							EBNPC npc = new EBNPC(newObject);
							npc.DoSpriteStuff(main.sprites.Definitions);
							parsedData.add(npc);
							break;
						case TRIGGER:
							parsedData.add(new EBProgrammable(newObject));
							break;
						default:
							switch(newObject.type_int){
								case 0x14:
									npc = new EBNPC(newObject);
									npc.DoSpriteStuff(main.sprites.Definitions);
									parsedData.add(npc);
									break;

								default:
									parsedData.add(newObject);
									break;
							}
							break;
					}
					i += myData.length;
				}
				Banks.get(mp).add(parsedData);

			}
			break;


		}




	}

}


