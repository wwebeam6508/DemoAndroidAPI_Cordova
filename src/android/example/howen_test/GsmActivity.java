package example.howen_test;

import com.example.yho_test.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;


public class GsmActivity extends Activity{
	private final String TAG = "G3Example";

	private ImageButton mIcon3G;
	private TextView mLabel3G;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gsm);

	//	mIcon3G = (ImageButton) findViewById(R.id.Icon_3GStatus);
		mLabel3G = (TextView) findViewById(R.id.Label_3GDetail);

		TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		tel.listen(new PhoneStateMonitor(),
				PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
						| PhoneStateListener.LISTEN_SERVICE_STATE);
	}

	public class PhoneStateMonitor extends PhoneStateListener {
		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
			super.onSignalStrengthsChanged(signalStrength);
			mLabel3G.setText("IsGsm :  " + signalStrength.isGsm()
					+ "\nCDMA Dbm :  " + signalStrength.getCdmaDbm() + " Dbm"
					+ "\nCDMA Ecio :  " + signalStrength.getCdmaEcio() + " dB*10"
					+ "\nEvdo Dbm :  " + signalStrength.getEvdoDbm() + " Dbm"
					+ "\nEvdo Ecio :  " + signalStrength.getEvdoEcio() + " dB*10"
					+ "\nGsm SignalStrength : "
					+ signalStrength.getGsmSignalStrength()
					+ "\nGsm BitErrorRate : "
					+ signalStrength.getGsmBitErrorRate());

//			mIcon3G.setImageLevel(Math.abs(signalStrength
//					.getGsmSignalStrength()));
		}

		public void onServiceStateChanged(ServiceState serviceState) {
			super.onServiceStateChanged(serviceState);

			switch (serviceState.getState()) {
			case ServiceState.STATE_EMERGENCY_ONLY:
				Log.d(TAG, "3G STATUS : STATE_EMERGENCY_ONLY");
				break;
			case ServiceState.STATE_IN_SERVICE:
				Log.d(TAG, "3G STATUS : STATE_IN_SERVICE");
				break;
			case ServiceState.STATE_OUT_OF_SERVICE:
				Log.d(TAG, "3G STATUS : STATE_OUT_OF_SERVICE");
				break;
			case ServiceState.STATE_POWER_OFF:
				Log.d(TAG, "3G STATUS : STATE_POWER_OFF");
				break;
			default:
				break;
			}
		}
	}
}
