package com.nativecode.serialport.serialport;

import android.os.Message;
import android.util.Log;
import org.apache.cordova.PluginResult ;
import org.apache.cordova.CallbackContext;

import com.wzh.yho_gpio_operate.gpio_info;

import android.os.Handler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
public class GPIOClass {

  private boolean isOpen = false;

  private JSONArray sender = new JSONArray() ;

  private String[] gpioStrings = {"P3B6","P3B7","P0B6","P5C1","P5C2","P3B3","P3B4","P4D3","P3B5","P0C1","P0C2","P3C1","P7C5","P5C0","P5C3"};

  private String TAG = "gpio";
  private boolean ifdestroy = false;
  private int gpio_value;
  private int i = 0;
  private String gpioValueState = "";

  CallbackContext context;
  public void init(CallbackContext context){
    this.context = context;
    if(!isOpen){
      new gpioThread().start();
      isOpen = true;
    }

  }

  class gpioThread extends Thread {
    @Override
    public void run() {
      // TODO Auto-generated method stub
      Log.i(TAG, "------gpioThread-start------");

      if (gpio_info.open_gpio() < 0)
        Log.e(TAG, "open gpio fail");
      else {
        while (true) {
          if (ifdestroy) {
            break;
          }
          try {
//						Log.d(TAG,"get gpio : "+ gpioStrings[i]+" value");
            //if (i == 8) {
            //	gpio_value = mAcGpio;
            //} else {
            gpio_value = gpio_info.get_gpio_data(gpioStrings[i]);
            //}

            if (gpio_value == 0) {
              gpioValueState = "low";
            } else if (gpio_value == 1) {
              gpioValueState = "high";
            } else{
              gpioValueState = "XX";
            }
            if(i==8){
              System.out.println("test: "+gpioValueState);
            }


            Message msg = new  Message();
            msg.obj = gpioValueState;
            msg.what = i;
            handleMessage(msg);
            i++;


            if (15 == i){
              String stringsender = sender.toString();
              PluginResult result = new PluginResult(PluginResult.Status.OK, stringsender);
              // PluginResult result = new PluginResult(PluginResult.Status.ERROR, "YOUR_ERROR_MESSAGE");
              result.setKeepCallback(true);
              context.sendPluginResult(result);
              sender = new JSONArray();
              i = 0;
            }


            sleep(500);

          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
      }
    }
  }
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case 0:
          try{
            JSONObject jsonobj = new JSONObject();
            jsonobj.put("name","OUT1(P3B6)");
            jsonobj.put("value", msg.obj);
            sender.put(jsonobj);
          }catch(Exception ex){
            System.out.println("error : "+msg.what);
          }
          break;
        case 1:

          try{
            JSONObject jsonobj = new JSONObject();
            jsonobj.put("name","P3B7");
            jsonobj.put("value", msg.obj);
            sender.put(jsonobj);
          }catch(Exception ex){
            System.out.println("error : "+msg.what);
          }
          break;
        case 2:

          try{
            JSONObject jsonobj = new JSONObject();
            jsonobj.put("name","P0B6");
            jsonobj.put("value", msg.obj);
            sender.put(jsonobj);
          }catch(Exception ex){
            System.out.println("error : "+msg.what);
          }
          break;
        case 3:

          try{
            JSONObject jsonobj = new JSONObject();
            jsonobj.put("name","P5C1");
            jsonobj.put("value", msg.obj);
            sender.put(jsonobj);
          }catch(Exception ex){
            System.out.println("error : "+msg.what);
          }
          break;
        case 4:

          try{
            JSONObject jsonobj = new JSONObject();
            jsonobj.put("name","P5C2");
            jsonobj.put("value", msg.obj);
            sender.put(jsonobj);
          }catch(Exception ex){
            System.out.println("error : "+msg.what);
          }
          break;
        case 5:

          try{
            JSONObject jsonobj = new JSONObject();
            jsonobj.put("name","P3B3");
            jsonobj.put("value", msg.obj);
            sender.put(jsonobj);
          }catch(Exception ex){
            System.out.println("error : "+msg.what);
          }
          break;
        case 6:

          try{
            JSONObject jsonobj = new JSONObject();
            jsonobj.put("name","P3B4");
            jsonobj.put("value", msg.obj);
            sender.put(jsonobj);
          }catch(Exception ex){
            System.out.println("error : "+msg.what);
          }
          break;
        case 7:

          try{
            JSONObject jsonobj = new JSONObject();
            jsonobj.put("name","P4D3");
            jsonobj.put("value", msg.obj);
            sender.put(jsonobj);
          }catch(Exception ex){
            System.out.println("error : "+msg.what);
          }
          break;
        case 8:

          try{
            JSONObject jsonobj = new JSONObject();
            jsonobj.put("name","P3B5");
            jsonobj.put("value", msg.obj);
            sender.put(jsonobj);
          }catch(Exception ex){
            System.out.println("error : "+msg.what);
          }
          break;
        case 9:

          try{
            JSONObject jsonobj = new JSONObject();
            jsonobj.put("name","P0C1");
            jsonobj.put("value", msg.obj);
            sender.put(jsonobj);
          }catch(Exception ex){
            System.out.println("error : "+msg.what);
          }
          break;
        case 10:

          try{
            JSONObject jsonobj = new JSONObject();
            jsonobj.put("name","P0C2");
            jsonobj.put("value", msg.obj);
            sender.put(jsonobj);
          }catch(Exception ex){
            System.out.println("error : "+msg.what);
          }
          break;
        case 11:

          try{
            JSONObject jsonobj = new JSONObject();
            jsonobj.put("name","P3C1");
            jsonobj.put("value", msg.obj);
            sender.put(jsonobj);
          }catch(Exception ex){
            System.out.println("error : "+msg.what);
          }
          break;
        case 12:

          try{
            JSONObject jsonobj = new JSONObject();
            jsonobj.put("name","P7C5");
            jsonobj.put("value", msg.obj);
            sender.put(jsonobj);
          }catch(Exception ex){
            System.out.println("error : "+msg.what);
          }
          break;
        case 13:

          try{
            JSONObject jsonobj = new JSONObject();
            jsonobj.put("name","P5C0");
            jsonobj.put("value", msg.obj);
            sender.put(jsonobj);
          }catch(Exception ex){
            System.out.println("error : "+msg.what);
          }
          break;
        case 14:

          try{
            JSONObject jsonobj = new JSONObject();
            jsonobj.put("name","P5C3");
            jsonobj.put("value", msg.obj);
            sender.put(jsonobj);
          }catch(Exception ex){
            System.out.println("error : "+msg.what);
          }
          break;
        default:
          break;
      }
    };

}
