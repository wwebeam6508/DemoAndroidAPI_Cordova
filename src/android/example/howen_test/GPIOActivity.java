package example.howen_test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

import com.example.yho_test.R;
import com.example.yho_test.R.color;
import wzh.yho_gpio_operate.gpio_info;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.os.BatteryManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;

@SuppressLint("HandlerLeak")
public class GPIOActivity extends Activity {

	//textview
	private TextView iN_1TV = null;
	private TextView iN_2TV = null;
	private TextView iN_3TV = null;
	private TextView ACCTV = null;
	private TextView out_1TV = null;
	private TextView out_2TV = null;
	private TextView usbtTextView = null;
	private TextView usbt3TextView = null;
	private TextView simTextView = null;
	private TextView ledlighTextView = null;

	private Button out1_bt = null;
	private Button out2_bt = null;
	private Button sim_switch = null;
	private Button usb_switch = null;
	private Button usb3_switch = null;
	private Button led_light = null;

	private String[] gpioStrings = {"P3B6","P3B7","P0B6","P5C1","P5C2","P3B3","P3B4","P4D3","P3B5","P0C1"};

	private String TAG = "gpio";

	private boolean ifdestroy = false;
	private int gpio_value;
	private int i = 0;
	private String gpioValueState = "";
	private int PG5_Value;
	private int mAcGpio = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gpio);
		Log.i(TAG, "------gpio oncreate------");

		//IntentFilter batteryfilter = new IntentFilter();
		//batteryfilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		//registerReceiver(batteryReceiver, batteryfilter);

		init();

		new gpioThread().start();
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
						Message msg = handler.obtainMessage();
						msg.obj = gpioValueState;
						msg.what = i;
						handler.sendMessage(msg);

						i++;
						if (10 == i)
							i = 0;

						sleep(500);

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				out_1TV.setText("OUT1(P3B6)status is:" + msg.obj);
				break;
			case 1:
				out_2TV.setText("OUT2(P3B7)status is:" + msg.obj);
				break;
			case 2:
				usbtTextView.setText("USB_HOST_1_2_SWITCH(P0B6)status is:" + msg.obj);
				break;
			case 3:
				simTextView.setText("SIM_SIWTCH(P5C1)status is:" + msg.obj);
				break;
			case 4:
				ledlighTextView.setText("LED_EN(P5C2)status is:" + msg.obj);

				break;
			case 5:
				iN_1TV.setText("IN_1(P3B3)status is:" + msg.obj);
				break;
			case 6:
				iN_2TV.setText("IN_2(P3B4)status is:" + msg.obj);
				break;
			case 7:
				iN_3TV.setText("IN_3(P4D3)status is:" + msg.obj);
				break;
			case 8:
				ACCTV.setText("acc(P3B5)status is:" + msg.obj);
				break;
			case 9:
				usbt3TextView.setText("USB_HOST_3_SWITCH(P0C1)status is: " + msg.obj);
				break;
			default:
				break;
			}
		};
	};

	private void init() {
		// TODO Auto-generated method stub
		Log.i(TAG, "------init------");
		//textview
		ACCTV = (TextView) findViewById(R.id.acc_tv);
		iN_1TV = (TextView) findViewById(R.id.in1);
		iN_2TV = (TextView) findViewById(R.id.in2);
		iN_3TV = (TextView) findViewById(R.id.in3);
		out_1TV = (TextView) findViewById(R.id.out1_tv);
		out_2TV = (TextView) findViewById(R.id.out2_tv);
		usbtTextView = (TextView) findViewById(R.id.usb_host_1_2_switch_tv);
		usbt3TextView = (TextView) findViewById(R.id.usb_host_3_switch_tv);
		simTextView = (TextView) findViewById(R.id.sim);
		ledlighTextView = (TextView) findViewById(R.id.led_tv);

		//button
		out1_bt = (Button) findViewById(R.id.out1_btn);
		out2_bt = (Button) findViewById(R.id.out2_btn);
		usb_switch = (Button) findViewById(R.id.usb_host_1_2_switch_btn);
		usb3_switch = (Button) findViewById(R.id.usb_host_3_switch_btn);
		sim_switch = (Button) findViewById(R.id.simcard_btn);
		led_light = (Button) findViewById(R.id.led_btn);

		buttonClick click = new buttonClick();

		out1_bt.setOnClickListener(click);
		out2_bt.setOnClickListener(click);
		usb_switch.setOnClickListener(click);
		usb3_switch.setOnClickListener(click);
		sim_switch.setOnClickListener(click);
		led_light.setOnClickListener(click);
	}

	class buttonClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Log.i(TAG, "------button onclick------");

			//LGS
			switch (v.getId()) {
			case R.id.out1_btn:
				if(gpio_info.get_gpio_data("P3B6") == 1){
					gpio_info.set_gpio_data("P3B6", 0);
				}else {
					gpio_info.set_gpio_data("P3B6", 1);
				}
				break;
			case R.id.out2_btn:

				if(gpio_info.get_gpio_data("P3B7") == 1){
					gpio_info.set_gpio_data("P3B7", 0);
				}else {
					gpio_info.set_gpio_data("P3B7", 1);
				}
				break;

			case R.id.usb_host_1_2_switch_btn:

				if(gpio_info.get_gpio_data("P0B6") == 1){
					gpio_info.set_gpio_data("P0B6", 0);
				}else {
					gpio_info.set_gpio_data("P0B6", 1);
				}
				break;

			case R.id.usb_host_3_switch_btn:

				if(gpio_info.get_gpio_data("P0C1") == 1){
					gpio_info.set_gpio_data("P0C1", 0);
				}else {
					gpio_info.set_gpio_data("P0C1", 1);
				}
				break;

			case R.id.simcard_btn:
				int state = 1;
				if(gpio_info.get_gpio_data("P5C1") == 1){
					Log.d(TAG, "sim is hight ,set low");
					state = 0;
				}

				savePG4StateToFile(state);
				gpio_info.set_gpio_data("P5C1", state);
				break;
			case R.id.led_btn:
				if(gpio_info.get_gpio_data("P5C2") == 1){
					gpio_info.set_gpio_data("P5C2", 0);
				}else {
					gpio_info.set_gpio_data("P5C2", 1);
				}
				break;

			default:
				break;
			}

		}
	}

	@SuppressLint("SdCardPath")
	private void savePG4StateToFile(int state) {

//		try {
//	        File file = new File("/data/simcard");
//	        RandomAccessFile raf = new RandomAccessFile(file, "rwd");
//	        raf.seek(0);
//	        raf.writeByte(state);
//	        raf.close();
//	    } catch (Exception e) {
//	    	e.printStackTrace();
//	    }


		//LGSUtil.switchSimCard(state);
	}

		private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (!action.equals(Intent.ACTION_BATTERY_CHANGED)) {
				return;
			}
			int plugged = intent.getIntExtra("plugged", 0);

			switch (plugged) {
			case BatteryManager.BATTERY_PLUGGED_AC:
				/* AC charge */
				mAcGpio = 1;
				break;
			default:
				/* Not ac charge */
				mAcGpio = 0;
				break;
			}
		}
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i(TAG, "------ondestroy------");
		//this.unregisterReceiver(batteryReceiver);

		ifdestroy = true;
		gpio_info.close_gpio();
	}

}
