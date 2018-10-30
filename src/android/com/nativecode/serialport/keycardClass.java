package com.nativecode.serialport;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.GsonBuilder;
import com.jhxd.serial.serialService;
import android.os.Message;

import java.io.UnsupportedEncodingException;
import org.apache.cordova.PluginResult ;
import org.apache.cordova.CallbackContext;

import org.json.JSONObject;
import org.json.JSONStringer;

import serial.PortInfo;
import serial.Serial;

import com.google.gson.Gson;

public class keycardClass {
  private int i = 0;
  private int fd = -1;
  private int readsize = 0;
  private boolean isOpen = false;
  public String messege = "";
  private boolean header = false;
  public CallbackContext context;
    public String init(String port,CallbackContext callback){
      context = callback;
      if(isOpen == false){
        if ((fd = serialService.serialOpen(port)) < 0) {
          System.out.println( "can't open /dev/ttyS4 port");
          context.error("can't open "+port);
          return "can't open "+port;
        }else{

          recDataThread datathread = new recDataThread();
          datathread.start();
          isOpen = true;
          System.out.println( "can open "+port);
          return "can open "+port;
        }
      }else{
        return "";
      }
    }


  class recDataThread extends Thread {
    byte[] readdata = new byte[1024];
    int readlen = 1024;

    public void run() {
      while (fd > 0) {
        if (fd < 0) {
          System.out.println("------Close Rece Thread------");
          break;
        }

        try {
          if(isOpen == false){
            context.success("close");
            break;
          }
          readsize = serialService.serialRead(fd, readdata, readlen);
          if (readsize > 0) {
            System.out.println("------readSize:" + String.valueOf(readsize) + "------");

            byte[] tempBytes = new byte[readsize];
            for (int i = 0; i < tempBytes.length; i++) {
              tempBytes[i] = readdata[i];
            }

            String recvdataString = new String(tempBytes, "GBK");

            System.out.println("------recvData:" + recvdataString + "------");



            Message msg = new Message();
            msg.obj = recvdataString;
            msg.what = 1;
            Pattern p = Pattern.compile("^\\s*?\\%.*$");
            Matcher m = p.matcher(recvdataString);
            if(m.find() == true){
              messege = "____" + recvdataString + "____";

            }else{
                messege = messege + recvdataString + "____";
                Pattern p1 = Pattern.compile("^[+].*?[?]$");
                Matcher m1 = p1.matcher(recvdataString);

                if(m1.find() == true){

                  PluginResult result = new PluginResult(PluginResult.Status.OK, messege);
                  // PluginResult result = new PluginResult(PluginResult.Status.ERROR, "YOUR_ERROR_MESSAGE");
                  result.setKeepCallback(true);
                  context.sendPluginResult(result);
                  messege = "";

                }

            }







          }
          Thread.sleep(100);
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

 // Keep callback
      }



    };
  };
  public void closeThread(CallbackContext callback){
    isOpen = false;
    serialService.serialClose(fd);
    fd = -1;
    callback.success("close");
  }
  public void loadPorts(CallbackContext callback) {
    context = callback;
    PortInfo[] ports = Serial.listPorts();
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    String jsonparse = gson.toJson(ports);
    PluginResult result = new PluginResult(PluginResult.Status.OK, jsonparse);
    context.sendPluginResult(result);
  }
}

