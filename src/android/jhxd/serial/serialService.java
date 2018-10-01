
package com.jhxd.serial;

public class serialService {

    static {
        try {
            System.loadLibrary("serial_jni");
        } catch (UnsatisfiedLinkError ule) {
        	ule.printStackTrace();
//            System.err.println("WARNING: Could not load serial_jni library!");
        }
    }

    public static native int serialOpen(String ttyportString);

    public static native int serialPortSetting(int fd, int baud, int dataBits, int parity, int stopBits);

    public static native int serialRead(int fd, byte[] data, int readlen);

    public static native int serialWrite(int fd, byte[] data, int writelen);

    public static native int serialClose(int fd);

}
