package me.uyuyuy99.bbhack.CE;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import javax.swing.*;

import me.uyuyuy99.bbhack.Info;

public class PanelOptionsCE extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private GridBagLayout layout;
	private GridBagConstraints c;
	
	JComboBox<String> tileset;
	JComboBox<String> palette64;
	
	public PanelOptionsCE() {
		layout = new GridBagLayout();
		c = new GridBagConstraints();
		setLayout(layout);
		
		tileset = new JComboBox<String>();
		palette64 = new JComboBox<String>();
		JLabel tilesetLabel = new JLabel("Tileset: ");
		JLabel palette64Label = new JLabel("64x64 Palette: ");
		JLabel altLabel = new JLabel(" = tile uses alternate tileset (tile graphics change if loaded tilesets change)");
		altLabel.setIcon(new ImageIcon(getRedImage()));
		
		for (int i=0; i<32; i++) {
			tileset.addItem(Info.tilesetNames[i]);
		}
		for (int i=0; i<32; i++) {
			palette64.addItem("" + i);
		}
		
		c.gridx = 0; c.gridy = 0;
		c.gridwidth = 3;
		add(altLabel, c);
		c.gridwidth = 1;
		
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(24, 4, 6, 4);
		c.gridx = 0; c.gridy = 1;
		add(tilesetLabel, c);
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(24, 4, 6, 4);
		c.gridx = 1; c.gridy = 1;
		add(tileset, c);
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(6, 4, 6, 4);
		c.gridx = 0; c.gridy = 2;
		add(palette64Label, c);
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(6, 4, 6, 4);
		c.gridx = 1; c.gridy = 2;
		add(palette64, c);
	}
	
	private BufferedImage getRedImage() {
		int[] pixels = new int[1024 * 3];
		
		for (int j=0; j<pixels.length; j+=3) {
				pixels[j] = 255;
				pixels[j+1] = 0;
				pixels[j+2] = 0;
		}
		BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
		WritableRaster raster = image.getRaster();
		raster.setPixels(0, 0, 32, 32, pixels);
		return image;
	}

}
