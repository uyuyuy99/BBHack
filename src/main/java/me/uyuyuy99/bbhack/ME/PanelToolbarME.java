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

public class PanelToolbarME extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private MainMenu main;
	private MapEditor ME;
	
	FlowLayout layout;
	
	JLabel paletteLabel;
	JLabel tileset1Label;
	JLabel tileset2Label;
	JLabel areaLabel;
	
	JComboBox<String> paletteDropdown;
	JComboBox<String> tileset1Dropdown;
	JComboBox<String> tileset2Dropdown;
	JComboBox<String> areaDropdown;
	
	public PanelToolbarME(MainMenu instance, MapEditor MEInstance) {
		main = instance;
		ME = MEInstance;
		
		layout = new FlowLayout();
		setLayout(layout);
		
		paletteLabel = new JLabel(" Palette:", FlowLayout.LEFT);
		tileset1Label = new JLabel(" Tileset 1:", FlowLayout.LEFT);
		tileset2Label = new JLabel(" Tileset 2:", FlowLayout.LEFT);
		areaLabel = new JLabel(" Area: ", FlowLayout.LEFT);
		
		paletteDropdown = new JComboBox<String>();
		tileset1Dropdown = new JComboBox<String>();
		tileset2Dropdown = new JComboBox<String>();
		areaDropdown = new JComboBox<String>();
		
		add(paletteLabel);
		add(paletteDropdown);
		add(tileset1Label);
		add(tileset1Dropdown);
		add(tileset2Label);
		add(tileset2Dropdown);
		add(areaLabel);
		add(areaDropdown);
		
		for (int i=0; i<32; i++) {
			paletteDropdown.addItem("" + i);
			tileset1Dropdown.addItem(Info.tilesetNames[i]);
			tileset2Dropdown.addItem(Info.tilesetNames[i]);
			areaDropdown.addItem("0x" + String.format("%02X", i * 2));
			areaDropdown.addItem("0x" + String.format("%02X", i * 2 + 1));
		}
		
		update();
		
		paletteDropdown.addItemListener(
			new ItemListener() {
				public void itemStateChanged(ItemEvent event) {
					int sectorX = ME.panelChunkSelect.selectX / 4;
					int sectorY = ME.panelChunkSelect.selectY / 4;
					main.map.sectorPalette[(sectorY * 64) + sectorX] = paletteDropdown.getSelectedIndex();
					repaintAll();
				}
			}
		); tileset1Dropdown.addItemListener(
			new ItemListener() {
				public void itemStateChanged(ItemEvent event) {
					int sectorX = ME.panelChunkSelect.selectX / 4;
					int sectorY = ME.panelChunkSelect.selectY / 4;
					main.map.sectorTileset1[(sectorY * 64) + sectorX] = tileset1Dropdown.getSelectedIndex();
					repaintAll();
				}
			}
		); tileset2Dropdown.addItemListener(
			new ItemListener() {
				public void itemStateChanged(ItemEvent event) {
					int sectorX = ME.panelChunkSelect.selectX / 4;
					int sectorY = ME.panelChunkSelect.selectY / 4;
					main.map.sectorTileset2[(sectorY * 64) + sectorX] = tileset2Dropdown.getSelectedIndex();
					repaintAll();
				}
			}
		); areaDropdown.addItemListener(
			new ItemListener() {
				public void itemStateChanged(ItemEvent event) {
					int sectorX = ME.panelChunkSelect.selectX / 4;
					int sectorY = ME.panelChunkSelect.selectY / 4;
					main.map.sectorArea[(sectorY * 64) + sectorX] = areaDropdown.getSelectedIndex();
					repaintAll();
				}
			}
		);
		
		ME.panelMap.addMouseListener(
			new MouseListener() {
				public void mousePressed(MouseEvent event) {
					update();
				}
				public void mouseClicked(MouseEvent event) {
					//Obese
				} public void mouseEntered(MouseEvent event) {
					//Parrots
				} public void mouseExited(MouseEvent event) {
					//Like
				} public void mouseReleased(MouseEvent event) {
					//Bacon
				}
			}
		);
	}
	
	private void update() {
		int sectorX = ME.panelChunkSelect.selectX / 4;
		int sectorY = ME.panelChunkSelect.selectY / 4;
		
		paletteDropdown.setSelectedIndex(main.map.sectorPaletteGet(sectorX, sectorY));
		tileset1Dropdown.setSelectedIndex(main.map.sectorTileset1Get(sectorX, sectorY));
		tileset2Dropdown.setSelectedIndex(main.map.sectorTileset2Get(sectorX, sectorY));
		areaDropdown.setSelectedIndex(main.map.sectorAreaGet(sectorX, sectorY));
	}
	
	private void repaintAll() {
		ME.panelMap.clearGraphicsCache();
		ME.panelMap.repaint();
		ME.panelChunkSelect.repaint();
	}
	
}
