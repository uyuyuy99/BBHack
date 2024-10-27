package me.uyuyuy99.bbhack;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class RomFileIO {
	
	byte[] data;
	File rompath;
	
	public RomFileIO(File rompathGiven) {
		load(rompathGiven);
	}
	
	public RomFileIO() {
		//Bananas are pretty great
	}
	
	public void load(File rompathGiven) {
		rompath = rompathGiven;
		try {
			RandomAccessFile rom = new RandomAccessFile(rompath, "r");
			data = new byte[(int) rom.length()];
			rom.readFully(data);
			rom.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void save() {
		try {
			RandomAccessFile rom = new RandomAccessFile(rompath, "w");
			rom.write(data);
			rom.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//get and return the actual byte
	public byte getT(int offset) {
		if (data.length >= offset)
			return data[offset];
		return (byte) 0;
	}
	public byte[] getT(int offset, int length) {
		byte[] bytes = new byte[length];
		//could probably be improved. whatever
		for (int i=0; i<length; i++) {
			bytes[i] = getT(offset + i);
		}
		return bytes;
	}

	//get straight to int.
	public int get(int offset) {
		if (data.length >= offset)
			return Byte.toUnsignedInt(data[offset]);
		return 0;
	}
	public int[] get(int offset, int length) {
		int[] bytes = new int[length];
		//could probably be improved. whatever
		for (int i=0; i<length; i++) {
			bytes[i] = get(offset + i);
		}
		return bytes;
	}
	
	public void write(int offset, byte b) {
		data[offset] = b;
	}
	
	public void saveMap() {
		try {
			RandomAccessFile rom = new RandomAccessFile(rompath, "w");
			
			final int START = 0x2010;
			final int END = 0x20010;

			rom.seek(START);
			rom.write(Arrays.copyOfRange(data, START, END));
			rom.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveObjects() {
		try {
			RandomAccessFile rom = new RandomAccessFile(rompath, "w");
			
			final int START = 0x20010;
			final int END = 0x25DF6;

			rom.seek(START);
			rom.write(Arrays.copyOfRange(data, START, END));
			rom.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
