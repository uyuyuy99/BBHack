package me.uyuyuy99.bbhack.ME;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.*;


public class InternalMap extends JInternalFrame {
	
	private static final long serialVersionUID = 1L;
	
	private PanelMap panelMap;
	
	public JScrollBar scrollH;
	public JScrollBar scrollV;
	
	public InternalMap(PanelMap panelInstance) {
		panelMap = panelInstance;
		
		scrollH = new JScrollBar(JScrollBar.HORIZONTAL, 0, 15, 0, 270);
		scrollV = new JScrollBar(JScrollBar.VERTICAL, 0, 15, 0, 270);
		
		add(scrollH, BorderLayout.SOUTH);
		add(scrollV, BorderLayout.EAST);
		
		scrollH.addAdjustmentListener(
			new AdjustmentListener() {
				public void adjustmentValueChanged(AdjustmentEvent event) {
					panelMap.scroll(event.getValue(), panelMap.scrollVLast);
				}
			}
		);
		
		scrollV.addAdjustmentListener(
			new AdjustmentListener() {
				public void adjustmentValueChanged(AdjustmentEvent event) {
					panelMap.scroll(panelMap.scrollHLast, event.getValue());
				}
			}
		);
		
		addMouseWheelListener(
			new MouseWheelListener() {
				public void mouseWheelMoved(MouseWheelEvent event) {
					//Vertical scrolling Vs. horizontal scrolling
					if ((getWidth() - event.getX()) < (getHeight() - event.getY())) {
						if (event.getWheelRotation() > 0) {
							if (scrollV.getValue() < (scrollV.getMaximum() - 15)) {
								scrollV.setValue(scrollV.getValue() + 1);
								panelMap.viewY = scrollV.getValue();
								panelMap.repaint();
							}
						} else if (event.getWheelRotation() < 0) {
							if (scrollV.getValue() > 0) {
								scrollV.setValue(scrollV.getValue() - 1);
								panelMap.viewY = scrollV.getValue();
								panelMap.repaint();
							}
						}
					} else {
						if (event.getWheelRotation() > 0) {
							if (scrollH.getValue() < (scrollH.getMaximum() - 15)) {
								scrollH.setValue(scrollH.getValue() + 1);
								panelMap.viewX = scrollH.getValue();
								panelMap.repaint();
							}
						} else if (event.getWheelRotation() < 0) {
							if (scrollH.getValue() > 0) {
								scrollH.setValue(scrollH.getValue() - 1);
								panelMap.viewX = scrollH.getValue();
								panelMap.repaint();
							}
						}
					}
				}
			}
		);
	}
	
}
