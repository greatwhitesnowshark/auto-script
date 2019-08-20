package util;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * This file is a part of the project auto-script, and is the intellectual property of its developer(s)
 * File created on July 14, 2019
 *
 * @author Sharky
 */
public class PacketUtil {

    //Credit to: Swordie devs for read- methods, muchas gracias ~~
    public static short ReadShort(DataInputStream dis) throws IOException {
        short s = (short) (dis.readByte() & 0xFF);
        s += (dis.readByte() & 0xFF) << 8;
        return s;
    }

    public static int ReadInt(DataInputStream dis) throws IOException {
        int s = (dis.readByte() & 0xFF);
        s += (dis.readByte() & 0xFF) << 8;
        s += (dis.readByte() & 0xFF) << 16;
        s += (dis.readByte() & 0xFF) << 24;
        return s;
    }

    public static long ReadLong(DataInputStream dis) throws IOException {
        long s = (dis.readByte() & 0xFF);
        s += (dis.readByte() & 0xFF) << 8;
        s += (dis.readByte() & 0xFF) << 16;
        s += (dis.readByte() & 0xFF) << 24;
        s += (long) (dis.readByte() & 0xFF) << 32;
        s += (long) (dis.readByte() & 0xFF) << 40;
        s += (long) (dis.readByte() & 0xFF) << 48;
        s += (long) (dis.readByte() & 0xFF) << 56;
        return s;
    }

    public static String ReadString(DataInputStream dis) throws IOException {
        int size = dis.readByte();
        byte[] arr = new byte[size];
        for (int i = 0; i < size; i++) {
            arr[i] = dis.readByte();
        }
        return new String(arr);
    }

    public static byte[] ReadArr(DataInputStream dis, int size) throws IOException {
        byte[] arr = new byte[size];
        for (int i = 0; i < size; i++) {
            arr[i] = dis.readByte();
        }
        return arr;
    }
}
