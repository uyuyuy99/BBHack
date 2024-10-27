package me.uyuyuy99.bbhack;

import java.awt.Font;

public class Functions {
	
	public static final Font setFontSize(Font f, int size) {
		return new Font(f.getName(), f.getStyle(), size);
	}
	
	public static final Font setFontBold(Font f) {
		return new Font(f.getName(), Font.BOLD, f.getSize());
	}
	
}
