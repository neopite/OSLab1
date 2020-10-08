package com.company;

import java.nio.ByteBuffer;

public class Utils {

    public static byte[] intToByteArray(int i){
        return ByteBuffer.allocate(4).putInt(i).array();
    }
    public static byte boolToByte(boolean bool) {
        return bool ? (byte) 1 : (byte) 0;
    }

    public static boolean byteToBool(byte value) {
        return value == 1;
    }

    public static int byteArrToInt(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }
}
