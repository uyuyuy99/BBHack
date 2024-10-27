package me.uyuyuy99.bbhack.ME;

import javax.swing.*;

import me.uyuyuy99.bbhack.Info;
import me.uyuyuy99.bbhack.MainMenu;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class PanelDebugME extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private MainMenu main;
	private MapEditor ME;
	
	private FlowLayout layout;
	
	JLabel xLabel;
	JLabel yLabel;
	JLabel areaLabel;
	
	private String b1 = "<html><b>";
	private String b2 = "</b> ";
	private String b3 = "</html>";
	
	public PanelDebugME(MainMenu instance, MapEditor MEInstance) {
		main = instance;
		ME = MEInstance;
		
		layout = new FlowLayout(FlowLayout.LEFT, 8, 1);
		setLayout(layout);
		
		setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0, 0, 0, 50)));
		
		xLabel = new JLabel();
		yLabel = new JLabel();
		areaLabel = new JLabel();
		
		add(xLabel);
		add(yLabel);
		add(areaLabel);
		
		ME.panelMap.addMouseMotionListener(new MouseMotionListener() {
			public void mouseMoved(MouseEvent event) {
				int x = (event.getX() / 16) + (ME.panelMap.viewX * 4);
				int y = (event.getY() / 16) + (ME.panelMap.viewY * 4);
				
				xLabel.setText(b1 + "x:" + b2 + x + b3);
				yLabel.setText(b1 + "y:" + b2 + y + b3);
				areaLabel.setText(b1 + "Area:" + b2 + main.map.sectorAreaGet(x / 16, y / 16) + b3);
			}
			public void mouseDragged(MouseEvent event) {
				// yeah
			}
		});
	}
}
