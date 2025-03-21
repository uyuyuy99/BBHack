package me.uyuyuy99.bbhack.rom;

import java.io.IOException;
import java.io.RandomAccessFile;

public class ROMAssetIO{
    public byte[] data;

	public static byte[] open(String filepath){
		byte[] rdata = new byte[0];
		try {
			RandomAccessFile rom = new RandomAccessFile(filepath, "r");
			rdata = new byte[(int) rom.length()];
			rom.readFully(rdata);
			rom.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rdata;
	}


    public ROMAssetIO(byte[] load){
        data = load;
    }

    public ROMAssetIO(String filepath){
		//load mapfile
		data = open(filepath);
    }

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

	public void write(int offset, byte b) {
		data[offset] = b;
	}

}