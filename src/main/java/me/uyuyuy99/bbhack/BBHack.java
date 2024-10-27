package me.uyuyuy99.bbhack;

import javax.swing.*;

import me.uyuyuy99.bbhack.SE.BBScript;

public class BBHack {
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		UIManager.put("DesktopPaneUI", "javax.swing.plaf.basic.BasicDesktopPaneUI");
		
		MainMenu gui = new MainMenu();
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.setSize(256, 512);
		gui.setVisible(true);
		gui.setLocationRelativeTo(null);
		
		BBScript.test();
	}
	
}
