package example.howen_test;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.example.yho_test.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LocationActivity extends Activity {
	private LocationManager locManager;
	private AbsoluteLayout layout;
	private MyView1 myView1;
	private MyView myView;

	private TextView tvLatitude, tvLongitude, tvSpeed, tvGpsTime,
			tvsatellItenumber;
	private Button resetbtn;

	private int satellItenumber = 0;
	private ArrayList<Gpsdata> info = null;

	private int upper_startleft = 135;
	private int upper_endleft = 20;
	private int below_startleft = 164;
	private int below_endleft = 330;
	private int upper_endleft_small = 340;
	private int below_endleft_small = 365;
	private int interval = 35;
	private int beizhi = 4;
	private int location = 8;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location);

		initView();

		openGPSSettings();

		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		Location location = locManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		updateView(location);

		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
				1, new LocationListener()
				{
					@Override
					public void onLocationChanged(Location location) {
						updateView(location);
					}

					@Override
					public void onProviderDisabled(String provider) {
						updateView(null);
					}

					@Override
					public void onProviderEnabled(String provider) {
						updateView(locManager.getLastKnownLocation(provider));
					}

					@Override
					public void onStatusChanged(String provider, int status,
							Bundle extras) {
					}
				});

		locManager.addGpsStatusListener(statusListener);
	}

	private void openGPSSettings() {
		// TODO Auto-generated method stub
		LocationManager alm = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		if (alm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Toast.makeText(LocationActivity.this, "GPS Module works Normally", Toast.LENGTH_SHORT)
					.show();
			return;
		}

		Toast.makeText(LocationActivity.this, "Please open GPS！", Toast.LENGTH_SHORT)
				.show();
		Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivityForResult(intent, 0);
	}

	private void initView() {
		// TODO Auto-generated method stub
		tvLongitude = (TextView) findViewById(R.id.longitude);
		tvLatitude = (TextView) findViewById(R.id.latitude);
		tvGpsTime = (TextView) findViewById(R.id.time);
		tvSpeed = (TextView) findViewById(R.id.speed);
		tvsatellItenumber = (TextView) findViewById(R.id.satellitenumber);
		resetbtn = (Button) findViewById(R.id.resetbtn);

		ButtonListener buttoner = new ButtonListener();
		resetbtn.setOnClickListener(buttoner);
		resetbtn.setOnTouchListener(buttoner);

		layout = (AbsoluteLayout) findViewById(R.id.base);
		myView1 = new MyView1(this);
		myView = new MyView(this);

		layout.addView(myView1);
	}

	class ButtonListener implements OnClickListener, OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == MotionEvent.ACTION_UP) {
				Log.d("test", "resetbtn ---> cancel");
				// resetbtn.setBackgroundColor(Color.BLUE);
				resetbtn.setBackgroundResource(R.drawable.blue);
			}
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				Log.d("test", "resetbtn ---> down");
				// resetbtn.setBackgroundColor(Color.LTGRAY);
				resetbtn.setBackgroundResource(R.drawable.green);
			}
			return false;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v.getId() == R.id.resetbtn) {
				Log.d("test", "resetbtn ---> onclick");

				tvLongitude.setText("0.00000");
				tvLatitude.setText("0.00000");
				tvGpsTime.setText("00:00:00");
				tvSpeed.setText("0.0");
				tvsatellItenumber.setText("0");
			}
		}
	}

	public void updateView(Location newLocation) {
		if (newLocation != null) {
			SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss");
			DecimalFormat formatterValue = new DecimalFormat("#.00000");
			DecimalFormat formatterSpeed = new DecimalFormat("#0.0");

			tvLongitude.setText(formatterValue.format(newLocation
					.getLongitude()));
			tvLatitude
					.setText(formatterValue.format(newLocation.getLatitude()));
			tvGpsTime
					.setText(formatter.format(new Date(newLocation.getTime())));
			tvSpeed.setText(formatterSpeed.format(newLocation.getSpeed()));
		}
	}

	private final GpsStatus.Listener statusListener = new GpsStatus.Listener() {
		public void onGpsStatusChanged(int event) {
			LocationManager locationManager = (LocationManager) LocationActivity.this
					.getSystemService(Context.LOCATION_SERVICE);
			GpsStatus status = locationManager.getGpsStatus(null);
			String satelliteInfo = updateGpsStatus(event, status);
			tvsatellItenumber.setText(null);
			tvsatellItenumber.setText(satelliteInfo);
		}
	};

	private String updateGpsStatus(int event, GpsStatus status) {
		StringBuilder sb2 = new StringBuilder("");
		if (status == null) {
			sb2.append(0);
		} else if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
			int maxSatellites = status.getMaxSatellites();
			Iterator<GpsSatellite> iters = status.getSatellites().iterator();

			int count = 0;

			info = null;
			info = new ArrayList<Gpsdata>();

			while (iters.hasNext() && count <= maxSatellites) {
				GpsSatellite s = iters.next();

				s.getPrn();
				s.getSnr();
				satellItenumber = count + 1;

				Gpsdata gpsdata = new Gpsdata();
				gpsdata.setSignalintensity(s.getSnr());
				gpsdata.setStatenumber(s.getPrn());

				info.add(gpsdata);

				count++;
			}

			if (myView != null) {
				layout.removeView(myView);
			}
			layout.addView(myView);

			satellItenumber = count;

			sb2.append(satellItenumber);

		}

		return sb2.toString();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_MENU) {
			// do something...
			System.exit(0);
		}

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// do something...
			System.exit(0);
		}

		return true;
	}

	private class MyView1 extends View {
		public MyView1(Context context) {
			super(context);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			canvas.drawColor(Color.TRANSPARENT);

			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.WHITE);
			for (int i = 1; i < 25; i++) {
				int upper_startleft_New = upper_startleft + interval * (i - 1);
				int below_endleft_New = below_startleft + interval * (i - 1);

				canvas.drawRect(upper_startleft_New, upper_endleft,
						below_endleft_New, below_endleft, paint);
				canvas.drawRect(upper_startleft_New, upper_endleft_small,
						below_endleft_New, below_endleft_small, paint);
			}
		}
	}

	private class MyView extends View {
		public MyView(Context context) {
			super(context);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			canvas.drawColor(Color.TRANSPARENT);
			if (info != null) {
				int j = 0;
				for (Gpsdata i : info) {
					j = j + 1;
					Paint paint2 = new Paint();
					paint2.setStyle(Paint.Style.FILL);
					paint2.setColor(getResources().getColor(R.color.gpsgreen));

					Paint paint1 = new Paint();
					paint1.setColor(Color.BLACK);
					paint1.setTextSize(24);
					int upper_startleft_New = upper_startleft + interval
							* (j - 1);
					int below_endleft_New = below_startleft + interval
							* (j - 1);

					if (j > 1) {
						canvas.drawRect(upper_startleft_New, below_endleft
								- (int) (i.getSignalintensity() * beizhi),
								below_endleft_New, below_endleft, paint2);
					} else {
						canvas.drawRect(upper_startleft, below_endleft
								- (int) (i.getSignalintensity() * beizhi),
								below_startleft, below_endleft, paint2);
					}

					if ((int) i.getSignalintensity() > 9) {
						canvas.drawText(
								String.valueOf((int) i.getSignalintensity()),
								upper_startleft + interval * (j - 1),
								below_endleft
										- (int) (i.getSignalintensity() * beizhi)
										- 5, paint1);
					} else {
						canvas.drawText(
								String.valueOf((int) i.getSignalintensity()),
								upper_startleft + interval * (j - 1) + location,
								below_endleft
										- (int) (i.getSignalintensity() * beizhi)
										- 5, paint1);
					}

					if (i.getStatenumber() > 9) {
						canvas.drawText(String.valueOf(i.getStatenumber()),
								upper_startleft + interval * (j - 1),
								below_endleft_small - 5, paint1);
					} else {
						canvas.drawText(
								String.valueOf(i.getStatenumber()),
								upper_startleft + interval * (j - 1) + location,
								below_endleft_small - 5, paint1);
					}

				}
			}
		}
	}
}
