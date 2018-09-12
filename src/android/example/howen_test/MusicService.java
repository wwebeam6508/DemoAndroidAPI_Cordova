package example.howen_test;

import java.io.IOException;

import com.example.yho_test.R;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.IBinder;
import android.widget.Toast;

public class MusicService extends Service {
	MediaPlayer mp3player;

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mp3player.stop();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		mp3player = MediaPlayer.create(this, R.raw.wish);
		mp3player.start();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub

		return null;
	}

}
