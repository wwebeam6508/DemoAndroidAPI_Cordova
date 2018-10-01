package com.nativecode.serialport;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class keycard {
    public void init(){
        super();
        try {
            System.loadLibrary("serial_jni");
        } catch (UnsatisfiedLinkError ule) {
            ule.printStackTrace();
//            System.err.println("WARNING: Could not load serial_jni library!");
        }
    }
}
