package me.uyuyuy99.bbhack;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import me.uyuyuy99.bbhack.CE.ChunkEditor;
import me.uyuyuy99.bbhack.ME.MapEditor;
import me.uyuyuy99.bbhack.SE.ScriptEditor;
import me.uyuyuy99.bbhack.rom.*;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class MainMenu extends JFrame {
	
	private static final long serialVersionUID = 4926338866347121535L;
	
	public RomFileIO rom;
	
	public ROMPalettes palettes;
	public ROMGraphics gfx;
	public ROMMapSectors map;
	public ROMObjects objects;
	public ROMItems items;
	public ROMText text;
	
	private JPanel panel;
	private GridBagLayout layout;
	private GridBagConstraints c;
	
//	private EnemyGroupEditor EGE;
	private MapEditor ME;
	private ChunkEditor CE;
	private ScriptEditor SE;
	
//	private JButton buttonEnemyGroups;
	private JButton buttonMap;
	private JButton buttonChunks;
	private JButton buttonScripts;
	
	public MainMenu() {
		super("BB Hack v" + Info.version);
		
		rom = new RomFileIO();
		
		layout = new GridBagLayout();
		panel = new JPanel(layout);
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(layout);
		
		//Default constraint settings
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(4, 4, 4, 4);
		
		//Menu bar
		setJMenuBar(new MenuBar());
		
		/*
		c.gridx = 0; c.gridy = 1;
		buttonEnemyGroups = new JButton("Enemy Groups");
		buttonEnemyGroups.setToolTipText("Edit the sets of enemies faced during random encounters.");
		panel.add(buttonEnemyGroups, c);
		buttonEnemyGroups.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (rom.rompath == null) { //Don't open if no ROM is loaded
						JOptionPane.showMessageDialog(panel, "You need to load a ROM first, silly.", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					if (EGE == null) {
						EGE = new EnemyGroupEditor();
						EGE.setVisible(true);
						EGE.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
						EGE.setSize(256, 512);
					} else {
						if (!EGE.isVisible()) {
							EGE = new EnemyGroupEditor();
							EGE.setVisible(true);
							EGE.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
							EGE.setSize(256, 512);
						}
					}
				}
			}
		);
		*/
		
		c.gridx = 0; c.gridy = 2;
		buttonMap = new JButton("Map Editor");
		buttonMap.setToolTipText("Edit the EB0 world map.");
		panel.add(buttonMap, c);
		buttonMap.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (rom.rompath == null) { //Don't open if no ROM is loaded
						JOptionPane.showMessageDialog(panel, "You need to load a ROM first, silly.", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					if (ME == null) {
						ME = new MapEditor(MainMenu.this);
						ME.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					} else {
						if (!ME.isVisible()) {
							ME = new MapEditor(MainMenu.this);
							ME.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
						}
					}
				}
			}
		);
		
		c.gridx = 0; c.gridy = 3;
		buttonChunks = new JButton("Chunk Editor");
		buttonChunks.setToolTipText("Edit the composition of the 64x64 tiles used in map editing.");
		panel.add(buttonChunks, c);
		buttonChunks.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (rom.rompath == null) { //Don't open if no ROM is loaded
						JOptionPane.showMessageDialog(panel, "You need to load a ROM first, silly.", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					if (CE == null) {
						CE = new ChunkEditor(MainMenu.this);
						CE.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					} else {
						if (!CE.isVisible()) {
							CE = new ChunkEditor(MainMenu.this);
							CE.useAlternateTileset = false;
							CE.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
						}
					}
				}
			}
		);
		
		c.gridx = 0; c.gridy = 4;
		buttonScripts = new JButton("Script Editor");
		buttonScripts.setToolTipText("Edit the script (made up of control codes) of every object in the game.");
		panel.add(buttonScripts, c);
		buttonScripts.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					/* if (rom.rompath == null) { //Don't open if no ROM is loaded
						JOptionPane.showMessageDialog(panel, "You need to load a ROM first, silly.", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					} */
					if (SE == null) {
						SE = new ScriptEditor(MainMenu.this);
						SE.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					} else {
						if (!SE.isVisible()) {
							SE = new ScriptEditor(MainMenu.this);
							SE.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
						}
					}
				}
			}
		);

//		c.gridx = 0; c.gridy = 4;
//		JButton buttonDebug = new JButton("Debug");
//		panel.add(buttonDebug, c);
//		buttonDebug.addActionListener((event) -> {
//			if (rom.rompath != null) {
//				System.out.println();
//			}
//		});
		
		//Icon iconEnemyGroups = new ImageIcon(getClass().getResource("/icons/enemygroup_editor.png"));
		//buttonEnemyGroups.setIcon(iconEnemyGroups);
		
		Icon iconMap = new ImageIcon(Info.class.getResource("/icons/map_editor.png"));
		buttonMap.setIcon(iconMap);
		
		Icon iconChunks = new ImageIcon(Info.class.getResource("/icons/chunk_editor.png"));
		buttonChunks.setIcon(iconChunks);
		
		//Set program icon
		Image windowIcon1 = new ImageIcon(Info.class.getResource("/icons/main1.png")).getImage();
		Image windowIcon2 = new ImageIcon(Info.class.getResource("/icons/main2.png")).getImage();
		ArrayList<Image> windowIcons = new ArrayList<Image>();
		windowIcons.add(windowIcon1);
		windowIcons.add(windowIcon2);
		this.setIconImages(windowIcons);
		
		KeyEventPostProcessor pp = new KeyEventPostProcessor() {
		    public boolean postProcessKeyEvent(KeyEvent event) {
		    	int key = event.getKeyCode();
		    	if (key == 155) { //INSERT
		    		if (CE != null) {
		    			if (CE.isVisible() && CE.isFocused()) {
		    				CE.keyInsert();
		    			}
		    		}
		    	} if (key == 49 || key == 50 || key == 51 || key == 52) { //1-4
		    		if (CE != null) {
		    			if (CE.isVisible() && CE.isFocused()) {
		    				CE.keyNumber(key - 49);
		    			}
		    		}
		    	}
		        return true;
		    }
		};
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(pp);
		
		// Auto-load rom for quick testing
//		loadROM(new File("C:/Users/Reed/Documents/EB0.nes"));
	}
	
	private void loadROM(File path) {
		rom.load(path);
		
		palettes = new ROMPalettes(this);
		gfx = new ROMGraphics(this);
		map = new ROMMapSectors(this);
		objects = new ROMObjects(this);
		items = new ROMItems(this);
		text = new ROMText(this);
		
		repaintAll();
	}
	
	public void repaintAll() {
		if (ME != null) {
			ME.clearGraphicsCache();
			if (ME.isVisible()) ME.repaintAll();
		} if (CE != null) {
			if (CE.isVisible()) CE.repaintAll();
		}
	}
	
	public JScrollPane createScrollingLabel(String text, boolean shorter) {
		int emptyLine = new JLabel("newline").getPreferredSize().height;
		JPanel labels = new JPanel();
		labels.setLayout(new BoxLayout(labels, 1));
		text = text.replaceAll("\n\n", "\nnewline\n");
		StringTokenizer st = new StringTokenizer(text, "\n");
		
		while (st.hasMoreTokens()) {
			JLabel temp = new JLabel(st.nextToken());
			if (temp.getText().equals("newline"))
			labels.add(Box.createVerticalStrut(emptyLine));
			else
			labels.add(temp);
		}
		
		JScrollPane out = new JScrollPane(labels, 22, 31);
		if (shorter) out.setPreferredSize(new Dimension(out.getPreferredSize().width + 20, 130));
		else out.setPreferredSize(new Dimension(out.getPreferredSize().width + 20, 220));
		return out;
	}
	
	public void openCEFromMap(int tileNum, boolean altTile, int palette, int tileset1, int tileset2) {
		boolean fullOpen = false;
		if (CE == null)
			fullOpen = true;
		if (CE != null) {
			if (!CE.isVisible()) {
				fullOpen = true;
			}
		}
		
		if (fullOpen) {
			CE = new ChunkEditor(MainMenu.this);
			CE.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		} else {
			CE.requestFocus();
		}
		
		CE.openFromMap(tileNum, altTile, palette, tileset1, tileset2);
	}
	
	public String getTextFromPointer(int pointer) {
		String string = "";
		
		int i = 0;
		while (true) {
			int curByte = rom.get(pointer + i);
			if (curByte == 0 || curByte == 255) break;
			
			if (curByte >= 0x80) {
				string += Character.toString((char) (curByte - Info.CHAR_OFFSET));
			} else {
				if (curByte == 1) {
					string += " / ";
				} else if (curByte == 0x21) {
					string += "[?]";
					i += 2;
				} else if (curByte == 0x23) {
					string += "####";
					i += 4;
				}
			}
			i++;
		}
		
		return string;
	}
	
	private class MenuBar extends JMenuBar {
		
		private static final long serialVersionUID = 1L;
		
		public MenuBar() {
			//File menu
			JMenu menuFile = new JMenu("File");
			add(menuFile);
			
			JMenuItem itemLoad = new JMenuItem("Load");
			menuFile.add(itemLoad);
			itemLoad.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						final JFileChooser fileChooser = new JFileChooser();
						int returnVal = fileChooser.showOpenDialog(MainMenu.this);
						
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							loadROM(fileChooser.getSelectedFile());
						}
					}
				}
			);
			JMenuItem itemExit = new JMenuItem("Exit");
			menuFile.add(itemExit);
			itemExit.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						System.exit(0);
					}
				}
			);
			
			//Help menu
			JMenu menuHelp = new JMenu("Help");
			add(menuHelp);
			
			JMenuItem itemAbout = new JMenuItem("About");
			menuHelp.add(itemAbout);
			itemAbout.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						String text = Info.aboutMessage;
						JScrollPane pane = createScrollingLabel(text, false);
						JOptionPane.showMessageDialog(MainMenu.this, pane, "About", JOptionPane.PLAIN_MESSAGE);
					}
				}
			);
			
			JMenuItem itemForum = new JMenuItem("Forum Thread");
			menuHelp.add(itemForum);
			itemForum.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						try {
							Desktop.getDesktop().browse(new URI(Info.forumThread));
						} catch (IOException e) {
							e.printStackTrace();
						} catch (URISyntaxException e) {
							e.printStackTrace();
						}
					}
				}
			);
		}
		
	}
	
}
