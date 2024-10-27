package me.uyuyuy99.bbhack;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;

public class EnemyGroupEditor extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private JPanel panel;
	private GridBagLayout layout;
	private GridBagConstraints c;
	
	private JComboBox<String> dropdownGroupList;
	
	private String[] groupList;
	
	public EnemyGroupEditor() {
		super("Enemy Groups Editor");
		
		layout = new GridBagLayout();
		panel = new JPanel(layout);
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(layout);
		
		//Default constraint settings
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(8, 8, 8, 8);
		
		
		
		// ------ Menu Bar ------ //
		JMenuBar menu = new JMenuBar();
		
		
		//File menu
		JMenu menuFile = new JMenu("File");
		menu.add(menuFile);
		JMenuItem itemExit = new JMenuItem("Exit");
		menuFile.add(itemExit);
		
		
		//Help menu
		JMenu menuHelp = new JMenu("Help");
		menu.add(menuHelp);
		JMenuItem itemAbout = new JMenuItem("About");
		menuHelp.add(itemAbout);
		
		
		setJMenuBar(menu);
		// --------------------- //
		
		
		
		c.gridx = 0; c.gridy = 0;
		initGroupList();
		dropdownGroupList = new JComboBox<String>(groupList);
		dropdownGroupList.setToolTipText("Edit the sets of enemies faced during random encounters.");
		panel.add(dropdownGroupList, c);
		
		//Set program icon
		Image windowIcon1 = new ImageIcon(getClass().getResource("/icons/main1.png")).getImage();
		Image windowIcon2 = new ImageIcon(getClass().getResource("/icons/main2.png")).getImage();
		ArrayList<Image> windowIcons = new ArrayList<Image>();
		windowIcons.add(windowIcon1);
		windowIcons.add(windowIcon2);
		this.setIconImages(windowIcons);
	}
	
	private void initGroupList() {
		groupList = new String[165];
		for (int i=0; i < 165; i++) {
			groupList[i] = "Enemy Group " + i;
		}
	}
}
