package example.howen_test;

import com.example.yho_test.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.TextView;

public class BatteryActivity extends Activity {
	private int BatteryN;
	private int BatteryV;
	private int BatteryT;
	private String BatteryStatus;
	private String BatteryTemp;

	public TextView TV;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.battery);

		registerReceiver(mBatInfoReceiver, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));

		TV = (TextView) findViewById(R.id.battery_testview);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(mBatInfoReceiver);
	}

	private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
				BatteryN = intent.getIntExtra("level", 0);
				BatteryV = intent.getIntExtra("voltage", 0);
				BatteryT = intent.getIntExtra("temperature", 0);

				switch (intent.getIntExtra("status",
						BatteryManager.BATTERY_STATUS_UNKNOWN)) {
				case BatteryManager.BATTERY_STATUS_CHARGING:
					BatteryStatus = "Battry is being charging";
					break;
				case BatteryManager.BATTERY_STATUS_DISCHARGING:
					BatteryStatus = "Discharging the battery";
					break;
				case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
					BatteryStatus = "Battery is not being charging";
					break;
				case BatteryManager.BATTERY_STATUS_FULL:
					BatteryStatus = "Battery is fully charged";
					break;
				case BatteryManager.BATTERY_STATUS_UNKNOWN:
					BatteryStatus = "Battery is in unknown status";
					break;
				}

				switch (intent.getIntExtra("health",
						BatteryManager.BATTERY_HEALTH_UNKNOWN)) {
				case BatteryManager.BATTERY_HEALTH_UNKNOWN:
					BatteryTemp = "Unknown error";
					break;
				case BatteryManager.BATTERY_HEALTH_GOOD:
					BatteryTemp = "Battery is in a good condition";
					break;
				case BatteryManager.BATTERY_HEALTH_DEAD:
					BatteryTemp = "Battery is dead";
					break;
				case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
					BatteryTemp = "Batttery volatge is too high";
					break;
				case BatteryManager.BATTERY_HEALTH_OVERHEAT:
					BatteryTemp = "Battery is overheated";
					break;
				}
				TV.setText("Battery level: " + BatteryN + "% \n" + "Battery status: "
						+ BatteryStatus + "\n" + "battery voltage is: " + BatteryV
						+ "mV \n" + "Condition of battery usage:  " + BatteryTemp + "\n"
						+ "Battery temperature:  " + (BatteryT * 0.1) + "℃");
			}
		}
	};

}
