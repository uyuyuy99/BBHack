package me.uyuyuy99.bbhack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class RomFileIO {
	
	static final int ROM_SIZE = 524304;
	short[] data;
	File rompath;
	
	public RomFileIO(File rompathGiven) {
		this.rompath = rompathGiven;
		data = new short[ROM_SIZE];
		
		try {
			RandomAccessFile rom = loadRead(rompath);
			
			for (int i=0; i<ROM_SIZE; i++) {
				data[i] = rom.readByte();
			}
			
			rom.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public RomFileIO() {
		//Bananas are pretty great
	}
	
	public void load(File rompathGiven) {
		this.rompath = rompathGiven;
		data = new short[ROM_SIZE];
		
		try {
			RandomAccessFile rom = loadRead(rompath);
			
			for (int i=0; i<ROM_SIZE; i++) {
				data[i] = rom.readByte();
			}
			
			rom.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void save() {
		try {
			RandomAccessFile rom = loadWrite(rompath);
			
			for (int i=0; i<ROM_SIZE; i++) {
				rom.writeByte(data[i]);
			}
			
			rom.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public short get(int offset) {
		if (data.length >= offset)
			return (short) (data[offset] & 0xff);
		return (byte) 0;
	}
	
	public short[] get(int offset, int length) {
		if (data.length >= offset) {
			short[] bytes = new short[length];
			for (int i=0; i<length; i++) {
				bytes[i] = data[offset + i];
			}
			return bytes;
		}
		return new short[length];
	}
	
	public void write(int offset, short b) {
		data[offset] = b;
	}
	
	private RandomAccessFile loadRead(File rompath) throws FileNotFoundException, IOException {
		RandomAccessFile rom = new RandomAccessFile(rompath, "r");
		return rom;
    }
	
	private RandomAccessFile loadWrite(File rompath) throws FileNotFoundException, IOException {
		RandomAccessFile rom = new RandomAccessFile(rompath, "rw");
		return rom;
    }
	
	public void saveMap() {
		try {
			RandomAccessFile rom = loadWrite(rompath);
			
			final int START = 0x2010;
			final int END = 0x20010;
			
			rom.seek(START);
			for (int i=START; i<END; i++) {
				rom.writeByte(data[i]);
			}
			
			rom.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveObjects() {
		try {
			RandomAccessFile rom = loadWrite(rompath);
			
			final int START = 0x20010;
			final int END = 0x25DF6;
			
			rom.seek(START);
			for (int i=START; i<END; i++) {
				rom.writeByte(data[i]);
			}
			
			rom.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
