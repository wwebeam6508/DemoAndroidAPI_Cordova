package example.howen_test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.example.yho_test.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class TelephonyStatus extends Activity {
	ListView showView;
	String[] statusNames;
	ArrayList<String> statusValues = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sim_main);
		TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		statusNames = getResources().getStringArray(R.array.statusNames);
		String[] simState = getResources().getStringArray(R.array.simState);
		String[] phoneType = getResources().getStringArray(R.array.phoneType);
		statusValues.add(tManager.getDeviceId());
		statusValues.add(tManager.getDeviceSoftwareVersion() != null ? tManager
				.getDeviceSoftwareVersion() : "未知");
		statusValues.add(tManager.getNetworkOperator());
		statusValues.add(tManager.getNetworkOperatorName());
		statusValues.add(phoneType[tManager.getPhoneType()]);
		statusValues.add(tManager.getCellLocation() != null ? tManager
				.getCellLocation().toString() : "未知位置");
		statusValues.add(tManager.getSimCountryIso());
		statusValues.add(tManager.getSimSerialNumber());
		statusValues.add(simState[tManager.getSimState()]);
		showView = (ListView) findViewById(R.id.show_sim);
		ArrayList<Map<String, String>> status = new ArrayList<Map<String, String>>();
		for (int i = 0; i < statusValues.size(); i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("name", statusNames[i]);
			map.put("value", statusValues.get(i));
			status.add(map);
		}
		SimpleAdapter adapter = new SimpleAdapter(this, status, R.layout.sim_line,
				new String[] { "name", "value" }, new int[] { R.id.name_sim,
						R.id.value_sim });
		showView.setAdapter(adapter);
	}
}
