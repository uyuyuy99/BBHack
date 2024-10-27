package me.uyuyuy99.bbhack.rom;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;

import me.uyuyuy99.bbhack.MainMenu;
import me.uyuyuy99.bbhack.ObjectInfo;
import me.uyuyuy99.bbhack.ObjectPosition;

public class ROMObjects {
	
	public static final int MAX_OBJECTS = 64;
	public static final int[] BANK_SIZES = new int[] { 0x2000, 0x2000, 0x1DE6 };
	public static final int[] AREAS = new int[] { 0, 26, 43, 64 };
	
	private MainMenu main;
	
	private int[] freeSpace;
	public ObjectInfo[][] objects;
	
	//Lists of subroutine calls and jumps in scripts (needed because they should change when recompiling objects)
	//Format: Map<From, To> where the key is the source and the value is the target
	public Map<ObjectPosition, ObjectPosition> pointersToSubroutines;
	public Map<ObjectPosition, Integer> pointersToJumps;
	public Map<ObjectPosition, ObjectPosition> pointersToMovement;
	
	public ROMObjects(MainMenu instance) {
		main = instance;
		
		//Start of free space for all 3 banks
		freeSpace = new int[3];
		//Map height * width in terms of 64x64 chunks
		objects = new ObjectInfo[64][64];
		
		//Initialize the above maps
		pointersToSubroutines = new HashMap<ObjectPosition, ObjectPosition>();
		pointersToJumps = new HashMap<ObjectPosition, Integer>();
		pointersToMovement = new HashMap<ObjectPosition, ObjectPosition>();
		
		// --- 0000-1FFF (skip 1st bank) --- //
		int[] banksObjects = new int[] { 0x20010, 0x22010, 0x24010 };
		//Free space finder
		int curBank = 0;
		for (int i=0; i<3; i++) {
			freeSpace[i] = -1;
		}
		for (int offset : banksObjects) {
			int end;
			if (curBank == 2) end = 0x25DF6;
			else end = banksObjects[curBank + 1];
			for (int i=offset; i<end; i++) {
				if (main.rom.get(i)==0xFF && main.rom.get(i+1)==0xFF && main.rom.get(i+2)==0xFF && main.rom.get(i+3)==0xFF) { //If it begins with FF FF FF FF then it's free space
					freeSpace[curBank] = i;
					break;
				}
			}
			curBank++;
		}
		//Load objects/scripts
		curBank = 0;
		for (int offset : banksObjects) {
			for (int area=AREAS[curBank]; area<AREAS[curBank+1]; area++) {
				int areaIndex = (area - AREAS[curBank]) * 2; //Offset from the list of area pointers
				int pointer1 = main.rom.get(offset + areaIndex); //Pointer to list of objects
				pointer1 += ((main.rom.get(offset + areaIndex+1) - 128) * 256); //Other 2 digits (-128 is because 80 = 00)
				pointer1 += offset; //Add 5th digit and header
				
				int objectNum = 0;
				while (true) {
					if ((main.rom.get(pointer1) == 0) && (main.rom.get(pointer1+1) == 0))
						break;
					
					int pointer2 = main.rom.get(pointer1); //Pointer to specific object
					pointer2 += ((main.rom.get(pointer1+1) - 128) * 256); //Other 2 digits (-128 is because 80 = 00)
					pointer2 += offset; //Add 5th digit and header
					
					//Load known header
					int type = main.rom.get(pointer2) % 64;
					int x = (main.rom.get(pointer2) / 64) + (main.rom.get(pointer2+1) * 4);
					int dir = main.rom.get(pointer2+2) % 64;
					int y = (main.rom.get(pointer2+2) / 64) + (main.rom.get(pointer2+3) * 4) - 128;
					
					//Load script
					LinkedList<Integer> script = new LinkedList<Integer>();
					int pointerNext;
					if (!((main.rom.get(pointer1+2) == 0) && (main.rom.get(pointer1+3) == 0))) { //If it's not the last object in the list (most cases)
						pointerNext = main.rom.get(pointer1+2);
						pointerNext += ((main.rom.get(pointer1+3) - 128) * 256);
						pointerNext += offset;
					} else { //Otherwise, deal with overflow into other areas
						if ((area + 1) == AREAS[curBank + 1]) { //If it's the last area in the bank, set next 'area' to start of free space
							pointerNext = freeSpace[curBank];
						} else { //If it's not the last area, set pointer to next area
							pointerNext = main.rom.get(offset + areaIndex+2);
							pointerNext += ((main.rom.get(offset + areaIndex+3) - 128) * 256);
							pointerNext += offset;
						}
					}
					int headerLength = ObjectInfo.getHeaderSize(type);
					if (headerLength == -1) {
						headerLength = 4; //If header length is unknown, make everything after 4 bytes the 'script'
					}
					for (int i=0; i<((pointerNext-pointer2) - headerLength); i++) { //Max length of script is length of object minus obj header
						int scriptOffset = pointer2 + headerLength + i; //Current position in file
						int curByte = main.rom.get(scriptOffset);
						script.add(i, curByte);
					}
					
//					System.out.println(area + " " + objectNum);
					objects[area][objectNum] = new ObjectInfo(type, dir, x, y, script, pointer2); //Finally, initialize the object... object :P
					
					//Load variable portion of header
					if (headerLength == 8) {
						if (type == 32) {
							int pointerSprite = main.rom.get(pointer2+4);
							pointerSprite += ((main.rom.get(pointer2+5) - 128) * 256);
							objects[area][objectNum].setSprite(pointerSprite);
							objects[area][objectNum].setPresentItem(main.rom.get(pointer2+6));
							objects[area][objectNum].setPresentId(main.rom.get(pointer2+7));
						} else {
							objects[area][objectNum].setTargetMusic(main.rom.get(pointer2+4) % 64);
							objects[area][objectNum].setTargetX((main.rom.get(pointer2+4) / 64) + (main.rom.get(pointer2+5) * 4));
							objects[area][objectNum].setTargetDir(main.rom.get(pointer2+6) % 64);
							objects[area][objectNum].setTargetY((main.rom.get(pointer2+6) / 64) + (main.rom.get(pointer2+7) * 4) - 128);
						}
					} else if (headerLength == 6) {
						int pointerSprite = main.rom.get(pointer2+4);
						pointerSprite += ((main.rom.get(pointer2+5) - 128) * 256);
						objects[area][objectNum].setSprite(pointerSprite);
					}
					
					pointer1 += 2;
					objectNum++;
				}
				
			}
			curBank++;
		}
		//Load movement data pointers from recently loaded scripts - do it twice to eliminate false movement pointers
		for (int loop=0; loop<2; loop++) {
			pointersToMovement.clear();
			boolean secondIteration = false;
			if (loop > 0) secondIteration = true;
			
			for (int area=0; area<64; area++) {
				for (int objectNum=0; objectNum<MAX_OBJECTS; objectNum++) {
					ObjectInfo object = objects[area][objectNum];
					if (object != null) {
						if (ObjectInfo.getHeaderSize(object.getType()) != -1) {
							LinkedList<Integer> script = object.script;
							int movementIndex = script.size();
							if (object.movementIndex != null) movementIndex = object.movementIndex;
							for (int i=0; i<movementIndex; i++) { //Loop through script
								int curOffset = object.pointer + ObjectInfo.getHeaderSize(object.getType()) + i;
								if (curOffset < 1000) break; //If the offset is somehow incorrect, make a run for it!
								int code = script.get(i);
								
								if (ObjectInfo.getCodeArgumentSize(code) == -1) {
									break;
								}
								
								if (code == 0x3E) {
									int pointer = main.rom.get(curOffset+1);
									pointer += ((main.rom.get(curOffset+2) - 128) * 256);
									pointer += 0x20010;
									if (area >= 26) pointer += 0x2000;
									if (area >= 43) pointer += 0x2000;
									
									//System.out.println("0x" + Integer.toHexString(main.rom.get(curOffset+1)) + " 0x" + Integer.toHexString(main.rom.get(curOffset+2)) + " " + area + " " + objectNum);
									
									if (main.rom.get(curOffset+2) < 0x80 || main.rom.get(curOffset+2) > 0x9F) {
										if (secondIteration) System.out.println("ERROR: Movement pointer in script 0x" + Integer.toHexString(objectNum) + " in area 0x" + Integer.toHexString(area) + " is invalid!" +
												" (" + Integer.toHexString(main.rom.get(curOffset+1)) + " " + Integer.toHexString(main.rom.get(curOffset+2)) + ")");
										break;
									}
									
									ObjectPosition otherFullObjectNum = getClosestObjectFromPointer(pointer);
									if (otherFullObjectNum != null) {
										int otherArea = otherFullObjectNum.area;
										int otherObjectNum = otherFullObjectNum.objectNum;
										int otherIndex = otherFullObjectNum.index;
										pointersToMovement.put(new ObjectPosition(area, objectNum, (curOffset+1)-object.pointer),
												new ObjectPosition(otherArea, otherObjectNum, otherIndex));
										objects[otherArea][otherObjectNum].movementIndex = otherIndex;
									} else {
										if (secondIteration) System.out.println("ERROR: Movement pointer in script 0x" + Integer.toHexString(objectNum) + " in area 0x" + Integer.toHexString(area) + " does not point to an object!");
									}
								}
								i += ObjectInfo.getCodeArgumentSize(code);
							}
						}
					}
				}
			}
		}
		//Now, load subroutine calls & jumps from the recently loaded scripts
		for (int area=0; area<64; area++) {
			for (int objectNum=0; objectNum<MAX_OBJECTS; objectNum++) {
				ObjectInfo object = objects[area][objectNum];
				if (object != null) {
					if (ObjectInfo.getHeaderSize(object.getType()) != -1) {
						LinkedList<Integer> script = object.script;
						int movementIndex = script.size();
						if (object.movementIndex != null) movementIndex = object.movementIndex;
						for (int i=0; i<movementIndex; i++) { //Loop through script
							int curOffset = object.pointer + ObjectInfo.getHeaderSize(object.getType()) + i;
							if (curOffset < 1000) break; //If the offset is somehow incorrect, make a run for it!
							int code = script.get(i);
							
							if (ObjectInfo.getCodeArgumentSize(code) == -1) {
								if (code < 0x6C	) {
									for (int j : script) {
//										System.out.print(Integer.toHexString(j) + " ");
									}
//									System.out.println();
//									System.out.println("Object Offset: 0x" + Integer.toHexString(object.pointer));
//									System.out.println("x: " + object.getX() + ", y: " + object.getY());
//									System.out.println("Code: 0x" + Integer.toHexString(code));
//									System.out.println();
								}
								object.editable = false;
								break;
							}
							
							if (code == 0x2) {
								int pointer = main.rom.get(curOffset+1);
								pointer += ((main.rom.get(curOffset+2) - 128) * 256);
								pointer += 0x20010;
								if (area >= 26) pointer += 0x2000;
								if (area >= 43) pointer += 0x2000;
								
								Integer otherFullObjectNum = getObjectFromPointer(pointer);
								if (otherFullObjectNum != null) {
									int otherArea = otherFullObjectNum / MAX_OBJECTS;
									int otherObjectNum = otherFullObjectNum % MAX_OBJECTS;
									pointersToSubroutines.put(new ObjectPosition(area, objectNum, (curOffset+1)-object.pointer),
											new ObjectPosition(otherArea, otherObjectNum, main.rom.get(curOffset+3)));
								} else {
									System.out.println("ERROR: Object pointer in script 0x" + Integer.toHexString(objectNum) + " in area 0x" + Integer.toHexString(area) + " does not point to an object!");
								}
							}
							if (ObjectInfo.hasJump(code)) {
								for (int j : ObjectInfo.jump1) {
									if (j == code) pointersToJumps.put(new ObjectPosition(area, objectNum, (curOffset+1)-object.pointer), (int) main.rom.get(curOffset+1));
								} for (int j : ObjectInfo.jump2) {
									if (j == code) pointersToJumps.put(new ObjectPosition(area, objectNum, (curOffset+2)-object.pointer), (int) main.rom.get(curOffset+2));
								} for (int j : ObjectInfo.jump3) {
									if (j == code && j != 2) pointersToJumps.put(new ObjectPosition(area, objectNum, (curOffset+3)-object.pointer), (int) main.rom.get(curOffset+3));
								} for (int j : ObjectInfo.jump4) {
									if (j == code) pointersToJumps.put(new ObjectPosition(area, objectNum, (curOffset+4)-object.pointer), (int) main.rom.get(curOffset+4));
								} for (int j : ObjectInfo.jump5) {
									if (j == code) pointersToJumps.put(new ObjectPosition(area, objectNum, (curOffset+5)-object.pointer), (int) main.rom.get(curOffset+5));
								}
							}
							i += ObjectInfo.getCodeArgumentSize(code);
						}
					}
				}
			}
		}
		/*
		//Print object script
		System.out.println(); System.out.println();
		for (int i=0; i<objects[0x30][0x1B].script.size(); i++){
			System.out.print(Integer.toHexString(objects[0x30][0x1B].script.get(i)).toUpperCase() + " ");
		}
		*/
		/*
		//Find object from script size
		for (int area=0; area<64; area++) {
			for (int objectNum=0; objectNum<MAX_OBJECTS; objectNum++) {
				ObjectInfo object = objects[area][objectNum];
				if (object != null) {
					if (object.script.size() == 209) System.out.println("AREA " + area + " OBJECTNUM " + objectNum);
				}
			}
		}
		*/
		/*
		//Finding new object types
		System.out.println(); System.out.println();
		LinkedList<Integer> alreadyShown = new LinkedList<Integer>();
		for (int area=0; area<64; area++) {
			for (int objectNum=0; objectNum<MAX_OBJECTS; objectNum++) {
				ObjectInfo object = objects[area][objectNum];
				if (object != null) {
					if (object.getType() == 28) {
						if (!alreadyShown.contains(object.getType())) alreadyShown.add(object.getType());
						
						for (int i=0; i<objects[area][objectNum].script.size(); i++){
							System.out.print(Integer.toHexString(objects[area][objectNum].script.get(i)).toUpperCase() + " ");
						}
						System.out.println(); System.out.println();
					}
				}
			}
		}
		System.out.println(alreadyShown);
		*/
	}
	
	@SuppressWarnings("unused")
	public void save() {
		//First, fill with free space
		for (int i=0x20010; i<0x25DF6; i++) {
			main.rom.write(i, (short) 0xFF);
		}
		//Then, set up variables
		int curBank = 0;
		int offset = 0x20010;
		int table1Offs = offset;
		int table2Offs = offset + (26 * 2);
		for (int area=0; area<64; area++) {
			if (area == 26) {
				curBank += 1;
				offset += 0x2000;
				table1Offs = offset;
				table2Offs = offset + ((43-26) * 2);
			} else if (area == 43) {
				curBank += 1;
				offset += 0x2000;
				table1Offs = offset;
				table2Offs = offset + ((64-43) * 2);
			}
			//Add entry to area pointer table
			int pointer1 = table2Offs - offset;
			main.rom.write(table1Offs, (short) (pointer1 % 256));
			main.rom.write(table1Offs+1, (short) ((pointer1 / 256) + 128));
			
			int curOffs = table2Offs + ((getObjectsInArea(area)+1) * 2); //Add 1 for the 00 00 on the end
			for (int objectNum=0; objectNum<getObjectsInArea(area); objectNum++) {
				ObjectInfo object = objects[area][objectNum];
				int headerLength = ObjectInfo.getHeaderSize(object.getType());
				
				//Save pointer for when re-pointing data later
				object.pointer = curOffs;
				
				//Add entry to object pointer table
				int pointer2 = curOffs - offset;
				main.rom.write(table2Offs, (short) (pointer2 % 256));
				main.rom.write(table2Offs+1, (short) ((pointer2 / 256) + 128));
				
				//Store standard portion of header
				main.rom.write(curOffs, (short) (object.getType() + ((object.getX() % 4) * 64)));
				main.rom.write(curOffs+1, (short) (object.getX() / 4));
				main.rom.write(curOffs+2, (short) (object.getDir() + ((object.getY() % 4) * 64)));
				main.rom.write(curOffs+3, (short) ((object.getY() + 128) / 4));
				
				//Store variable portion of header
				if (headerLength == 8) {
					if (object.getType() == 32) {
						int pointerSprite = objects[area][objectNum].getSprite();
						main.rom.write(curOffs+4, (short) (pointerSprite % 256));
						main.rom.write(curOffs+5, (short) ((pointerSprite / 256) + 128));
						main.rom.write(curOffs+6, (short) (object.getPresentItem()));
						main.rom.write(curOffs+7, (short) (object.getPresentId()));
					} else {
						main.rom.write(curOffs+4, (short) (object.getTargetMusic() + ((object.getTargetX() % 4) * 64)));
						main.rom.write(curOffs+5, (short) (object.getTargetX() / 4));
						main.rom.write(curOffs+6, (short) (object.getTargetDir() + ((object.getTargetY() % 4) * 64)));
						main.rom.write(curOffs+7, (short) ((object.getTargetY() + 128) / 4));
					}
				} else if (headerLength == 6) {
					int pointerSprite = objects[area][objectNum].getSprite();
					main.rom.write(curOffs+4, (short) (pointerSprite % 256));
					main.rom.write(curOffs+5, (short) ((pointerSprite / 256) + 128));
				}
				
				for (int i=0; i<object.script.size(); i++) {
					int scriptOffs = curOffs + headerLength + i;
					main.rom.write(scriptOffs, object.script.get(i).shortValue());
				}
				
				curOffs += headerLength + object.script.size();
				table2Offs += 2;
			}
			main.rom.write(table2Offs, (short) 0);
			main.rom.write(table2Offs+1, (short) 0);
			if (area == 0) {
				main.rom.write(table2Offs+2, (short) 0);
				main.rom.write(table2Offs+3, (short) 0);
				table2Offs += 2;
				curOffs += 2;
			}
			table1Offs += 2;
			table2Offs = curOffs;
		}
		//Re-store pointers/jumps
		for (Entry<ObjectPosition, ObjectPosition> entry : pointersToSubroutines.entrySet()) {
			int area = entry.getKey().area;
			int objectNum = entry.getKey().objectNum;
			int index = entry.getKey().index;
			
			int targetArea = entry.getValue().area;
			int targetObjectNum = entry.getValue().objectNum;
			int targetIndex = entry.getValue().index;
			
			ObjectInfo object = objects[area][objectNum];
			
			int pointerObject = (objects[targetArea][targetObjectNum].pointer - 16) % 0x2000;
			main.rom.write(object.pointer+index, (short) (pointerObject % 256));
			main.rom.write(object.pointer+index+1, (short) ((pointerObject / 256) + 128));
			main.rom.write(object.pointer+index+2, (short) targetIndex);
		}
		for (Entry<ObjectPosition, Integer> entry : pointersToJumps.entrySet()) {
			/*
			 * Until it is shown that I need to manually update the jumps, I won't
			 * 
			int area = entry.getKey().area;
			int objectNum = entry.getKey().objectNum;
			int index = entry.getKey().index;
			
			int targetIndex = entry.getValue();
			
			ObjectInfo object = objects[area][objectNum];
			
			main.rom.write(object.pointer+index, (short) targetIndex);
			*/
		}
		for (Entry<ObjectPosition, ObjectPosition> entry : pointersToMovement.entrySet()) {
			int area = entry.getKey().area;
			int objectNum = entry.getKey().objectNum;
			int index = entry.getKey().index;
			
			int targetArea = entry.getValue().area;
			int targetObjectNum = entry.getValue().objectNum;
			int targetIndex = entry.getValue().index;
			
			ObjectInfo object = objects[area][objectNum];
			ObjectInfo targetObject = objects[targetArea][targetObjectNum];
			
			int pointerObject = targetObject.pointer + ObjectInfo.getHeaderSize(targetObject.getType()) + targetIndex;
			pointerObject -= 0x20010;
			if (targetArea >= 26) pointerObject -= 0x2000;
			if (targetArea >= 43) pointerObject -= 0x2000;
			main.rom.write(object.pointer+index, (short) (pointerObject % 256));
			main.rom.write(object.pointer+index+1, (short) ((pointerObject / 256) + 128));
		}
		
		main.rom.saveObjects(); // Save
	}
	
	private Integer getObjectFromPointer(int pointer) {
		for (int area=0; area<64; area++) {
			for (int objectNum=0; objectNum<MAX_OBJECTS; objectNum++) {
				if (objects[area][objectNum] != null) {
					if (objects[area][objectNum].pointer == pointer) {
						return (area * MAX_OBJECTS) + objectNum;
					}
				}
			}
		}
		return null;
	}
	
	public ObjectPosition getClosestObjectFromPointer(int pointer) {
		for (int area=0; area<64; area++) {
			for (int objectNum=0; objectNum<MAX_OBJECTS; objectNum++) {
				ObjectInfo object = objects[area][objectNum];
				if (object != null) {
					for (int i=0; i<object.script.size(); i++) {
						int realPointer = object.pointer + ObjectInfo.getHeaderSize(object.getType()) + i;
						if (realPointer == pointer) {
							return new ObjectPosition(area, objectNum, i);
						}
					}
				}
			}
		}
		return null;
	}
	
	public int getObjectsInArea(int area) {
		for (int objectNum=0; objectNum<MAX_OBJECTS; objectNum++) {
			if (objects[area][objectNum] == null)
				return objectNum;
		}
		return MAX_OBJECTS;
	}
	
	public int getFilledSpace(final int bank) {
		int index = 0;
		for (int area=AREAS[bank]; area<AREAS[bank+1]; area++) {
			for (int objectNum=0; objectNum<MAX_OBJECTS; objectNum++) {
				final ObjectInfo object = objects[area][objectNum];
				if (object == null) break;
				
				index += 2;
				index += ObjectInfo.getHeaderSize(object.getType());
				index += object.script.size();
			}
			index += 4;
		}
		return index;
	}
	
	public boolean isRoom(ObjectInfo object, int area) {
		int bank = 0;
		if (area >= ROMObjects.AREAS[1]) bank++;
		if (area >= ROMObjects.AREAS[2]) bank++;
		
		if (((BANK_SIZES[bank]) - getFilledSpace(bank)) - object.size() <= 0) {
			return false;
		}
		
		if (main.objects.getObjectsInArea(area) >= ROMObjects.MAX_OBJECTS) {
			return false;
		}
		
		return true;
	}
	
	public boolean addObject(ObjectInfo objectGiven, int area) {
		ObjectInfo object = objectGiven.clone();
		
		int bank = 0;
		if (area >= ROMObjects.AREAS[1]) bank++;
		if (area >= ROMObjects.AREAS[2]) bank++;
		
		if (((BANK_SIZES[bank]) - getFilledSpace(bank)) - object.size() <= 0) {
			return false;
		}
		
		int objectsInArea = main.objects.getObjectsInArea(area);
		if (objectsInArea >= ROMObjects.MAX_OBJECTS) {
			return false;
		}
		
		objects[area][objectsInArea] = object;
		
		return true;
	}
	
	//Returns null if it worked, returns list of references to the object(s) in need of deletion if it failed
	public ArrayList<ObjectPosition> removeObject(int area, int objectNum) {
		//First, check for references to this object
		for (Entry<ObjectPosition, ObjectPosition> entry : pointersToSubroutines.entrySet()) {
			ArrayList<ObjectPosition> list = new ArrayList<ObjectPosition>();
			
			int targetArea = entry.getValue().area;
			int targetObjectNum = entry.getValue().objectNum;
			
			if (targetArea == area && targetObjectNum == objectNum) { //If object is pointing to the one about to be deleted
				if (!(entry.getKey().area == targetArea && entry.getKey().objectNum == targetObjectNum)) { //Make sure it's not the same object pointing to itself
					list.add(entry.getKey());
				}
			}
			
			if (list.size() > 0) {
				return list;
			}
		} for (Entry<ObjectPosition, ObjectPosition> entry : pointersToMovement.entrySet()) {
			ArrayList<ObjectPosition> list = new ArrayList<ObjectPosition>();
			
			int targetArea = entry.getValue().area;
			int targetObjectNum = entry.getValue().objectNum;
			
			if (targetArea == area && targetObjectNum == objectNum) { //If object is pointing to the one about to be deleted
				if (!(entry.getKey().area == targetArea && entry.getKey().objectNum == targetObjectNum)) { //Make sure it's not the same object pointing to itself
					list.add(entry.getKey());
				}
			}
			
			if (list.size() > 0) {
				return list;
			}
		}
		
		//Remove the object
		objects[area][objectNum] = null;
		
		//Shift objects
		for (int i=objectNum+1; i<getObjectsInArea(area); i++) {
			objects[area][i-1] = objects[area][i];
			objects[area][i] = null;
		}
		
		//Shift references to the objects
		for (Entry<ObjectPosition, ObjectPosition> entry : pointersToSubroutines.entrySet()) {
			int targetArea = entry.getValue().area;
			int targetObjectNum = entry.getValue().objectNum;
			
			if (targetArea != area) continue;
			
			if (targetObjectNum > objectNum) {
				entry.getValue().objectNum--;
			}
		} for (Entry<ObjectPosition, ObjectPosition> entry : pointersToMovement.entrySet()) {
			int targetArea = entry.getValue().area;
			int targetObjectNum = entry.getValue().objectNum;
			
			if (targetArea != area) continue;
			
			if (targetObjectNum > objectNum) {
				entry.getValue().objectNum--;
			}
		}
		
		return null;
	}
	
	//Returns null if it worked, returns list of references to the object(s) in need of deletion if it failed
	public ArrayList<ObjectPosition> moveObject(int area, int objectNum, int area2) {
		//Double-check if object exists
		if (objects[area][objectNum] == null) {
			return null;
		}
		
		//Double-check if there is room for object
		if (!isRoom(objects[area][objectNum], area2)) {
			return null;
		}
		
		//Check for references to this object
		for (Entry<ObjectPosition, ObjectPosition> entry : pointersToSubroutines.entrySet()) {
			ArrayList<ObjectPosition> list = new ArrayList<ObjectPosition>();
			
			int targetArea = entry.getValue().area;
			int targetObjectNum = entry.getValue().objectNum;
			
			if (targetArea == area && targetObjectNum == objectNum) { //If object is pointing to the one about to be deleted
				if (!(entry.getKey().area == targetArea && entry.getKey().objectNum == targetObjectNum)) { //Make sure it's not the same object pointing to itself
					list.add(entry.getKey());
				}
			}
			
			if (list.size() > 0) {
				return list;
			}
		} for (Entry<ObjectPosition, ObjectPosition> entry : pointersToMovement.entrySet()) {
			ArrayList<ObjectPosition> list = new ArrayList<ObjectPosition>();
			
			int targetArea = entry.getValue().area;
			int targetObjectNum = entry.getValue().objectNum;
			
			if (targetArea == area && targetObjectNum == objectNum) { //If object is pointing to the one about to be deleted
				if (!(entry.getKey().area == targetArea && entry.getKey().objectNum == targetObjectNum)) { //Make sure it's not the same object pointing to itself
					list.add(entry.getKey());
				}
			}
			
			if (list.size() > 0) {
				return list;
			}
		}
		
		//Add object to other area
		addObject(objects[area][objectNum], area2);
		
		// -- Re-add pointers to other area -- //
		for (Entry<ObjectPosition, ObjectPosition> entry : pointersToSubroutines.entrySet()) {
			int targetArea = entry.getValue().area;
			int targetObjectNum = entry.getValue().objectNum;
			
			if (targetArea != area) continue;
			
			if (targetObjectNum > objectNum) {
				entry.getValue().objectNum--;
			}
		} for (Entry<ObjectPosition, ObjectPosition> entry : pointersToMovement.entrySet()) {
			int targetArea = entry.getValue().area;
			int targetObjectNum = entry.getValue().objectNum;
			
			if (targetArea != area) continue;
			
			if (targetObjectNum > objectNum) {
				entry.getValue().objectNum--;
			}
		}
		
		//Remove the object
		objects[area][objectNum] = null;
		
		//Shift objects
		for (int i=objectNum+1; i<getObjectsInArea(area); i++) {
			objects[area][i-1] = objects[area][i];
			objects[area][i] = null;
		}
		
		//Shift references to the objects
		for (Entry<ObjectPosition, ObjectPosition> entry : pointersToSubroutines.entrySet()) {
			int targetArea = entry.getValue().area;
			int targetObjectNum = entry.getValue().objectNum;
			
			if (targetArea != area) continue;
			
			if (targetObjectNum > objectNum) {
				entry.getValue().objectNum--;
			}
		} for (Entry<ObjectPosition, ObjectPosition> entry : pointersToMovement.entrySet()) {
			int targetArea = entry.getValue().area;
			int targetObjectNum = entry.getValue().objectNum;
			
			if (targetArea != area) continue;
			
			if (targetObjectNum > objectNum) {
				entry.getValue().objectNum--;
			}
		}
		
		return null;
	}
	
	/*
	public void shiftPointers(int startArea, int startObject, int shift) {
		for (int area=startArea; area<64; area++) {
			for (int objectNum=0; objectNum<MAX_OBJECTS; objectNum++) {
				if (objectNum <= startObject && area == startArea) continue;
				if (objects[area][objectNum] != null) {
					objects[area][objectNum].pointer += shift;
				}
			}
		}
	}
	*/
	
	/*
	public void updateJumps(ObjectPosition pos, int shift) { //Always add shift to index before using
		for (Entry<ObjectPosition, ObjectPosition> entry : pointersToSubroutines.entrySet()) {
			int targetArea = entry.getValue().area;
			int targetObjectNum = entry.getValue().objectNum;
			int targetIndex = entry.getValue().index;
			
			boolean moveIt = false;
			if (targetArea > pos.area) {
				moveIt = true;
			} else if (targetArea == pos.area) {
				if (targetObjectNum > pos.objectNum) {
					moveIt = true;
				} else if (targetObjectNum == pos.objectNum) {
					if (targetIndex > pos.index) {
						moveIt = true;
					}
				}
			}
			
			if (moveIt) {
				entry.getValue().index += shift;
			}
		}
		for (Entry<ObjectPosition, Integer> entry : pointersToJumps.entrySet()) {
			int targetArea = entry.getKey().area;
			int targetObjectNum = entry.getKey().objectNum;
			int targetIndex = entry.getValue();
			
			boolean moveIt = false;
			if (targetArea > pos.area) {
				moveIt = true;
			} else if (targetArea == pos.area) {
				if (targetObjectNum > pos.objectNum) {
					moveIt = true;
				} else if (targetObjectNum == pos.objectNum) {
					if (targetIndex > pos.index) {
						moveIt = true;
					}
				}
			}
			
			if (moveIt) {
				entry.setValue(targetIndex + shift);
			}
		}
		for (Entry<ObjectPosition, ObjectPosition> entry : pointersToMovement.entrySet()) {
			int targetArea = entry.getValue().area;
			int targetObjectNum = entry.getValue().objectNum;
			int targetIndex = entry.getValue().index;
			
			boolean moveIt = false;
			if (targetArea > pos.area) {
				moveIt = true;
			} else if (targetArea == pos.area) {
				if (targetObjectNum > pos.objectNum) {
					moveIt = true;
				} else if (targetObjectNum == pos.objectNum) {
					if (targetIndex > pos.index) {
						moveIt = true;
					}
				}
			}
			
			if (moveIt) {
				entry.getValue().index += shift;
			}
		}
	}
	*/
	
}
