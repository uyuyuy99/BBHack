package me.uyuyuy99.bbhack;

import java.util.LinkedList;

public class CoordList {
	
	private LinkedList<Integer> xList;
	private LinkedList<Integer> yList;
	
	public CoordList() {
		xList = new LinkedList<>();
		yList = new LinkedList<>();
	}
	
	public boolean contains(int x, int y) {
		for (int i=0; i<xList.size(); i++) {
			if (xList.get(i) == x && yList.get(i) == y)
				return true;
		}
		return false;
	}
	
	public void add(int x, int y) {
		if (!contains(x, y)) {
			xList.add(x);
			yList.add(y);
		}
	}
	
	public void remove(int x, int y) {
		if (contains(x, y)) {
			xList.remove(Integer.valueOf(x));
			yList.remove(Integer.valueOf(y));
		}
	}
	
	public int size() {
		return xList.size();
	}
	
	public int getX(int i) {
		return xList.get(i);
	}
	
	public int getY(int i) {
		return yList.get(i);
	}
	
	public void clear() {
		xList.clear();
		yList.clear();
	}

}
