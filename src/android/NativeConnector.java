package cordova-plugin-nativeconnector;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class NativeConnector extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if(action.equals("sendValue"))
        {
            this.sendValue(args, callbackContext);
            return true;
        }
        return false;
    }

    private void sendValue(JSONArray args,CallbackContext callback){
        if(args != null){
            try{
                String text1 = args.getJSONObject(0).getString("param1");
                callback.success(text1);
            }catch(Excaption ex){
                callback.error("I don't Know" + ex);
            }
        }else{
            callback.error('Please Insert PLEASEEEEEE!');
        }
    }
}
