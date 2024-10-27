package me.uyuyuy99.bbhack.SE;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import me.uyuyuy99.bbhack.Info;
import me.uyuyuy99.bbhack.MainMenu;
import me.uyuyuy99.bbhack.ObjectInfo;
import me.uyuyuy99.bbhack.ObjectPosition;
import me.uyuyuy99.bbhack.rom.ROMItems;
import me.uyuyuy99.bbhack.rom.ROMObjects;
import me.uyuyuy99.bbhack.rom.ROMText;

public class ScriptEditor extends JFrame {
	
	private static final long serialVersionUID = 1L;

	private MainMenu main;
	private ScriptEditor thisRef = this;
	
	private boolean dontUpdateScript = false;
	private int free;
	private int bank;
	
	JPanel panelMain;
	PanelArguments panelArguments;
	PanelOptionsSE panelOptions;
	
	JScrollPane scriptPane;
	DefaultListModel<String> scriptListModel = new DefaultListModel<String>();
	JList<String> scriptList;
	LinkedList<int[]> script = new LinkedList<int[]>();
	
	public ScriptEditor(MainMenu instance) {
		super("Script Editor");
		
		main = instance;
		
		setVisible(true);
		setSize(800, 702);
		setLocationRelativeTo(null);
		
		//Menu bar
		setJMenuBar(new MenuBar());
		
		panelMain = new JPanel();
		panelArguments = new PanelArguments(this);
		add(panelMain, BorderLayout.CENTER);
		add(panelArguments, BorderLayout.LINE_END);
		panelArguments.disableAll();
		
		panelOptions = new PanelOptionsSE(main);
		add(panelOptions, BorderLayout.PAGE_START);
		panelOptions.setArea(1);
		
		scriptList = new JList<String>(scriptListModel);
		scriptList.setLayoutOrientation(JList.VERTICAL);
		scriptList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scriptPane = new JScrollPane(scriptList);
		panelMain.setLayout(new BorderLayout());
		panelMain.add(scriptPane, BorderLayout.CENTER);
		loadScript(panelOptions.getArea(), panelOptions.getObject());
		
		panelOptions.resetLast();
		updateFreeSpace(false);
		
		panelOptions.areaList.addItemListener(
			new ItemListener() {
				public void itemStateChanged(ItemEvent event) {
					dontUpdateScript = true;
					panelOptions.setArea(panelOptions.getArea());
					panelOptions.objectList.setSelectedIndex(0);
					
					saveScript();
					loadScript(panelOptions.getArea(), 0);
					updateScript();
					panelOptions.resetLast();
					updateFreeSpace(false);
				}
			}
		);
		
		panelOptions.objectList.addItemListener(
			new ItemListener() {
				public void itemStateChanged(ItemEvent event) {
					if (!dontUpdateScript) {
						saveScript();
						loadScript(panelOptions.getArea(), panelOptions.getObject());
						updateScript();
						panelOptions.resetLast();
					}
					dontUpdateScript = false;
					panelArguments.disableAll();
				}
			}
		);
		
		panelArguments.bAdd.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					final int selected = Math.max(scriptList.getSelectedIndex(), 0);
					final int code = panelArguments.getCode(); panelArguments.getCode();
					final int codeLength = ObjectInfo.getCodeArgumentSize(code) + 1;
					
					if (codeLength == 0) {
						JOptionPane.showMessageDialog(thisRef, "That is not a valid script code.", "Nope", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					int scriptSize = 0;
					for (int[] args : script) {
						scriptSize += args.length;
					}
					
					if (((ROMObjects.BANK_SIZES[bank] - free) - ObjectInfo.getCodeArgumentSize(code)) <= 0) {
						JOptionPane.showMessageDialog(thisRef, "You cannot fit any more object/script data in this bank!", "Nope", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					if ((scriptSize + codeLength) < 246) { //If script isn't full, go on
						int[] args = new int[codeLength];
						
						args[0] = code;
						for (int i=1; i<codeLength; i++) {
							args[i] = 0;
						}
						
						script.add(selected, args);
						updateScript();
						scriptList.setSelectedIndex(selected);
					} else { //Else, tell the user
						JOptionPane.showMessageDialog(thisRef, "You have reached the size limit for a script.", "Nope", JOptionPane.ERROR_MESSAGE);
					}
					saveScript();
					updateFreeSpace();
					loadScript(panelOptions.getArea(), panelOptions.getObject());
				}
			}
		);
		
		panelArguments.bRemove.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					final int selected = scriptList.getSelectedIndex();
					if (selected != -1) {
						script.remove(selected);
						scriptListModel.removeElementAt(selected);
						updateScript();
						updateFreeSpace(false);
					}
				}
			}
		);
		
		panelArguments.bUp.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					final int selected = scriptList.getSelectedIndex();
					if (selected > 0) {
						int[] args = script.get(selected);
						
						script.remove(selected);
						script.add(selected - 1, args);
						
						scriptListModel.removeElementAt(selected);
						updateScript();
						
						scriptList.setSelectedIndex(selected - 1);
					}
				}
			}
		); panelArguments.bDown.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					final int selected = scriptList.getSelectedIndex();
					if (selected > -1 && selected < script.size()-1) {
						int[] args = script.get(selected);
						
						script.remove(selected);
						script.add(selected + 1, args);
						
						scriptListModel.removeElementAt(selected);
						updateScript();
						
						scriptList.setSelectedIndex(selected + 1);
					}
				}
			}
		);
		
		panelArguments.bApply.addActionListener(new ApplyButtonListener());
		
		scriptList.addListSelectionListener(new ScriptSelectionListener());
		
		addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent event) {
		        exitSave();
		    }
		});
	}
	
	private void updateFreeSpace(boolean update) {
//		if (update) saveScript(); TODO
		
		final int area = panelOptions.getArea();
		bank = 0;
		if (area >= ROMObjects.AREAS[1]) bank++;
		if (area >= ROMObjects.AREAS[2]) bank++;
		free = main.objects.getFilledSpace(bank);
		
		panelOptions.freeSpace.setText(panelOptions.freeSpaceMessage + (ROMObjects.BANK_SIZES[bank] - free) + " bytes ");
	}
	private void updateFreeSpace() {
		updateFreeSpace(true);
	}
	
	private int getLineFromIndex(ObjectInfo object, int index) {
		LinkedList<Integer> script = object.script;
		index -= ObjectInfo.getHeaderSize(object.getType());
		
		int line = 0;
		for (int i=0; i<index; i++) { //Loop through script
			int code = script.get(i);
			i += ObjectInfo.getCodeArgumentSize(code);
			line++;
		}
		
		return line;
	}
	
	private int getIndexFromLine(int type, int line) {
		int index = 0;
		int headerLength = ObjectInfo.getHeaderSize(type);
		if (line >= script.size()) line = script.size() - 1;
		
		for (int i=0; i<(line+1); i++) {
			if (line == i)
				return headerLength + index;
			index += script.get(i).length;
		}
		return headerLength;
	}
	
	private int maxLines() {
		return scriptListModel.size() - 1;
	}
	
	/*
	private int getFreeSpace(final int bank) {
		int index = 0;
		for (int area=ROMObjects.AREAS[bank]; area<ROMObjects.AREAS[bank+1]; area++) {
			for (int objectNum=0; objectNum<ROMObjects.MAX_OBJECTS; objectNum++) {
				final ObjectInfo object = main.objects.objects[area][objectNum];
				if (object == null) break;
				
				index += 2;
				index += ObjectInfo.getHeaderSize(object.getType());
				int scriptLength = 0;
				for (int[] args : script) {
					scriptLength += args.length;
					index += args.length;
				}
				Integer movementIndex = object.movementIndex;
				if (movementIndex == null) movementIndex = 0;
				index += (scriptLength - movementIndex);
			}
			index += 4;
		}
		return index;
	}
	*/
	
	private void loadScript(int area, int objectNum) {
		this.script.clear();
		ObjectInfo object = main.objects.objects[area][objectNum];
		
		LinkedList<Integer> script = object.script;
		int movementIndex = script.size();
		if (object.movementIndex != null) movementIndex = object.movementIndex;
		
		for (int i=0; i<movementIndex; i++) { //Loop through script
			int code = script.get(i);
			int argumentSize = ObjectInfo.getCodeArgumentSize(code);
			
			if (argumentSize == -1) break;
			
			int[] args = new int[argumentSize + 1];
			args[0] = code;
			for (int j=0; j<argumentSize; j++) {
				args[j + 1] = script.get(i + j + 1);
			}
			
			if (ObjectInfo.hasJump(code)) {
				for (int j : ObjectInfo.jump1) {
					if (j == code) args[1] = getLineFromIndex(object, args[1]);
				} for (int j : ObjectInfo.jump2) {
					if (j == code) args[2] = getLineFromIndex(object, args[2]);
				} for (int j : ObjectInfo.jump3) {
					if (j == code && j != 2) args[3] = getLineFromIndex(object, args[3]);
				} for (int j : ObjectInfo.jump4) {
					if (j == code) args[4] = getLineFromIndex(object, args[4]);
				} for (int j : ObjectInfo.jump5) {
					if (j == code) args[5] = getLineFromIndex(object, args[5]);
				}
			}
			
			this.script.add(args);
			
			i += ObjectInfo.getCodeArgumentSize(code);
		}
	}
	
	private void saveScript() {
		ObjectInfo object = main.objects.objects[panelOptions.lastArea][panelOptions.lastObject];
		
		final int oldSize = object.script.size();
		Integer movementIndex = object.movementIndex;
		if (movementIndex == null) movementIndex = oldSize;
		
		int[] movement = new int[oldSize - movementIndex];
		if (movementIndex < oldSize) {
			for (int i=0; i<movement.length; i++) {
				movement[i] = object.script.get(movementIndex + i);
			}
		}
		object.script.clear();
		
		int index = 0;
		for (int[] args : script) {
			final int code = args[0];
			final int type = object.getType();
			
			if (ObjectInfo.hasJump(code)) {
				for (int j : ObjectInfo.jump1) {
					if (j == code) args[1] = getIndexFromLine(type, args[1]);
				} for (int j : ObjectInfo.jump2) {
					if (j == code) args[2] = getIndexFromLine(type, args[2]);
				} for (int j : ObjectInfo.jump3) {
					if (j == code && j != 2) args[3] = getIndexFromLine(type, args[3]);
				} for (int j : ObjectInfo.jump4) {
					if (j == code) args[4] = getIndexFromLine(type, args[4]);
				} for (int j : ObjectInfo.jump5) {
					if (j == code) args[5] = getIndexFromLine(type, args[5]);
				}
			}
			
			for (int i=0; i<args.length; i++) {
				object.script.add(args[i]);
				index++;
			}
		}
		
		object.movementIndex = index;
		if (movementIndex < oldSize) {
			for (int i=0; i<movement.length; i++) {
				object.script.add(movement[i]);
			}
		}
	}
	
	private void updateScript() {
		scriptListModel.clear();
		
		for (int line=0; line<script.size(); line++) {
			int[] args = script.get(line);
			updateScriptLine(line, args);
		}
	}
	
	private void updateScriptLine(int line, int[] args) {
		String prefix = "<html><span style=\"font-size: 10px\"><b>[" + line + "] </b>";
		
		switch (args[0]) {
		case 0:
			scriptListModel.add(line, prefix + "END");
			return;
		case 1:
			scriptListModel.add(line, prefix + "Jump to line " + args[1]);
			return;
		case 2:
			scriptListModel.add(line, prefix + "Run another object's script (" + args[1] + " " + args[2] + ", position " + args[3] + ")");
			return;
		case 3:
			scriptListModel.add(line, prefix + "Return to original object's script");
			return;
		case 4:
			float secs = ((float) args[1]) / 4F;
			scriptListModel.add(line, prefix + "Delay for " + secs + " seconds");
			return;
		case 5:
			scriptListModel.add(line, prefix + "If flag #" + args[1] + " is set, make object disappear");
			return;
		case 6:
			scriptListModel.add(line, prefix + "If flag #" + args[1] + " is set, make object appear");
			return;
		case 7:
			scriptListModel.add(line, prefix + "(freeze game)");
			return;
		case 8:
			scriptListModel.add(line, prefix + "Display text: <i>" + main.text.getText(args[1] + (args[2] * 256)) + "</i>");
			return;
		case 9:
			scriptListModel.add(line, prefix + "Ask yes/no, jump to line " + args[1] + " if 'no' selected");
			return;
		case 10:
			scriptListModel.add(line, prefix + "Jump to line " + args[1] + " unless TALKing to object");
			return;
		case 11:
			scriptListModel.add(line, prefix + "Jump to line " + args[1] + " unless CHECKing to object");
			return;
		case 12:
			scriptListModel.add(line, prefix + "Jump to line " + args[2] + " unless using PSI power " + args[1] + " on object");
			return;
		case 13:
			scriptListModel.add(line, prefix + "Jump to line " + args[2] + " unless using " + main.items.getItemName(args[1]) + " on object");
			return;
		case 14:
			scriptListModel.add(line, prefix + "(freeze game)");
			return;
		case 15:
			scriptListModel.add(line, prefix + "Reset NES");
			return;
		case 16:
			scriptListModel.add(line, prefix + "Set flag #" + args[1]);
			return;
		case 17:
			scriptListModel.add(line, prefix + "Clear flag #" + args[1]);
			return;
		case 18:
			scriptListModel.add(line, prefix + "Jump to line " + args[2] + " unless flag #" + args[1] + " set");
			return;
		case 19:
			scriptListModel.add(line, prefix + "Decrease counter #" + args[1]);
			return;
		case 20:
			scriptListModel.add(line, prefix + "Increase counter #" + args[1]);
			return;
		case 21:
			scriptListModel.add(line, prefix + "Set counter #" + args[1] + " to 0");
			return;
		case 22:
			scriptListModel.add(line, prefix + "Jump to line " + args[3] + " if counter #" + args[1] + " is less than " + args[2]);
			return;
		case 23:
			scriptListModel.add(line, prefix + "Change map variable #" + args[1] + " to " + args[2]);
			return;
		case 24:
			scriptListModel.add(line, prefix + "Choose character, jump to line " + args[1] + " if B pressed");
			return;
		case 25:
			scriptListModel.add(line, prefix + "Load character " + args[1] + " (" + Info.characterNames[args[1]] + ")");
			return;
		case 26:
			scriptListModel.add(line, prefix + "Jump to line " + args[2] + " unless character " + args[1] + " (" + Info.characterNames[args[1]] + ") selected");
			return;
		case 27:
			scriptListModel.add(line, prefix + "Jump to line " + args[1] + " if no money added to bank acct since last call");
			return;
		case 28:
			scriptListModel.add(line, prefix + "Input a number, jump to line " + args[1] + " if B pressed");
			return;
		case 29:
			scriptListModel.add(line, prefix + "Load the number " + (args[1] + (args[2] * 256)) + " into memory");
			return;
		case 30:
			scriptListModel.add(line, prefix + "Jump to line " + args[3] + " if loaded number is less than " + (args[1] + (args[2] * 256)));
			return;
		case 31:
			scriptListModel.add(line, prefix + "Show money");
			return;
		case 32:
			scriptListModel.add(line, prefix + "Choose item from inventory, jump to line " + main.items.getItemName(args[1]) + " if B pressed");
			return;
		case 33:
			scriptListModel.add(line, prefix + "Choose item from closet, jump to line " + args[1] + " if B pressed");
			return;
		case 34:
			scriptListModel.add(line, prefix + "Choose item from list (" + main.items.getItemName(args[1]) + ", " + main.items.getItemName(args[2]) + ", " + main.items.getItemName(args[3]) + ", " + main.items.getItemName(args[4]) + ") jump to line " + args[5] + " if B pressed");
			return;
		case 35:
			scriptListModel.add(line, prefix + "Jump to line " + args[2] + " unless " + main.items.getItemName(args[1]) + " is in loaded character's inventory");
			return;
		case 36:
			scriptListModel.add(line, prefix + "Jump to line " + args[2] + " unless " + main.items.getItemName(args[1]) + " is in closet");
			return;
		case 37:
			scriptListModel.add(line, prefix + "Load item " + main.items.getItemName(args[1]));
			return;
		case 38:
			scriptListModel.add(line, prefix + "Jump to line " + args[2] + " unless " + main.items.getItemName(args[1]) + " is loaded");
			return;
		case 39:
			scriptListModel.add(line, prefix + "Jump to line " + args[2] + " unless " + main.items.getItemName(args[1]) + " is in any character's inventory");
			return;
		case 40:
			scriptListModel.add(line, prefix + "Give loaded number as money, jump to line " + args[1] + " if can't hold any more");
			return;
		case 41:
			scriptListModel.add(line, prefix + "Take loaded number as money, jump to line " + args[1] + " if not enough");
			return;
		case 42:
			scriptListModel.add(line, prefix + "Add loaded number to bank acct, jump to line " + args[1] + " if can't hold any more");
			return;
		case 43:
			scriptListModel.add(line, prefix + "Take loaded number from bank acct, jump to line " + args[1] + " if not enough");
			return;
		case 44:
			scriptListModel.add(line, prefix + "Jump to line " + args[1] + " if loaded item is unsellable");
			return;
		case 45:
			scriptListModel.add(line, prefix + "Add loaded item to inventory, jump to line " + args[1] + " if full");
			return;
		case 46:
			scriptListModel.add(line, prefix + "Remove loaded item from inventory, jump to line " + args[1] + " if not present");
			return;
		case 47:
			scriptListModel.add(line, prefix + "Add loaded item to closet, jump to line " + args[1] + " if full");
			return;
		case 48:
			scriptListModel.add(line, prefix + "Remove loaded item from closet, jump to line " + args[1] + " if not present");
			return;
		case 49:
			scriptListModel.add(line, prefix + "Load item from index " + args[1] + " from loaded character's inventory, jump to line " + args[2] + " if empty slot");
			return;
		case 50:
			float num = ((float) args[1]) / 256F;
			scriptListModel.add(line, prefix + "Multiply loaded number by " + num);
			return;
		case 51:
			scriptListModel.add(line, prefix + "Jump to line " + args[2] + " if character " + args[1] + " (" + Info.characterNames[args[1]] + ") not in party");
			return;
		case 52:
			scriptListModel.add(line, prefix + "???");
			return;
		case 53:
			scriptListModel.add(line, prefix + "Jump to line " + args[1] + " unless touching object");
			return;
		case 54:
			scriptListModel.add(line, prefix + "Jump to line " + args[1] + " unless?...");
			return;
		case 55:
			scriptListModel.add(line, prefix + "Show 2-option menu, jump to line " + args[3] + " if 2nd selected, jump to line " + args[4] + " if B pressed");
			return;
		case 56:
			scriptListModel.add(line, prefix + "Jump to line " + args[1] + " if no items in loaded character's inventory");
			return;
		case 57:
			scriptListModel.add(line, prefix + "Jump to line " + args[1] + " if no items in closet");
			return;
		case 58:
			scriptListModel.add(line, prefix + "Load character from party (index " + args[1] + "), jump to line " + args[2] + " if not present");
			return;
		case 59:
			scriptListModel.add(line, prefix + "Change object type to " + ObjectInfo.objectTypes[args[1]]);
			return;
		case 60:
			scriptListModel.add(line, prefix + "???");
			return;
		case 61:
			scriptListModel.add(line, prefix + "Teleport player to " + (args[1] + (args[2] * 256)) + ", " + (args[3] + (args[4] * 256)));
			return;
		case 62:
			scriptListModel.add(line, prefix + "Move object (movement pointer: " + Integer.toHexString(args[1]).toUpperCase() + " " + Integer.toHexString(args[2]).toUpperCase() + ")");
			return;
		case 63:
			scriptListModel.add(line, prefix + "Signal object #" + args[1] + " (same area)");
			return;
		case 64:
			scriptListModel.add(line, prefix + "Jump to line " + args[1] + " unless signalled by another object");
			return;
		case 65:
			scriptListModel.add(line, prefix + "Teleport to saved game location");
			return;
		case 66:
			scriptListModel.add(line, prefix + "Add character " + args[1] + " (" + Info.characterNames[args[1]] + ") to party, jump to line " + args[2] + " if party full");
			return;
		case 67:
			scriptListModel.add(line, prefix + "Remove character " + args[1] + " (" + Info.characterNames[args[1]] + ") from party, jump to line " + args[2] + " if not present");
			return;
		case 68:
			scriptListModel.add(line, prefix + "Start battle w/ enemy group #" + args[1]);
			return;
		case 69:
			scriptListModel.add(line, prefix + "Multiply loaded number by amount of characters in party");
			return;
		case 70:
			scriptListModel.add(line, prefix + "Rocket");
			return;
		case 71:
			scriptListModel.add(line, prefix + "Airplane");
			return;
		case 72:
			scriptListModel.add(line, prefix + "Tank");
			return;
		case 73:
			scriptListModel.add(line, prefix + "Boat");
			return;
		case 74:
			scriptListModel.add(line, prefix + "Train");
			return;
		case 75:
			scriptListModel.add(line, prefix + "Elevator");
			return;
		case 76:
			scriptListModel.add(line, prefix + "No Vehicle");
			return;
		case 77:
			scriptListModel.add(line, prefix + "???");
			return;
		case 78:
			scriptListModel.add(line, prefix + "???");
			return;
		case 79:
			scriptListModel.add(line, prefix + "???");
			return;
		case 80:
			scriptListModel.add(line, prefix + "Jump to line " + args[1] + " if loaded character at less than max HP");
			return;
		case 81:
			scriptListModel.add(line, prefix + "Heal loaded character's HP by " + args[1]);
			return;
		case 82:
			scriptListModel.add(line, prefix + "Jump to line " + args[2] + " if loaded character has any of the specified status(es)");
			return;
		case 83:
			scriptListModel.add(line, prefix + "Remove status(es) from loaded character");
			return;
		case 84:
			scriptListModel.add(line, prefix + "Jump to line " + args[2] + " if loaded character below level " + args[1]);
			return;
		case 85:
			scriptListModel.add(line, prefix + "Sleep");
			return;
		case 86:
			scriptListModel.add(line, prefix + "Save game");
			return;
		case 87:
			scriptListModel.add(line, prefix + "Load loaded character's exp needed for next level");
			return;
		case 88:
			scriptListModel.add(line, prefix + "Load money");
			return;
		case 89:
			scriptListModel.add(line, prefix + "Inflict status(es) on loaded character");
			return;
		case 90:
			scriptListModel.add(line, prefix + "Change BG music to " + Info.musicNames[args[1]]);
			return;
		case 91:
			scriptListModel.add(line, prefix + "Play bank 1 sound effect " + Info.soundEffectNames1[args[1]]);
			return;
		case 92:
			scriptListModel.add(line, prefix + "Play bank 2 sound effect " + Info.soundEffectNames2[args[1]]);
			return;
		case 93:
			scriptListModel.add(line, prefix + "Play bank 3 sound effect " + Info.soundEffectNames3[args[1]]);
			return;
		case 94:
			scriptListModel.add(line, prefix + "(freeze game)");
			return;
		case 95:
			scriptListModel.add(line, prefix + "Teach characters 1 and 2 to teleport");
			return;
		case 96:
			scriptListModel.add(line, prefix + "Jump to line " + args[1] + " if loaded character at less than max PP");
			return;
		case 97:
			scriptListModel.add(line, prefix + "Heal loaded character's PP by " + args[1]);
			return;
		case 98:
			scriptListModel.add(line, prefix + "Confiscate weapon, jump to line " + args[1] + " if none");
			return;
		case 99:
			scriptListModel.add(line, prefix + "Load confiscated weapon, jump to line " + args[1] + " if none");
			return;
		case 100:
			scriptListModel.add(line, prefix + "Live show routine");
			return;
		case 101:
			scriptListModel.add(line, prefix + "Jump to line " + args[1] + " unless all 8 melodies learned");
			return;
		case 102:
			scriptListModel.add(line, prefix + "Register your name");
			return;
		case 103:
			scriptListModel.add(line, prefix + "Darken palette (Magicant end)");
			return;
		case 104:
			scriptListModel.add(line, prefix + "Land mine routine");
			return;
		case 105:
			scriptListModel.add(line, prefix + "Horizontal shake (EVE?)");
			return;
		case 106:
			scriptListModel.add(line, prefix + "XX-Stone routine");
			return;
		case 107:
			scriptListModel.add(line, prefix + "???");
			return;
		}
	}
	
	class ScriptSelectionListener implements ListSelectionListener {
		
		public void valueChanged(ListSelectionEvent event) {
			if (scriptList.getSelectedIndex() == -1)
				return;
			
			panelArguments.disableAll();
			int[] args = script.get(scriptList.getSelectedIndex());
			PanelArguments p = panelArguments;
			
			switch (args[0]) {
			case 0:
				break;
			case 1:
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[1]);
				break;
			case 2:
				int pointer = args[1];
				pointer += (args[2] - 128) * 256;
				pointer += 0x20010;
				if (panelOptions.getArea() >= 26) pointer += 0x2000;
				if (panelOptions.getArea() >= 43) pointer += 0x2000;
				ObjectPosition pos = main.objects.getClosestObjectFromPointer(pointer);
				p.spinner1Label.setText("Area");
				p.spinner1.setEnabled(true);
				p.setMin1(1);
				p.setMax1(63);
				p.spinner1.setValue(pos.area);
				
				p.spinner2Label.setText("Object");
				p.spinner2.setEnabled(true);
				p.setMax2(main.objects.getObjectsInArea(pos.area));
				p.spinner2.setValue(pos.objectNum);
				
				p.spinner3Label.setText("Script Position");
				p.spinner3.setEnabled(true);
				p.setMax3(255);
				p.spinner3.setValue(args[3]);
				break;
			case 3:
				break;
			case 4:
				p.spinner1Label.setText("Delay (1/4 seconds)");
				p.spinner1.setEnabled(true);
				p.setMax1(255);
				p.spinner1.setValue(args[1]);
				break;
			case 5:
				p.spinner1Label.setText("Flag");
				p.spinner1.setEnabled(true);
				p.setMax1(255);
				p.spinner1.setValue(args[1]);
				break;
			case 6:
				p.spinner1Label.setText("Flag");
				p.spinner1.setEnabled(true);
				p.setMax1(255);
				p.spinner1.setValue(args[1]);
				break;
			case 7:
				break;
			case 8:
				p.dropdownTextLabel.setText("Text");
				p.dropdownText.setEnabled(true);
				for (int i=0; i<ROMText.MAX_POINTERS; i++) {
					String text = main.text.getText(i);
					int maxLength = text.length() - 1;
					if (maxLength < 0) maxLength = 0;
					if (maxLength > 30) maxLength = 30;
					p.dropdownText.addItem("[" + i + "] " + text);
				}
				p.dropdownText.setSelectedIndex(args[1] + (args[2] * 256));
				p.dropdownText.setWide(true);
				break;
			case 9:
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[1]);
				break;
			case 10:
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[1]);
				break;
			case 11:
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[1]);
				break;
			case 12:
				p.dropdown1Label.setText("PSI");
				p.dropdown1.setEnabled(true);
				for (int i=ROMItems.INDEX_PSI; i<256; i++) {
					p.dropdown1.addItem("[" + i + "] " + main.items.getItemName(i));
				}
				p.dropdown1.setSelectedIndex(args[1] - ROMItems.INDEX_PSI);
				
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[2]);
				break;
			case 13:
				p.dropdown1Label.setText("Item");
				p.dropdown1.setEnabled(true);
				for (int i=0; i<0xA8; i++) {
					p.dropdown1.addItem("[" + i + "] " + main.items.getItemName(i));
				}
				p.dropdown1.setSelectedIndex(args[1]);
				
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[2]);
				break;
			case 14:
				break;
			case 15:
				break;
			case 16:
				p.spinner1Label.setText("Flag");
				p.spinner1.setEnabled(true);
				p.setMax1(255);
				p.spinner1.setValue(args[1]);
				break;
			case 17:
				p.spinner1Label.setText("Flag");
				p.spinner1.setEnabled(true);
				p.setMax1(255);
				p.spinner1.setValue(args[1]);
				break;
			case 18:
				p.spinner1Label.setText("Flag");
				p.spinner1.setEnabled(true);
				p.setMax1(255);
				p.spinner1.setValue(args[1]);
				
				p.spinner2Label.setText("Line");
				p.spinner2.setEnabled(true);
				p.setMax2(maxLines());
				p.spinner2.setValue(args[2]);
				break;
			case 19:
				p.spinner1Label.setText("Counter");
				p.spinner1.setEnabled(true);
				p.setMax1(255);
				p.spinner1.setValue(args[1]);
				break;
			case 20:
				p.spinner1Label.setText("Counter");
				p.spinner1.setEnabled(true);
				p.setMax1(255);
				p.spinner1.setValue(args[1]);
				break;
			case 21:
				p.spinner1Label.setText("Counter");
				p.spinner1.setEnabled(true);
				p.setMax1(255);
				p.spinner1.setValue(args[1]);
				break;
			case 22:
				p.spinner1Label.setText("Counter");
				p.spinner1.setEnabled(true);
				p.setMax1(255);
				p.spinner1.setValue(args[1]);
				
				p.spinner2Label.setText("Number");
				p.spinner2.setEnabled(true);
				p.setMax2(255);
				p.spinner2.setValue(args[2]);
				
				p.spinner3Label.setText("Line");
				p.spinner3.setEnabled(true);
				p.setMax3(maxLines());
				p.spinner3.setValue(args[3]);
				break;
			case 23:
				p.spinner1Label.setText("Variable");
				p.spinner1.setEnabled(true);
				p.setMin1(0x1B);
				p.setMax1(0x1E);
				p.spinner1.setValue(args[1]);
				
				p.spinner2Label.setText("Value");
				p.spinner2.setEnabled(true);
				p.setMax2(255);
				p.spinner2.setValue(args[2]);
				break;
			case 24:
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[1]);
				break;
			case 25:
				p.dropdown1Label.setText("Character");
				p.dropdown1.setEnabled(true);
				for (String i : Info.characterNames) {
					p.dropdown1.addItem(i);
				}
				p.dropdown1.setSelectedIndex(args[1]);
				break;
			case 26:
				p.dropdown1Label.setText("Character");
				p.dropdown1.setEnabled(true);
				for (String i : Info.characterNames) {
					p.dropdown1.addItem(i);
				}
				p.dropdown1.setSelectedIndex(args[1]);
				
				p.spinner2Label.setText("Line");
				p.spinner2.setEnabled(true);
				p.setMax2(maxLines());
				p.spinner2.setValue(args[2]);
				break;
			case 27:
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[1]);
				break;
			case 28:
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[1]);
				break;
			case 29:
				int num29 = args[1] + (args[2] * 256);
				p.spinner1Label.setText("Number (0-65,535)");
				p.spinner1.setEnabled(true);
				p.setMax1((256 * 256) - 1);
				p.spinner1.setValue(num29);
				break;
			case 30:
				int num30 = args[1] + (args[2] * 256);
				p.spinner1Label.setText("Number (0-65,535)");
				p.spinner1.setEnabled(true);
				p.setMax1((256 * 256) - 1);
				p.spinner1.setValue(num30);
				
				p.spinner2Label.setText("Line");
				p.spinner2.setEnabled(true);
				p.setMax2(maxLines());
				p.spinner2.setValue(args[3]);
				break;
			case 31:
				break;
			case 32:
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[1]);
				break;
			case 33:
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[1]);
				break;
			case 34:
				p.dropdown1Label.setText("Item 1");
				p.dropdown1.setEnabled(true);
				for (int i=0; i<0xA8; i++) {
					p.dropdown1.addItem("[" + i + "] " + main.items.getItemName(i));
				}
				p.dropdown1.setSelectedIndex(args[1]);
				
				p.dropdown2Label.setText("Item 2");
				p.dropdown2.setEnabled(true);
				for (int i=0; i<0xA8; i++) {
					p.dropdown2.addItem("[" + i + "] " + main.items.getItemName(i));
				}
				p.dropdown2.setSelectedIndex(args[2]);
				
				p.dropdown3Label.setText("Item 3");
				p.dropdown3.setEnabled(true);
				for (int i=0; i<0xA8; i++) {
					p.dropdown3.addItem("[" + i + "] " + main.items.getItemName(i));
				}
				p.dropdown3.setSelectedIndex(args[3]);
				
				p.dropdown4Label.setText("Item 4");
				p.dropdown4.setEnabled(true);
				for (int i=0; i<0xA8; i++) {
					p.dropdown4.addItem("[" + i + "] " + main.items.getItemName(i));
				}
				p.dropdown4.setSelectedIndex(args[4]);
				
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[5]);
				break;
			case 35:
				p.dropdown1Label.setText("Item");
				p.dropdown1.setEnabled(true);
				for (int i=0; i<0xA8; i++) {
					p.dropdown1.addItem("[" + i + "] " + main.items.getItemName(i));
				}
				p.dropdown1.setSelectedIndex(args[1]);
				
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[2]);
				break;
			case 36:
				p.dropdown1Label.setText("Item");
				p.dropdown1.setEnabled(true);
				for (int i=0; i<0xA8; i++) {
					p.dropdown1.addItem("[" + i + "] " + main.items.getItemName(i));
				}
				p.dropdown1.setSelectedIndex(args[1]);
				
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[2]);
				break;
			case 37:
				p.dropdown1Label.setText("Item");
				p.dropdown1.setEnabled(true);
				for (int i=0; i<0xA8; i++) {
					p.dropdown1.addItem("[" + i + "] " + main.items.getItemName(i));
				}
				p.dropdown1.setSelectedIndex(args[1]);
				break;
			case 38:
				p.dropdown1Label.setText("Item");
				p.dropdown1.setEnabled(true);
				for (int i=0; i<0xA8; i++) {
					p.dropdown1.addItem("[" + i + "] " + main.items.getItemName(i));
				}
				p.dropdown1.setSelectedIndex(args[1]);
				
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[2]);
				break;
			case 39:
				p.dropdown1Label.setText("Item");
				p.dropdown1.setEnabled(true);
				for (int i=0; i<0xA8; i++) {
					p.dropdown1.addItem("[" + i + "] " + main.items.getItemName(i));
				}
				p.dropdown1.setSelectedIndex(args[1]);
				
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[2]);
				break;
			case 40:
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[1]);
				break;
			case 41:
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[1]);
				break;
			case 42:
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[1]);
				break;
			case 43:
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[1]);
				break;
			case 44:
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[1]);
				break;
			case 45:
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[1]);
				break;
			case 46:
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[1]);
				break;
			case 47:
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[1]);
				break;
			case 48:
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[1]);
				break;
			case 49:
				p.spinner1Label.setText("Slot");
				p.spinner1.setEnabled(true);
				p.setMax1(7);
				p.spinner1.setValue(args[1]);
				
				p.spinner2Label.setText("Line");
				p.spinner2.setEnabled(true);
				p.setMax2(maxLines());
				p.spinner2.setValue(args[2]);
				break;
			case 50:
				p.spinner1Label.setText("Number");
				p.spinner1.setEnabled(true);
				p.setMax1(255);
				p.spinner1.setValue(args[1]);
				break;
			case 51:
				p.dropdown1Label.setText("Character");
				p.dropdown1.setEnabled(true);
				for (String i : Info.characterNames) {
					p.dropdown1.addItem(i);
				}
				p.dropdown1.setSelectedIndex(args[1]);
				
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[2]);
				break;
			case 52:
				break;
			case 53:
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[1]);
				break;
			case 54:
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[1]);
				break;
			case 55:
				p.dropdownTextLabel.setText("Text");
				p.dropdownText.setEnabled(true);
				for (int i=0; i<ROMText.MAX_POINTERS; i++) {
					String text = main.text.getText(i);
					int maxLength = text.length() - 1;
					if (maxLength < 0) maxLength = 0;
					if (maxLength > 30) maxLength = 30;
					p.dropdownText.addItem("[" + i + "] " + text);
				}
				p.dropdownText.setSelectedIndex(args[1] + (args[2] * 256));
				p.dropdownText.setWide(true);
				
				p.spinner1Label.setText("Line (1)");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[3]);
				
				p.spinner2Label.setText("Line (2)");
				p.spinner2.setEnabled(true);
				p.setMax2(maxLines());
				p.spinner2.setValue(args[4]);
				break;
			case 56:
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[1]);
				break;
			case 57:
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[1]);
				break;
			case 58:
				p.spinner1Label.setText("Party Slot");
				p.spinner1.setEnabled(true);
				p.setMax1(7);
				p.spinner1.setValue(args[1]);
				
				p.spinner2Label.setText("Line");
				p.spinner2.setEnabled(true);
				p.setMax2(maxLines());
				p.spinner2.setValue(args[2]);
				break;
			case 59:
				p.dropdownTextLabel.setText("Object Type");
				p.dropdownText.setEnabled(true);
				for (String i : ObjectInfo.objectTypes) {
					p.dropdownText.addItem(i);
				}
				p.dropdownText.setSelectedIndex(args[1]);
				break;
			case 60:
				p.spinner1Label.setText("?");
				p.spinner1.setEnabled(true);
				p.setMax1(255);
				p.spinner1.setValue(args[1]);
				break;
			case 61:
				int num61_1 = args[1] + (args[2] * 256);
				p.spinner1Label.setText("X");
				p.spinner1.setEnabled(true);
				p.setMax1((256 * 256) - 1);
				p.spinner1.setValue(num61_1);
				
				int num61_2 = args[3] + (args[4] * 256);
				p.spinner2Label.setText("Y");
				p.spinner2.setEnabled(true);
				p.setMax2((256 * 256) - 1);
				p.spinner2.setValue(num61_2);
				break;
			case 62:
				//TODO
				break;
			case 63:
				p.spinner1Label.setText("Object");
				p.spinner1.setEnabled(true);
				p.setMax1(main.objects.getObjectsInArea(panelOptions.getArea()));
				p.spinner1.setValue(args[1]);
				break;
			case 64:
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[1]);
				break;
			case 65:
				break;
			case 66:
				p.dropdown1Label.setText("Character");
				p.dropdown1.setEnabled(true);
				for (String i : Info.characterNames) {
					p.dropdown1.addItem(i);
				}
				p.dropdown1.setSelectedIndex(args[1]);
				
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[2]);
				break;
			case 67:
				p.dropdown1Label.setText("Character");
				p.dropdown1.setEnabled(true);
				for (String i : Info.characterNames) {
					p.dropdown1.addItem(i);
				}
				p.dropdown1.setSelectedIndex(args[1]);
				
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[2]);
				break;
			case 68:
				p.spinner1Label.setText("Enemy Group");
				p.spinner1.setEnabled(true);
				p.setMax1(164);
				p.spinner1.setValue(args[1]);
				break;
			case 69:
				break;
			case 70:
				p.dropdown1Label.setText("Direction");
				p.dropdown1.setEnabled(true);
				for (String i : Info.directionNames) {
					p.dropdown1.addItem(i);
				}
				p.dropdown1.setSelectedIndex(args[1]);
				break;
			case 71:
				p.dropdown1Label.setText("Direction");
				p.dropdown1.setEnabled(true);
				for (String i : Info.directionNames) {
					p.dropdown1.addItem(i);
				}
				p.dropdown1.setSelectedIndex(args[1]);
				break;
			case 72:
				p.dropdown1Label.setText("Direction");
				p.dropdown1.setEnabled(true);
				for (String i : Info.directionNames) {
					p.dropdown1.addItem(i);
				}
				p.dropdown1.setSelectedIndex(args[1]);
				break;
			case 73:
				p.dropdown1Label.setText("Direction");
				p.dropdown1.setEnabled(true);
				for (String i : Info.directionNames) {
					p.dropdown1.addItem(i);
				}
				p.dropdown1.setSelectedIndex(args[1]);
				break;
			case 74:
				break;
			case 75:
				p.dropdown1Label.setText("Direction");
				p.dropdown1.setEnabled(true);
				for (String i : Info.directionNames) {
					p.dropdown1.addItem(i);
				}
				p.dropdown1.setSelectedIndex(args[1]);
				break;
			case 76:
				p.dropdown1Label.setText("Direction");
				p.dropdown1.setEnabled(true);
				for (String i : Info.directionNames) {
					p.dropdown1.addItem(i);
				}
				p.dropdown1.setSelectedIndex(args[1]);
				break;
			case 77:
				break;
			case 78:
				break;
			case 79:
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[1]);
				break;
			case 80:
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[1]);
				break;
			case 81:
				p.spinner1Label.setText("HP");
				p.spinner1.setEnabled(true);
				p.setMax1(255);
				p.spinner1.setValue(args[1]);
				break;
			case 82:
				p.enableCheckboxes();
				for (int i=0; i<p.checkboxes.length; i++) {
					p.checkboxes[i].setSelected(((args[1] / Math.pow(2, i)) % 2) != 0);
				}
				
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[2]);
				break;
			case 83:
				p.enableCheckboxes();
				for (int i=0; i<p.checkboxes.length; i++) {
					p.checkboxes[i].setSelected(((args[1] / Math.pow(2, i)) % 2) != 0);
				}
				break;
			case 84:
				p.spinner1Label.setText("Level");
				p.spinner1.setEnabled(true);
				p.setMax1(255);
				p.spinner1.setValue(args[1]);
				
				p.spinner2Label.setText("Line");
				p.spinner2.setEnabled(true);
				p.setMax2(maxLines());
				p.spinner2.setValue(args[2]);
				break;
			case 85:
				break;
			case 86:
				break;
			case 87:
				break;
			case 88:
				break;
			case 89:
				p.enableCheckboxes();
				for (int i=0; i<p.checkboxes.length; i++) {
					p.checkboxes[i].setSelected(((args[1] / Math.pow(2, i)) % 2) != 0);
				}
				break;
			case 90:
				p.dropdownTextLabel.setText("Music");
				p.dropdownText.setEnabled(true);
				for (String i : Info.musicNames) {
					p.dropdownText.addItem(i);
				}
				p.dropdownText.setSelectedIndex(args[1]);
				break;
			case 91:
				p.dropdownTextLabel.setText("Sound Effect");
				p.dropdownText.setEnabled(true);
				for (String i : Info.soundEffectNames1) {
					p.dropdownText.addItem(i);
				}
				p.dropdownText.setSelectedIndex(args[1]);
				break;
			case 92:
				p.dropdownTextLabel.setText("Sound Effect");
				p.dropdownText.setEnabled(true);
				for (String i : Info.soundEffectNames2) {
					p.dropdownText.addItem(i);
				}
				p.dropdownText.setSelectedIndex(args[1]);
				break;
			case 93:
				p.dropdownTextLabel.setText("Sound Effect");
				p.dropdownText.setEnabled(true);
				for (String i : Info.soundEffectNames3) {
					p.dropdownText.addItem(i);
				}
				p.dropdownText.setSelectedIndex(args[1]);
				break;
			case 94:
				break;
			case 95:
				break;
			case 96:
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[1]);
				break;
			case 97:
				p.spinner1Label.setText("PP");
				p.spinner1.setEnabled(true);
				p.setMax1(255);
				p.spinner1.setValue(args[1]);
				break;
			case 98:
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[1]);
				break;
			case 99:
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[1]);
				break;
			case 100:
				break;
			case 101:
				p.spinner1Label.setText("Line");
				p.spinner1.setEnabled(true);
				p.setMax1(maxLines());
				p.spinner1.setValue(args[1]);
				break;
			case 102:
				break;
			case 103:
				break;
			case 104:
				break;
			case 105:
				break;
			case 106:
				break;
			case 107:
				break;
			}
		}
		
	}
	
	private void exitSave() {
		final JOptionPane optionPane = new JOptionPane("<html>Save script data to ROM?<br/><br/><i>(Note: choosing 'no' will NOT<br/>discard your changes)</i></html>",
				JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION);
		final JDialog dialog = new JDialog(thisRef, "Save", true);
		dialog.setContentPane(optionPane);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.setResizable(false);
		
		optionPane.addPropertyChangeListener(
	    new PropertyChangeListener() {
	        public void propertyChange(PropertyChangeEvent event) {
	            String prop = event.getPropertyName();
	            if (dialog.isVisible() && (event.getSource() == optionPane) && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
	            	if (((Integer) optionPane.getValue()).intValue() == JOptionPane.YES_OPTION) {
						main.objects.save();
					}
	            	main.repaintAll();
	                dialog.dispose();
	                dispose();
	            }
	        }
	    });
		dialog.pack();
		dialog.setLocationRelativeTo(thisRef);
		dialog.setVisible(true);
	}
	
	private class MenuBar extends JMenuBar {
		
		private static final long serialVersionUID = 1L;
		
		public MenuBar() {
			//File menu
			JMenu menuFile = new JMenu("File");
			add(menuFile);
			
			JMenuItem itemSave = new JMenuItem("Save");
			menuFile.add(itemSave);
			itemSave.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						saveScript();
						main.objects.save();
						JOptionPane.showMessageDialog(thisRef, "Successfully saved!");
					}
				}
			);
			JMenuItem itemExit = new JMenuItem("Exit");
			menuFile.add(itemExit);
			itemExit.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						exitSave();
					}
				}
			);
			
			//Help menu
			JMenu menuHelp = new JMenu("Help");
			add(menuHelp);
			
			/*
			JMenuItem itemShortcuts = new JMenuItem("Keyboard Shortcuts");
			menuHelp.add(itemShortcuts);
			itemShortcuts.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						String text = "<html><b>RIGHT CLICK:</b> Select (copy) a tile from the chunk editor</html>\n" +
								"<html><b>1-4:</b> Select sub-palette 1 through 4</html>\n" +
								"<html><b>INSERT:</b> Place a tile from the alternate tileset (shouldn't need to be used very often)</html>";
						
						JOptionPane.showMessageDialog(thisRef, main.createScrollingLabel(text, true), "Keyboard Shortcuts", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			);
			*/
			JMenuItem itemAbout = new JMenuItem("About");
			menuHelp.add(itemAbout);
			itemAbout.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						String text = Info.aboutMessage;
						JOptionPane.showMessageDialog(thisRef, main.createScrollingLabel(text, false), "About", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			);
		}
		
	}
	
	class ApplyButtonListener implements ActionListener {
		
		public void actionPerformed(ActionEvent event) {
			final int selected = scriptList.getSelectedIndex();

			// TODO testing
			System.out.println("");
			System.out.println("--------  SCRIPT  --------");
			for (int[] l : script) {
				for (int i : l) {
					System.out.print(i + " ");
				}
				System.out.println();
			}
			
			if (selected == -1)
				return;
			
			int[] args = script.get(scriptList.getSelectedIndex());
			PanelArguments p = panelArguments;
			
			switch (args[0]) {
			case 0:
				break;
			case 1:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 2:
				//TODO
				break;
			case 3:
				break;
			case 4:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 5:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 6:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 7:
				break;
			case 8:
				args[1] = p.dropdownText.getSelectedIndex() % 256;
				args[2] = p.dropdownText.getSelectedIndex() / 256;
				break;
			case 9:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 10:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 11:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 12:
				args[1] = p.dropdown1.getSelectedIndex() + ROMItems.INDEX_PSI;
				args[2] = valueSpinner(p.spinner1);
				break;
			case 13:
				args[1] = p.dropdown1.getSelectedIndex();
				args[2] = valueSpinner(p.spinner1);
				break;
			case 14:
				break;
			case 15:
				break;
			case 16:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 17:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 18:
				args[1] = valueSpinner(p.spinner1);
				args[2] = valueSpinner(p.spinner2);
				break;
			case 19:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 20:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 21:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 22:
				args[1] = valueSpinner(p.spinner1);
				args[2] = valueSpinner(p.spinner2);
				args[3] = valueSpinner(p.spinner3);
				break;
			case 23:
				args[1] = valueSpinner(p.spinner1);
				args[2] = valueSpinner(p.spinner2);
				break;
			case 24:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 25:
				args[1] = p.dropdown1.getSelectedIndex();
				break;
			case 26:
				args[1] = p.dropdown1.getSelectedIndex();
				args[2] = valueSpinner(p.spinner2);
				break;
			case 27:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 28:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 29:
				args[1] = valueSpinner(p.spinner1) % 256;
				args[2] = valueSpinner(p.spinner1) / 256;
				break;
			case 30:
				args[1] = valueSpinner(p.spinner1) % 256;
				args[2] = valueSpinner(p.spinner1) / 256;
				args[3] = valueSpinner(p.spinner2);
				break;
			case 31:
				break;
			case 32:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 33:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 34:
				args[1] = p.dropdown1.getSelectedIndex();
				args[2] = p.dropdown2.getSelectedIndex();
				args[3] = p.dropdown3.getSelectedIndex();
				args[4] = p.dropdown4.getSelectedIndex();
				args[5] = valueSpinner(p.spinner1);
				break;
			case 35:
				args[1] = p.dropdown1.getSelectedIndex();
				args[2] = valueSpinner(p.spinner1);
				break;
			case 36:
				args[1] = p.dropdown1.getSelectedIndex();
				args[2] = valueSpinner(p.spinner1);
				break;
			case 37:
				args[1] = p.dropdown1.getSelectedIndex();
				break;
			case 38:
				args[1] = p.dropdown1.getSelectedIndex();
				args[2] = valueSpinner(p.spinner1);
				break;
			case 39:
				args[1] = p.dropdown1.getSelectedIndex();
				args[2] = valueSpinner(p.spinner1);
				break;
			case 40:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 41:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 42:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 43:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 44:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 45:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 46:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 47:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 48:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 49:
				args[1] = valueSpinner(p.spinner1);
				args[2] = valueSpinner(p.spinner2);
				break;
			case 50:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 51:
				args[1] = p.dropdown1.getSelectedIndex();
				args[2] = valueSpinner(p.spinner1);
				break;
			case 52:
				break;
			case 53:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 54:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 55:
				args[1] = p.dropdownText.getSelectedIndex() % 256;
				args[2] = p.dropdownText.getSelectedIndex() / 256;
				args[3] = valueSpinner(p.spinner1);
				args[4] = valueSpinner(p.spinner2);
				break;
			case 56:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 57:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 58:
				args[1] = valueSpinner(p.spinner1);
				args[2] = valueSpinner(p.spinner2);
				break;
			case 59:
				args[1] = p.dropdown1.getSelectedIndex();
				break;
			case 60:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 61:
				args[1] = valueSpinner(p.spinner1) % 256;
				args[2] = valueSpinner(p.spinner1) / 256;
				args[3] = valueSpinner(p.spinner2) % 256;
				args[4] = valueSpinner(p.spinner2) / 256;
				break;
			case 62:
				//TODO
				break;
			case 63:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 64:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 65:
				break;
			case 66:
				args[1] = p.dropdown1.getSelectedIndex();
				args[2] = valueSpinner(p.spinner1);
				break;
			case 67:
				args[1] = p.dropdown1.getSelectedIndex();
				args[2] = valueSpinner(p.spinner1);
				break;
			case 68:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 69:
				break;
			case 70:
				args[1] = p.dropdown1.getSelectedIndex();
				break;
			case 71:
				args[1] = p.dropdown1.getSelectedIndex();
				break;
			case 72:
				args[1] = p.dropdown1.getSelectedIndex();
				break;
			case 73:
				args[1] = p.dropdown1.getSelectedIndex();
				break;
			case 74:
				break;
			case 75:
				args[1] = p.dropdown1.getSelectedIndex();
				break;
			case 76:
				args[1] = p.dropdown1.getSelectedIndex();
				break;
			case 77:
				break;
			case 78:
				break;
			case 79:
				args[1] = p.dropdown1.getSelectedIndex();
				break;
			case 80:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 81:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 82:
				for (int i=0; i<p.checkboxes.length; i++) {
					if (p.checkboxes[i].isSelected()) {
						args[1] += Math.pow(2, i);
					}
				}
				args[2] = valueSpinner(p.spinner1);
				break;
			case 83:
				for (int i=0; i<p.checkboxes.length; i++) {
					if (p.checkboxes[i].isSelected()) {
						args[1] += Math.pow(2, i);
					}
				}
				break;
			case 84:
				args[1] = valueSpinner(p.spinner1);
				args[2] = valueSpinner(p.spinner2);
				break;
			case 85:
				break;
			case 86:
				break;
			case 87:
				break;
			case 88:
				break;
			case 89:
				for (int i=0; i<p.checkboxes.length; i++) {
					if (p.checkboxes[i].isSelected()) {
						args[1] += Math.pow(2, i);
					}
				}
				break;
			case 90:
				args[1] = p.dropdownText.getSelectedIndex();
				break;
			case 91:
				args[1] = p.dropdownText.getSelectedIndex();
				break;
			case 92:
				args[1] = p.dropdownText.getSelectedIndex();
				break;
			case 93:
				args[1] = p.dropdownText.getSelectedIndex();
				break;
			case 94:
				break;
			case 95:
				break;
			case 96:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 97:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 98:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 99:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 100:
				break;
			case 101:
				args[1] = valueSpinner(p.spinner1);
				break;
			case 102:
				break;
			case 103:
				break;
			case 104:
				break;
			case 105:
				break;
			case 106:
				break;
			case 107:
				break;
			}
			updateScript();
			scriptList.setSelectedIndex(selected);
			
			saveScript();
			loadScript(panelOptions.getArea(), panelOptions.getObject());
			
			//TODO testing
			System.out.println();
			for (int[] l : script) {
				for (int i : l) {
					System.out.print(i + " ");
				}
				System.out.println();
			}
		}
	}
	
	private static final int valueSpinner(JSpinner spinner) {
		return (Integer) spinner.getValue();
	}
	
}
