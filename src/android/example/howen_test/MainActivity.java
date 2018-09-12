
package example.howen_test;

import example.sockettest.SocketActivity;
import com.example.yho_test.R;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

    private Button settings_button, sim_button, camera_button, sd_button,
            record_button, gps_button, phone_button, net_button, music_button,
            video_button, gsm_button, battery_button, gpio_button, com_button, sockeT_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setConfiguration();

        init_view();

        settings_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.i("yho", "---------Settings-test-------");
                Intent settings_intent = new Intent();
                settings_intent
                        .setComponent(new ComponentName("com.android.settings",
                                "com.android.settings.Settings"));
                startActivity(settings_intent);
            }
        });

        sim_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.i("yho", "--------SIM-test-------");
                Intent sim_intent = new Intent(MainActivity.this,
                        TelephonyStatus.class);
                MainActivity.this.startActivity(sim_intent);
            }
        });

        camera_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.i("yho", "--------camera-test-------");
                Intent camera_intent = new Intent(MainActivity.this,
                        CameraActivity.class);
                MainActivity.this.startActivity(camera_intent);
            }
        });

        sd_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.i("yho", "--------sdcard-test-------");
                Intent sd_intent = new Intent(MainActivity.this,
                        SDCardStatus.class);
                MainActivity.this.startActivity(sd_intent);
            }
        });

        record_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.i("yho", "--------record-test-------");
                Intent record_intent = new Intent(MainActivity.this,
                        RecordActivity.class);
                MainActivity.this.startActivity(record_intent);
            }
        });

        gps_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.i("yho", "--------GPS-test-------");
                Intent gps_intent = new Intent(MainActivity.this,
                        LocationActivity.class);
                MainActivity.this.startActivity(gps_intent);
            }
        });

        phone_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.i("yho", "--------phone-test-------");
                Intent phone_intent = new Intent();
                phone_intent.setAction(Intent.ACTION_DIAL);
                MainActivity.this.startActivity(phone_intent);
            }
        });

        net_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.i("yho", "--------net-test-------");
                Intent net_intent = new Intent();
                Uri uri = Uri.parse("http://www.google.com");
                net_intent.setAction(Intent.ACTION_VIEW);
                net_intent.setData(uri);
                MainActivity.this.startActivity(net_intent);
            }
        });

        music_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.i("yho", "--------music-test-------");
                Intent music_intent = new Intent(MainActivity.this,
                        MusicActivity.class);
                MainActivity.this.startActivity(music_intent);
            }
        });

        video_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.i("yho", "--------video-test-------");
                Intent video_intent = new Intent(MainActivity.this,
                        VideoActivity.class);
                MainActivity.this.startActivity(video_intent);
            }
        });

        gsm_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.i("yho", "--------gsm-test-------");
                Intent gsm_intent = new Intent(MainActivity.this,
                        GsmActivity.class);
                MainActivity.this.startActivity(gsm_intent);
            }
        });

        battery_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.i("yho", "--------battery-test-------");
                Intent battery_intent = new Intent(MainActivity.this,
                        BatteryActivity.class);
                MainActivity.this.startActivity(battery_intent);
            }
        });

        gpio_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.e("yho", "------gpio-test------");
                Intent gpio_intent = new Intent(MainActivity.this,GPIOActivity.class);
                MainActivity.this.startActivity(gpio_intent);
                Log.e("yho", "------gpio-test finish------");
            }
        });

        com_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.i("yho", "------com-test------");
                Intent com_intent = new Intent(MainActivity.this,
                        SerialPortMain.class);
                MainActivity.this.startActivity(com_intent);
            }
        });

        sockeT_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.i("yho", "------socket-test------");
                Intent socket_intent = new Intent(MainActivity.this,
                        SocketActivity.class);
                MainActivity.this.startActivity(socket_intent);
            }
        });
    }

    private void setConfiguration() {
        // TODO Auto-generated method stub
        Configuration configuration = getResources().getConfiguration();
        if (configuration.orientation == configuration.ORIENTATION_PORTRAIT) {
            MainActivity.this
                    .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    private void init_view() {
        // TODO Auto-generated method stub
        settings_button = (Button) findViewById(R.id.settings_button);
        sim_button = (Button) findViewById(R.id.sim_button);
        camera_button = (Button) findViewById(R.id.camera_button);
        sd_button = (Button) findViewById(R.id.sd_button);
        record_button = (Button) findViewById(R.id.record_button);
        gps_button = (Button) findViewById(R.id.gps_button);
        phone_button = (Button) findViewById(R.id.phone_button);
        net_button = (Button) findViewById(R.id.net_button);
        music_button = (Button) findViewById(R.id.music_button);
        video_button = (Button) findViewById(R.id.video_button);
        gsm_button = (Button) findViewById(R.id.gsm_button);
        battery_button = (Button) findViewById(R.id.battery_button);
        gpio_button = (Button) findViewById(R.id.gpioButton);
        com_button = (Button) findViewById(R.id.comButton);
        sockeT_button = (Button) findViewById(R.id.socketButton);
    }
}
