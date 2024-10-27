package me.uyuyuy99.bbhack.CE;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import me.uyuyuy99.bbhack.Info;
import me.uyuyuy99.bbhack.MainMenu;

public class ChunkEditor extends JFrame {
	
	public MainMenu main;
	private ChunkEditor thisRef;
	
	private static final long serialVersionUID = 1L;
	
	private JDesktopPane desktop;
	
	InternalChunkSelectCE internalChunkSelect;
	JInternalFrame internalChunkView;
	JInternalFrame internalTileSelect;
	JInternalFrame internalOptions;
	
	PanelChunkSelectCE panelChunkSelect;
	PanelChunkView panelChunkView;
	PanelTileSelectCE panelTileSelect;
	PanelOptionsCE panelOptions;
	
	PanelPaletteSelectCE panelPaletteSelect;
	
	public int selectedPalette;
	public int selectedTileset1;
	public int selectedTileset2;
	
	public boolean useAlternateTileset;
	
	public ChunkEditor(MainMenu instance) {
		super("Chunk Editor");
		main = instance;
		thisRef = this;
		
		setSize(832, 512);
		setVisible(true);
		setLocationRelativeTo(null);
		
		selectedPalette = 4;
		selectedTileset1 = 10;
		selectedTileset2 = 3;
		
		useAlternateTileset = false;
		
		desktop = new JDesktopPane();
		desktop.setBackground(new Color(0.9F, 0.9F, 0.9F));
		add(desktop, BorderLayout.CENTER);
		
		//Chunk selector setup
		panelChunkSelect = new PanelChunkSelectCE(main, this);
		internalChunkSelect = new InternalChunkSelectCE(panelChunkSelect);
		internalChunkSelect.show();
		internalChunkSelect.setFrameIcon(null);
		internalChunkSelect.setTitle("Chunk Selector");
		internalChunkSelect.add(panelChunkSelect);
		((BasicInternalFrameUI) internalChunkSelect.getUI()).setNorthPane(null);
		internalChunkSelect.setBorder(null);
		desktop.add(internalChunkSelect);
		
		//Chunk viewer setup
		panelChunkView = new PanelChunkView(main, this);
		internalChunkView = new JInternalFrame();
		internalChunkView.show();
		internalChunkView.setFrameIcon(null);
		internalChunkView.setTitle("Chunk Editor");
		internalChunkView.add(panelChunkView);
		((BasicInternalFrameUI) internalChunkView.getUI()).setNorthPane(null);
		internalChunkView.setBorder(null);
		desktop.add(internalChunkView);
		
		//Tile selector setup
		internalTileSelect = new JInternalFrame();
		panelPaletteSelect = new PanelPaletteSelectCE();
		internalTileSelect.add(panelPaletteSelect, BorderLayout.NORTH);
		panelTileSelect = new PanelTileSelectCE(main, this);
		internalTileSelect.show();
		internalTileSelect.setFrameIcon(null);
		internalTileSelect.setTitle("16x16 Tile Selector");
		internalTileSelect.add(panelTileSelect);
		((BasicInternalFrameUI) internalTileSelect.getUI()).setNorthPane(null);
		internalTileSelect.setBorder(null);
		desktop.add(internalTileSelect);
		
		//Options window setup
		panelOptions = new PanelOptionsCE();
		internalOptions = new JInternalFrame();
		internalOptions.show();
		internalOptions.setFrameIcon(null);
		internalOptions.setTitle("Options");
		internalOptions.add(panelOptions);
		((BasicInternalFrameUI) internalOptions.getUI()).setNorthPane(null);
		internalOptions.setBorder(null);
		desktop.add(internalOptions);
		
		//Chunk selector
		internalChunkSelect.setLocation(0, 0);
		panelChunkSelect.setPreferredSize(new Dimension(64 * 2, 64 * 6));
		internalChunkSelect.pack();
		//Chunk viewer
		internalChunkView.setLocation(internalChunkSelect.getWidth(), 0);
		panelChunkView.setPreferredSize(new Dimension(128, 128));
		internalChunkView.pack();
		//Tile selector
		internalTileSelect.setLocation(internalChunkSelect.getWidth(), internalChunkView.getHeight());
		internalTileSelect.setSize(getContentPane().getWidth() - internalChunkSelect.getWidth(), getContentPane().getHeight() - internalChunkView.getHeight());
		//Options window
		internalOptions.setLocation(internalChunkSelect.getWidth() + internalChunkView.getWidth(), 0);
		internalOptions.setSize(getContentPane().getWidth() - internalChunkSelect.getWidth() - internalChunkView.getWidth(), getContentPane().getHeight() - internalTileSelect.getHeight());
		
		internalChunkSelect.addHierarchyBoundsListener(
			new HierarchyBoundsListener() {
				public void ancestorMoved(HierarchyEvent event) {
					//Nothing... FOR NOW
				}
				public void ancestorResized(HierarchyEvent event) {
					//Chunk selector
					internalChunkSelect.setLocation(0, 0);
					panelChunkSelect.setPreferredSize(new Dimension(64 * 2, 64 * 6));
					internalChunkSelect.pack();
					internalChunkSelect.setSize(internalChunkSelect.getWidth(), getContentPane().getHeight());
					//Chunk viewer
					internalChunkView.setLocation(internalChunkSelect.getWidth(), 0);
					panelChunkView.setPreferredSize(new Dimension(128, 128));
					internalChunkView.pack();
					//Tile selector
					internalTileSelect.setLocation(internalChunkSelect.getWidth(), internalChunkView.getHeight());
					internalTileSelect.setSize(getContentPane().getWidth() - internalChunkSelect.getWidth(), getContentPane().getHeight() - internalChunkView.getHeight());
					//Options window
					internalOptions.setLocation(internalChunkSelect.getWidth() + internalChunkView.getWidth(), 0);
					internalOptions.setSize(getContentPane().getWidth() - internalChunkSelect.getWidth() - internalChunkView.getWidth(), getContentPane().getHeight() - internalTileSelect.getHeight());
					
					internalChunkSelect.scroll.setMaximum(32 - (panelChunkSelect.getHeight() / 64) + 15); //Last number = extent
				}
			}
		);
		
		//Redraw when alternate tileset options are changed
		panelPaletteSelect.altCheckbox.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					panelChunkView.repaint();
				}
			}
		); panelPaletteSelect.altDropdown.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					panelChunkView.repaint();
				}
			}
		);
		
		//Options window
		panelOptions.tileset.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					panelPaletteSelect.altCheckbox.setSelected(false);
					selectedTileset1 = panelOptions.tileset.getSelectedIndex();
					panelChunkSelect.repaint();
					panelTileSelect.repaint();
					panelChunkView.repaint();
				}
			}
		); panelOptions.palette64.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					selectedPalette = panelOptions.palette64.getSelectedIndex();
					panelChunkSelect.repaint();
					panelTileSelect.repaint();
					panelChunkView.repaint();
				}
			}
		);
		
		selectedTileset1 = panelOptions.tileset.getSelectedIndex();
		selectedPalette = panelOptions.palette64.getSelectedIndex();
		panelChunkSelect.repaint();
		panelTileSelect.repaint();
		panelChunkView.repaint();
		
		//Menu bar
		setJMenuBar(new MenuBar());
		
		//Set program icon
		Image windowIcon1 = new ImageIcon(Info.class.getResource("/icons/main1.png")).getImage();
		Image windowIcon2 = new ImageIcon(Info.class.getResource("/icons/main2.png")).getImage();
		ArrayList<Image> windowIcons = new ArrayList<Image>();
		windowIcons.add(windowIcon1);
		windowIcons.add(windowIcon2);
		this.setIconImages(windowIcons);
		
		addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent event) {
		        exitSave();
		    }
		});
	}
	
	private void exitSave() {
		final JOptionPane optionPane = new JOptionPane("<html>Save chunk data to ROM?<br/><br/><i>(Note: choosing 'no' will NOT<br/>discard your changes)</i></html>",
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
						main.gfx.save64();
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
	
	public void openFromMap(int tileNum, boolean altTile, int palette, int tileset1, int tileset2) {
		if (altTile) {
			int temp = tileset1;
			tileset1 = tileset2;
			tileset2 = temp;
		}
		panelChunkSelect.chunkSelected = tileNum;
		selectedPalette = palette;
		panelOptions.palette64.setSelectedIndex(palette);
		selectedTileset1 = tileset1;
		panelOptions.tileset.setSelectedIndex(tileset1);
		selectedTileset2 = tileset2;
		panelPaletteSelect.altDropdown.setSelectedIndex(tileset2);
		
		panelChunkView.repaint();
		panelTileSelect.repaint();
		panelChunkSelect.repaint();
	}
	
	public void keyInsert() {
		useAlternateTileset = true;
		panelPaletteSelect.altCheckbox.setEnabled(true);
		panelPaletteSelect.altDropdown.setEnabled(true);
		panelPaletteSelect.altCheckbox.setSelected(true);
		panelTileSelect.repaint();
	}
	
	public void keyNumber(int number) {
		panelPaletteSelect.palette.setSelectedIndex(number);
		panelTileSelect.repaint();
	}
	
	public void repaintAll() {
		panelChunkSelect.repaint();
		panelChunkView.repaint();
		panelTileSelect.repaint();
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
						main.gfx.save64();
						JOptionPane.showMessageDialog(desktop, "Successfully saved!");
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
	
}
