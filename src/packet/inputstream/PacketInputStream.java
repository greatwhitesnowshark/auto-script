/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet.inputstream;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import packet.ClientCode;
import packet.LoopbackCode;
import util.Logger;

/**
 *
 * @author Somebody from swordie, credits due
 */
public class PacketInputStream extends DataInputStream {
    
    private int nHeader = 0;
    
    public PacketInputStream(InputStream in) {
        super(in);
    }
    
    public PacketInputStream(int nHeader, InputStream in) {
        super(in);
        this.nHeader = nHeader;
    }
    
    public boolean ReadBoolean() throws IOException {
        return super.readBoolean();
    }
    
    public byte ReadByte() throws IOException {
        return super.readByte();
    }

    public short ReadShort() throws IOException {
        short nVal = (short) (readByte() & 0xFF);
        nVal += (readByte() & 0xFF) << 8;
        return nVal;
    }

    public int ReadInt() throws IOException {
        int nVal = (readByte() & 0xFF);
        nVal += (readByte() & 0xFF) << 8;
        nVal += (readByte() & 0xFF) << 16;
        nVal += (readByte() & 0xFF) << 24;
        return nVal;
    }

    public long ReadLong() throws IOException {
        long nVal = (readByte() & 0xFF);
        nVal += (readByte() & 0xFF) << 8;
        nVal += (readByte() & 0xFF) << 16;
        nVal += (readByte() & 0xFF) << 24;
        nVal += (long) (readByte() & 0xFF) << 32;
        nVal += (long) (readByte() & 0xFF) << 40;
        nVal += (long) (readByte() & 0xFF) << 48;
        nVal += (long) (readByte() & 0xFF) << 56;
        return nVal;
    }
    
    public String ReadString() throws IOException {
        return ReadString(false);
    }
    
    /**
     * Reads a string, off-sets by one for fucked up situations.
     * @param bOffsetByOne No idea why this is necessary, maybe because of conversion to a ByteArrayInputStream but @see UserTalk as a reference.
     * @return String read from the packet
     * @throws IOException 
     */
    public String ReadString(boolean bOffsetByOne) throws IOException {
        int nSize = readByte();
        if (nSize > 0) {
            byte[] aBuffer = new byte[nSize];
            for (int i = 0; i <= aBuffer.length && available() > 0; i++) {
                if (bOffsetByOne && i == 0) {
                    readByte();
                    continue;
                }
                if (!bOffsetByOne && i == aBuffer.length) {
                    break;
                }
                int nArrIndex = bOffsetByOne && i > 0 ? (i - 1) : i;
                aBuffer[nArrIndex] = readByte();
            }
            String str = new String(aBuffer);
            return str;
        }
        return "";
    }

    public byte[] ReadArr(int nSize) throws IOException {
        byte[] aBuffer = new byte[nSize];
        for (int i = 0; i < nSize; i++) {
            aBuffer[i] = readByte();
        }
        return aBuffer;
    }
    
    public void SetHeader(int nHeader) {
        this.nHeader = nHeader;
    }
}
