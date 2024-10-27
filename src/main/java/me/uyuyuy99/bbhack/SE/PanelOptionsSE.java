package me.uyuyuy99.bbhack.SE;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.*;

import me.uyuyuy99.bbhack.MainMenu;

public class PanelOptionsSE extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	MainMenu main;
	
	final String freeSpaceMessage = "Free Space in Current Bank: ";
	
	JComboBox<String> areaList = new JComboBox<String>();
	JComboBox<String> objectList = new JComboBox<String>();
	JLabel freeSpace;
	
	int lastArea;
	int lastObject;
	
	public PanelOptionsSE(MainMenu instance) {
		main = instance;
		
		BorderLayout layout = new BorderLayout();
		setLayout(layout);
		
		JPanel objectPanel = new JPanel();
		objectPanel.add(areaList);
		objectPanel.add(objectList);
		add(objectPanel, BorderLayout.LINE_START);
		
		freeSpace = new JLabel();
		freeSpace.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		add(freeSpace, BorderLayout.LINE_END);
		
		for (int i=1; i<64; i++) {
			if (main.objects.getObjectsInArea(i) != 0) {
				areaList.addItem("Area " + i);
			}
		}
	}
	
	void setArea(int area) {
		objectList.removeAllItems();
		for (int i=0; i<main.objects.getObjectsInArea(area); i++) {
			if (main.objects.objects[area][i].editable) {
				objectList.addItem("Object " + i);
			}
		}
	}
	
	int getArea() {
		return new Integer(areaList.getSelectedItem().toString().substring(5));
	}
	
	int getObject() {
		return new Integer(objectList.getSelectedItem().toString().substring(7));
	}
	
	void resetLast() {
		lastArea = getArea();
		lastObject = getObject();
	}
}
