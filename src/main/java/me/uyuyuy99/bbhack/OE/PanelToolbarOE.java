package me.uyuyuy99.bbhack.OE;

import me.uyuyuy99.bbhack.MainMenu;
import me.uyuyuy99.bbhack.types.EBObjects.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class PanelToolbarOE extends JPanel {

	private MainMenu main;
	private ObjectEditor OE;

	FlowLayout layout;

	JLabel xLabel;
	JLabel yLabel;
	JLabel typeLabel;
	JLabel dirLabel;
	JButton gotoObject;

	JComboBox<String> typeDropdown;
	JComboBox<String> dirDropdown;

	public PanelToolbarOE(MainMenu instance, ObjectEditor OEInstance) {
		main = instance;
		OE = OEInstance;

		layout = new FlowLayout();
		setLayout(layout);

		xLabel = new JLabel(" X: ", SwingConstants.CENTER);
		yLabel = new JLabel(" Y: ", SwingConstants.CENTER);
		typeLabel = new JLabel(" Type: ", SwingConstants.CENTER);
		dirLabel = new JLabel(" Direction: ", SwingConstants.CENTER);

		typeDropdown = new JComboBox<String>();
		dirDropdown = new JComboBox<String>();

		gotoObject = new JButton("Go to object");

		add(gotoObject);
		add(xLabel);
		add(yLabel);
		add(typeLabel);
		add(typeDropdown);
		add(dirLabel);
		add(dirDropdown);

		for (int i = 0, b = 0; b < 0x40;){
			boolean a = i < EBObject.OBJTYPE.values().length;
			boolean bb = false;
			if (a) {
				int actualValue = EBObject.OBJTYPE.values()[i].getValue();
				bb = actualValue == b;
				if (bb) {
					typeDropdown.addItem(EBObject.OBJTYPE.values()[i].name());
					i++;
				}
			}
			if (!(a && bb)){
				typeDropdown.addItem("UNK"+b);
			}
			b++;
		}

		for (int i = 0; i < EBObject.DIRECTION.values().length; i++) {
			dirDropdown.addItem(EBObject.DIRECTION.values()[i].name());
		}
		
		update();

		dirDropdown.addItemListener(
			new ItemListener() {
				public void itemStateChanged(ItemEvent event) {
					OE.myObject.dir = EBObject.DIRECTION.values()[dirDropdown.getSelectedIndex()];
					repaintAll();
				}
			}
		);

		gotoObject.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					OE.gotoObj();
				}
			}
		);
	}

	public void objUpdate(){
		update();
	}

	private void update() {
		xLabel.setText(" X: "+OE.myObject.x);
		yLabel.setText(" Y: "+(OE.myObject.y-0x80));
		typeDropdown.setSelectedIndex(OE.myObject.type.getValue());
		dirDropdown.setSelectedIndex(OE.myObject.dir.ordinal());
	}

	private void repaintAll() {
		OE.mapEditor.clearGraphicsCache();
		//redraw object if uses sprite w/ directions
		if (OE.myObject instanceof EBNPC){
			EBNPC npc = (EBNPC) OE.myObject;
			npc.getDirectionFromMain(main.sprites.Definitions);
		}
		OE.mapEditor.repaint();
	}
	
}
