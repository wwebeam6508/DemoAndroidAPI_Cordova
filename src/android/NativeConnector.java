package cordova.plugin.nativeconnector;
import com.nativecode.serialport.keycardClass;
import com.nativecode.serialport.GPIOClass;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * This class echoes a string called from JavaScript.
 */
public class NativeConnector extends CordovaPlugin {
  keycardClass keycard =  new keycardClass();
  GPIOClass gpio = new GPIOClass();
  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    if(action.equals("sendValue"))
    {
      this.sendValue(args, callbackContext);
      return true;
    }else if(action.equals("openKeycardPort")){
      this.openKeycardPort(callbackContext);
      return true;
    }else if(action.equals("openGPIOPort")){
      this.openGPIOPort(callbackContext);
      return true;
    }else if(action.equals("closeGPIOPort")){
      this.closeGPIOPort(callbackContext);
      return true;
    }else if(action.equals("closeKeycardPort")){
      this.closeKeycardPort(callbackContext);
      return true;
    }
    return false;
  }

  private void sendValue(JSONArray args,CallbackContext callback){
    if(args != null){
      try{
        String text1 = args.getJSONObject(0).getString("param1");
        callback.success(text1);
      }catch(Exception ex){
        callback.error("I don't Know" + ex);
      }
    }else{
      callback.error("Please Insert PLEASEEEEEE!");
    }
  }

  private void openKeycardPort(CallbackContext callback){

    try{
      keycard.init(callback);
    }catch(Exception ex){
      callback.error("I don't Know" + ex);
    }
  }

  private void closeKeycardPort(CallbackContext callback){

    try{
      keycard.closeThread();

    }catch(Exception ex){
      callback.error("I don't Know" + ex);
    }
  }

  private void openGPIOPort(CallbackContext callback){

    try{
      gpio.init(callback);
    }catch(Exception ex){
      callback.error("I don't Know" + ex);
    }
  }

  private void closeGPIOPort(CallbackContext callback){

    try{
      gpio.closeThread();
      gpio = new GPIOClass();
    }catch(Exception ex){
      callback.error("I don't Know" + ex);
    }
  }
}
