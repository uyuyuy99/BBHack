package me.uyuyuy99.bbhack.ME;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import me.uyuyuy99.bbhack.Info;
import me.uyuyuy99.bbhack.MainMenu;

public class MapEditor extends JFrame {
	
	public MainMenu main;
	private MapEditor thisRef = this;
	
	private static final long serialVersionUID = 1L;
	
	private JDesktopPane desktop;
	
	InternalMap internalMap;
	InternalChunkSelectME internalChunkSelect;
	
	PanelMap panelMap;
	PanelChunkSelectME panelChunkSelect;
	
	PanelToolbarME panelToolbar;
	PanelDebugME panelDebug;
	
	public MapEditor(MainMenu instance) {
		super("Map Editor");
		main = instance;
		
		setSize(1024, 576);
		setVisible(true);
		setLocationRelativeTo(null);
		
		desktop = new JDesktopPane();
		add(desktop, BorderLayout.CENTER);
		
		panelMap = new PanelMap(main);
		internalMap = new InternalMap(panelMap);
		internalMap.show();
		internalMap.setSize(getContentPane().getWidth(), getContentPane().getHeight());
		internalMap.setLocation(0, 0);
		internalMap.setResizable(true);
		internalMap.setFrameIcon(null);
		internalMap.setTitle("Map Editor");
		internalMap.add(panelMap);
		((BasicInternalFrameUI) internalMap.getUI()).setNorthPane(null);
		internalMap.setBorder(null);
		desktop.add(internalMap);
		
		panelChunkSelect = new PanelChunkSelectME(main, this, panelMap);
		internalChunkSelect = new InternalChunkSelectME(panelChunkSelect);
		internalChunkSelect.show();
		internalChunkSelect.setSize((panelChunkSelect.viewWidth * 64) + internalChunkSelect.getInsets().left + internalChunkSelect.getInsets().right, getContentPane().getHeight());
		internalChunkSelect.setLocation(getContentPane().getWidth() - internalChunkSelect.getWidth(), 0);
		internalChunkSelect.setFrameIcon(null);
		internalChunkSelect.setTitle("Chunk Selector");
		internalChunkSelect.add(panelChunkSelect);
		((BasicInternalFrameUI) internalChunkSelect.getUI()).setNorthPane(null);
		internalChunkSelect.setBorder(null);
		panelChunkSelect.viewHeight = (panelChunkSelect.getHeight() / 64) + 1;
		desktop.add(internalChunkSelect);
		
		internalMap.setSize(getContentPane().getWidth() - internalChunkSelect.getWidth(), getContentPane().getHeight());
		
		panelMap.setPanelChunkSelect(panelChunkSelect);
		
		panelToolbar = new PanelToolbarME(main, this);
		internalMap.add(panelToolbar, BorderLayout.NORTH);
		
		panelDebug = new PanelDebugME(main, this);
		add(panelDebug, BorderLayout.SOUTH);
		
		internalMap.addHierarchyBoundsListener(
			new HierarchyBoundsListener() {
				public void ancestorMoved(HierarchyEvent event) {
					//Nothing... FOR NOW
				}
				public void ancestorResized(HierarchyEvent event) {
					//Make sure the resizing doesn't result in map showing offscreen areas
					if (panelMap.viewX > (256 - panelMap.viewWidth)) {
						panelMap.viewX = (256 - panelMap.viewWidth);
					} if (panelMap.viewY > (224 - panelMap.viewHeight)) {
						panelMap.viewY = (224 - panelMap.viewHeight);
					}
					internalMap.setLocation(0, 0);
					internalMap.setSize(getContentPane().getWidth() - internalChunkSelect.getWidth(), getContentPane().getHeight() - panelDebug.getHeight());
					internalMap.scrollH.setMaximum(255 - (internalMap.getWidth() / 64) + 15); //Last number = extent
					internalMap.scrollV.setMaximum(223 - (internalMap.getHeight() / 64) + 15); //Last number = extent
					
					internalChunkSelect.setLocation(getContentPane().getWidth() - internalChunkSelect.getWidth(), 0);
					internalChunkSelect.setSize((panelChunkSelect.viewWidth * 64) + internalChunkSelect.getInsets().left + internalChunkSelect.getInsets().right
							+ internalChunkSelect.scroll.getWidth(), getContentPane().getHeight());
					internalChunkSelect.scroll.setMaximum(32 - (panelChunkSelect.getHeight() / 64) + 15); //Last number = extent
					panelChunkSelect.viewHeight = (panelChunkSelect.getHeight() / 64) + 1;
				}
			}
		);
		
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
		final JOptionPane optionPane = new JOptionPane("<html>Save map data to ROM?<br/><br/><i>(Note: choosing 'no' will NOT<br/>discard your changes)</i></html>",
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
						main.map.save();
					}
	                dialog.dispose();
	                dispose();
	            }
	        }
	    });
		dialog.pack();
		dialog.setLocationRelativeTo(thisRef);
		dialog.setVisible(true);
	}
	
	public void clearGraphicsCache() {
		panelMap.clearGraphicsCache();
	}
	
	public void repaintAll() {
		panelMap.repaint();
		panelChunkSelect.repaint();
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
						main.map.save();
//						main.objects.save(); TODO
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
			
			//View menu
			JMenu menuView = new JMenu("View");
			add(menuView);
			
			final JCheckBoxMenuItem itemViewGridChunk = new JCheckBoxMenuItem("Chunk Grid");
			menuView.add(itemViewGridChunk);
			itemViewGridChunk.setSelected(panelMap.viewGridChunk);
			itemViewGridChunk.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						//panelMap.viewGridChunk(itemViewGridChunk.isSelected());
						panelMap.viewGridChunk = itemViewGridChunk.isSelected();
						panelMap.repaint();
					}
				}
			);
			final JCheckBoxMenuItem itemViewGridSector = new JCheckBoxMenuItem("Sector Grid");
			menuView.add(itemViewGridSector);
			itemViewGridSector.setSelected(panelMap.viewGridSector);
			itemViewGridSector.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						//panelMap.viewGridSector(itemViewGridSector.isSelected());
						panelMap.viewGridSector = itemViewGridSector.isSelected();
						panelMap.repaint();
					}
				}
			);
			menuView.addSeparator();
			final JCheckBoxMenuItem itemViewTilesetWarnings = new JCheckBoxMenuItem("Tileset Warnings");
			menuView.add(itemViewTilesetWarnings);
			itemViewTilesetWarnings.setSelected(panelMap.viewTilesetWarnings);
			itemViewTilesetWarnings.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						//panelMap.viewTilesetWarnings(itemViewTilesetWarnings.isSelected());
						panelMap.viewTilesetWarnings = itemViewTilesetWarnings.isSelected();
						panelMap.repaint();
					}
				}
			);
			final JCheckBoxMenuItem itemViewTilesetColors = new JCheckBoxMenuItem("Tileset Colors");
			menuView.add(itemViewTilesetColors);
			itemViewTilesetColors.setSelected(panelMap.viewTilesetColors);
			itemViewTilesetColors.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						//panelMap.viewTilesetColors(itemViewTilesetColors.isSelected());
						panelMap.viewTilesetColors = itemViewTilesetColors.isSelected();
						if (itemViewTilesetColors.isSelected()) {
							panelMap.viewGridChunk = false;
							panelMap.viewGridSector = true;
							itemViewGridChunk.setSelected(false);
							itemViewGridSector.setSelected(true);
						}
						panelMap.repaint();
					}
				}
			);
			final JCheckBoxMenuItem itemViewObjects = new JCheckBoxMenuItem("Objects");
			menuView.add(itemViewObjects);
			itemViewObjects.setSelected(panelMap.viewObjects);
			itemViewObjects.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						//panelMap.viewObjects(itemViewObjects.isSelected());
						panelMap.viewObjects = itemViewObjects.isSelected();
						panelMap.repaint();
					}
				}
			);
			
			KeyEventDispatcher dispatcher = new KeyEventDispatcher() {
				public boolean dispatchKeyEvent(KeyEvent event) {
					if (!SwingUtilities.isDescendingFrom(event.getComponent(), thisRef)) return false;
					if (event.getID() != KeyEvent.KEY_PRESSED) return true;
					
			    	int key = event.getKeyCode();
			    	
			    	switch (key) {
				    	case KeyEvent.VK_1:
				    		itemViewGridChunk.doClick();
				    		break;
				    	case KeyEvent.VK_2:
				    		itemViewGridSector.doClick();
				    		break;
				    	case KeyEvent.VK_3:
				    		itemViewTilesetWarnings.doClick();
				    		break;
				    	case KeyEvent.VK_4:
				    		itemViewTilesetColors.doClick();
				    		break;
				    	case KeyEvent.VK_5:
				    		itemViewObjects.doClick();
				    		break;
			    	}
			    	
			        return true;
				}
			};
			KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(dispatcher);
			
			//Help menu
			JMenu menuHelp = new JMenu("Help");
			add(menuHelp);
			
			JMenuItem itemShortcuts = new JMenuItem("Keyboard Shortcuts");
			menuHelp.add(itemShortcuts);
			itemShortcuts.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						String text = "<html><b>RIGHT CLICK:</b> Select (copy) a chunk from the map</html>\n" +
								"<html><b>CTRL+LEFT CLICK:</b> Select tileset/palette from map without placing tile</html>\n" +
								"<html><b>SHIFT+LEFT CLICK:</b> Open chunk in chunk editor from the map editor</html>";
						
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
