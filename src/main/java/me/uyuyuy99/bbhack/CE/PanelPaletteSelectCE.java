package me.uyuyuy99.bbhack.CE;

import java.awt.*;

import javax.swing.*;

import me.uyuyuy99.bbhack.Info;

public class PanelPaletteSelectCE extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private GridBagLayout layout;
	private GridBagConstraints c;
	
	JComboBox<String> palette;
	JCheckBox altCheckbox;
	JComboBox<String> altDropdown;
	
	public PanelPaletteSelectCE() {
		layout = new GridBagLayout();
		c = new GridBagConstraints();
		setLayout(layout);
		
		palette = new JComboBox<String>();
		altCheckbox = new JCheckBox("Preview alternate tileset: ");
		altDropdown = new JComboBox<String>();
		
		c.insets = new Insets(0, 0, 0, 48);
		c.anchor = GridBagConstraints.WEST;
		add(palette, c);
		c.insets = new Insets(4, 0, 4, 0);
		c.weightx = 0;
		c.anchor = GridBagConstraints.CENTER;
		add(altCheckbox, c);
		add(altDropdown, c);
		
		altCheckbox.setEnabled(false);
		altDropdown.setEnabled(false);
		
		//Palettes
		for (int i=0; i<4; i++) {
			palette.addItem("Palette " + i);
		}
		
		//Alternate tilesets
		for (int i=0; i<32; i++) {
			altDropdown.addItem(Info.tilesetNames[i]);
		}
		
		setVisible(true);
	}
	
}
