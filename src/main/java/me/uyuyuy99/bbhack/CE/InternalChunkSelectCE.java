package me.uyuyuy99.bbhack.CE;

import java.awt.BorderLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.*;


public class InternalChunkSelectCE extends JInternalFrame {
	
	private static final long serialVersionUID = 1L;
	
	private PanelChunkSelectCE panelChunkSelect;
	
	public JScrollBar scroll;
	
	public InternalChunkSelectCE(PanelChunkSelectCE panelInstance) {
		panelChunkSelect = panelInstance;
		
		scroll = new JScrollBar(JScrollBar.VERTICAL, 0, 15, 0, 255);
		add(scroll, BorderLayout.EAST);
		
		scroll.addAdjustmentListener(
			new AdjustmentListener() {
				public void adjustmentValueChanged(AdjustmentEvent event) {
					panelChunkSelect.viewY = event.getValue();
					panelChunkSelect.repaint();
				}
			}
		);
		
		addMouseWheelListener(
			new MouseWheelListener() {
				public void mouseWheelMoved(MouseWheelEvent event) {
					if (event.getWheelRotation() > 0) {
						if (scroll.getValue() < (scroll.getMaximum() - 15)) {
							scroll.setValue(scroll.getValue() + 1);
							panelChunkSelect.viewY = scroll.getValue();
							panelChunkSelect.repaint();
						}
					} else if (event.getWheelRotation() < 0) {
						if (scroll.getValue() > 0) {
							scroll.setValue(scroll.getValue() - 1);
							panelChunkSelect.viewY = scroll.getValue();
							panelChunkSelect.repaint();
						}
					}
				}
			}
		);
	}
	
}
